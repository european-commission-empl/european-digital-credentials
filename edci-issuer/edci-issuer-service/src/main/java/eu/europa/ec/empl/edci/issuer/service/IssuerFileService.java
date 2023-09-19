package eu.europa.ec.empl.edci.issuer.service;

import com.apicatalog.jsonld.JsonLdError;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.MediaType;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConstants;
import eu.europa.ec.empl.edci.issuer.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class IssuerFileService {

    private static final Logger logger = LogManager.getLogger(IssuerFileService.class);

    @Autowired
    private EDCIFileService edciFileService;


    @Autowired
    private IssuerConfigService issuerConfigService;


    @Autowired
    private CredentialMapper credentialMapper;

    @Autowired
    private Validator validator;

    @Inject
    private FileUtil fileUtil;

    @Autowired
    private IssuerFileService dynamicfileService;

    @Autowired
    private CredentialMapper dynamicCredentialMapper;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private CredentialUtil credentialUtil;

    public void deleteSessionFolder(String sessionId) {
        File folder = this.getEdciFileService().getOrCreateFile(fileUtil.getCredentialPrivateFolderName(sessionId));
        logger.trace("[SESSION] DELETING FOLDER: " + folder.getAbsolutePath());
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file != null) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        logger.trace("[I] - Succesfully deleted file: " + file.getAbsolutePath());
                    } else {
                        logger.error("[E] - Error deleting file " + file.getAbsolutePath());
                    }
                }
            }

            boolean deleted = folder.delete();
            if (deleted) {
                logger.trace("[I] - Succesfully deleted folder: " + folder.getAbsolutePath());
            } else {
                logger.error("[E] - Error deleting folder " + folder.getAbsolutePath());
            }
        }
    }

    public File createJSONLDFile(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, String resultPath, String resultFile) throws JsonLdError, IOException {
        //get or create folder name
        fileUtil.getOrCreateFolder(resultPath);
        //get file
        File credFile = this.getEdciFileService().getOrCreateFile(resultPath.concat(File.separator).concat(resultFile));
        Path credFilePath = Paths.get(credFile.getAbsolutePath());
        Files.deleteIfExists(credFilePath);
        //create json file
        byte[] bytes = this.getCredentialUtil().marshallCredentialAsBytes(europeanDigitalCredentialDTO);
        Files.write(credFilePath, bytes);
        return credFile;
    }

    public File createJSONLDFile(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) throws JsonLdError, IOException {
        //get predefined folder name
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        File folder = fileUtil.getOrCreateFolder(fileUtil.getCredentialPrivateFolderName(sessionId));
        //get file
        return this.createJSONLDFile(europeanDigitalCredentialDTO, folder.getAbsolutePath(), this.getFileUtil().getFileName(europeanDigitalCredentialDTO.getId()));
    }

    public File createJSONLDFile(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, String resultPath) throws JsonLdError, IOException {
        //get file
        return this.createJSONLDFile(europeanDigitalCredentialDTO, resultPath, this.getFileUtil().getFileName(europeanDigitalCredentialDTO.getId()));
    }


//    public List<EuropassCredentialDTO> getEuropassCredentialsFromUploadFile(MultipartFile file) {
//        String regex = this.issuerConfigService.getString(IssuerConfig.Issuer.UPLOAD_FILE_CREDENTIAL_REGEX);
//        List<String> credentialXmls = this.edciCredentialModelUtil.splitUploadMultipleCredentialsFile(file, regex);
//        List<EuropassCredentialDTO> europassCredentialDTOS = new ArrayList<>();
//        for (String xml : credentialXmls) {
//            try {
//                europassCredentialDTOS.add(edciCredentialModelUtil.fromString(xml).getCredential());
//            } catch (JAXBException e) {
//                logger.error("Error parsing multiple credentials XML file: " + e.getMessage(), e);
//            }
//        }
//        return europassCredentialDTOS;
//    }

    public byte[] getTemplate(String type) {
        byte[] bytes = null;
        Resource resource = new ClassPathResource(fileUtil.getTemplateFilePath(type));
        try {
            bytes = StreamUtils.copyToByteArray(resource.getInputStream());
        } catch (IOException e) {
            logger.error("Download file not found for profile " + type);
            throw new EDCINotFoundException(EDCIMessageKeys.Exception.Template.TEMPLATE_NOTFOUND, type);
        }
        return bytes;
    }

    public ResponseEntity<byte[]> downloadTemplate(String type) {
        Resource resource = new ClassPathResource(fileUtil.getTemplateFilePath(type));
        return new ResponseEntity<byte[]>(this.getTemplate(type), prepareHttpHeadersForFileDownload(resource.getFilename()), HttpStatus.OK);
    }

    public HttpHeaders prepareHttpHeadersForFileDownload(String fileName) {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (fileName.endsWith(IssuerConstants.EXTENSION_XLSM)) {
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XLSM_VALUE);
        } else if (fileName.endsWith(IssuerConstants.EXTENSION_XLSX)) {
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XLSX_VALUE);
        } else if (fileName.endsWith(IssuerConstants.EXTENSION_XLS)) {
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XLS_VALUE);
        }

        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return httpHeaders;
    }


    public EDCIFileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(EDCIFileService edciFileService) {
        this.edciFileService = edciFileService;
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
