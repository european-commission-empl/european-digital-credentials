package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import eu.europa.ec.empl.edci.constants.MediaType;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.model.EDCIByteArrayMultiPartFile;
import eu.europa.ec.empl.edci.model.external.EDCISignatureReports;
import eu.europa.ec.empl.edci.model.external.VerificationCheckReport;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ExternalServicesUtil {
    private static final Logger logger = LogManager.getLogger(ExternalServicesUtil.class);

    @Autowired
    private BaseConfigService baseConfigService;

    public <T> T doPostRequest(String url, MultipartFile body, String paramName, Class<T> responseType, MediaType accept) {
        return new EDCIRestRequestBuilder(HttpMethod.POST, url)
                .addHeaderRequestedWith()
                .addHeaders(MediaType.MULTIPART_FORM_DATA, accept)
                .addQueryParam("locale", LocaleContextHolder.getLocale())
                .addBody(EDCIRestRequestBuilder.prepareMultiPartFileBody(paramName, body, accept))
                .buildRequest(responseType)
                .execute();
    }

    public EDCISignatureReports verifySignature(byte[] file, ContentType contentType) {
        String extension = ContentType.APPLICATION_XML.equals(contentType) ? EDCIConstants.XML.EXTENSION_XML : EDCIConstants.JSON.EXTENSION_JSON_LD;
        MultipartFile credentialFile = new EDCIByteArrayMultiPartFile("signedTempFile" + extension, file, contentType);
        return verifySignature(credentialFile);
    }

    public EDCISignatureReports verifySignature(MultipartFile body) {
        String url = this.getBaseConfigService().getString("eseal.validation.url");
        EDCISignatureReports returnValue = null;
        try {
            returnValue = new EDCIRestRequestBuilder(HttpMethod.POST, url)
                    .addHeaderRequestedWith()
                    .addHeaders(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON)
                    .addQueryParam("locale", LocaleContextHolder.getLocale())
                    .addBody(EDCIRestRequestBuilder.prepareMultiPartFileBody(EDCIParameter.EXTERNAL_FILE, body, MediaType.APPLICATION_JSON))
                    .buildRequest(EDCISignatureReports.class)
                    .execute();
        } catch (EDCIException e) {
            e.addDescription("Credential signature couldn't be verified");
            throw e;
        } catch (Exception e) {
            throw new EDCIException("credential.eseal.service.error").setCause(e);
        }
        return returnValue;
    }

    public byte[] convertCredential(byte[] file) {
        MultipartFile credentialFile = new EDCIByteArrayMultiPartFile("signedTempFile.xml", file, ContentType.APPLICATION_XML);
        return convertCredential(credentialFile);
    }

    public byte[] convertCredential(MultipartFile body) {
        String url = this.getBaseConfigService().getString("credential.conversion.url");

        byte[] returnValue = null;
        try {
            returnValue = new EDCIRestRequestBuilder(HttpMethod.POST, url)
                    .addHeaderRequestedWith()
                    .addHeaders(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON)
                    .addQueryParam("locale", LocaleContextHolder.getLocale())
                    .addBody(EDCIRestRequestBuilder.prepareMultiPartFileBody(EDCIParameter.EXTERNAL_FILE, body, MediaType.APPLICATION_JSON))
                    .buildRequest(byte[].class)
                    .execute();
        } catch (EDCIRestException e) {
            if (e.getCode() != null && e.getCode().toString().startsWith("CI-")) {
                throw new EDCIException(HttpStatus.BAD_REQUEST, e.getCode(), e.getMessage()).setCause(e).addDescription(e.getDescription());
            }

            throw new EDCIException("credential.conversion.service.error").setCause(e);
        } catch (Exception e) {
            throw new EDCIException("credential.conversion.service.error").setCause(e);
        }

        return returnValue;
    }

    public List<VerificationCheckReport> verifyCredential(byte[] file) {
        MultipartFile credentialFile = new EDCIByteArrayMultiPartFile("credentialTempFile.jsonld", file, ContentType.APPLICATION_JSON);
        return verifyCredential(credentialFile);
    }

    public List<VerificationCheckReport> verifyCredential(MultipartFile body) {
        String url = this.getBaseConfigService().getString("credential.verification.url");
        List<VerificationCheckReport> returnValue = null;
        try {
            returnValue = Arrays.stream(new EDCIRestRequestBuilder(HttpMethod.POST, url)
                    .addHeaderRequestedWith()
                    .addHeaders(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON)
                    .addQueryParam("locale", LocaleContextHolder.getLocale())
                    .addBody(EDCIRestRequestBuilder.prepareMultiPartFileBody(EDCIParameter.EXTERNAL_FILE, body, MediaType.APPLICATION_JSON))
                    .buildRequest(VerificationCheckReport[].class)
                    .execute()).collect(Collectors.toList());
        } catch (EDCIException e) {
            e.addDescription("Credential couldn't be verified");
            throw e;
        } catch (Exception e) {
            throw new EDCIException("credential.verification.service.error").setCause(e);
        }
        return returnValue;
    }

    public BaseConfigService getBaseConfigService() {
        return baseConfigService;
    }

    public void setBaseConfigService(BaseConfigService baseConfigService) {
        this.baseConfigService = baseConfigService;
    }
}
