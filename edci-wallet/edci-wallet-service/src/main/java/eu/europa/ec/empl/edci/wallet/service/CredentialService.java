package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.constants.EuropassConstants;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.datamodel.view.EuropassCredentialPresentationView;
import eu.europa.ec.empl.edci.datamodel.view.EuropassDiplomaDTO;
import eu.europa.ec.empl.edci.dss.validation.DSSValidationUtils;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.mapper.EuropassCredentialPresentationMapper;
import eu.europa.ec.empl.edci.service.EDCIMailService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.*;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletMessages;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.wallet.mapper.WalletMapper;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialUtil;
import eu.europa.ec.empl.edci.wallet.service.utils.EuropassCredentialVerifyUtil;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CredentialService {
    private static final Logger logger = Logger.getLogger(CredentialService.class);

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

    /*BUSINESS LOGIC METHODS**/

    public String getDDBBCredType(CredentialHolderDTO credentialHolderDTO) {
        if (credentialHolderDTO instanceof EuropassPresentationDTO) {
            return EDCIWalletConstants.CREDENTIAL_STORED_TYPE_EUROPASS_PRESENTATION;
        } else {
            return EDCIWalletConstants.CREDENTIAL_STORED_TYPE_EUROPASS_CREDENTIAL;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CredentialDTO createCredential(CredentialDTO credentialDTO) {

        WalletDTO walletDTO = walletService.fetchWalletByUserId(credentialDTO.getWalletDTO().getUserId());

        credentialDTO.setWalletDTO(walletDTO);
        CredentialDTO savedCredentialDTO = null;
        try {
            //Check if seal with a valid Qseal
            if (!walletConfigService.getBoolean("allow.unsigned.credentials", false)) {
                DSSValidationUtils dssValidationUtils = new DSSValidationUtils();
                Reports reports = dssValidationUtils.validateXML(credentialDTO.getCredentialXML(), certificateVerifier);
                if (reports == null || !(reports.getSimpleReport().getValidSignaturesCount() > 0)) {
                    throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_UNSIGNED, "wallet.credential.not.sealed.error", credentialDTO.getUuid());
                }
            }

            CredentialHolderDTO credentialHolderDTO = edciCredentialModelUtil.fromByteArray(credentialDTO.getCredentialXML());
            credentialDTO.setUuid(credentialHolderDTO.getId().toString());
            credentialDTO.setCredentialLocalizableInfoDTOS(credentialUtil.getLocalizableInfoDTOS(credentialHolderDTO.getCredential(), credentialDTO));
            credentialDTO.setType(getDDBBCredType(credentialHolderDTO));


            savedCredentialDTO = this.addCredentialEntity(credentialDTO);
            sendCreateNotificationEmail(savedCredentialDTO);
        } catch (JAXBException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        } catch (IOException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        }

        return savedCredentialDTO;
    }


    public void sendCreateNotificationEmail(CredentialDTO credentialDTO) {
        EuropassCredentialDTO europassCredentialDTO = null;
        //get context locale as default
        String locale = LocaleContextHolder.getLocale().toString();
        //Read credential bytes and extract locale
        try {
            europassCredentialDTO = edciCredentialModelUtil.fromByteArray(credentialDTO.getCredentialXML()).getCredential();
            locale = edciCredentialModelUtil.guessCredentialLocale(europassCredentialDTO).toString();
        } catch (JAXBException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        } catch (IOException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        }

        String subject = edciMessageService.getMessage(LocaleUtils.toLocale(locale), EDCIWalletMessages.MAIL_SUBJECT, europassCredentialDTO.getTitle().getLocalizedStringOrAny(locale));
        String toEmail = credentialDTO.getWalletDTO().getUserEmail();
        Map<String, String> wildCards = new HashMap<String, String>();

        //Add all wildcards
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_FULLNAME, europassCredentialDTO.getCredentialSubject().getFullName().getStringContent(locale));
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_ISSUER, europassCredentialDTO.getIssuer().getPreferredName().getLocalizedStringOrAny(locale));
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_CREDENTIALNAME, europassCredentialDTO.getTitle().getLocalizedStringOrAny(locale));
        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_EUROPASSURL, walletConfigService.getString(EDCIWalletConstants.CONFIG_EUROPASS_URL));

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

        wildCards.put(EDCIWalletConstants.MAIL_WILDCARD_VIEWER_URL, viewerURL);
        try {
            edciMailService.sendTemplatedEmail(EDCIWalletConstants.MAIL_TEMPLATES_DIRECTORY, templateFileName,
                    subject, wildCards, Collections.singletonList(toEmail), locale, attachment, fileName);
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
            credentialDTO.setCredentialXML(edciCredentialModelUtil.toXML(retrieveVP ? cred : cred.getCredential()).getBytes()); //TODO: Temporary change
        } catch (JAXBException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        } catch (IOException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        }

        return new ResponseEntity<byte[]>(credentialDTO.getCredentialXML(),
                credentialUtil.prepareHttpHeadersForCredentialDownload(Defaults.CREDENTIAL_DEFAULT_PREFIX
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

        return downloadVerifiablePresentationXML(fetchCredentialByUUID(userId, credentialUuid), expirationdate, false);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(EuropassPresentationDTO europassPresentationDTO, Date expirationdate, boolean onlyRetrieveVP) {

        europassPresentationDTO.setExpirationDate(expirationdate);

        byte[] bytes = null;
        try {

            ByteArrayOutputStream sw = new ByteArrayOutputStream();

            String schemaLocation = edciCredentialModelUtil.getSchemaLocation(EuropassPresentationDTO.class, europassPresentationDTO.getType().getUri());
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
        return new ResponseEntity<byte[]>(bytes, credentialUtil.prepareHttpHeadersForCredentialDownload(Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".xml"), MediaType.APPLICATION_OCTET_STREAM_VALUE), HttpStatus.OK);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(CredentialDTO credentialDTOs, Date expirationdate, boolean onlyRetrieveVP) {

        return downloadVerifiablePresentationXML(credentialUtil.buildEuropassVerifiablePresentation(credentialDTOs, onlyRetrieveVP), expirationdate, onlyRetrieveVP);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<byte[]> downloadVerifiablePresentationXML(byte[] europassPresentationBytes, Date expirationdate, boolean onlyRetrieveVP) {

        return downloadVerifiablePresentationXML(credentialUtil.buildEuropassVerifiablePresentation(europassPresentationBytes, onlyRetrieveVP), expirationdate, onlyRetrieveVP);

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
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(String userId, String uuidCred, Date expirationdate, boolean onlyRetrieveVP) {

        walletService.validateWalletExists(userId);

        return downloadVerifiablePresentationPDF(fetchCredentialByUUID(userId, uuidCred), expirationdate, onlyRetrieveVP);

    }

    /*
     * Gets a verifiable presentation of the credential in PDF format (signed)
     */
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(CredentialDTO credentialDTOS, Date expirationdate, boolean onlyRetrieveVP) {

        EuropassPresentationDTO europassPresentationDTO = credentialUtil.buildEuropassVerifiablePresentation(credentialDTOS, onlyRetrieveVP);
        europassPresentationDTO.setExpirationDate(expirationdate);

        String shareLink = null;
        if (!StringUtils.isEmpty(credentialDTOS.getWalletDTO().getUserId()) && europassPresentationDTO.getExpirationDate() != null) {
            ShareLinkDTO shLinkDTO = shareLinkService.createShareLink(credentialDTOS.getWalletDTO().getUserId(), credentialDTOS.getUuid(), europassPresentationDTO.getExpirationDate());
            shareLink = walletConfigService.getString(EDCIWalletConstants.CONFIG_PROPERTY_VIEWER_SHARED_URL).replaceAll(Parameter.SHARED_HASH, shLinkDTO.getShareHash());
        }

        return downloadVerifiablePresentationPDF(europassPresentationDTO, shareLink);
    }

    /*
     * Gets a verifiable presentation of the credential in PDF format (signed)
     */
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(EuropassPresentationDTO europassPresentationDTO, String shareLink) {
        try {

            Context context = new Context();

            EuropassCredentialDTO europassCredentialDTO = europassPresentationDTO.getCredential();
            EuropassCredentialPresentationView europassCredentialDetailView = europassCredentialPresentationMapper.toEuropassCredentialPresentationView(europassPresentationDTO, true);

            List<EuropassCredentialDTO> subCredentials = new ArrayList<EuropassCredentialDTO>();
            try {
                subCredentials = edciCredentialModelUtil.parseSubCredentials(europassPresentationDTO.getCredential().getSubCredentialsXML());
            } catch (JAXBException e) {
                throw new EDCIException("wallet.credential.parsing.subcredential.error").setCause(e);
            }
            EuropassDiplomaDTO diploma = diplomaUtils.extractEuropassDiplomaDTO(europassCredentialDTO, EuropassConstants.DEFAULT_LOCALE);
            context.setVariable("indexCount", new AtomicInteger(1));
            context.setVariable("europassCredentialDetailView", europassCredentialDetailView);
            context.setVariable("europassCredential", europassPresentationDTO.getCredential());
            context.setVariable("europassVerifications", europassCredentialPresentationMapper.toVerificationCheckFieldViewList(europassPresentationDTO.getVerifications()));
            context.setVariable("europassSubCredentials", subCredentials);
            context.setVariable("diploma", diploma);

            // Add european logo (footer)
            context.setLocale(LocaleContextHolder.getLocale());
            String b64 = Base64.getEncoder().encodeToString(getLogo(LocaleContextHolder.getLocale().getLanguage()).getBytes());
            context.setVariable("europeLogo", "data:image/svg+xml;base64," + b64);

            if (!StringUtils.isEmpty(shareLink)) {
                context.setVariable("shareLink", shareLink);
                context.setVariable("shareLinkQR",
                        new StringBuffer("data:image/png;base64,").append(new String(Base64.getEncoder().encode(
                                imageUtil.generateQRCodeImageBytes(shareLink, "png")), StandardCharsets.UTF_8).toString())); //
            }

            String html = processTemplate("verifiable_presentation_template", context);

            String pdfDownloadURL = getPdfDownloadUrl();

            if (pdfDownloadURL != null && !pdfDownloadURL.isEmpty()) {
                byte[] bytes = new EDCIRestRequestBuilder(HttpMethod.POST, pdfDownloadURL)
                        .addHeaderRequestedWith()
                        .addHeaders(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_PDF)
                        .addBody(EDCIRestRequestBuilder.prepareMultiPartStringBody("html", html, new HashMap<>()))
                        .buildRequest(byte[].class)
                        .execute();

                // Sign PDF
                //TODO: see EDCI-752
//                DSSDocument doc = new InMemoryDocument(bytes, "temp.pdf", MimeType.PDF);
//                DSSDocument signedDoc = DSSSignatureUtils.tokenSignPDFDocument(doc, getPkcs12Token());
//                bytes = DSSUtils.toByteArray(signedDoc);

                ByteArrayResource resource = new ByteArrayResource(bytes);

                String fileName = "";
                try {
                    fileName = Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()));
                } catch (Exception e) {
                    logger.error(e);
                    fileName = "Credential";
                }

                return new ResponseEntity<ByteArrayResource>(resource,
                        credentialUtil.prepareHttpHeadersForCredentialDownload(fileName.concat(".pdf"), MediaType.APPLICATION_PDF_VALUE), HttpStatus.OK);
            } else {
                return null;
            }

        } catch (Exception e) {
            logger.error(e);
            throw new EDCIException(e);
        }
    }

    public String getPdfDownloadUrl() {
        return walletConfigService.getString("pdf.download.url");
    }

    public String processTemplate(String templateFile, Context context) {
        return templateEngine.process(templateFile, context);
    }

    private String getLogo(String locale) {
        if (locale == null) {
            locale = Defaults.DEFAULT_LOCALE;
        }
        ClassPathResource template = new ClassPathResource(
                "logo/".concat("logo")
                        .concat("--")
                        .concat(locale)
                        .concat(".svg"));
        String logoSource;
        try (InputStream inputStream = template.getInputStream()) {
            logoSource = IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            throw new EDCIException().addDescription(String.format("Could not get logo %s", template.getPath()));
        }
        return logoSource;
    }
}
