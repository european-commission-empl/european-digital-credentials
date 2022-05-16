package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.dss.service.DSSEDCIValidationService;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.mapper.EuropassCredentialPresentationMapper;
import eu.europa.ec.empl.edci.service.EDCIMailService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.*;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletMessages;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import eu.europa.ec.empl.edci.wallet.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.wallet.mapper.WalletMapper;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialUtil;
import eu.europa.ec.empl.edci.wallet.service.utils.EuropassCredentialVerifyUtil;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CredentialService {
    private static final Logger logger = LogManager.getLogger(CredentialService.class);

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private CredentialMapper credentialMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Inject
    private EuropassCredentialVerifyUtil europassCredentialVerifyUtil;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private Validator validator;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private EuropassCredentialPresentationMapper europassCredentialPresentationMapper;

    @Autowired
    private ControlledListsUtil controlledListsUtil;

    @Autowired
    private XmlUtil xmlUtil;

    @Autowired
    private DiplomaUtils diplomaUtils;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private ShareLinkService shareLinkService;

    @Autowired
    private EDCIMailService edciMailService;

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private CredentialPDFService credentialPDFService;

    @Autowired
    private DSSEDCIValidationService dssedciValidationService;

    /*BUSINESS LOGIC METHODS**/

    public String getDDBBCredType(CredentialHolderDTO credentialHolderDTO) {
        if (credentialHolderDTO instanceof EuropassPresentationDTO) {
            return EDCIWalletConstants.CREDENTIAL_STORED_TYPE_EUROPASS_PRESENTATION;
        } else {
            return EDCIWalletConstants.CREDENTIAL_STORED_TYPE_EUROPASS_CREDENTIAL;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CredentialDTO createCredential(CredentialDTO credentialDTO, Boolean sendMail) {

        WalletDTO walletDTO = walletService.fetchWalletByUserId(credentialDTO.getWalletDTO().getUserId());

        credentialDTO.setWalletDTO(walletDTO);
        CredentialDTO savedCredentialDTO = null;
        try {
            //Check if seal with a valid Qseal
            if (!walletConfigService.getBoolean("allow.unsigned.credentials", false)) {
                Reports reports = this.getDssedciValidationService().validateXML(credentialDTO.getCredentialXML(), false);
                if (reports == null || !(reports.getSimpleReport().getValidSignaturesCount() > 0)) {
                    throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_UNSIGNED, "wallet.credential.not.sealed.error", credentialDTO.getUuid());
                }
            }

            CredentialHolderDTO credentialHolderDTO = edciCredentialModelUtil.fromByteArray(credentialDTO.getCredentialXML());
            credentialDTO.setUuid(credentialHolderDTO.getId().toString());
            credentialDTO.setCredentialLocalizableInfoDTOS(credentialUtil.getLocalizableInfoDTOS(credentialHolderDTO.getCredential(), credentialDTO));
            credentialDTO.setType(getDDBBCredType(credentialHolderDTO));


            savedCredentialDTO = this.addCredentialEntity(credentialDTO);
            if (sendMail == null || sendMail) {
                sendCreateNotificationEmail(savedCredentialDTO);
            }
        } catch (JAXBException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        } catch (IOException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        }

        return savedCredentialDTO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CredentialDTO createCredentialByEmail(CredentialDTO credentialDTO, String userEmail, Boolean sendMail) {
        WalletDTO wallet = null;

        EmailValidator emailValidator = new EmailValidator();
        if (!emailValidator.isValid(userEmail, null)) {
            throw new EDCIBadRequestException().addDescription("Invalid email");
        }
        //Check if auto creation of temporal wallets is activated
        if (walletConfigService.get("wallet.create.sending.credential", Boolean.class, true)) {
            wallet = walletService.fetchWalletByUserEmail(userEmail, false);
            //If no wallet detected and auto-creation enabled, use addBulkWalletEntity
            if (wallet == null) {
                wallet = new WalletDTO();
                wallet.setUserEmail(userEmail);
                wallet = walletService.addBulkWalletEntity(wallet);
            }
        } else {
            //If auto creation not enabled, just try to fetch by email, an exception if thrown if it does not exist
            wallet = walletService.fetchWalletByUserEmail(userEmail);
        }
        //set appropriate wallet and create the credential with normal method, so notifications are sent
        credentialDTO.setWalletDTO(wallet);
        return this.createCredential(credentialDTO, sendMail);
    }

    public void sendCreateNotificationEmail(CredentialDTO credentialDTO) {
        CredentialHolderDTO credentialHolderDTO = null;
        //get context locale as default
        String locale = LocaleContextHolder.getLocale().toString();
        //Read credential bytes and extract locale
        try {
            credentialHolderDTO = edciCredentialModelUtil.fromByteArray(credentialDTO.getCredentialXML());

        } catch (JAXBException | IOException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        }

        EuropassCredentialDTO europassCredentialDTO = credentialHolderDTO.getCredential();
        locale = edciCredentialModelUtil.guessCredentialLocale(europassCredentialDTO).toString();

        String subject = edciMessageService.getMessage(LocaleUtils.toLocale(locale), EDCIWalletMessages.MAIL_SUBJECT, europassCredentialDTO.getTitle().getLocalizedStringOrAny(locale));
        String toEmail = credentialDTO.getWalletDTO().getUserEmail();
        Map<String, String> wildCards = new HashMap<String, String>();

        //Add all wildcards
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_FULLNAME, europassCredentialDTO.getCredentialSubject().getFullName().getStringContent(locale));
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_ISSUER, europassCredentialDTO.getIssuer().getPreferredName().getLocalizedStringOrAny(locale));
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_CREDENTIALNAME, europassCredentialDTO.getTitle().getLocalizedStringOrAny(locale));
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_EUROPASSURL, walletConfigService.getString(EDCIWalletConstants.CONFIG_EUROPASS_URL));

        //check presentation-dependent items
        if (edciCredentialModelUtil.isPresentation(credentialHolderDTO)) {
            wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_ISSUER, credentialHolderDTO.getIssuer().getPreferredName().getLocalizedStringOrAny(locale));
        }
        //Check temporay-dependent items
        String templateFileName = EDCIWalletConstants.TEMPLATE_MAIL_WALLET_CREATED_CRED;
        byte[] attachment = null;
        String fileName = null;
        String viewerURL = walletConfigService.getViewerURL(credentialDTO);

        if (credentialDTO.getWalletDTO().getTemporary()) {
            templateFileName = EDCIWalletConstants.TEMPLATE_MAIL_WALLET_CREATED_TEMPCRED;
            attachment = credentialDTO.getCredentialXML();
            fileName = edciCredentialModelUtil.getEncodedFileName(europassCredentialDTO, locale);
            viewerURL = walletConfigService.getString(EDCIWalletConstants.CONFIG_PROPERTY_VIEWER_URL);
        }
        byte[] thumbnail = null;
        try {
            thumbnail = getDiplomaImage(credentialDTO.getWalletDTO().getUserId(), credentialDTO.getUuid()).get(0);
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
        return credentialMapper.toDTOList(credentialRepository.findCredentialsByUserId(userId));
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
        CredentialHolderDTO cred = null;

        //TODO: By now the wallet only returns credentials
        try {
            cred = edciCredentialModelUtil.fromByteArray(credentialDTO.getCredentialXML());
            String credentialString = null;

            if (!retrieveVP) {
                String schemaVersion = edciCredentialModelUtil.getSchemaVersionFromBytes(credentialDTO.getCredentialXML());
                Marshaller marshaller = xmlUtil.getMarshallerWithSchemaLocation(cred.getCredential().getClass(),
                        edciCredentialModelUtil.getSchemaLocation(cred.getCredential().getClass(), cred.getCredential().getType().getUri(), schemaVersion));
                try (StringWriter stringWriter = new StringWriter()) {
                    marshaller.marshal(cred.getCredential(), stringWriter);
                    credentialString = stringWriter.toString();
                }
            } else {
                credentialString = edciCredentialModelUtil.toXML(cred);
            }

            credentialDTO.setCredentialXML(credentialString.getBytes()); //TODO: Temporary change

        } catch (JAXBException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        } catch (IOException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        }

        return new ResponseEntity<byte[]>(credentialDTO.getCredentialXML(),
                credentialUtil.prepareHttpHeadersForFile(EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX
                        .concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())) + ".xml", MediaType.APPLICATION_OCTET_STREAM_VALUE), HttpStatus.OK);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<VerificationCheckDTO> verifyCredential(String userId, String credentialUUID) {
        CredentialDTO credentialDTO = this.fetchCredentialByUUID(userId, credentialUUID);
        return europassCredentialVerifyUtil.verifyCredential(credentialDTO.getCredentialXML());
    }

    public List<VerificationCheckDTO> verifyCredential(MultipartFile file) throws IOException {
        return europassCredentialVerifyUtil.verifyCredential(file);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(String userId, String credentialUuid, Date expirationdate) {

        walletService.validateWalletExists(userId);

        return downloadVerifiablePresentationXML(fetchCredentialByUUID(userId, credentialUuid), expirationdate);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(EuropassPresentationDTO europassPresentationDTO, Date expirationdate, String schemaVersion) {

        europassPresentationDTO.setExpirationDate(expirationdate);

        byte[] bytes = null;
        try {

            ByteArrayOutputStream sw = new ByteArrayOutputStream();

            String schemaLocation = edciCredentialModelUtil.getSchemaLocation(EuropassPresentationDTO.class, europassPresentationDTO.getType().getUri(), schemaVersion);
            Marshaller jaxbMarshaller = xmlUtil.getMarshallerWithSchemaLocation(EuropassPresentationDTO.class, schemaLocation);
            jaxbMarshaller.marshal(europassPresentationDTO, sw);

            bytes = sw.toByteArray();

            //TODO: see EDCI-752
//            DSSDocument doc = new InMemoryDocument(bytes, "temp.xml", MimeType.XML);
//            DSSDocument signedDoc = DSSSignatureUtils.tokenSignXMLDocument(doc, getPkcs12Token(), DSSConstants.VERIFIABLE_PRESENTATION_XPATH_LOCATION);
//            bytes = DSSUtils.toByteArray(signedDoc);

        } catch (JAXBException e) {
            throw new EDCIException().addDescription("Error Marshalling XML").setCause(e);
        }

        //ToDo -> MARSHAL TO A FILE? SIGN ARRAY OF BYTES?
        return new ResponseEntity<byte[]>(bytes, credentialUtil.prepareHttpHeadersForFile(EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".xml"), MediaType.APPLICATION_OCTET_STREAM_VALUE), HttpStatus.OK);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(CredentialDTO credentialDTOs, Date expirationdate) {

        String schemaVersion = edciCredentialModelUtil.getSchemaVersionFromBytes(credentialDTOs.getCredentialXML());
        return downloadVerifiablePresentationXML(credentialUtil.buildEuropassVerifiablePresentation(credentialDTOs), expirationdate, schemaVersion);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(byte[] europassPresentationBytes, Date expirationdate) {

        String schemaVersion = edciCredentialModelUtil.getSchemaVersionFromBytes(europassPresentationBytes);
        return downloadVerifiablePresentationXML(credentialUtil.buildEuropassVerifiablePresentation(europassPresentationBytes), expirationdate, schemaVersion);

    }

    /*DB ACCESS METHODS*/

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CredentialDTO> findCredentialsByUuids(String userId, Set<String> uuids) {

        List<CredentialDTO> returnValue = credentialMapper.toDTOList(credentialRepository.findByUuids(userId, uuids));

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

        if (!credentialExists(credentialEntityDTO.getWalletDTO().getUserId(), credentialEntityDTO.getUuid())) {
            return credentialMapper.toDTO(credentialRepository.save(credentialMapper.toDAO(credentialEntityDTO)));
        } else {
            throw new EDCIException(HttpStatus.CONFLICT, ErrorCode.CREDENTIAL_EXISTS_UUID, "wallet.credential.uuid.exists.error", credentialEntityDTO.getUuid());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCredentialEntityByUuid(String userId, String uuid) {
        if (credentialExists(userId, uuid)) {
            credentialRepository.delete(credentialMapper.toDAO(this.fetchCredentialByUUID(userId, uuid)));
        } else {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.CREDENTIAL_NOT_EXISTS_UUID, "wallet.credential.uuid.not.exists.error", uuid);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CredentialDTO fetchCredentialByUUID(String userId, String UUID) {
        if (credentialExists(userId, UUID)) {
            return credentialMapper.toDTO(credentialRepository.fetchByUUID(userId, UUID));
        } else {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.CREDENTIAL_NOT_EXISTS_UUID, "wallet.credential.uuid.not.exists.error", UUID);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CredentialDTO fetchCredentialByUUID(String userId, String UUID, String type) {
        if (credentialExists(userId, UUID)) {
            return credentialMapper.toDTO(credentialRepository.fetchByUUID(userId, UUID, type));
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
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(CredentialDTO credentialDTOS, Date expirationdate, String pdfType) {

        EuropassPresentationDTO europassPresentationDTO = credentialUtil.buildEuropassVerifiablePresentation(credentialDTOS);
        europassPresentationDTO.setExpirationDate(expirationdate);

        String shareLink = null;
        if (!StringUtils.isEmpty(credentialDTOS.getWalletDTO().getUserId()) && europassPresentationDTO.getExpirationDate() != null) {
            ShareLinkDTO shLinkDTO = shareLinkService.createShareLink(credentialDTOS.getWalletDTO().getUserId(), credentialDTOS.getUuid(), europassPresentationDTO.getExpirationDate());
            shareLink = shLinkDTO.getShareHash();
        }

        return downloadVerifiablePresentationPDF(europassPresentationDTO, credentialDTOS, shareLink, pdfType, expirationdate);
    }

    /*
     * Gets a verifiable presentation of the credential in PDF format (signed)
     */
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(ShareLinkDTO shareLinkDTO, String pdfType) {

        CredentialDTO credentialDTO = shareLinkDTO.getCredentialDTO();
        EuropassPresentationDTO europassPresentationDTO = credentialUtil.buildEuropassVerifiablePresentation(credentialDTO);
        europassPresentationDTO.setExpirationDate(shareLinkDTO.getExpirationDate());

        return downloadVerifiablePresentationPDF(europassPresentationDTO, credentialDTO, shareLinkDTO.getShareHash(), pdfType, shareLinkDTO.getExpirationDate());
    }

    /*
     * Gets a verifiable presentation of the credential in PDF format (signed)
     */
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(EuropassPresentationDTO europassPresentationDTO, String shareLink, String pdfType, Date expireDate) {
        return downloadVerifiablePresentationPDF(europassPresentationDTO, null, shareLink, pdfType, expireDate);
    }

    /*
     * Gets a verifiable presentation of the credential in PDF format (signed)
     */
    protected ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(EuropassPresentationDTO europassPresentationDTO, CredentialDTO credentialDTOS, String shareLink, String pdfType, Date expireDate) {
        return credentialPDFService.buildCredentialPDF(europassPresentationDTO, credentialDTOS, shareLink, pdfType, expireDate);
    }

    /*
     * Gets a PNG image of a credential's diploma
     */
    public List<byte[]> getDiplomaImage(String userId, String uuid) {

        List<byte[]> diploma = null;
        CredentialDAO credential = null;

        if (credentialExists(userId, uuid)) {
            credential = credentialRepository.fetchByUUID(userId, uuid);
        } else {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.CREDENTIAL_NOT_EXISTS_UUID, "wallet.credential.uuid.not.exists.error", uuid);
        }

        //If the diploma is stored in the database, we retrieve it
        if (credential.getDiplomaImage() != null && credential.getDiplomaImage().isEmpty()) {
            logger.debug("Diploma from credential " + uuid + " retrieved from database");
            diploma = credential.getDiplomaImage();
        } else {

            //If not, we generate it
            try {
                logger.debug("Generating diploma for credential " + uuid);
                diploma = diplomaUtils.getDiplomaFromCredential(edciCredentialModelUtil.fromByteArray(credential.getCredentialXML()));
            } catch (Exception e) {
                throw new EDCIException();
            }

            //If we have enabled the database storage of the diploma we save it for later accesses
            if (walletConfigService.getBoolean("store.diploma.database", true)) {
                //Check first if we are retrieving the 503 status img. We don't want this one to be stored
                byte[] imgError = imageUtil.get503ImgError();
                if (!diploma.stream().anyMatch(img -> Arrays.equals(imgError,img))) {
                    logger.debug("Saving diploma from credential " + uuid + " into database");
                    credential.setDiplomaImage(diploma);
                    credentialRepository.save(credential);
                }
            }
        }

        return diploma;
    }

    public DSSEDCIValidationService getDssedciValidationService() {
        return dssedciValidationService;
    }

    public void setDssedciValidationService(DSSEDCIValidationService dssedciValidationService) {
        this.dssedciValidationService = dssedciValidationService;
    }
}
