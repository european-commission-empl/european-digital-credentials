package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.dss.service.validation.JadesValidationService;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CredentialServiceUtil {

    private static final Logger logger = LogManager.getLogger(CredentialServiceUtil.class);

    @Autowired
    private JadesValidationService jadesValidationService;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private CredentialUtil credentialUtil;

    /**
     * Validates the XML credential with the corresponding XSD, returns the unlocalized validation result
     *
     * @param credentialDTO the credential XML file
     * @return the Validation result
     */
    public ValidationResult validateCredentialtoSHACL(CredentialDTO credentialDTO, @Nullable String fileBatch) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setValid(false);
        try {
            byte[] credentialBytes = Files.readAllBytes(Paths.get(this.getFileUtil().getCredentialFileAbsolutePath(credentialDTO, fileBatch)));
            String credentialString = this.getJadesValidationService().getCredentialOrPayload(credentialBytes);
            ValidationResult shaclValidation = this.getCredentialUtil().validateCredential(credentialString);
            if (shaclValidation.isValid()) {
                validationResult.setValid(true);
            } else {
                validationResult.getValidationErrors().addAll(shaclValidation.getValidationErrors());
            }
        } catch (Exception e) {
            //TODO -> improve this 
            validationResult.addValidationError(new ValidationError(EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT));
            e.printStackTrace();
            logger.error(e);
        }
        return validationResult;
    }

    public HttpHeaders prepareHttpHeadersForCredentialDownload(String fileName) {
        return prepareHttpHeadersForDownload(fileName, eu.europa.ec.empl.edci.constants.MediaType.APPLICATION_XML_VALUE);
    }

    public HttpHeaders prepareHttpHeadersForDownload(String fileName, String mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return httpHeaders;
    }

    public JadesValidationService getJadesValidationService() {
        return jadesValidationService;
    }

    public void setJadesValidationService(JadesValidationService jadesValidationService) {
        this.jadesValidationService = jadesValidationService;
    }

    public FileUtil getFileUtil() {
        return fileUtil;
    }

    public void setFileUtil(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }
}
