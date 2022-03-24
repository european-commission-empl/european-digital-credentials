package eu.europa.ec.empl.edci.security.oidc.filter;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.*;
import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mitre.jwt.signer.service.JWTSigningAndValidationService;
import org.mitre.jwt.signer.service.impl.JWKSetCacheService;
import org.mitre.jwt.signer.service.impl.SymmetricKeyJWTValidatorCacheService;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.PKCEAlgorithm;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.openid.connect.client.OIDCAuthenticationFilter;
import org.mitre.openid.connect.client.model.IssuerServiceResponse;
import org.mitre.openid.connect.config.ServerConfiguration;
import org.mitre.openid.connect.model.PendingOIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.*;

public class EDCIOIDCAuthenticationFilter extends OIDCAuthenticationFilter {

    private HttpClient httpClient;
    @Autowired(
            required = false
    )
    private JWKSetCacheService validationServices;
    @Autowired(
            required = false
    )
    private SymmetricKeyJWTValidatorCacheService symmetricCacheService;
    @Autowired(
            required = false
    )
    private JWTSigningAndValidationService authenticationSignerService;
    @Autowired(
            required = false
    )
    //must be configured at spring-security.xml
    private IConfigService configService;

    @Autowired
    public void setFilterProcessUrl() {
        super.setFilterProcessesUrl(configService.getString(EDCIConfig.Security.LOGIN_URL));
    }

    public IConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(IConfigService configService) {
        this.configService = configService;
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (this.validationServices == null) {
            this.validationServices = new JWKSetCacheService();
        }

        if (this.symmetricCacheService == null) {
            this.symmetricCacheService = new SymmetricKeyJWTValidatorCacheService();
        }

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //if user is mocked, do not request IDP
        if (this.getConfigService().getBoolean(EDCIConfig.Security.MOCK_USER_ACTIVE)) {
            return this.getAuthenticationManager().authenticate(new PreAuthenticatedAuthenticationToken(null, null, null));
        }

        //If no mock User, proceed as normal, but use custom implementation of handleAuthorizationRequest
        if (!Strings.isNullOrEmpty(request.getParameter("error"))) {
            this.handleError(request, response);
            return null;
        } else if (!Strings.isNullOrEmpty(request.getParameter("code"))) {
            Authentication auth = this.handleAuthorizationCodeResponse(request, response);
            logger.debug(String.format("OIDC - Finished code flow authentication for user %s in session %s", auth.getName(), request.getSession().getId()));
            return auth;
        } else {
            String redirectURI = request.getParameter(EDCIParameter.REDIRECTURI);
            if (redirectURI != null) {
                request.getSession().setAttribute(EDCIParameter.REDIRECTURI, redirectURI);
            }
            logger.debug(String.format("OIDC - Starting redirection to ECAS for session %s, url to redirect will be %s", request.getSession().getId(), redirectURI));
            this.handleAuthorizationRequest(request, response);
            return null;
        }

    }

