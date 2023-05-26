package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.model.external.EDCISignatureReports;
import eu.europa.ec.empl.edci.model.external.VerificationCheckReport;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMailService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.DiplomaUtil;
import eu.europa.ec.empl.edci.util.ExternalServicesUtil;
import eu.europa.ec.empl.edci.util.MultilangFieldUtil;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletMessages;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.wallet.mapper.CycleAvoidingMappingContext;
import eu.europa.ec.empl.edci.wallet.mapper.WalletMapper;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialStorageUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CredentialService {
    private static final Logger logger = LogManager.getLogger(CredentialService.class);

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private CredentialStorageUtil credentialStorageUtil;

    @Autowired
    private CredentialMapper credentialMapper;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private ShareLinkService shareLinkService;

    @Autowired
    private EDCIMailService edciMailService;

    @Autowired
    private CredentialPDFService credentialPDFService;

    @Autowired
    private ExternalServicesUtil externalServicesUtil;

    @Autowired
    private DiplomaUtil diplomaUtil;

    @Autowired
    private WalletMapper walletMapper;

    /*BUSINESS LOGIC METHODS**/

    public String getDDBBCredType(EuropeanDigitalCredentialDTO credentialHolderDTO) {
//        if (credentialHolderDTO instanceof EuropassPresentationDTO) {
//            return EDCIWalletConstants.CREDENTIAL_STORED_TYPE_EUROPASS_PRESENTATION;
//        } else {
        return EDCIWalletConstants.CREDENTIAL_STORED_TYPE_EUROPASS_CREDENTIAL;
//        }
    }

    public EDCISignatureReports validateCredential(byte[] credential, ContentType type) {
        return externalServicesUtil.verifySignature(credential, type);
    }

    public CredentialDTO createCredentialByEmail(MultipartFile multipartFile, MultipartFile oldFile, String userEmail, boolean sendMail, String locale) {
        boolean requiresConvertion = this.requiresConvertion(multipartFile, oldFile);
        byte[] credentialBytes = this.getCredentialBytesFromParameters(multipartFile, oldFile);
        WalletDAO wallet = walletService.createOrRetrieveWalletByEmail(userEmail);
        return this.createCredential(
                walletMapper.toDTO(wallet, new CycleAvoidingMappingContext())
                , credentialBytes, sendMail, requiresConvertion);
    }


    private byte[] getCredentialBytesFromParameters(MultipartFile file, MultipartFile oldFile) {
        byte[] credentialBytes = null;

        try {
            if (file != null) {
                credentialBytes = file.getBytes();
            } else if (oldFile != null) {
                credentialBytes = oldFile.getBytes();
            } else {
                throw new EDCIBadRequestException("wallet.error.no.credential.provided");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new EDCIException(e);
        }
        return credentialBytes;
    }

    private boolean requiresConvertion(MultipartFile file, MultipartFile oldFile) {
        boolean needsConversion = false;

        try {
            if (file != null) {
                if (EDCIConstants.XML.XML_VALUE.equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()))) {
                    needsConversion = true;
                }
            } else if (oldFile != null) {
                if (EDCIConstants.XML.XML_VALUE.equalsIgnoreCase(FilenameUtils.getExtension(oldFile.getOriginalFilename()))) {
                    needsConversion = true;
                }
            } else {
                throw new EDCIBadRequestException("wallet.error.no.credential.provided");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new EDCIException(e);
        }

        return needsConversion;
    }

    @Transactional(propagation = Propagation.REQUIRED)

    public CredentialDTO createCredential(WalletDAO walletDAO, byte[] credentialBytes, Boolean sendMail, Boolean convert) {
        return this.createCredential(this.walletMapper.toDTO(walletDAO, new CycleAvoidingMappingContext()), credentialBytes, sendMail, convert);
    }

    public CredentialDTO createCredential(WalletDTO walletDTO, byte[] credentialBytes, Boolean sendMail, Boolean convert) {

        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setWallet(walletDTO);
        CredentialDTO savedCredentialDTO = null;

        byte[] credentialBytesToStore = credentialBytes;
        EDCISignatureReports reports = null;

        // Convert the credential to json-ld format (the XML is being validated in the process)
        if (convert) {
            credentialBytesToStore = externalServicesUtil.convertCredential(credentialBytes);
            // Check if the credential has been sealed with a valid Qseal
        } else {
            reports = validateCredential(credentialBytes, ContentType.APPLICATION_JSON);
            //Acceptance workaround to allow credentials being sealed without QSeal certificates
            if(!walletConfigService.getBoolean("allow.unsigned.credentials", false)) {
                if (reports == null || !(reports.getValidSignaturesCount() == reports.getSignaturesCount() && reports.getSignaturesCount() > 0)) {
                    throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_UNSIGNED, "wallet.credential.not.sealed.error", credentialDTO.getUuid());
                }
            }
        }

        EuropeanDigitalCredentialDTO europeanCredential = null;

        try {
            europeanCredential = credentialUtil.unMarshallCredential(credentialBytesToStore);

            if (europeanCredential.getId() == null) {
                throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.jsonld.unreadable")
                        .addDescription("No credential Id found");
            }
        } catch (Exception e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.jsonld.unreadable").setCause(e);
        }

        if (credentialRepository.countByUUID(walletDTO.getUserId(), europeanCredential.getId().toString()) > 0) {
            String title = europeanCredential.getDisplayParameter() != null
                    && europeanCredential.getDisplayParameter().getTitle() != null ?
                    MultilangFieldUtil.getLiteralStringOrAny(europeanCredential.getDisplayParameter().getTitle(),
                            LocaleContextHolder.getLocale().getLanguage()) : "";
            throw new EDCIException(HttpStatus.CONFLICT, ErrorCode.CREDENTIAL_EXISTS_UUID,
                    "wallet.credential.uuid.exists.error", title);
        }

        if (!convert) {
            ValidationResult report = credentialUtil.validateCredential(credentialBytesToStore);
            if (!report.isValid()) {
                throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.jsonld.unreadable");
            }
        }

        String walletFolder = !StringUtils.isEmpty(walletDTO.getFolder()) ? walletDTO.getFolder() :
                walletService.createWalletFolderIfNotCreated(walletService.fetchWalletDAOByUserId(walletDTO.getUserId()));
        String fileStored = credentialStorageUtil.storeCredentialIntoFilesystem(walletFolder, credentialBytesToStore);

        credentialDTO.setUuid(europeanCredential.getId().toString());
        credentialDTO.setCredentialLocalizableInfo(credentialStorageUtil.getLocalizableInfoDTOS(europeanCredential, credentialDTO));
        credentialDTO.setType(getDDBBCredType(europeanCredential));
        credentialDTO.setFile(fileStored);
        credentialDTO.setSigned(true);
        if (reports != null) {
            credentialDTO.setSignatureExpiryDate(reports.getExpiryDate());
        }

        savedCredentialDTO = this.addCredentialEntity(credentialDTO);

        try {
            if (sendMail == null || sendMail) {
                sendCreateNotificationEmail(savedCredentialDTO, credentialBytes, europeanCredential);
            }
        } catch (Exception e) {
            if (walletConfigService.getBoolean("wallet.create.credential.abort.if.error.sending.email", true)) {
                throw e;
            } else {
                String title = europeanCredential.getDisplayParameter() != null
                        && europeanCredential.getDisplayParameter().getTitle() != null ?
                        MultilangFieldUtil.getLiteralStringOrAny(europeanCredential.getDisplayParameter().getTitle(),
                                LocaleContextHolder.getLocale().getLanguage()) : "";
                logger.error("There has been a problem while sending the credential " + title, e);
            }
        }

        return savedCredentialDTO;
    }

    @Named("getStudentName")
    public String getStudentName(PersonDTO credentialSubject, @Context String locale) {
        String name;

        if (credentialSubject.getFullName() != null && !credentialSubject.getFullName().isEmpty()) {
            name = MultilangFieldUtil.getLiteralString(credentialSubject.getFullName(), locale).orElse(EDCIConstants.StringPool.STRING_EMPTY);
        } else if (credentialSubject.getGivenName() != null && !credentialSubject.getGivenName().isEmpty()) {
            name = MultilangFieldUtil.getLiteralString(credentialSubject.getGivenName(), locale).orElse(EDCIConstants.StringPool.STRING_EMPTY);

            name = name.concat(credentialSubject.getPatronymicName() != null ?
                    EDCIConstants.StringPool.STRING_SPACE +
                            MultilangFieldUtil.getLiteralString(credentialSubject.getPatronymicName(), locale).orElse(EDCIConstants.StringPool.STRING_EMPTY) : EDCIConstants.StringPool.STRING_EMPTY);

            name = name.concat(credentialSubject.getFamilyName() != null ?
                    EDCIConstants.StringPool.STRING_SPACE +
                            MultilangFieldUtil.getLiteralString(credentialSubject.getFamilyName(), locale).orElse(EDCIConstants.StringPool.STRING_EMPTY) : EDCIConstants.StringPool.STRING_EMPTY);
        } else if (credentialSubject.getBirthName() != null && !credentialSubject.getBirthName().isEmpty()) {
            name = MultilangFieldUtil.getLiteralString(credentialSubject.getBirthName(), locale).orElse(EDCIConstants.StringPool.STRING_EMPTY);
        } else if (credentialSubject.getFamilyName() != null && !credentialSubject.getFamilyName().isEmpty()) {
            name = MultilangFieldUtil.getLiteralString(credentialSubject.getFamilyName(), locale).orElse(EDCIConstants.StringPool.STRING_EMPTY);
        } else {
            name = "Anonymous";
        }

        return name;
    }

    public void sendCreateNotificationEmail(CredentialDTO credentialDTO, byte[] credentialBytes, EuropeanDigitalCredentialDTO europeanCredential) {
        //get context locale as default
        String locale = LocaleContextHolder.getLocale().toString();
        //Read credential bytes and extract locale

        locale = controlledListCommonsService.searchLanguageISO639ByConcept(europeanCredential.getDisplayParameter().getPrimaryLanguage());

        String credentialTitle = MultilangFieldUtil.getLiteralStringOrAny(europeanCredential.getDisplayParameter().getTitle(), locale);
        String credentialSubject = getStudentName(europeanCredential.getCredentialSubject(), locale);

        String subject = edciMessageService.getMessage(LocaleUtils.toLocale(locale), EDCIWalletMessages.MAIL_SUBJECT, credentialTitle);
        String toEmail = credentialDTO.getWallet().getUserEmail();
        Map<String, String> wildCards = new HashMap<String, String>();

        //Add all wildcards
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_FULLNAME, credentialSubject);
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_ISSUER, MultilangFieldUtil.getLiteralStringOrAny(europeanCredential.getIssuer().getLegalName(), locale));
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_CREDENTIALNAME, credentialTitle);
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_EUROPASSURL, walletConfigService.getString(EDCIWalletConstants.CONFIG_EUROPASS_URL));

