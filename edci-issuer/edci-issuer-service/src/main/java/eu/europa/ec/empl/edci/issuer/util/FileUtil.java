package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConfig;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConstants;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(FileUtil.class);

    @Autowired
    private EDCIFileService edciFileService;

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
            File dirCred = getOrCreateFolder(issuerConfigService.getString(IssuerConfig.Issuer.TMP_DATA_LOCATION)
                    .concat(issuerConfigService.getString(IssuerConfig.Issuer.TMP_CRED_FOLDER)));
            FileUtils.cleanDirectory(dirCred);
        } catch (IOException e) {
            logger.error("Error creating or cleaning the credentials temporal folder", e);
        }
    }

    public String getCredentialPublicFolderName() {
        String folderName = getCredentialTemporalFolderName().concat(issuerConfigService.getString(IssuerConfig.Issuer.TMP_PUBLIC_CRED_FOLDER));
        return folderName.endsWith(EDCIConstants.StringPool.STRING_SLASH) ? folderName : folderName.concat(EDCIConstants.StringPool.STRING_SLASH);
    }

    public String getCredentialTemporalFolderName() {
        return this.getTemporalDataLocation().concat(issuerConfigService.getString(IssuerConfig.Issuer.TMP_CRED_FOLDER));
    }

    public String getCredentialPrivateFolderName(String sessionId) {
        return getCredentialTemporalFolderName().concat(sessionId).concat(EDCIConstants.StringPool.STRING_SLASH);
    }

    private String getTemporalDataLocation() {
        return issuerConfigService.getString(IssuerConfig.Issuer.TMP_DATA_LOCATION);
    }

    @Cacheable("CL_FileType")
    public Code getFileType(String extension) {
        String fileType = extension;
        if (extension.equalsIgnoreCase("JPG")) {
            fileType = "JPEG";
        }
        Page<Code> fileTypeCode = controlledListCommonsService.searchConcepts(ControlledList.FILE_TYPE.getUrl(), fileType, EDCIConfig.Defaults.DEFAULT_LOCALE, 0, 1, null);
        return fileTypeCode != null ? fileTypeCode.getContent().get(0) : null;
    }

    public String getFileName(String credId) {
        return IssuerConstants.XML_FILE_PREFIX.concat(credId.substring(credId.lastIndexOf(":") + 1)).concat(EDCIConstants.XML.EXTENSION_XML);
    }

    public String getCredentialFileAbsolutePath(CredentialDTO credentialDTO) {
        return getCredentialFileAbsolutePath(credentialDTO.getUuid());
    }

    public String getCredentialFileAbsolutePath(String credId) {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        return getCredentialFileAbsolutePath(credId, sessionId);
    }

    public String getCredentialFileAbsolutePath(String credId, String sessionId) {
        return getCredentialPrivateFolderName(sessionId).concat(getFileName(credId));
    }

    public File getOrCreateFolder(String folderName) {

        File folder = this.getEdciFileService().getOrCreateFile(folderName);

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
        return IssuerConstants.TEMPLATES_DIRECTORY
                .concat("/").concat(this.getTemplateFileName(type));
    }

    public String getTemplateFileName(String type) {
        return IssuerConstants.EXCEL_TEMPLATE_PREFIX
                .concat(type.replaceAll("\\s", "_").toLowerCase())
                .concat(IssuerConstants.EXTENSION_XLSM);
    }


    /*public MultipartFile createMultipartFile(byte[] bytes, String fieldName, String uuid) {
        File file = new File(this.getTemporalDataLocation().concat(EDCIConstants.StringPool.STRING_SLASH).concat(uuid));
        FileItem fileItem = null;
        try {
            if (file.createNewFile()) {
                fileItem = new DiskFileItem(fieldName, Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
                InputStream input = new FileInputStream(file);
                OutputStream os = fileItem.getOutputStream();
                IOUtils.copy(input, os);
            }
        } catch (IOException ex) {
            logger.error(String.format("Could not create multipartFile for file with uuid %s", uuid));
        }

        return new CommonsMultipartFile(fileItem);
    }*/

    public EDCIFileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(EDCIFileService edciFileService) {
        this.edciFileService = edciFileService;
    }
}