    //Copied from parent to override createCodeVerifier call for eu login compatibility
    protected void handleAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        IssuerServiceResponse issResp = super.getIssuerService().getIssuer(request);
        if (issResp == null) {
            this.logger.error("Null issuer response returned from eu.europa.ec.empl.edci.dss.service.");
            throw new AuthenticationServiceException("No issuer found.");
        } else {
            if (issResp.shouldRedirect()) {
                response.sendRedirect(issResp.getRedirectUrl());
            } else {
                String issuer = issResp.getIssuer();
                if (!Strings.isNullOrEmpty(issResp.getTargetLinkUri())) {
                    session.setAttribute("target", issResp.getTargetLinkUri());
                }

                if (Strings.isNullOrEmpty(issuer)) {
                    this.logger.error("No issuer found: " + issuer);
                    throw new AuthenticationServiceException("No issuer found: " + issuer);
                }

                ServerConfiguration serverConfig = this.getServerConfigurationService().getServerConfiguration(issuer);
                if (serverConfig == null) {
                    this.logger.error("No server configuration found for issuer: " + issuer);
                    throw new AuthenticationServiceException("No server configuration found for issuer: " + issuer);
                }

                session.setAttribute("issuer", serverConfig.getIssuer());
                RegisteredClient clientConfig = this.getClientConfigurationService().getClientConfiguration(serverConfig);
                if (clientConfig == null) {
                    this.logger.error("No client configuration found for issuer: " + issuer);
                    throw new AuthenticationServiceException("No client configuration found for issuer: " + issuer);
                }

                String redirectUri = null;
                if (clientConfig.getRegisteredRedirectUri() != null && clientConfig.getRegisteredRedirectUri().size() == 1) {
                    redirectUri = (String) Iterables.getOnlyElement(clientConfig.getRegisteredRedirectUri());
                } else {
                    redirectUri = request.getRequestURL().toString();
                }

                session.setAttribute("redirect_uri", redirectUri);
                String nonce = createNonce(session);
                String state = createState(session);
                Map<String, String> options = this.getAuthRequestOptionsService().getOptions(serverConfig, clientConfig, request);
                String codeVerifier;
                if (clientConfig.getCodeChallengeMethod() != null) {
                    codeVerifier = createCodeVerifier(session);
                    options.put("code_challenge_method", clientConfig.getCodeChallengeMethod().getName());
                    if (clientConfig.getCodeChallengeMethod().equals(PKCEAlgorithm.plain)) {
                        options.put("code_challenge", codeVerifier);
                    } else if (clientConfig.getCodeChallengeMethod().equals(PKCEAlgorithm.S256)) {
                        try {
                            MessageDigest digest = MessageDigest.getInstance("SHA-256");
                            String hash = Base64URL.encode(digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII))).toString();
                            options.put("code_challenge", hash);
                        } catch (NoSuchAlgorithmException var15) {
                            logger.error(var15);
                        }
                    }
                }

