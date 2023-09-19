package eu.europa.ec.empl.edci.dss.service.signature;

import eu.europa.ec.empl.edci.dss.config.ESealCoreConfigService;
import eu.europa.ec.empl.edci.dss.constants.ESealConfig;
import eu.europa.ec.empl.edci.dss.constants.ESealMessageKeys;
import eu.europa.ec.empl.edci.dss.exception.ESealException;
import eu.europa.ec.empl.edci.dss.model.signature.*;
import eu.europa.ec.empl.edci.dss.service.certificate.ESealCertificateService;
import eu.europa.ec.empl.edci.dss.service.messages.FileService;
import eu.europa.ec.empl.edci.dss.service.validation.ESealValidationService;
import eu.europa.ec.empl.edci.dss.util.DSSSignatureUtils;
import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.model.*;
import eu.europa.esig.dss.model.x509.revocation.crl.CRL;
import eu.europa.esig.dss.model.x509.revocation.ocsp.OCSP;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.spi.x509.revocation.RevocationSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ESealSignService {

    private static final Logger logger = LogManager.getLogger(ESealSignService.class);

    @Autowired
    private FileService edciFileService;

    @Autowired
    private OnlineTSPSource onlineTSPSource;

    @Autowired
    private ESealCoreConfigService configService;

    @Autowired
    private RevocationSource<CRL> crlSource;

    @Autowired
    private RevocationSource<OCSP> ocspSource;

    @Autowired
    private TLValidationJob validationJob;

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private ESealCertificateService dssedciCertificateService;

    @Autowired
    private JadesSignService jsonSignService;

    @Autowired
    private XadesSignService xmlSignService;

    /**
     * Signs a Document using a local Certificate and the Default signature service and signature configuration
     *
     * @param toSignDocumentPath document to be signed
     * @param certPath           certificate path
     * @param password           certificate password
     * @return the signed DSSDocument
     */
    public DSSDocument signDocument(String toSignDocumentPath, String certPath, String password) {
        return getDefaultSignatureService().signDocument(toSignDocumentPath, certPath, password);
    }

    public DSSDocument extendDocumentSignature(byte[] signedDocument) {
        DSSDocument dssDocument = new InMemoryDocument(signedDocument, "temp.json", MimeType.JSON);
        return this.getJsonSignService().extendDocumentSignature(dssDocument);
    }

    public DSSDocument extendDocumentSignatureXML(byte[] signedDocument) {
        DSSDocument dssDocument = new InMemoryDocument(signedDocument, "temp.xml", MimeType.XML);
        return this.getXmlSignService().extendDocumentSignature(dssDocument);
    }

    /**
     * Generate the Signature Bytes for the provided file, this is used in combination of a Nexu frontend as the first step of signing.
     * The Default signature service and signature configuration is used.
     *
     * @param fileAbsolutePathList document to be signed
     * @param certificate          certificate
     * @param certificateChain     certificate chain
     * @return generated signature bytes
     */
    public SignatureBytesDTO getSignatureBytes(String fileAbsolutePathList, String certificate, List<String> certificateChain) {

        SignatureBytesDTO returnValue = null;
        try {

            //Generate Signing Bytes
            logger.debug("Getting TimeStamped DataToSign... ");
            DSSEDCIToBeSignedDTO dssedciToBeSignedDTO = getTimeStampedDataToSign(certificate, certificateChain, fileAbsolutePathList);
            logger.debug("EDCI To Be Signed DTO is null : " + dssedciToBeSignedDTO == null);
            //generate DSSTimestampDTO
            logger.debug("Getting Content Timestamp from signature... ");
            DSSTimestamp dssTimestamp = dssedciToBeSignedDTO.getSignatureDocumentForm().getContentTimestamp();
            DSSTimestampDTO dssTimestampDTO = new DSSTimestampDTO(dssTimestamp.getBase64Timestamp(), dssTimestamp.getCanonicalizationMethod(), dssTimestamp.getType());
            //Add the resulting signature bytes to the DTO list as a new SignatureBytesDTO based on ToBeSigned and DSSTimestamp data
            returnValue = new SignatureBytesDTO(DatatypeConverter.printBase64Binary(dssedciToBeSignedDTO.getBytes()), dssedciToBeSignedDTO.getSigningDate(), dssTimestampDTO);
        } catch (ESealException e) {
            logger.error("Error generating signature bytes for file " + fileAbsolutePathList, e);
            throw e;
        } catch (Exception e) {
            //Catch any unexpected exception
            logger.error("Error generating signature bytes for file " + fileAbsolutePathList, e);
            throw e;
        }

        return returnValue;
    }

    /**
     * Signs a Document using the information retrieved from Nexu after sending the signature bytes.
     *
     * @param filePath         document to be signed
     * @param signatureNexuDTO Information obtained from Nexu
     * @return a signed document, ready to be saved into a file.
     */
    public DSSDocument signDocument(String filePath, SignatureNexuDTO signatureNexuDTO) {

        DSSDocument signedDocument = null;
        try {
            //Sign the file
            DSSTimeStampedSignatureDocumentForm form = this.createTimeStampedSignatureDocumentForm(signatureNexuDTO, filePath);
            signedDocument = this.signDocument(form,
                    DigestAlgorithm.forName(getConfigService().getString(ESealConfig.Properties.DIGEST_ALGORITHM,
                            ESealConfig.Defaults.DIGEST_ALGORITHM_SIGN)));
        } catch (Exception e) {
            logger.error("Error signing credential " + signatureNexuDTO.getUuid(), e);
        }

        return signedDocument;

    }

    /**
     * Gets data to be signed for the signature, used in the first step of nexu sealing
     *
     * @param certificate      certificate
     * @param certificateChain certificate chain
     * @param filePath         path of the file to sign
     * @return the to be signed data, for frontend use
     */
    public DSSEDCIToBeSignedDTO getTimeStampedDataToSign(String certificate, List<String> certificateChain, String filePath) {
        DSSTimeStampedSignatureDocumentForm dssTimeStampedSignatureDocumentForm = this.createTimeStampedSignatureDocumentForm(certificate, certificateChain, filePath);
        ToBeSigned toBeSigned = this.getDataToSign(dssTimeStampedSignatureDocumentForm);
        DSSEDCIToBeSignedDTO dssedciToBeSignedDTO = new DSSEDCIToBeSignedDTO(toBeSigned);
        dssedciToBeSignedDTO.setSignatureDocumentForm(dssTimeStampedSignatureDocumentForm);
        dssedciToBeSignedDTO.setSigningDate(dssTimeStampedSignatureDocumentForm.getSigningDate());
        return dssedciToBeSignedDTO;
    }


    /**
     * Creates a document form for the Signature itself, used in the second step of signing through nexu
     *
     * @param signatureNexuDTO the signature information from nexu
     * @param filePath         the credential to generate the form form
     * @return the document form
     */
    private DSSTimeStampedSignatureDocumentForm createTimeStampedSignatureDocumentForm(SignatureNexuDTO signatureNexuDTO, String filePath) {
        DSSTimeStampedSignatureDocumentForm form = (DSSTimeStampedSignatureDocumentForm) this.createBaseSignatureDocumentForm(filePath
                , getDefaultSignatureService().getSignatureLevel()
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
     * @param certificate      certificate
     * @param certificateChain certificate chain
     * @param filePath         the credential to generate the form
     * @return
     */
    private DSSTimeStampedSignatureDocumentForm createSignatureBytesDocumentForm(String certificate, List<String> certificateChain, String filePath) {
        DSSTimeStampedSignatureDocumentForm form = createBaseSignatureDocumentForm(filePath
                , getDefaultSignatureService().getSignatureLevel()
                , this.getDigestAlgorithm()
                , certificate
                , certificateChain
                , this.getEncryptionAlgorithm());
        form.setContainerType(null);
        Date signingDate = new Date();
        form.setSigningDate(signingDate);
        form.setAddContentTimestamp(true);
        form.setDocumentToSign(this.getEdciFileService().getOrCreateFile(filePath));
        return form;
    }

    /**
     * Base Form Creator for documents
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
        form.setSignatureForm(getDefaultSignatureService().getSignatureForm());
        form.setSignaturePackaging(getDefaultSignatureService().getSignaturePackaging());
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
     * @param certificate      certificate
     * @param certificateChain certificate chain
     * @param filePath         the path of the file to sign
     * @return the TimpeStamped signature form
     */
    private DSSTimeStampedSignatureDocumentForm createTimeStampedSignatureDocumentForm(String certificate, List<String> certificateChain, String filePath) {
        DSSTimeStampedSignatureDocumentForm dssTimeStampedSignatureDocumentForm = this.createSignatureBytesDocumentForm(certificate, certificateChain, filePath);
        TimestampToken timeStampToken = this.getContentTimestamp(dssTimeStampedSignatureDocumentForm);
        DSSTimestamp dssTimestamp = DSSSignatureUtils.fromTimestampToken(timeStampToken);
        dssTimeStampedSignatureDocumentForm.setContentTimestamp(dssTimestamp);
        return dssTimeStampedSignatureDocumentForm;
    }


    /**
     * Sign document using Nexu signature form
     *
     * @param form            the signature form
     * @param digestAlgorithm the digestAlgorithm to be used
     * @return the signed document
     * @throws DSSException
     */
    private DSSDocument signDocument(DSSSignatureDocumentForm form, DigestAlgorithm digestAlgorithm) throws DSSException {
        logger.debug("Start signDocument with one document");
        ISignService signService = getSignatureService(form.getSignatureForm());
        DocumentSignatureService service = signService.getSignatureService(onlineTSPSource);

        AbstractSignatureParameters parameters = signService.fillParameters(form);

        DSSDocument signedDocument = null;

        DSSDocument toSignDocument = DSSSignatureUtils.toDSSDocument(form.getDocumentToSign());
        SignatureAlgorithm sigAlgorithm = SignatureAlgorithm.getAlgorithm(form.getEncryptionAlgorithm(), digestAlgorithm);
        SignatureValue signatureValue = new SignatureValue(sigAlgorithm, Utils.fromBase64(form.getBase64SignatureValue()));
        signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);

        logger.debug("End signDocument with one document");
        return signedDocument;
    }

    /**
     * Generates the bytes for the data to be signed
     *
     * @param form Signature configuration
     * @return To-be-signed byte array
     */
    private ToBeSigned getDataToSign(DSSSignatureDocumentForm form) {
        logger.debug("Start getDataToSign with one document");

        // Check if qualified
        ESealValidationService dssValidationUtils = new ESealValidationService();
        CertificateQualification qualification = dssValidationUtils.getCertificateQualification(form.getBase64Certificate(), certificateVerifier);

        logger.info("Certificate information: readable=" + qualification.getReadable() + ", label="
                + qualification.getLabel() + ", type=" + qualification.getType() + ", isForEseal="
                + qualification.isForEseal() + ", isForEsig=" + qualification.isForEsig() + ", isQc="
                + qualification.isQc() + ", isQscd=" + qualification.isQscd());

        String[] allowedSeals = getConfigService().getStringArray(ESealConfig.Properties.ESEAL_CQ_ALLOWED);

        boolean isSealAllowed = allowedSeals != null && allowedSeals.length > 0 ?
                Arrays.stream(allowedSeals).filter(s -> s.equalsIgnoreCase(qualification.name())).findAny().isPresent() :
                !qualification.getType().equals(CertificateType.ESEAL) && qualification.isQc();

        if (getConfigService().getBoolean(ESealConfig.Properties.ADV_QSEAL_ONLY, ESealConfig.Defaults.ADV_QSEAL_ONLY)
                && !isSealAllowed) {
            throw new ESealException(ESealMessageKeys.Exception.ESeal.CERTIFICATE_NOT_QSEAL_ERROR, qualification.getLabel()).addDescription("CertificateQualification - " + qualification.name());
        }

        DocumentSignatureService service = getSignatureService(form.getSignatureForm()).getSignatureService(onlineTSPSource);
        AbstractSignatureParameters parameters = getJsonSignService().fillParameters(form);

        ToBeSigned toBeSigned = null;
        try {
            DSSDocument toSignDocument = DSSSignatureUtils.toDSSDocument(form.getDocumentToSign());
            toBeSigned = service.getDataToSign(toSignDocument, parameters);
        } catch (Exception e) {
            logger.debug("error in getDataToSign");
            throw new ESealException(e).addDescription("Unable to execute getDataToSign");
        }
        logger.debug("End getDataToSign with one document");


        return toBeSigned;
    }

    private ISignService getDefaultSignatureService() {
        return getJsonSignService();
    }

    private ISignService getSignatureService(SignatureForm signatureForm) {
        logger.debug("Start getSignatureService with signatureForm: " + signatureForm);
        ISignService service = null;
        switch (signatureForm) {
            case JAdES:
                service = getJsonSignService();
                break;
            default:
                logger.error("Unknow signature form : " + signatureForm);
        }
        return service;
    }

    private TimestampToken getContentTimestamp(DSSSignatureDocumentForm form) {
        logger.debug("Start getContentTimestamp with one document");

        DocumentSignatureService service = getSignatureService(form.getSignatureForm()).getSignatureService(getOnlineTSPSource());
        logger.debug("Accquired signature service : {}", () -> service != null);
        AbstractSignatureParameters parameters = getJsonSignService().fillParameters(form);
        logger.debug("Accquired signature parameters : {}", () -> parameters != null);
        DSSDocument toSignDocument = DSSSignatureUtils.toDSSDocument(form.getDocumentToSign());
        logger.debug("Transformed to DSS Document : {}", () -> toSignDocument != null);

        TimestampToken contentTimestamp = service.getContentTimestamp(toSignDocument, parameters);

        logger.debug("End getContentTimestamp with one document");
        return contentTimestamp;
    }

    private DigestAlgorithm getDigestAlgorithm() {
        String digestAlgorightmName = this.getConfigService().getString(ESealConfig.Properties.DIGEST_ALGORITHM, ESealConfig.Defaults.DIGEST_ALGORITHM_SIGN);
        return DigestAlgorithm.forName(digestAlgorightmName);
    }

    private EncryptionAlgorithm getEncryptionAlgorithm() {
        String encryptionAlgorithmName = this.getConfigService().getString(ESealConfig.Properties.ENCRYPTION_ALGORITHM, ESealConfig.Defaults.ENCRYPTION_ALGORITHM);
        return EncryptionAlgorithm.forName(encryptionAlgorithmName);
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

    public ESealCoreConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ESealCoreConfigService configService) {
        this.configService = configService;
    }

    public CertificateVerifier getCertificateVerifier() {
        return certificateVerifier;
    }

    public void setCertificateVerifier(CertificateVerifier certificateVerifier) {
        this.certificateVerifier = certificateVerifier;
    }

    public ESealCertificateService getDssedciCertificateService() {
        return dssedciCertificateService;
    }

    public void setDssedciCertificateService(ESealCertificateService dssedciCertificateService) {
        this.dssedciCertificateService = dssedciCertificateService;
    }

    public FileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(FileService edciFileService) {
        this.edciFileService = edciFileService;
    }

    public JadesSignService getJsonSignService() {
        return jsonSignService;
    }

    public void setJsonSignService(JadesSignService jsonSignService) {
        this.jsonSignService = jsonSignService;
    }

    public XadesSignService getXmlSignService() {
        return xmlSignService;
    }

    public void setXmlSignService(XadesSignService xmlSignService) {
        this.xmlSignService = xmlSignService;
    }
}