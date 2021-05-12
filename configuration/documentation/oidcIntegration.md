# EuLogin OIDC integration

## What is EU Login?
EuLogin is a Oauth2/Open ID Connect Identity provider service which allows central authentication and single sing-on between applications using it, see more at https://webgate.ec.europa.eu/CITnet/confluence/pages/viewpage.action?pageId=24641907.

## Which implementation/flow is used in EDCI applications?
There are multiple possible OIDC implementations described at https://webgate.ec.europa.eu/CITnet/confluence/pages/viewpage.action?pageId=738132209.

For EDCI applications, back-end implementation (classic web-app application) is used, in both login flow (auth code) and client credentials flow (token introspection).

For an explantion on this, see https://webgate.ec.europa.eu/CITnet/confluence/display/IAM/OpenID+Connect+for+back-end+applications.

## Which libraries are used?
Spring-security(see https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/) manages the secured areas of the EDCI applications and intercepts any-non authenticated requests, redirecting them to login page or responding with a 401 http Status code.

For the authentication and communicating with IDP, MITREid Connect client (https://github.com/mitreid-connect/) is used. Once the user is correctly authenticated, autentication is wired into Spring-security context which controls the access.

## Implementation notes
In this section, you will found all of the required implementation for OIDC in EDCI applications.

## Dependencies
The following dependencies will be necessary, note that httpClient should be excluded from openid-connect-common. If not, it would conflict with dds' httpclient version.

``` xml

<dependency>
 <groupId>org.mitre</groupId>
 <artifactId>openid-connect-client</artifactId>
 <version>1.3.3</version>
</dependency>

<dependency>
 <groupId>org.mitre</groupId>
 <artifactId>openid-connect-common</artifactId>
 <version>1.3.3</version>
 <exclusions>
 <exclusion>
 <groupId>org.apache.httpcomponents</groupId>
 <artifactId>httpclient</artifactId>
 </exclusion>
 </exclusions>
</dependency>

<dependency>
 <groupId>org.springframework.security.oauth</groupId>
 <artifactId>spring-security-oauth2</artifactId>
 <version>2.2.0.RELEASE</version>
</dependency>

```

Notice the exclussion of the httpClient artifact from openid-connect-common, which would cause an issue with dss artifacts dependencies due to diferent versions.

## MITREid Connect configuration
MITREied is implemented in two different filters that support two ways of authentication: Unlogged users that are logging in through EDCI applications, or users that already had a valid token in the Authentication HTTP header.

### Common Configuration
The Authentication Manager and Authentication Provider need to be configured for both of the two authentication flows.

#### Authentication Manager.

``` xml

<security:authentication-manager alias="authenticationManager">
 <security:authentication-provider ref="openIdConnectAuthenticationProvider"/>
</security:authentication-manager>

``` 

Here, you can see an example configuration for the AuthenticationManager, which is the default one for spring-security, but with a custom authentication provider

##### Authentication Provider.

``` xml

<bean id="openIdConnectAuthenticationProvider" class="eu.europa.ec.empl.edci.issuer.service.security.EDCIOIDCAuthenticationProvider">
 <property name="tokenServices" ref="introspectingService"/>
 <property name="serverConfigurationService" ref="dynamicServerConfigurationService"/>
</bean>

``` 

Here, you can see a configuration for the EDCIOIDCAuthenticationProvider, which is the custom implementation of OIDCAuthenticationProvider (org.mitre.openid.connect.client.OIDCAuthenticationProvider could be used if not introspection token flow is required or different types of authentication context objects are acceptable).

Also, a configuration for the Token Service (The bean which will call the introspecting endpoint to validate the token) must be specified. For this, the default mitre filter (org.mitre.oauth2.introspectingfilter.IntrospectingTokenService) is valid.


``` xml

<bean id="introspectingService" class="org.mitre.oauth2.introspectingfilter.IntrospectingTokenService">
 <property name="introspectionConfigurationService" ref="staticIntrospectionConfigurationService"/>
 <property name="introspectionAuthorityGranter" ref="simpleIntrospectionAuthorityGranter"/>
</bean>

``` 

Below you can see an example of a static Introspection service ( an introspection configuration in which the IDP config stays the same over time). The instrospection Endpoint URL as well as the clientID an clientSecret are required.

``` xml

<bean id="staticIntrospectionConfigurationService" class="org.mitre.oauth2.introspectingfilter.service.impl.StaticIntrospectionConfigurationService">
 <property name="introspectionUrl" value="${oidc.idp.introspection.url}"/>
 <property name="clientConfiguration">
 <bean class="org.mitre.oauth2.model.RegisteredClient">
 <property name="clientId" value="${oidc.client.id}"/>
 <property name="clientSecret" value="${oidc.client.secret}"/>
 </bean>
 </property>
</bean>

```

Even though roles are not yet used in EDCI applications, an authority granter is required, in this case the simplest one is used:

``` xml
<bean id="simpleIntrospectionAuthorityGranter" class=" org.mitre.oauth2.introspectingfilter.service.impl.SimpleIntrospectionAuthorityGranter"/>
```

### Login through EDCI application
The class that implements this filter is OIDCAuthenticationFilter (from MITREid library), which handles the IDP calls. And the one that provides the authentication context to spring is OIDCAuthenticationProvider. Both of this classes have been customized and will be explained later on in this document.

#### Login EntryPoint HTTP Configuration 
The filter is configured in a single URL and added to the spring-security chain, this URL is both, the entry point for redirecting to the IDP with the correct parameters, and also the endopint that handles the response redirect from the IDP and authenticates de user.

The security Configuration for the filter to take place would be as follows:

``` xml

<security:http auto-config="false" use-expressions="true" create-session="always" disable-url-rewriting="true" pattern="${oidc.login.url}" entry-point-ref="authenticationEntryPoint">
         <security:custom-filter after="PRE_AUTH_FILTER" ref="openIdConnectAuthenticationFilter"/>
</security:http>

```

Here, the filter has been set up in his independent security context, to only be executed in the login entrypoint URL ${oidc.login.url}.

#### EntryPoint Filter Configuration
The following example represents a configuration for theOIDCAuthenticationfilter, which has been replaced with the custom "EDCIOIDCAuthenticationFilter" (org.mitre.openid.connect.client.OIDCAuthenticationFilter could be used if not introspection token flow is required or different types of authentication context objects are acceptable)

#### Authentication Filter

``` xml

<bean id="openIdConnectAuthenticationFilter" class="eu.europa.ec.empl.edci.issuer.web.filter.EDCIOIDCAuthenticationFilter">
 <property name="authenticationManager" ref="authenticationManager"/>
 <property name="issuerService" ref="staticIssuerService"/>
 <property name="serverConfigurationService" ref="dynamicServerConfigurationService"/>
 <property name="clientConfigurationService" ref="staticClientConfigurationService"/>
 <property name="authRequestOptionsService" ref="staticAuthRequestOptionsService"/>
 <property name="authRequestUrlBuilder" ref="plainAuthRequestUrlBuilder"/>
 <property name="authenticationSuccessHandler" ref="simpleUrlSuccessHandler"/>
 <property name="filterProcessesUrl" value="${oidc.login.url}"/>
</bean>

```

Here, you can see an example configuration for the required properties ofthe Authentication Filter Bean. All of these properties are required, and they are also beans that need to be configured.

The configuration Manager is treated in the "Common Configuration" section of this document.

#### Issuer Service

``` xml

<bean class="org.mitre.openid.connect.client.service.impl.StaticSingleIssuerService" id="staticIssuerService">
 <property name="issuer" value="${oidc.idp.url}"/>
</bean>

```

This is the simplest configuration required for the issuer, it will only take the base URL of the IDP.

#### Server Configuration Service

If the server provides a /.well-known/openid-configuration endpoint with a JSON response that contains the IDP configuration, a dynamic configuration service can be used (see example below). Otherwise a static or hybrid configuration would be required.

``` xml
<bean class="org.mitre.openid.connect.client.service.impl.DynamicServerConfigurationService" id="dynamicServerConfigurationService"/>
```

#### Client Configuration Service

``` xml

<bean class="org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService"
          id="staticClientConfigurationService">
        <property name="clients">
            <map>
                <entry key="${oidc.idp.url}">
                    <bean class="org.mitre.oauth2.model.RegisteredClient">
                        <property name="clientId" value="${oidc.client.id}"/>
                        <property name="clientSecret" value="${oidc.client.secret}"/>
                        <property name="scope">
                            <set value-type="java.lang.String">
                                <value>openid</value>
                                <value>email</value>
                                <value>address</value>
                                <value>profile</value>
                                <value>phone</value>
                            </set>
                        </property>
                        <property name="tokenEndpointAuthMethod" value="SECRET_BASIC"/>
                        <property name="redirectUris">
                            <set>
                                <value>${oidc.redirect.url}</value>
                            </set>
                        </property>
                        <property name="requestObjectSigningAlg" value="RS256"/>
                        <property name="jwksUri" value="%{oid.jwk.url}"/>
                    </bean>
                </entry>
            </map>
        </property>
    </bean>
    
```
    
This is an example configuration for a Static client using the SECRET_BASIC Auth Method. Both ClientId and ClientSecret must be indicated, as well as the IDP base URL and the URL to which the idp will redirect after login. Keep in mind that the redirect URL must match exactly the one that is configured in the IDP. 

Optionally, is possible to indicate the signign algorithm and the JWK endpoint.

#### Auth Request Options Service

<bean class="org.mitre.openid.connect.client.service.impl.StaticAuthRequestOptionsService" id="staticAuthRequestOptionsService"/>
This service is used to add the optional parameters to the Auth request, in this case none is required so the bean can be injected blank. An example of extra options can be found in MitreId's simple webapp:

``` xml

<bean class="org.mitre.openid.connect.client.service.impl.StaticAuthRequestOptionsService" id="staticAuthRequestOptionsService">
        <property name="options">
             <map>
             <!-- Entries in this map are sent as key-value parameters to the auth request -->
                    <entry key="display" value="page" />
                    <entry key="max_age" value="30" />
                    <entry key="prompt" value="none" />
            </map>
       </property>
 </bean>
 
``` 

#### Auth Request URL Builder

This bean will build de URL for the auth request to the idp, there are many which can be used or implemented but currenty the plainAuth is enough:

``` xml
<bean class="org.mitre.openid.connect.client.service.impl.PlainAuthRequestUrlBuilder" id="plainAuthRequestUrlBuilder"/>
```

#### Success Handler

This bean will define the behaviour of a successful login action. Currently, a simple redirection to a URL in the app:

``` xml

<bean id="simpleUrlSuccessHandler" class="org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler">
   <property name="defaultTargetUrl" value="${oidc.success.url}"/>
</bean>

```

#### FilterProcessURL

This String property indicates which URL is to be filtered. As this filter is used as an entry point for authentication, it does not need to be executed in each URL but only in the login endpoint url. 

``` xml
<property name="filterProcessesUrl" value="${oidc.login.url}"/>
```

This property can be changed to a bean that Matches the request based on custom logic.

``` xml
<property name="requiresAuthenticationRequestMatcher" ref="oidcRequestMatcher"/>
```

### Login Through Access Token
The class that handles the Access Token is in fact the standard OAuth2AuthenticationProcessingFilter from Spring framework, although some beans of the MitreId are wired into it to provide custom functionality.

An Authentication Manager and a Provider must be previously configured.

#### Oauth2 Token filter
The filter class is defined through "oauth" namespace. Here , it is defined an id for the bean, and stateless is configured as false, as we rely on sessions to delete files that the user may create without actually logging in.

``` xml
<oauth:resource-server id="resourceServerFilter" token-services-ref="introspectingService" stateless="false" authentication-manager-ref="authenticationManager"/>
```

Also, notice that the authentication-manager that was previously defined, is wired into the filter.

#### Introspecting Service and Authority Granter

The same introspecting service that the Oauth2 Token filter, is the one that was previously defined for the common configuration:

``` xml

<bean id="introspectingService" class="org.mitre.oauth2.introspectingfilter.IntrospectingTokenService">
 <property name="introspectionConfigurationService" ref="staticIntrospectionConfigurationService"/>
 <property name="introspectionAuthorityGranter" ref="simpleIntrospectionAuthorityGranter"/>
</bean>

```

The same happens with the authority granter:

``` xml
<bean id="simpleIntrospectionAuthorityGranter" class=" org.mitre.oauth2.introspectingfilter.service.impl.SimpleIntrospectionAuthorityGranter"/>
```

#### Token Filter HTTP Configuration

In this section takes place the security definition for the whole application (pattern /.*). The rules are checking in a descending way, the first rule that matches the pattern will be the one applied.

This means that patterns should be written from most to least specific.

Is important to provide access (permitAll) for OPTIONS method even for unauthenticated users, otherwise preflight request will fail.

``` xml

<security:http auto-config="false" use-expressions="true" create-session="always" disable-url-rewriting="true"
 request-matcher="regex" pattern="/.*" entry-point-ref="oauthAuthenticationEntryPoint"
 authentication-manager-ref="authenticationManager">
 <security:intercept-url pattern="${oidc.secured.pattern}" access="permitAll()" method="OPTIONS"/>
 <security:intercept-url pattern="${oidc.secured.pattern}" access="isAuthenticated()"/>
 <security:intercept-url pattern="${oidc.secured.testing.pattern}" access="permitAll()" method="OPTIONS"/>
 <security:intercept-url pattern="${oidc.secured.testing.pattern}" access="isAuthenticated()"/>
 <security:intercept-url pattern="${oidc.anonymous.pattern}" access="permitAll()"/>
 <security:custom-filter after="SECURITY_CONTEXT_FILTER" ref="corsFilter"/>
 <security:custom-filter before="PRE_AUTH_FILTER" ref="resourceServerFilter"/>
 <security:custom-filter after="PRE_AUTH_FILTER"
 ref="openIdConnectAuthenticationFilter"/>
 <security:custom-filter before="SESSION_MANAGEMENT_FILTER" ref="EDCISessionManagementFilter"/>
 <security:logout logout-url="${oidc.logout.url}" logout-success-url="${oidc.logout.success.url}"
 delete-cookies="_session" invalidate-session="true"/>
 <security:session-management session-fixation-protection="none">
 </security:session-management>
</security:http>

```

Here you can see that we have configured the "resourceServerFilter" that was previously configured, also it is included the logout url, and the session invalidation, which will result on firing all SessionDestroy listener/handlers.

#### Entry point for Oauth
An OAuth entry point must be configured in order to return a 401 when Authentication fails  (Default behaviour is redirecting to LoginURL).

``` xml

<bean id="oauthAuthenticationEntryPoint"
 class="org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint">
 <property name="realmName" value="EDCI"/>
</bean>

```

### Overrided Classes

Two classes have been overrided, to add Mock functionality and to Unify the Type of the Authentication Object stored in the Spring Context.

#### Mock User
Here, is required to override MitreId's OIDCAuthenticationFilter, adding a check for the mock property, if found, we skip the normal attempAuthentication flow which would make the request to the IDP. 

Instead, If a mock user configuration is found, the request gets directy passed to the AuthenticationManager.

``` java

public class EDCIOIDCAuthenticationFilter extends OIDCAuthenticationFilter {
 @Autowired
 private IssuerConfigService issuerConfigService;

 @Override
 public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
 //if user is mocked, do not request IDP
 if (issuerConfigService.getBoolean(Constant.CONFIG_PROPERTY_OIDC_MOCK_USER_ACTIVE)) {
 return this.getAuthenticationManager().authenticate(new PreAuthenticatedAuthenticationToken(null, null, null));
 }
 return super.attemptAuthentication(request, response);
 }
}

``` 

In the EDCIOIDCAuthenticationProvider bean, the authenticate method is override, if a mock user configuration is found, the createMockUser() method is called

``` java

public Authentication authenticate(org.springframework.security.core.Authentication authentication) throws AuthenticationException {
 _log.debug(String.format("Is mock user: %b", issuerConfigService.getBoolean(Constant.CONFIG_PROPERTY_OIDC_MOCK_USER_ACTIVE)));
 //if mock user, get it from property json
 if (issuerConfigService.getBoolean(Constant.CONFIG_PROPERTY_OIDC_MOCK_USER_ACTIVE)) {
 return createMockUser();
 }

```

The createMockUser method will look for a json in the property file, parse it and return a mock user based on that information.

``` java

private Authentication createMockUser() {
 JsonObject userInfoJson = new JsonParser().parse(issuerConfigService.getString(Constant.CONFIG_PROPERTY_OIDC_MOCK_USER_INFO)).getAsJsonObject();
 return new OIDCAuthenticationToken("mockUser", "mockIDP", DefaultUserInfo.fromJson(userInfoJson), null, null, null, null);
}

```

#### Authentication Type Discrepancies
As introspection is managed through normal Spring OAuth2 filter, the Authentication Object produced would be a Spring's OAuth2Authentication token, which would be different from the OIDCAuthenticationToken created by MitreID.

To overcome this, the AuthenticationProvider is overrided, to cast the PreAuthenticatedAuthenticationToken Object  from spring (Created previously to the Authentication/Introspection with IDP, based on token information) to the PendingOIDCAuthenticationToken which is then used by MitreID to perfom the Authentication request to the IDP. After that Cast, normal flow continues.

``` java

} else if (authentication instanceof PendingOIDCAuthenticationToken) {
            return super.authenticate(authentication);
        } else {
            if (authentication == null) {
                throw new InvalidTokenException("Invalid token (token not found)");
            } else {
                String token = (String) authentication.getPrincipal();
                OAuth2Authentication auth = this.tokenServices.loadAuthentication(token);
                if (auth == null) {
                    throw new InvalidTokenException("Invalid token: " + token);
                } else {
                    Collection<String> resourceIds = auth.getOAuth2Request().getResourceIds();
                    JsonObject jsonDetails = (JsonObject) auth.getUserAuthentication().getCredentials();
                    String issuer = jsonDetails.get("iss").getAsString();
                    PendingOIDCAuthenticationToken oidcToken = new PendingOIDCAuthenticationToken(String.valueOf(auth.getUserAuthentication().getPrincipal()), issuer, servers.getServerConfiguration(issuer), null, token, null);
                    UserInfo userInfo = this.userInfoFetcher.loadUserInfo(oidcToken);
                    if (userInfo != null && !Strings.isNullOrEmpty(userInfo.getSub()) && !userInfo.getSub().equals(oidcToken.getSub())) {
                        throw new UsernameNotFoundException("user_id mismatch between id_token and user_info call: " + oidcToken.getSub() + " / " + userInfo.getSub());
                    } else {
                        return this.createAuthenticationToken(oidcToken, auth.getAuthorities(), userInfo);
                    }
                }
            }
        }

```        
        
#### Properties example
Find below an example configuration of the security:

``` properties

app.session.timeout=5
expired.session.url=${server.full.address}${app.context.root}
oidc.security.enabled=none
oidc.jwk.url=http://localhost:9000/certs
oidc.login.url=/auth/oidc/eulogin
oidc.logout.url=${oidc.login.url}/logout
oidc.logout.success.url=/
oidc.idp.url=http://localhost:9000
oidc.idp.introspection.url=${oidc.idp.url}/token/introspection
oidc.redirect.url=${server.full.address}${app.context.root}${oidc.login.url}
oidc.secured.pattern=.*/specs(/.*|$|\\?.*)
oidc.secured.testing.pattern=.*/test/.*
oidc.success.url=${server.full.address}${app.context.root}/#/credential-builder
oidc.anonymous.pattern=/.*
oidc.client.id=edci
oidc.client.secret=europass2
oidc.mock.user.active=false
oidc.mock.user.info={"sub":"mockuser","email":"mockuser@everis.com","email_verified":true,"name":"Mock","nickname":"MockU","password":"password","groups":["Everyone", "issuer", "viewer"]}
proxy.http.enabled=false
proxy.https.enabled=false

```

## Links of Interest

[About eu Login](https://webgate.ec.europa.eu/CITnet/confluence/pages/viewpage.action?pageId=24641907)

[Eu Login for developers](https://webgate.ec.europa.eu/CITnet/confluence/display/IAM/ECAS+for+Developers)

[OpenID Connect](https://webgate.ec.europa.eu/CITnet/confluence/pages/viewpage.action?pageId=738132209)

[OpenID Connect for back-end applications](https://webgate.ec.europa.eu/CITnet/confluence/display/IAM/OpenID+Connect+for+back-end+applications)

[Client Registration](https://webgate.ec.europa.eu/CITnet/confluence/display/IAM/OpenID+Connect+-+Client+Registration)

[OpenID Connect libraries](https://webgate.ec.europa.eu/CITnet/confluence/display/IAM/OpenID+Connect+Libraries)

[MitreId Client](https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server/tree/master/openid-connect-client)

[Mitreid Server](https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server)