                codeVerifier = super.getAuthRequestUrlBuilder().buildAuthRequestUrl(serverConfig, clientConfig, redirectUri, nonce, state, options, issResp.getLoginHint());
                this.logger.debug("Auth Request:  " + codeVerifier);
                response.sendRedirect(codeVerifier);
            }
        }
    }

    protected static String createCodeVerifier(HttpSession session) {
        final byte[] bytes = new byte[43];
        new SecureRandom().nextBytes(bytes);
        String challenge = Base64.getEncoder().encodeToString(bytes);
        session.setAttribute("code_verifier", challenge);
        return challenge;
    }

    //Copied from parent to add refresh_token_max_age
    protected Authentication handleAuthorizationCodeResponse(HttpServletRequest request, HttpServletResponse response) {
        String authorizationCode = request.getParameter("code");
        HttpSession session = request.getSession();
        String storedState = getStoredState(session);
        String requestState = request.getParameter("state");
        if (storedState != null && storedState.equals(requestState)) {
            String issuer = getStoredSessionString(session, "issuer");
            ServerConfiguration serverConfig = super.getServerConfigurationService().getServerConfiguration(issuer);
            final RegisteredClient clientConfig = super.getClientConfigurationService().getClientConfiguration(serverConfig);
            MultiValueMap<String, String> form = new LinkedMultiValueMap();
            form.add("grant_type", "authorization_code");
            form.add("code", authorizationCode);
            form.setAll(super.getAuthRequestOptionsService().getTokenOptions(serverConfig, clientConfig, request));
            String codeVerifier = getStoredCodeVerifier(session);
            if (codeVerifier != null) {
                form.add("code_verifier", codeVerifier);
            }

            String redirectUri = getStoredSessionString(session, "redirect_uri");
            if (redirectUri != null) {
                form.add("redirect_uri", redirectUri);
            }

            if (this.httpClient == null) {
                this.httpClient = HttpClientBuilder.create().useSystemProperties().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(this.httpSocketTimeout).build()).build();
            }

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(this.httpClient);
            RestTemplate restTemplate;
            JWTSigningAndValidationService signer;
            Date accessTokenExpirationDate;
            Date idTokenExpirationDate;
            if (ClientDetailsEntity.AuthMethod.SECRET_BASIC.equals(clientConfig.getTokenEndpointAuthMethod())) {
                restTemplate = new RestTemplate(factory) {
                    protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
                        ClientHttpRequest httpRequest = super.createRequest(url, method);
                        httpRequest.getHeaders().add("Authorization", String.format("Basic %s", com.nimbusds.jose.util.Base64.encode(String.format("%s:%s", UriUtils.encodePathSegment(clientConfig.getClientId(), "UTF-8"), UriUtils.encodePathSegment(clientConfig.getClientSecret(), "UTF-8")))));
                        return httpRequest;
                    }
                };
            } else {
                restTemplate = new RestTemplate(factory);
                if (!ClientDetailsEntity.AuthMethod.SECRET_JWT.equals(clientConfig.getTokenEndpointAuthMethod()) && !ClientDetailsEntity.AuthMethod.PRIVATE_KEY.equals(clientConfig.getTokenEndpointAuthMethod())) {
                    form.add("client_id", clientConfig.getClientId());
                    form.add("client_secret", clientConfig.getClientSecret());
                } else {
                    signer = null;
                    JWSAlgorithm alg = clientConfig.getTokenEndpointAuthSigningAlg();
                    if (!ClientDetailsEntity.AuthMethod.SECRET_JWT.equals(clientConfig.getTokenEndpointAuthMethod()) || !JWSAlgorithm.HS256.equals(alg) && !JWSAlgorithm.HS384.equals(alg) && !JWSAlgorithm.HS512.equals(alg)) {
                        if (ClientDetailsEntity.AuthMethod.PRIVATE_KEY.equals(clientConfig.getTokenEndpointAuthMethod())) {
                            signer = this.authenticationSignerService;
                            if (alg == null) {
                                alg = this.authenticationSignerService.getDefaultSigningAlgorithm();
                            }
                        }
                    } else {
                        signer = this.symmetricCacheService.getSymmetricValidtor(clientConfig.getClient());
                    }

                    if (signer == null) {
                        throw new AuthenticationServiceException("Couldn't find required signer eu.europa.ec.empl.edci.dss.service for use with private key auth.");
                    }

                    JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();
                    claimsSet.issuer(clientConfig.getClientId());
                    claimsSet.subject(clientConfig.getClientId());
                    claimsSet.audience(Lists.newArrayList(new String[]{serverConfig.getTokenEndpointUri()}));
                    claimsSet.jwtID(UUID.randomUUID().toString());
                    accessTokenExpirationDate = new Date(System.currentTimeMillis() + 60000L);
                    claimsSet.expirationTime(accessTokenExpirationDate);
                    idTokenExpirationDate = new Date(System.currentTimeMillis());
                    claimsSet.issueTime(idTokenExpirationDate);
                    claimsSet.notBeforeTime(idTokenExpirationDate);
                    JWSHeader header = new JWSHeader(alg, (JOSEObjectType) null, (String) null, (Set) null, (URI) null, (JWK) null, (URI) null, (Base64URL) null, (Base64URL) null, (List) null, signer.getDefaultSignerKeyId(), (Map) null, (Base64URL) null);
                    SignedJWT jwt = new SignedJWT(header, claimsSet.build());
                    signer.signJwt(jwt, alg);
                    form.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
                    form.add("client_assertion", jwt.serialize());
                }
            }

            this.logger.debug("tokenEndpointURI = " + serverConfig.getTokenEndpointUri());
            this.logger.debug("form = " + form);
            signer = null;

            String jsonString;

            //Modification: Add refresh token max age if session timeout is defined, otherwise eu login default is 1 min
            Integer sessionTimeOut = this.getConfigService().getInteger(EDCIConstants.Security.CONFIG_PROPERTY_SESSION_TIMEOUT);
            if (sessionTimeOut != null && sessionTimeOut != 0) {
                form.add("refresh_token_max_age", String.valueOf(sessionTimeOut * 60));
            }
            try {
                jsonString = (String) restTemplate.postForObject(serverConfig.getTokenEndpointUri(), form, String.class, new Object[0]);
            } catch (RestClientException var30) {
                this.logger.error("Token Endpoint error response:  " + var30.getMessage());
                throw new AuthenticationServiceException("Unable to obtain Access Token: " + var30.getMessage());
            }

            this.logger.debug("from TokenEndpoint jsonString = " + jsonString);
            JsonElement jsonRoot = (new JsonParser()).parse(jsonString);
            if (!jsonRoot.isJsonObject()) {
                throw new AuthenticationServiceException("Token Endpoint did not return a JSON object: " + jsonRoot);
            } else {
                JsonObject tokenResponse = jsonRoot.getAsJsonObject();
                String accessTokenValue;
                if (tokenResponse.get("error") != null) {
                    accessTokenValue = tokenResponse.get("error").getAsString();
                    this.logger.error("Token Endpoint returned: " + accessTokenValue);
                    throw new AuthenticationServiceException("Unable to obtain Access Token.  Token Endpoint returned: " + accessTokenValue);
                } else {
                    accessTokenValue = null;
                    String refreshTokenValue = null;
                    if (tokenResponse.has("access_token")) {
                        accessTokenValue = tokenResponse.get("access_token").getAsString();
                        if (!tokenResponse.has("id_token")) {
                            this.logger.error("Token Endpoint did not return an id_token");
                            throw new AuthenticationServiceException("Token Endpoint did not return an id_token");
                        } else {
                            String idTokenValue = tokenResponse.get("id_token").getAsString();
                            if (tokenResponse.has("refresh_token")) {
                                refreshTokenValue = tokenResponse.get("refresh_token").getAsString();
                            }

                            try {
                                JWT idToken = JWTParser.parse(idTokenValue);
                                JWTClaimsSet idClaims = idToken.getJWTClaimsSet();
                                JWTSigningAndValidationService jwtValidator = null;
                                Algorithm tokenAlg = idToken.getHeader().getAlgorithm();
                                Algorithm clientAlg = clientConfig.getIdTokenSignedResponseAlg();
                                if (clientAlg != null && !clientAlg.equals(tokenAlg)) {
                                    throw new AuthenticationServiceException("Token algorithm " + tokenAlg + " does not match expected algorithm " + clientAlg);
                                } else {
                                    if (idToken instanceof PlainJWT) {
                                        if (clientAlg == null) {
                                            throw new AuthenticationServiceException("Unsigned ID tokens can only be used if explicitly configured in client.");
                                        }

                                        if (tokenAlg != null && !tokenAlg.equals(Algorithm.NONE)) {
                                            throw new AuthenticationServiceException("Unsigned token received, expected signature with " + tokenAlg);
                                        }
                                    } else if (idToken instanceof SignedJWT) {
                                        SignedJWT signedIdToken = (SignedJWT) idToken;
                                        if (!tokenAlg.equals(JWSAlgorithm.HS256) && !tokenAlg.equals(JWSAlgorithm.HS384) && !tokenAlg.equals(JWSAlgorithm.HS512)) {
                                            jwtValidator = this.validationServices.getValidator(serverConfig.getJwksUri());
                                        } else {
                                            jwtValidator = this.symmetricCacheService.getSymmetricValidtor(clientConfig.getClient());
                                        }

                                        if (jwtValidator == null) {
                                            this.logger.error("No validation eu.europa.ec.empl.edci.dss.service found. Skipping signature validation");
                                            throw new AuthenticationServiceException("Unable to find an appropriate signature validator for ID Token.");
                                        }

                                        if (!jwtValidator.validateSignature(signedIdToken)) {
                                            throw new AuthenticationServiceException("Signature validation failed");
                                        }
                                    }

                                    if (idClaims.getIssuer() == null) {
                                        throw new AuthenticationServiceException("Id Token Issuer is null");
                                        //Add request auth URL for docker environment
                                    } else if (!idClaims.getIssuer().equals(serverConfig.getIssuer())) {
                                        throw new AuthenticationServiceException("Issuers do not match, expected " + serverConfig.getIssuer() + " got " + idClaims.getIssuer());
                                    } else if (idClaims.getExpirationTime() == null) {
                                        throw new AuthenticationServiceException("Id Token does not have required expiration claim");
                                    } else {
                                        Date now = new Date(System.currentTimeMillis() - (long) (super.getTimeSkewAllowance() * 1000));
                                        if (now.after(idClaims.getExpirationTime())) {
                                            throw new AuthenticationServiceException("Id Token is expired: " + idClaims.getExpirationTime());
                                        } else {
                                            if (idClaims.getNotBeforeTime() != null) {
                                                now = new Date(System.currentTimeMillis() + (long) (super.getTimeSkewAllowance() * 1000));
                                                if (now.before(idClaims.getNotBeforeTime())) {
                                                    throw new AuthenticationServiceException("Id Token not valid untill: " + idClaims.getNotBeforeTime());
                                                }
                                            }

                                            if (idClaims.getIssueTime() == null) {
                                                throw new AuthenticationServiceException("Id Token does not have required issued-at claim");
                                            } else {
                                                now = new Date(System.currentTimeMillis() + (long) (super.getTimeSkewAllowance() * 1000));
                                                if (now.before(idClaims.getIssueTime())) {
                                                    throw new AuthenticationServiceException("Id Token was issued in the future: " + idClaims.getIssueTime());
                                                } else if (idClaims.getAudience() == null) {
                                                    throw new AuthenticationServiceException("Id token audience is null");
                                                } else if (!idClaims.getAudience().contains(clientConfig.getClientId())) {
                                                    throw new AuthenticationServiceException("Audience does not match, expected " + clientConfig.getClientId() + " got " + idClaims.getAudience());
                                                } else {
                                                    String nonce = idClaims.getStringClaim("nonce");
                                                    if (Strings.isNullOrEmpty(nonce)) {
                                                        this.logger.error("ID token did not contain a nonce claim.");
                                                        throw new AuthenticationServiceException("ID token did not contain a nonce claim.");
                                                    } else {
                                                        String storedNonce = getStoredNonce(session);
                                                        if (!nonce.equals(storedNonce)) {
                                                            this.logger.error("Possible replay attack detected! The comparison of the nonce in the returned ID Token to the session nonce failed. Expected " + storedNonce + " got " + nonce + ".");
                                                            throw new AuthenticationServiceException("Possible replay attack detected! The comparison of the nonce in the returned ID Token to the session nonce failed. Expected " + storedNonce + " got " + nonce + ".");
                                                        } else {
                                                            PendingOIDCAuthenticationToken token = new PendingOIDCAuthenticationToken(idClaims.getSubject(), idClaims.getIssuer(), serverConfig, idToken, accessTokenValue, refreshTokenValue);
                                                            Authentication authentication = this.getAuthenticationManager().authenticate(token);
                                                            return authentication;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (ParseException var31) {
                                throw new AuthenticationServiceException("Couldn't parse idToken: ", var31);
                            }
                        }
                    } else {
                        throw new AuthenticationServiceException("Token Endpoint did not return an access_token: " + jsonString);
                    }
                }
            }
        } else {
            throw new AuthenticationServiceException("State parameter mismatch on return. Expected " + storedState + " got " + requestState);
        }
    }

    private static String getStoredSessionString(HttpSession session, String key) {
        Object o = session.getAttribute(key);
        return o != null && o instanceof String ? o.toString() : null;
    }

    @Override
    public JWKSetCacheService getValidationServices() {
        return validationServices;
    }

    @Override
    public void setValidationServices(JWKSetCacheService validationServices) {
        this.validationServices = validationServices;
    }

    @Override
    public SymmetricKeyJWTValidatorCacheService getSymmetricCacheService() {
        return symmetricCacheService;
    }

    @Override
    public void setSymmetricCacheService(SymmetricKeyJWTValidatorCacheService symmetricCacheService) {
        this.symmetricCacheService = symmetricCacheService;
    }

    public JWTSigningAndValidationService getAuthenticationSignerService() {
        return authenticationSignerService;
    }

    public void setAuthenticationSignerService(JWTSigningAndValidationService authenticationSignerService) {
        this.authenticationSignerService = authenticationSignerService;
    }
}
