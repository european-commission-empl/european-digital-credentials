package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.constants.*;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConfig;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConstants;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialFileDTO;
import eu.europa.ec.empl.edci.issuer.common.model.ELementCLBasicDTO;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientDataDTO;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientFileDTO;
import eu.europa.ec.empl.edci.issuer.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.issuer.util.EDCIWorkBookUtil;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.util.XmlUtil;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class IssuerFileService {

    private static final Logger logger = Logger.getLogger(IssuerFileService.class);

    @Autowired
    private EDCIFileService edciFileService;


    @Autowired
    private IssuerConfigService issuerConfigService;

    @Autowired
    private @Qualifier("WorkBookServiceV2")
    IWorkBookService workBookService;

    @Autowired
    private CredentialMapper credentialMapper;

    @Autowired
    private Validator validator;

    @Inject
    private FileUtil fileUtil;

    @Autowired
    private XmlUtil xmlUtil;

    @Autowired
    private IssuerFileService dynamicfileService;

    @Autowired
    private CredentialMapper dynamicCredentialMapper;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private EDCIWorkBookUtil edciWorkBookUtil;

    /**
     * Overwrite a credential holder depending on the type, writes the changes to the file
     *
     * @param credHolder the credential holder
     * @param type       the type of the credential holder
     */
    public void convertToPresentation(CredentialHolderDTO credHolder, String type) {
        if (credHolder instanceof EuropassPresentationDTO) {
            logger.error("We're not expecting a EuropassPresentationDTO at this point");
        } else if (EDCIConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(type) && credHolder instanceof EuropassCredentialDTO) {
            //Convert to Europass Presentation if specified in SignatureParametersDTO
            EuropassPresentationDTO presentation = new EuropassPresentationDTO();
            presentation.setVerifiableCredential((EuropassCredentialDTO) credHolder);
            presentation.setType(this.getEdciCredentialModelUtil().getTypeCode(EuropassPresentationDTO.class, ControlledListConcept.VERIFICATION_TYPE_MANDATED_ISSUE.getUrl()));
            presentation.setId(URI.create(presentation.getPrefix(presentation).concat(UUID.randomUUID().toString())));
            try {
                this.createXMLFiles(presentation);
            } catch (IOException e) {
                logger.error("Error creating EuropassPresentation XML file", e);
                throw new EDCIException(e);
            }
        }
    }

    public RecipientFileDTO uploadRecipientsExcelFile(RecipientFileDTO recipientFileDTO) {
        Workbook workbook;
        List<RecipientDataDTO> recipientDataDTOS;
        try {
            workbook = workBookService.createWorkBook(recipientFileDTO.getFile().getInputStream());
            recipientFileDTO.setValid(workBookService.isValidFormat(workbook));
            recipientDataDTOS = workBookService.parseRecipientsData(workbook);
        } catch (IOException | InvalidFormatException e) {
            recipientFileDTO.setValid(false);
            throw new EDCIBadRequestException("Excel file could not be read");
        }
        recipientFileDTO.setRecipientDataDTOS(recipientDataDTOS);

        return recipientFileDTO;
    }

    public CredentialFileDTO uploadCredentialsExcelFile(CredentialFileDTO fileDTO) {
        InputStream inputStream = null;
        Workbook excelFile = null;
        String jsonResponse = "";
        List<EuropassCredentialDTO> europassCredentialDTOList;
        try {
            inputStream = fileDTO.getFile().getInputStream();
            excelFile = WorkbookFactory.create(inputStream);
            fileDTO.setValid(workBookService.isValidFormat(excelFile));
            europassCredentialDTOList = workBookService.parseCredentialData(excelFile);
            this.createXMLFiles(europassCredentialDTOList.toArray(new EuropassCredentialDTO[europassCredentialDTOList.size()]));
            fileDTO.setCredentials(dynamicCredentialMapper.europassToDTOList(europassCredentialDTOList, EDCIConstants.DEFAULT_LOCALE));
            inputStream.close();

        } catch (IOException e) {
            throw new EDCIBadRequestException().addDescription("Error while parsing the file").setCause(e);
        } catch (IllegalArgumentException e) {
            throw new EDCIBadRequestException("file.excel.extension.error").setCause(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
            if (excelFile != null) {
                try {
                    excelFile.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
        return fileDTO;
    }


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

    public List<CredentialHolderDTO> createXMLFiles(CredentialHolderDTO... xmlCredentials) throws IOException {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        File folder = fileUtil.getOrCreateFolder(fileUtil.getCredentialPrivateFolderName(sessionId));

        for (CredentialHolderDTO xmlCredential : xmlCredentials) {
            File file;
            if (xmlCredential.getCredential() != null && xmlCredential.getCredential().getValid()) {
                try {
                    String schemaLocation = null;
                    schemaLocation = edciCredentialModelUtil.getSchemaLocation(xmlCredential.getClass(), xmlCredential.getType().getUri());
                    file = this.getEdciFileService().getOrCreateFile(folder, fileUtil.getFileName(xmlCredential.getCredential().getId().toString()));
                    Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
                    Marshaller jaxbMarshaller = null;
                    jaxbMarshaller = xmlUtil.getMarshallerWithSchemaLocation(xmlCredential.getClass(), schemaLocation);
//                    }
                    jaxbMarshaller.marshal(xmlCredential, file);
                } catch (IOException e) {
                    //If the XML creation fails, credential is marked as not valid, throws exception for upper function to catch
                    xmlCredential.getCredential().setValid(false);
                    throw e;
                } catch (JAXBException e) {
                    //ToDo -> Exception handling for upload-xml flow
                    xmlCredential.getCredential().setValid(false);
                    logger.error(e);
                }
            }
        }

        return Arrays.asList(xmlCredentials);
    }


    public File createXMLFile(CredentialHolderDTO xmlCredential, String resultPath) throws IOException {
        File file = null;
        if (xmlCredential.getCredential() != null && xmlCredential.getCredential().getValid()) {
            try {
                String schemaLocation = null;
                schemaLocation = edciCredentialModelUtil.getSchemaLocation(xmlCredential.getClass(), xmlCredential.getType().getUri());
                file = this.getEdciFileService().getOrCreateFile(resultPath);
                Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
                Marshaller jaxbMarshaller = null;
                jaxbMarshaller = xmlUtil.getMarshallerWithSchemaLocation(xmlCredential.getClass(), schemaLocation);
                jaxbMarshaller.marshal(xmlCredential, file);
            } catch (IOException e) {
                xmlCredential.getCredential().setValid(false);
                throw e;
            } catch (JAXBException e) {
                //ToDo -> Exception handling for upload-xml flow
                xmlCredential.getCredential().setValid(false);
                logger.error(e);
            }
        }

        return file;
    }


    public Set<ELementCLBasicDTO> getAvailableTemplates() {

        List<RDFConcept> elements = controlledListCommonsService.searchRDFConcepts(ControlledList.CREDENTIAL_TYPE.getUrl(), "",
                LocaleContextHolder.getLocale().getLanguage(), 0, 100, ControlledListCommonsService.ALLOWED_LANGS);

        if (elements == null) return new HashSet<ELementCLBasicDTO>();

        Set<ELementCLBasicDTO> existingTemplates = new HashSet<>();

        //Loop through all CL elements, check if any of the translated targetNames has an existing template, if so, use that targetName  for that lang
        elements.stream().forEach(
                //For each element, find if any of its target names has an excel existing in the templates folder
                elementCLDAO -> {
                    String existingTemplateLocale = elementCLDAO.getTargetName().keySet().stream().filter(
                            labelCLDAO -> fileUtil.doesTemplateExist(elementCLDAO.getTargetName().get(labelCLDAO))).findFirst().orElse(null);
                    if (existingTemplateLocale != null) {
                        ELementCLBasicDTO existingTemplate = new ELementCLBasicDTO();
                        existingTemplate.setLabel(elementCLDAO.getTargetName().get(existingTemplateLocale));
                        existingTemplates.add(existingTemplate);
                    }
                }
        );

        return existingTemplates;
    }

    public List<EuropassCredentialDTO> getEuropassCredentialsFromUploadFile(MultipartFile file) {
        String regex = this.issuerConfigService.getString(IssuerConfig.Issuer.UPLOAD_FILE_CREDENTIAL_REGEX);
        List<String> credentialXmls = this.edciCredentialModelUtil.splitUploadMultipleCredentialsFile(file, regex);
        List<EuropassCredentialDTO> europassCredentialDTOS = new ArrayList<>();
        for (String xml : credentialXmls) {
            try {
                europassCredentialDTOS.add(edciCredentialModelUtil.fromString(xml).getCredential());
            } catch (JAXBException e) {
                logger.error("Error parsing multiple credentials XML file: " + e.getMessage(), e);
            }
        }
        return europassCredentialDTOS;
    }

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

    public EDCICredentialModelUtil getEdciCredentialModelUtil() {
        return edciCredentialModelUtil;
    }

    public void setEdciCredentialModelUtil(EDCICredentialModelUtil edciCredentialModelUtil) {
        this.edciCredentialModelUtil = edciCredentialModelUtil;
    }

    public EDCIFileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(EDCIFileService edciFileService) {
        this.edciFileService = edciFileService;
    }
}
