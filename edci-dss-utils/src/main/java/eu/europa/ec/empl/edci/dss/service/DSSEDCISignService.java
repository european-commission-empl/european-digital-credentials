package eu.europa.ec.empl.edci.dss.service;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.dss.model.signature.*;
import eu.europa.ec.empl.edci.dss.util.DSSSignatureUtils;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DomUtils;
import eu.europa.esig.dss.asic.cades.ASiCWithCAdESSignatureParameters;
import eu.europa.esig.dss.asic.cades.signature.ASiCWithCAdESService;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESService;
import eu.europa.esig.dss.definition.DSSNamespace;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.model.*;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.revocation.crl.CRL;
import eu.europa.esig.dss.model.x509.revocation.ocsp.OCSP;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.signature.MultipleDocumentsSignatureService;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.x509.revocation.RevocationSource;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class DSSEDCISignService {

    private static final Logger logger = LogManager.getLogger(DSSEDCISignService.class);

    @Autowired
    private EDCIFileService edciFileService;

    @Autowired
    private OnlineTSPSource onlineTSPSource;

    @Autowired
    private IConfigService iConfigService;

    @Autowired
    private DataLoader dataLoader;

    @Autowired
    private RevocationSource<CRL> crlSource;

    @Autowired
    private RevocationSource<OCSP> ocspSource;

    @Autowired
    private TLValidationJob validationJob;

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private DSSEDCICertificateService dssedciCertificateService;

    //ToDo-> Make DigestAlgorithm configurable

    /**
     * Sign an Europass Credential using local certificate, uses the signature level configured at properties
     *
     * @param toSignDocumentPath document to be signed
     * @param certPath           certificate path
     * @param password           certificate password
     * @param doRefresh          refresh the OCSP and CRL caches, recommended false
     * @return the signed DSSDocument
     */
    public DSSDocument signXMLCredential(String toSignDocumentPath, String certPath, String password, boolean doRefresh) {
        return this.signXMLDocument(toSignDocumentPath, certPath, password, EDCIConfig.XML.XML_SIGNATURE_XPATH, doRefresh);
    }

    /**
     * Sign an XML Document using local Certificate, uses the signature level configured at properties
     *
     * @param toSignDocumentPath document to be signed
     * @param certPath           certificate path
     * @param password           certificate password
     * @param xpathLocation      signature location inside xml
     * @param doRefresh          refresh the OCSP and CRL caches, recommended false
     * @return the signed DSSDocument
     */
    public DSSDocument signXMLDocument(String toSignDocumentPath, String certPath, String password, String xpathLocation, boolean doRefresh) {
        DSSDocument toSignDocument = new FileDocument(this.getEdciFileService().getOrCreateFile(toSignDocumentPath));
        if (StringUtils.isEmpty(certPath)) {
            throw new EDCIException(ErrorCode.LOCAL_CERTIFICATE_NOT_DEFINED, EDCIMessageKeys.Exception.DSS.LOCAL_CERTIFICATE_NOT_DEFINED);
        }
        SignatureTokenConnection signatureTokenConnection = this.getDssedciCertificateService().getCertificateSignatureToken(certPath, password);
        return this.tokenSignXMLDocument(toSignDocument, signatureTokenConnection, xpathLocation, doRefresh);
    }

    /**
     * Sign an XML document, using information from nexu and provided signature level
     *
     * @param signatureNexuDTO    information from nexu after signature form treatment
     * @param filePath            the file to be signed
     * @param xPathLocationString signature location inside xml
     * @param signatureLevel      the signature level
     * @return
     */
    public DSSDocument signDocument(SignatureNexuDTO signatureNexuDTO, String filePath, String xPathLocationString, SignatureLevel signatureLevel) {
        DSSTimeStampedSignatureDocumentForm form = this.createTimeStampedSignatureDocumentForm(signatureNexuDTO, filePath, signatureLevel);
        return this.signDocument(form, xPathLocationString, DigestAlgorithm.SHA256);
    }

    /**
     * Gets data to be signed for the signature, used in the first step of nexu sealing
     *
     * @param signatureParametersDTO information from nexu
     * @param filePath               path of the file to sign
     * @param xPathLocation          the xpathLocation where the signature is to be inserted
     * @param onlyQseals
     * @param signatureLevel
     * @return the to be signed data, for frontend use
     */

    public DSSEDCIToBeSignedDTO getTimeStampedDataToSign(SignatureParametersDTO signatureParametersDTO, String filePath, String xPathLocation, boolean onlyQseals, SignatureLevel signatureLevel) {
        DSSTimeStampedSignatureDocumentForm dssTimeStampedSignatureDocumentForm = this.createTimeStampedSignatureDocumentForm(signatureParametersDTO, filePath, xPathLocation, signatureLevel);
        ToBeSigned toBeSigned = this.getDataToSign(dssTimeStampedSignatureDocumentForm, onlyQseals, xPathLocation);
        DSSEDCIToBeSignedDTO dssedciToBeSignedDTO = new DSSEDCIToBeSignedDTO(toBeSigned);
        dssedciToBeSignedDTO.setSignatureDocumentForm(dssTimeStampedSignatureDocumentForm);
        dssedciToBeSignedDTO.setSigningDate(dssTimeStampedSignatureDocumentForm.getSigningDate());
        return dssedciToBeSignedDTO;
    }


    /**
     * Signs the input document with a XaDES service and the FIRST key of the given SignatureTokenConnection and returns the signed document
     *
     * @param toSignDocument XML document to be signed
     * @param signingToken   XML signed document
     * @return A signed XML document
     */
    private DSSDocument tokenSignXMLDocument(DSSDocument toSignDocument, SignatureTokenConnection signingToken, String xPathLocation, boolean doRefresh) {
        //Check configuration parameters
        SignatureLevel signatureLevel = null;
        try {
            signatureLevel = SignatureLevel.valueByName(this.getiConfigService().getString(EDCIConfig.DSS.SIGNATURE_LEVEL));
        } catch (IllegalArgumentException e) {
            throw new EDCIException(EDCIMessageKeys.Exception.DSS.INVALID_SIGNATURE_LEVEL);
        }
        //if xPath is not null, register namespaces
        if (xPathLocation != null) {
            this.registerNamespaces();
        }
        DSSPrivateKeyEntry privateKey = signingToken.getKeys().get(0);
        // Preparing parameters for the XAdES signature
        XAdESSignatureParameters parameters = new XAdESSignatureParameters();
        parameters.setXPathLocationString(xPathLocation);
        parameters.setSignatureLevel(signatureLevel);
        parameters.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        parameters.setSigningCertificate(privateKey.getCertificate());
        parameters.setCertificateChain(privateKey.getCertificateChain());
        //if required, refresh validation job
        if (doRefresh) this.getValidationJob().onlineRefresh();
        // Create XAdES service for signature
        XAdESService service = new XAdESService(this.getCertificateVerifier());
        service.setTspSource(this.getOnlineTSPSource());
        // Get the SignedInfo XML segment that need to be signed.
        ToBeSigned dataToSign = service.getDataToSign(toSignDocument, parameters);
        // This function obtains the signature value for signed information using the rivate key and specified algorithm
        SignatureValue signatureValue = signingToken.sign(dataToSign, parameters.getDigestAlgorithm(), privateKey);
        // Sign the document
        DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);
        return signedDocument;
    }

        /* TBD IN FUTURE RELEASE
 public static DSSDocument tokenSignPDFDocument(DSSDocument toSignDocument, SignatureTokenConnection signingToken) {
     DSSPrivateKeyEntry privateKey = signingToken.getKeys().get(0);
     // tag::demo[]
     // Preparing parameters for the PAdES signature
     PAdESSignatureParameters parameters = new PAdESSignatureParameters();
     // We choose the level of the signature (-B, -T, -LT, -LTA).
     parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
     // We set the digest algorithm to use with the signature algorithm. You must use the
     // same parameter when you invoke the method sign on the token. The default value is
     // SHA256
     parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
     // We set the signing certificate
     parameters.setSigningCertificate(privateKey.getCertificate());
     // We set the certificate chain
     parameters.setCertificateChain(privateKey.getCertificateChain());
     // Create common certificate verifier
     CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
     // Create PAdESService for signature
     PAdESService eu.europa.ec.empl.edci.dss.service = new PAdESService(commonCertificateVerifier);
     // Get the SignedInfo segment that need to be signed.
     ToBeSigned dataToSign = eu.europa.ec.empl.edci.dss.service.getDataToSign(toSignDocument, parameters);
     // This function obtains the signature value for signed information using the
     // private key and specified algorithm
     DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
     SignatureValue signatureValue = signingToken.sign(dataToSign, digestAlgorithm, privateKey);
     // We invoke the xadesService to sign the document with the signature value obtained in
     // the previous step.
     DSSDocument signedDocument = eu.europa.ec.empl.edci.dss.service.signDocument(toSignDocument, parameters, signatureValue);
     return signedDocument;
 }*/
    /*public SignatureBytesDTO generateSignatureBytes(SignatureParametersDTO signatureParametersDTO, String filePath) {


    }*/

    /**
     * Creates a document form for the Signature itself, used in the second step of signing through nexu
     *
     * @param signatureNexuDTO the signature information from nexu
     * @param filePath         the credential to generate the form form
     * @param signatureLevel   the signature level
     * @return the document form
     */
    private DSSTimeStampedSignatureDocumentForm createTimeStampedSignatureDocumentForm(SignatureNexuDTO signatureNexuDTO, String filePath, SignatureLevel signatureLevel) {
        DSSTimeStampedSignatureDocumentForm form = (DSSTimeStampedSignatureDocumentForm) this.createBaseSignatureDocumentForm(filePath
                , signatureLevel
                , DigestAlgorithm.forName(signatureNexuDTO.getResponse().getSignatureAlgorithm().split("_")[1])
                , signatureNexuDTO.getResponse().getCertificate()
                , signatureNexuDTO.getResponse().getCertificateChain()
                , this.getEncryptionAlgorithm());
        form.setBase64SignatureValue(signatureNexuDTO.getResponse().getSignatureValue());
        form.setDocumentToSign(this.getEdciFileService().getOrCreateFile(filePath));
        form.setSigningDate(signatureNexuDTO.getDate());
        if (signatureNexuDTO.getDssTimestampDTO() != null) {
            form.setContentTimestamp(new DSSTimestamp(signatureNexuDTO.getDssTimestampDTO().getBase64Timestamp(), signatureNexuDTO.getDssTimestampDTO().getCanonicalizationMethod(), signatureNexuDTO.getDssTimestampDTO().getType()));
        }
        return form;
    }

    /**
     * Creates a document form for the signature bytes, used in the first step of signing through nexu
     *
     * @param signatureParametersDTO the certificate information from nexu
     * @param filePath               the credential to generate the form
     * @param signatureLevel         the signature level
     * @return
     */
    private DSSTimeStampedSignatureDocumentForm createSignatureBytesDocumentForm(SignatureParametersDTO signatureParametersDTO, String filePath, SignatureLevel signatureLevel) {
        DSSTimeStampedSignatureDocumentForm form = this.createBaseSignatureDocumentForm(filePath
                , signatureLevel
                , this.getDigestAlgorithm()
                , signatureParametersDTO.getResponse().getCertificate()
                , signatureParametersDTO.getResponse().getCertificateChain()
                , this.getEncryptionAlgorithm());
        form.setContainerType(null);
        Date signingDate = new Date();
        form.setSigningDate(signingDate);
        form.setAddContentTimestamp(true);
        form.setDocumentToSign(this.getEdciFileService().getOrCreateFile(filePath));
        return form;
    }

    /**
     * Base Form Creator for XaDES documents (XML)
     *
     * @param filePath            the file path
     * @param signatureLevel      the signature level
     * @param digestAlgorithm     the digest algorithm
     * @param base64Cert          the cert  in a base64 string
     * @param base64CertChain     the cert chain in a base64 string
     * @param encryptionAlgorithm the encryption algorithm
     * @return
     */
    private DSSTimeStampedSignatureDocumentForm createBaseSignatureDocumentForm(String filePath, SignatureLevel signatureLevel, DigestAlgorithm digestAlgorithm, String base64Cert, List<String> base64CertChain, EncryptionAlgorithm encryptionAlgorithm) {
        DSSTimeStampedSignatureDocumentForm form = new DSSTimeStampedSignatureDocumentForm();
        form.setSignatureForm(SignatureForm.XAdES);
        form.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        form.setSignatureLevel(signatureLevel);
        form.setDigestAlgorithm(digestAlgorithm);
        form.setBase64Certificate(base64Cert);
        form.setBase64CertificateChain(base64CertChain);
        form.setEncryptionAlgorithm(encryptionAlgorithm);
        form.setDocumentToSign(this.getEdciFileService().getOrCreateFile(filePath));
        return form;
    }

    /**
     * Creates a document form for the signature
     *
     * @param signatureParametersDTO information from nexu
     * @param filePath               the path of the file to sign
     * @param xPathLocation          the xpathLocation where the signature is to be inserted
     * @param signatureLevel         the Signature level
     * @return the TimpeStamped signature form
     */
    private DSSTimeStampedSignatureDocumentForm createTimeStampedSignatureDocumentForm(SignatureParametersDTO signatureParametersDTO, String filePath, String xPathLocation, SignatureLevel signatureLevel) {
        DSSTimeStampedSignatureDocumentForm dssTimeStampedSignatureDocumentForm = this.createSignatureBytesDocumentForm(signatureParametersDTO, filePath, signatureLevel);
        TimestampToken timeStampToken = this.getContentTimestamp(dssTimeStampedSignatureDocumentForm, xPathLocation);
        DSSTimestamp dssTimestamp = DSSSignatureUtils.fromTimestampToken(timeStampToken);
        dssTimeStampedSignatureDocumentForm.setContentTimestamp(dssTimestamp);
        return dssTimeStampedSignatureDocumentForm;
    }


    /**
     * Sign document using Nexu signature form
     *
     * @param form                the signature form
     * @param xPathLocationString the signature location in the XML
     * @param digestAlgorithm     the digestAlgorithm to be used
     * @return the signed document
     * @throws DSSException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private DSSDocument signDocument(DSSSignatureDocumentForm form, String xPathLocationString, DigestAlgorithm digestAlgorithm) throws DSSException {
        logger.debug("Start signDocument with one document");
        DocumentSignatureService service = getSignatureService(form.getContainerType(), form.getSignatureForm());

        AbstractSignatureParameters parameters = fillParameters(form, xPathLocationString);

        DSSDocument signedDocument = null;

        DSSDocument toSignDocument = DSSSignatureUtils.toDSSDocument(form.getDocumentToSign());
        SignatureAlgorithm sigAlgorithm = SignatureAlgorithm.getAlgorithm(form.getEncryptionAlgorithm(), digestAlgorithm);
        SignatureValue signatureValue = new SignatureValue(sigAlgorithm, Utils.fromBase64(form.getBase64SignatureValue()));
        signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);

        logger.debug("End signDocument with one document");
        return signedDocument;
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private ToBeSigned getDataToSign(DSSSignatureDocumentForm form, boolean onlyQseals, String xPathLocationString) {
        logger.debug("Start getDataToSign with one document");

        // Check if qualified
        DSSEDCIValidationService dssValidationUtils = new DSSEDCIValidationService();
        CertificateQualification qualification = dssValidationUtils.getCertificateQualification(form.getBase64Certificate(), certificateVerifier);

        logger.info("Certificate information: readable=" + qualification.getReadable() + ", label="
                + qualification.getLabel() + ", type=" + qualification.getType() + ", isForEseal="
                + qualification.isForEseal() + ", isForEsig=" + qualification.isForEsig() + ", isQc="
                + qualification.isQc() + ", isQscd=" + qualification.isQscd());

        if (onlyQseals && !qualification.getType().equals(CertificateType.ESEAL)) {
            throw new EDCIException(EDCIMessageKeys.Exception.DSS.CERTIFICATE_NOT_QSEAL_ERROR);
        }

        DocumentSignatureService service = getSignatureService(form.getContainerType(), form.getSignatureForm());
        AbstractSignatureParameters parameters = fillParameters(form, xPathLocationString);

        ToBeSigned toBeSigned = null;
        try {
            registerNamespaces();
            DSSDocument toSignDocument = DSSSignatureUtils.toDSSDocument(form.getDocumentToSign());
            toBeSigned = service.getDataToSign(toSignDocument, parameters);
        } catch (Exception e) {
            logger.debug("error in getDataToSign");
            throw new EDCIException(e).addDescription("Unable to execute getDataToSign");
        }
        logger.debug("End getDataToSign with one document");


        return toBeSigned;
    }

    private DocumentSignatureService getSignatureService(ASiCContainerType containerType, SignatureForm signatureForm) {
        logger.debug("Start getSignatureService with signatureForm: " + signatureForm);
        DocumentSignatureService service = null;
        if (containerType != null) {
            service = (DocumentSignatureService) getASiCSignatureService(signatureForm);
        } else {
            switch (signatureForm) {
                case CAdES:
                    service = getCAdESSignatureService();
                    break;
                case PAdES:
                    service = getPAdESSignatureService();
                    break;
                case XAdES:
                    logger.debug("Case: XADES calling getXAdESSignatureService... ");
                    service = getXAdESSignatureService();
                    logger.debug("Case: XADES getXAdESSignatureService OK");
                    service.setTspSource(onlineTSPSource);
                    logger.debug("Case: XADES setting onlineTSPSource " + onlineTSPSource != null);
                    break;
                default:
                    logger.error("Unknow signature form : " + signatureForm);
            }
        }
        return service;
    }

    private AbstractSignatureParameters fillParameters(DSSSignatureDocumentForm form, String xPathLocationString) {
        AbstractSignatureParameters parameters = getSignatureParameters(form.getContainerType(), form.getSignatureForm(), xPathLocationString);
        parameters.setSignaturePackaging(form.getSignaturePackaging());

        fillParameters(parameters, form);

        return parameters;
    }

    private void fillParameters(AbstractSignatureParameters parameters, AbstractSignatureForm form) {

        logger.debug("Calling fillParameters...");
        parameters.setSignatureLevel(form.getSignatureLevel());
        logger.debug("fillParameters - setSignatureLevel OK");
        parameters.setDigestAlgorithm(DigestAlgorithm.forName(this.getiConfigService().getString(EDCIConfig.DSS.DIGEST_ALGORITHM_NAME)));
        logger.debug("fillParameters - setDigestAlgorithm OK");
        // parameters.setEncryptionAlgorithm(form.getEncryptionAlgorithm()); retrieved from certificate
        parameters.bLevel().setSigningDate(form.getSigningDate());
        logger.debug("fillParameters - setSigningDate OK");
        //parameters.setSignWithExpiredCertificate(form.isSignWithExpiredCertificate());

        if (form.getContentTimestamp() != null) {
            parameters.setContentTimestamps(Arrays.asList(DSSSignatureUtils.toTimestampToken(form.getContentTimestamp())));
            logger.debug("fillParameters - setContentTimestamps OK");
        }

        CertificateToken signingCertificate = DSSUtils.loadCertificateFromBase64EncodedString(form.getBase64Certificate());
        parameters.setSigningCertificate(signingCertificate);
        logger.debug("fillParameters - signingCertificate OK");

        List<String> base64CertificateChain = form.getBase64CertificateChain();
        if (Utils.isCollectionNotEmpty(base64CertificateChain)) {
            List<CertificateToken> certificateChain = new LinkedList<CertificateToken>();
            for (String base64Certificate : base64CertificateChain) {
                certificateChain.add(DSSUtils.loadCertificateFromBase64EncodedString(base64Certificate));
            }
            parameters.setCertificateChain(certificateChain);
        }
        logger.debug("fillParameters - base64CertificateChain OK ");
    }

    @SuppressWarnings("rawtypes")
    private MultipleDocumentsSignatureService getASiCSignatureService(SignatureForm signatureForm) {
        MultipleDocumentsSignatureService service = null;
        switch (signatureForm) {
            case CAdES:
                service = getASiCWithCAdESService();
                break;
            case XAdES:
                service = getASiCWithXAdESService();
                break;
            default:
                logger.error("Unknow signature form : " + signatureForm);
        }
        return service;
    }

    private AbstractSignatureParameters getASiCSignatureParameters(ASiCContainerType containerType, SignatureForm signatureForm) {
        AbstractSignatureParameters parameters = null;
        switch (signatureForm) {
            case CAdES:
                ASiCWithCAdESSignatureParameters asicCadesParams = new ASiCWithCAdESSignatureParameters();
                asicCadesParams.aSiC().setContainerType(containerType);
                parameters = asicCadesParams;
                break;
            case XAdES:
                ASiCWithXAdESSignatureParameters asicXadesParams = new ASiCWithXAdESSignatureParameters();
                asicXadesParams.aSiC().setContainerType(containerType);
                parameters = asicXadesParams;
                break;
            default:
                logger.error("Unknow signature form for ASiC container: " + signatureForm);
        }
        return parameters;
    }

    private AbstractSignatureParameters getSignatureParameters(ASiCContainerType containerType, SignatureForm signatureForm, String xPathLocationString) {
        logger.debug("Calling getSignatureParameters with xPathLocationString " + xPathLocationString);
        AbstractSignatureParameters parameters = null;
        if (containerType != null) {
            parameters = getASiCSignatureParameters(containerType, signatureForm);
        } else {
            switch (signatureForm) {
                case CAdES:
                    parameters = new CAdESSignatureParameters();
                    break;
                case PAdES:
                    PAdESSignatureParameters padesParams = new PAdESSignatureParameters();
                    //padesParams.setSignatureSize(9472 * 2); // double reserved space for signature
                    parameters = padesParams;
                    break;
                case XAdES:
                    parameters = new XAdESSignatureParameters();
                    ((XAdESSignatureParameters) parameters).setXPathLocationString(xPathLocationString);
                    break;
                default:
                    logger.error("Unknow signature form : " + signatureForm);
            }
        }
        logger.debug("Called getSignatureParameters OK");
        return parameters;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private TimestampToken getContentTimestamp(DSSSignatureDocumentForm form, String xPathLocationString) {
        logger.debug("Start getContentTimestamp with one document");

        DocumentSignatureService service = getSignatureService(form.getContainerType(), form.getSignatureForm());
        logger.debug("Accquired signature service : {}", () -> service != null);
        AbstractSignatureParameters parameters = fillParameters(form, xPathLocationString);
        logger.debug("Accquired signature parameters : {}", () -> parameters != null);
        DSSDocument toSignDocument = DSSSignatureUtils.toDSSDocument(form.getDocumentToSign());
        logger.debug("Transformed to DSS Document : {}", () -> toSignDocument != null);

        TimestampToken contentTimestamp = service.getContentTimestamp(toSignDocument, parameters);

        logger.debug("End getContentTimestamp with one document");
        return contentTimestamp;
    }


    private void registerNamespaces() {
        DomUtils.registerNamespace(new DSSNamespace("http://data.europa.eu/snb/vp", "vpp"));
        DomUtils.registerNamespace(new DSSNamespace("http://data.europa.eu/snb", "eup"));
        DomUtils.registerNamespace(new DSSNamespace("http://data.europa.eu/europass/model/credentials/w3c#", "cred"));
    }

    private DigestAlgorithm getDigestAlgorithm() {
        String digestAlgorightmName = this.getiConfigService().getString(EDCIConfig.DSS.DIGEST_ALGORITHM, EDCIConfig.Defaults.DIGEST_ALGORITHM);
        return DigestAlgorithm.forName(digestAlgorightmName);
    }

    private EncryptionAlgorithm getEncryptionAlgorithm() {
        String encryptionAlgorithmName = this.getiConfigService().getString(EDCIConfig.DSS.ENCRYPTION_ALGORITHM, EDCIConfig.Defaults.ENCRYPTION_ALGORITHM);
        return EncryptionAlgorithm.forName(encryptionAlgorithmName);
    }


    /**
     * Checks if the XMl is Signed. This does not check for the validity of the signature itself
     *
     * @param europassCredentialXML the credential
     * @return true if valid, false otherwise
     * @throws ParserConfigurationException error parsing
     * @throws SAXException                 error parsing
     * @throws IOException                  error parsing
     */
    public boolean isCredentialSigned(byte[] europassCredentialXML) throws
            ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setNamespaceAware(true);
        DocumentBuilder parser = factory.newDocumentBuilder();

        Document document = parser.parse(new ByteArrayInputStream(europassCredentialXML));

        NodeList numCredentials = document.getElementsByTagNameNS("*", "europassCredential");
        NodeList credentialsSigned = document.getElementsByTagNameNS("*", "Signature");

        return credentialsSigned.getLength() > 0 && numCredentials.getLength() == credentialsSigned.getLength();

    }

    private XAdESService getXAdESSignatureService() {

        return new XAdESService(certificateVerifier);
    }

    private CAdESService getCAdESSignatureService() {

        return new CAdESService(certificateVerifier);
    }

    private PAdESService getPAdESSignatureService() {

        return new PAdESService(certificateVerifier);
    }

    private ASiCWithXAdESService getASiCWithXAdESService() {
        return new ASiCWithXAdESService(certificateVerifier);
    }

    private ASiCWithCAdESService getASiCWithCAdESService() {
        return new ASiCWithCAdESService(certificateVerifier);
    }

    public SignatureLevel getSignatureLevel() {
        String signatureLevelName = EDCIConfig.Defaults.SIGNATURE_LEVEL;
        return SignatureLevel.valueByName(signatureLevelName);
    }

    public OnlineTSPSource getOnlineTSPSource() {
        return onlineTSPSource;
    }

    public void setOnlineTSPSource(OnlineTSPSource onlineTSPSource) {
        this.onlineTSPSource = onlineTSPSource;
    }

    public RevocationSource<CRL> getCrlSource() {
        return crlSource;
    }

    public void setCrlSource(RevocationSource<CRL> crlSource) {
        this.crlSource = crlSource;
    }

    public RevocationSource<OCSP> getOcspSource() {
        return ocspSource;
    }

    public void setOcspSource(RevocationSource<OCSP> ocspSource) {
        this.ocspSource = ocspSource;
    }

    public TLValidationJob getValidationJob() {
        return validationJob;
    }

    public void setValidationJob(TLValidationJob validationJob) {
        this.validationJob = validationJob;
    }

    public IConfigService getiConfigService() {
        return iConfigService;
    }

    public void setiConfigService(IConfigService iConfigService) {
        this.iConfigService = iConfigService;
    }

    public DataLoader getDataLoader() {
        return dataLoader;
    }

    public void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public CertificateVerifier getCertificateVerifier() {
        return certificateVerifier;
    }

    public void setCertificateVerifier(CertificateVerifier certificateVerifier) {
        this.certificateVerifier = certificateVerifier;
    }

    public DSSEDCICertificateService getDssedciCertificateService() {
        return dssedciCertificateService;
    }

    public void setDssedciCertificateService(DSSEDCICertificateService dssedciCertificateService) {
        this.dssedciCertificateService = dssedciCertificateService;
    }

    public EDCIFileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(EDCIFileService edciFileService) {
        this.edciFileService = edciFileService;
    }
}