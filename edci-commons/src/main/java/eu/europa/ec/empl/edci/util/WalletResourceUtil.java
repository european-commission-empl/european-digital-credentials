package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.security.service.EDCIAuthenticationService;
import eu.europa.ec.empl.edci.security.service.oauth2.EDCIOauth2TokenService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WalletResourceUtil implements InitializingBean {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WalletResourceUtil.class);

    @Autowired
    private Validator validator;

    @Autowired
    private EDCIAuthenticationService edciAuthenticationService;

    @Autowired
    private EDCIOauth2TokenService oauth2TokenService;

    @Autowired
    private IConfigService configService;

    private String walletClientId;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.setWalletClientId(configService.getString(EDCIConfig.OIDC_WALLET_CLIENT_ID));
    }

    public <T> T doWalletGetRequest(String url, MediaType contentType, MediaType accept, Class<T> responseType, boolean addAuthToken) {
        EDCIRestRequestBuilder edciRestRequestBuilder = new EDCIRestRequestBuilder(HttpMethod.GET, url);
        if (addAuthToken) {
            this.getOauth2TokenService().reloadAccessToken(walletClientId);
            edciRestRequestBuilder.addAuthenticationToken(this.getEdciAuthenticationService().getAccessToken());
        }
        return edciRestRequestBuilder
                .addHeaderRequestedWith()
                .addHeaders(contentType, accept)
                .addQueryParam("locale", LocaleContextHolder.getLocale())
                .buildRequest(responseType)
                .execute();

    }

    public <T> T doWalletPostRequest(String url, String body, Class<T> responseType, MediaType contentType, MediaType accept, boolean addAuthToken) {
        EDCIRestRequestBuilder edciRestRequestBuilder = new EDCIRestRequestBuilder(HttpMethod.POST, url);
        if (addAuthToken) {
            this.getOauth2TokenService().reloadAccessToken(walletClientId);
            edciRestRequestBuilder.addAuthenticationToken(this.getEdciAuthenticationService().getAccessToken());
        }
        return edciRestRequestBuilder
                .addHeaderRequestedWith()
                .addHeaders(contentType, accept)
                .addQueryParam("locale", LocaleContextHolder.getLocale())
                .addBody(body)
                .buildRequest(responseType)
                .execute();
    }

    public <T> ResponseEntity<Resource<T>> doWalletPostRequest(String url, String body, ParameterizedTypeReference<Resource<T>> responseType, MediaType contentType, MediaType accept, boolean addAuthToken) {
        EDCIRestRequestBuilder edciRestRequestBuilder = new EDCIRestRequestBuilder(HttpMethod.POST, url);
        if (addAuthToken) {
            this.getOauth2TokenService().reloadAccessToken(walletClientId);
            edciRestRequestBuilder.addAuthenticationToken(this.getEdciAuthenticationService().getAccessToken());
        }
        return edciRestRequestBuilder
                .addHeaderRequestedWith()
                .addHeaders(contentType, accept)
                .addQueryParam("locale", LocaleContextHolder.getLocale())
                .addBody(body)
                .buildRequest(responseType)
                .execute();
    }

    public <T> T doWalletPostRequest(String url, MultipartFile body, String paramName, Class<T> responseType, MediaType accept, boolean addAuthToken) {
        EDCIRestRequestBuilder edciRestRequestBuilder = new EDCIRestRequestBuilder(HttpMethod.POST, url);
        if (addAuthToken) {
            this.getOauth2TokenService().reloadAccessToken(walletClientId);
            edciRestRequestBuilder.addAuthenticationToken(this.getEdciAuthenticationService().getAccessToken());
        }
        return new EDCIRestRequestBuilder(HttpMethod.POST, url)
                .addHeaderRequestedWith()
                .addHeaders(MediaType.MULTIPART_FORM_DATA, accept)
                .addQueryParam("locale", LocaleContextHolder.getLocale())
                .addBody(EDCIRestRequestBuilder.prepareMultiPartFileBody(paramName, body, MediaType.APPLICATION_XML))
                .buildRequest(responseType)
                .execute();
    }

    public EDCIOauth2TokenService getOauth2TokenService() {
        return oauth2TokenService;
    }

    public void setOauth2TokenService(EDCIOauth2TokenService oauth2TokenService) {
        this.oauth2TokenService = oauth2TokenService;
    }

    public EDCIAuthenticationService getEdciAuthenticationService() {
        return edciAuthenticationService;
    }

    public void setEdciAuthenticationService(EDCIAuthenticationService edciAuthenticationService) {
        this.edciAuthenticationService = edciAuthenticationService;
    }

    public String getWalletClientId() {
        return walletClientId;
    }

    public void setWalletClientId(String walletClientId) {
        this.walletClientId = walletClientId;
    }
}
