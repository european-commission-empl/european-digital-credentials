package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.constants.XML;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerConstants;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component("FileUtil")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FileUtil {

    private static final Logger logger = Logger.getLogger(FileUtil.class);

    @Autowired
    private IssuerConfigService issuerConfigService;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    public EuropassCredentialDTO getEuropassCredentialFromFile(CredentialDTO credentialDTO) throws IOException, JAXBException {
        return edciCredentialModelUtil.fromByteArray(this.getXmlBytes(credentialDTO)).getCredential();
    }

    public CredentialHolderDTO getCredentialHolderFromFile(CredentialDTO credentialDTO) throws IOException, JAXBException {
        return edciCredentialModelUtil.fromByteArray(this.getXmlBytes(credentialDTO));
    }

    public byte[] getXmlBytes(CredentialDTO credentialDTO) throws IOException {
        return this.getXmlBytes(credentialDTO.getUuid());
    }

    public byte[] getXmlBytes(String uuid) throws IOException {
        return Files.readAllBytes(Paths.get(this.getCredentialFileAbsolutePath(uuid)));
    }

    @PostConstruct
    public void createOrCleanCredentialsFolder() {
        try {
            File dirCred = getOrCreateFolder(issuerConfigService.getString(EDCIIssuerConstants.CONFIG_PROPERTY_TMP_DATA_LOCATION)
                    .concat(issuerConfigService.getString(EDCIIssuerConstants.CONFIG_PROPERTY_TMP_CRED_FOLDER)));
            FileUtils.cleanDirectory(dirCred);
        } catch (IOException e) {
            logger.error("Error creating or cleaning the credentials temporal folder", e);
        }
    }

    public String getFolderName(String sessionId) {
        return issuerConfigService.getString(EDCIIssuerConstants.CONFIG_PROPERTY_TMP_DATA_LOCATION)
                .concat(issuerConfigService.getString(EDCIIssuerConstants.CONFIG_PROPERTY_TMP_CRED_FOLDER))
                .concat(sessionId).concat(EDCIIssuerConstants.STRING_SLASH);
    }

    @Cacheable("CL_FileType")
    public Code getFileType(String extension) {
        String fileType = extension;
        if (extension.equalsIgnoreCase("JPG")) {
            fileType = "JPEG";
        }
        Page<Code> fileTypeCode = controlledListCommonsService.searchConcepts(ControlledList.FILE_TYPE.getUrl(), fileType, Defaults.DEFAULT_LOCALE, 0, 1, null);
        return fileTypeCode != null ? fileTypeCode.getContent().get(0) : null;
    }

    public String getFileName(String credId) {
        return EDCIIssuerConstants.XML_FILE_PREFIX.concat(credId.substring(credId.lastIndexOf(":") + 1)).concat(XML.EXTENSION_XML);
    }

    public String getCredentialFileAbsolutePath(CredentialDTO credentialDTO) {
        return getCredentialFileAbsolutePath(credentialDTO.getUuid());
    }

    public String getCredentialFileAbsolutePath(String credId) {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        return getCredentialFileAbsolutePath(credId, sessionId);
    }

    public String getCredentialFileAbsolutePath(String credId, String sessionId) {
        return getFolderName(sessionId).concat(getFileName(credId));
    }

    public File getOrCreateFolder(String folderName) {

        File folder = new File(folderName);

        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdir();
        }

        return folder;
    }

    public boolean doesTemplateExist(String templateName) {
        ClassPathResource classPathResource = new ClassPathResource(getTemplateFilePath(templateName));
        return classPathResource.exists();
    }

    public String getTemplateFilePath(String type) {
        return EDCIIssuerConstants.TEMPLATES_DIRECTORY
                .concat("/").concat(this.getTemplateFileName(type));
    }

    public String getTemplateFileName(String type) {
        return EDCIIssuerConstants.EXCEL_TEMPLATE_PREFIX
                .concat(type.replaceAll("\\s", "_").toLowerCase())
                .concat(EDCIIssuerConstants.EXTENSION_XLSM);
    }


}