//        //check presentation-dependent items
//        if (edciCredentialModelUtil.isPresentation(credentialHolderDTO)) {
//            wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_ISSUER, credentialHolderDTO.getIssuer().getPreferredName().getLocalizedStringOrAny(locale));
//        }
        //Check temporay-dependent items
        String templateFileName = EDCIWalletConstants.TEMPLATE_MAIL_WALLET_CREATED_CRED;
        byte[] attachment = null;
        String fileName = null;
        String viewerURL = walletConfigService.getViewerURL(credentialDTO);

        if (credentialDTO.getWallet().getTemporary()) {
            templateFileName = EDCIWalletConstants.TEMPLATE_MAIL_WALLET_CREATED_TEMPCRED;
            attachment = credentialBytes;
            fileName = credentialSubject + " - " + credentialTitle + EDCIConstants.JSON.EXTENSION_JSON_LD;
            viewerURL = walletConfigService.getString(EDCIWalletConstants.CONFIG_PROPERTY_VIEWER_URL);
        }
        byte[] thumbnail = null;
        try {
            thumbnail = diplomaUtil.getThumbnailImage(europeanCredential);
        } catch (Exception e) {
            logger.error("Could not generate diploma thumbnail for credential %s", () -> credentialDTO.getUuid(), () -> e);
        }

        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_VIEWER_URL, viewerURL);

        try {
            edciMailService.sendTemplatedEmail(EDCIWalletConstants.MAIL_TEMPLATES_DIRECTORY, templateFileName,
                    subject, wildCards, Collections.singletonList(toEmail), locale, attachment, fileName, thumbnail);
        } catch (EDCIException e) {
            throw new EDCIException().addDescription(String.format("Error sending creation mail, %s", e.getDescription())).setCause(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CredentialDTO> listCredentials(String userId) {
        return credentialMapper.toDTOList(credentialRepository.findJsonLdCredentialsByUserId(userId), new CycleAvoidingMappingContext());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCredential(String userId, String uuid) {
        try {
            this.deleteCredentialEntityByUuid(userId, uuid);
        } catch (EDCIException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR, "wallet.credential.deleted.error").setCause(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<byte[]> downloadCredential(String userId, String uuid, boolean retrieveVP) {

        CredentialDTO credentialDTO = this.fetchCredentialByUUID(userId, uuid);

        try {

            return new ResponseEntity<byte[]>(credentialStorageUtil.getCredentialFromFileSystem(credentialDTO),
                    credentialStorageUtil.prepareHttpHeadersForFile(EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX
                            .concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())) + DataModelConstants.JSON_LD_EXTENSION, MediaType.APPLICATION_OCTET_STREAM_VALUE), HttpStatus.OK);

        } catch (Exception e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.jsonld.unreadable").setCause(e);
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<VerificationCheckReport> verifyCredential(String userId, String credentialUUID) {
        CredentialDTO credentialDTO = this.fetchCredentialByUUID(userId, credentialUUID);
        return externalServicesUtil.verifyCredential(credentialStorageUtil.getCredentialFromFileSystem(credentialDTO));
    }

    public List<VerificationCheckReport> verifyCredential(MultipartFile file) throws IOException {
        return externalServicesUtil.verifyCredential(file.getBytes());
    }

//    @Transactional(propagation = Propagation.REQUIRED)
//    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(String userId, String credentialUuid, Date expirationdate) {
//
//        walletService.validateWalletExists(userId);
//
//        return downloadVerifiablePresentationXML(fetchCredentialByUUID(userId, credentialUuid), expirationdate);
//    }
//
//    @Transactional(propagation = Propagation.REQUIRED)
//    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(EuropassPresentationDTO europassPresentationDTO, Date expirationdate, String schemaVersion) {
//
//        europassPresentationDTO.setExpirationDate(expirationdate);
//
//        byte[] bytes = null;
//        try {
//
//            ByteArrayOutputStream sw = new ByteArrayOutputStream();
//
//            String schemaLocation = edciCredentialModelUtil.getSchemaLocation(EuropassPresentationDTO.class, europassPresentationDTO.getType().getUri(), schemaVersion);
//            Marshaller jaxbMarshaller = xmlUtil.getMarshallerWithSchemaLocation(EuropassPresentationDTO.class, schemaLocation);
//            jaxbMarshaller.marshal(europassPresentationDTO, sw);
//
//            bytes = sw.toByteArray();
//
//            //TODO: see EDCI-752
////            DSSDocument doc = new InMemoryDocument(bytes, "temp.xml", MimeType.XML);
////            DSSDocument signedDoc = DSSSignatureUtils.tokenSignXMLDocument(doc, getPkcs12Token(), DSSConstants.VERIFIABLE_PRESENTATION_XPATH_LOCATION);
////            bytes = DSSUtils.toByteArray(signedDoc);
//
//        } catch (JAXBException e) {
//            throw new EDCIException().addDescription("Error Marshalling XML").setCause(e);
//        }
//
//        //ToDo -> MARSHAL TO A FILE? SIGN ARRAY OF BYTES?
//        return new ResponseEntity<byte[]>(bytes, credentialUtil.prepareHttpHeadersForFile(EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".xml"), MediaType.APPLICATION_OCTET_STREAM_VALUE), HttpStatus.OK);
//    }

//    @Transactional(propagation = Propagation.REQUIRED)
//    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(CredentialDTO credentialDTOs, Date expirationdate) {
//
//        String schemaVersion = edciCredentialModelUtil.getSchemaVersionFromBytes(credentialDTOs.getCredentialXML());
//        return downloadVerifiablePresentationXML(credentialUtil.buildEuropassVerifiablePresentation(credentialDTOs), expirationdate, schemaVersion);
//
//    }
//
//    @Transactional(propagation = Propagation.REQUIRED)
//    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(byte[] europassPresentationBytes, Date expirationdate) {
//
//        String schemaVersion = edciCredentialModelUtil.getSchemaVersionFromBytes(europassPresentationBytes);
//        return downloadVerifiablePresentationXML(credentialUtil.buildEuropassVerifiablePresentation(europassPresentationBytes), expirationdate, schemaVersion);
//
//    }

    /*DB ACCESS METHODS*/

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CredentialDTO> findCredentialsByUuids(String userId, Set<String> uuids) {

        List<CredentialDTO> returnValue = credentialMapper.toDTOList(credentialRepository.findByUuids(userId, uuids), new CycleAvoidingMappingContext());

        List<String> uuidsFound = returnValue.stream().map(CredentialDTO::getUuid).collect(Collectors.toList());

        Set<String> uuidsNotFound = uuids.stream().collect(Collectors.toSet());
        uuidsNotFound.removeAll(uuidsFound);

        if (uuidsNotFound.size() > 0) {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.CREDENTIAL_NOT_EXISTS_VARIOUS, "wallet.credentials.not.found", uuidsNotFound.stream().collect(Collectors.joining(", ")));
        }

        return returnValue;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CredentialDTO addCredentialEntity(CredentialDTO credentialEntityDTO) {

        if (!credentialExists(credentialEntityDTO.getWallet().getUserId(), credentialEntityDTO.getUuid())) {
            return credentialMapper.toDTO(credentialRepository.save(
                    credentialMapper.toDAO(credentialEntityDTO, new CycleAvoidingMappingContext())), new CycleAvoidingMappingContext());
        } else {
            throw new EDCIException(HttpStatus.CONFLICT, ErrorCode.CREDENTIAL_EXISTS_UUID, "wallet.credential.uuid.exists.error", credentialEntityDTO.getUuid());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCredentialEntityByUuid(String userId, String uuid) {
        if (credentialExists(userId, uuid)) {
            CredentialDAO credential = credentialRepository.fetchByUUID(userId, uuid);
            credentialStorageUtil.removeCredentialFromFileSystem(credential.getWallet().getFolder(), credential.getFile());
            credentialRepository.delete(credential);
        } else {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.CREDENTIAL_NOT_EXISTS_UUID, "wallet.credential.uuid.not.exists.error", uuid);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CredentialDTO fetchCredentialByUUID(String userId, String UUID) {
        if (credentialExists(userId, UUID)) {
            return credentialMapper.toDTO(credentialRepository.fetchByUUID(userId, UUID), new CycleAvoidingMappingContext());
        } else {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.CREDENTIAL_NOT_EXISTS_UUID, "wallet.credential.uuid.not.exists.error", UUID);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CredentialDTO fetchCredentialByUUID(String userId, String UUID, String type) {
        if (credentialExists(userId, UUID)) {
            return credentialMapper.toDTO(credentialRepository.fetchByUUID(userId, UUID, type), new CycleAvoidingMappingContext());
        } else {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.CREDENTIAL_NOT_EXISTS_UUID, "wallet.credential.uuid.not.exists.error", UUID);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private boolean credentialExists(String userId, String UUID) {
        return credentialRepository.countByUUID(userId, UUID) > 0;
    }

    /*
     * Gets a verifiable presentation of the credential in PDF format (signed)
     */
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(CredentialDTO credentialDTO, Date expirationdate, String pdfType) {

//        EuropassPresentationDTO europassPresentationDTO = credentialUtil.buildEuropassVerifiablePresentation(credentialDTOS);
//        europassPresentationDTO.setExpirationDate(expirationdate);

        String shareLink = null;
        if (!StringUtils.isEmpty(credentialDTO.getWallet().getUserId()) && expirationdate != null) {
            ShareLinkDTO shLinkDTO = shareLinkService.createShareLink(credentialDTO.getWallet().getUserId(), credentialDTO.getUuid(), expirationdate);
            shareLink = shLinkDTO.getShareHash();
        }

        return downloadVerifiablePresentationPDF(credentialStorageUtil.getCredentialFromFileSystem(credentialDTO), credentialDTO, shareLink, pdfType, expirationdate);
    }

    /*
     * Gets a verifiable presentation of the credential in PDF format (signed)
     */
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(ShareLinkDTO shareLinkDTO, String pdfType) {
        CredentialDTO credentialDTO = shareLinkDTO.getCredential();
        return downloadVerifiablePresentationPDF(credentialStorageUtil.getCredentialFromFileSystem(shareLinkDTO.getCredential()),
                credentialDTO, shareLinkDTO.getShareHash(), pdfType, shareLinkDTO.getExpirationDate());
    }

    /*
     * Gets a verifiable presentation of the credential in PDF format (signed)
     */
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(byte[] europeanCredential, String shareLink, String pdfType, Date expireDate) {
        return downloadVerifiablePresentationPDF(europeanCredential, null, shareLink, pdfType, expireDate);
    }

    /*
     * Gets a verifiable presentation of the credential in PDF format (signed)
     */
    protected ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(byte[] europeanCredential, CredentialDTO credentialDTOS, String shareLink, String pdfType, Date expireDate) {
        return credentialPDFService.buildCredentialPDF(europeanCredential, pdfType, credentialDTOS, shareLink, expireDate);
    }

    /*
     * Gets a PNG image of a credential's diploma
     */
    public List<byte[]> getDiplomaImage(EuropeanDigitalCredentialDTO edc) {
        return diplomaUtil.getBase64DiplomaImages(edc, null).stream().map(String::getBytes).collect(Collectors.toList());
    }

    /*
     * Gets a PNG image of a credential's diploma
     */
    public List<byte[]> getDiplomaImage(String userId, String uuid) {

        CredentialDAO credential = null;

        if (credentialExists(userId, uuid)) {
            credential = credentialRepository.fetchByUUID(userId, uuid);
        } else {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.CREDENTIAL_NOT_EXISTS_UUID, "wallet.credential.uuid.not.exists.error", uuid);
        }

        EuropeanDigitalCredentialDTO edc = null;
        try {
            edc = credentialUtil.unMarshallCredential(credentialStorageUtil.getCredentialFromFileSystem(credential));
        } catch (Exception e) {
            throw new EDCIException(e);
        }

        return getDiplomaImage(edc);

    }

    /*
     * Gets a PNG image of a credential's diploma
     */
    public EuropeanDigitalCredentialDTO getEuropeanDigitalCredential(CredentialDTO credential) {

        EuropeanDigitalCredentialDTO edc = null;
        try {
            edc = credentialUtil.unMarshallCredential(credentialStorageUtil.getCredentialFromFileSystem(credential));
        } catch (Exception e) {
            throw new EDCIException(e);
        }

        return edc;

    }


}
