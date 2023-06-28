package eu.europa.ec.empl.edci.dss.service.signature;

import eu.europa.ec.empl.edci.dss.config.ESealCoreConfigService;
import eu.europa.ec.empl.edci.dss.constants.ESealConfig;
import eu.europa.ec.empl.edci.dss.constants.ESealMessageKeys;
import eu.europa.ec.empl.edci.dss.constants.ErrorCode;
import eu.europa.ec.empl.edci.dss.exception.ESealException;
import eu.europa.ec.empl.edci.dss.model.signature.AbstractSignatureForm;
import eu.europa.ec.empl.edci.dss.service.certificate.ESealCertificateService;
import eu.europa.ec.empl.edci.dss.service.messages.FileService;
import eu.europa.ec.empl.edci.dss.util.DSSSignatureUtils;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.jades.signature.JAdESService;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CertificateVerifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class JadesSignService implements ISignService {

    private static final Logger logger = LogManager.getLogger(JadesSignService.class);

    @Autowired
    private FileService edciFileService;

    @Autowired
    private OnlineTSPSource onlineTSPSource;

    @Autowired
    private ESealCoreConfigService configService;

    @Autowired
    private TLValidationJob validationJob;

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private ESealCertificateService dssedciCertificateService;

    /**
     * Sign a Json Document using local Certificate, uses the signature level configured at properties
     *
     * @param toSignDocumentPath document to be signed
     * @param certPath           certificate path
     * @param password           certificate password
     * @return the signed DSSDocument
     */
    @Override
    public DSSDocument signDocument(String toSignDocumentPath, String certPath, String password) {
        DSSDocument toSignDocument = new FileDocument(this.getEdciFileService().getOrCreateFile(toSignDocumentPath));
        if (StringUtils.isEmpty(certPath)) {
            throw new ESealException(ErrorCode.LOCAL_CERTIFICATE_NOT_DEFINED, ESealMessageKeys.Exception.ESeal.LOCAL_CERTIFICATE_NOT_DEFINED);
        }
        SignatureTokenConnection signatureTokenConnection = this.getDssedciCertificateService().getCertificateSignatureToken(certPath, password);
        return this.signTokenDocument(toSignDocument, signatureTokenConnection);
    }

    protected DSSDocument extendDocumentSignature(DSSDocument toSignDocument) {
        // Create JAdES service for signature
        JAdESSignatureParameters parameters = new JAdESSignatureParameters();
        parameters.setSignatureLevel(SignatureLevel.JAdES_BASELINE_LTA);
        parameters.setJwsSerializationType(
                JWSSerializationType.valueOf(getConfigService().getString(ESealConfig.Properties.JWS_SERIALIZATION_TYPE,
                        ESealConfig.Defaults.Jades.JWS_SERIALIZATION_TYPE)));

        JAdESService service = new JAdESService(this.getCertificateVerifier());
        service.setTspSource(this.getOnlineTSPSource());
        return service.extendDocument(toSignDocument, parameters);
    }

    /**
     * Signs the input document with a JaDES service and the FIRST key of the given SignatureTokenConnection and returns the signed document
     *
     * @param toSignDocument Json document to be signed
     * @param signingToken   Json signed document
     * @return A signed Json document
     */
    protected DSSDocument signTokenDocument(DSSDocument toSignDocument, SignatureTokenConnection signingToken) {

        //Check configuration parameters
        DSSPrivateKeyEntry privateKey = signingToken.getKeys().get(0);

        JAdESSignatureParameters parameters = (JAdESSignatureParameters) fillParameters(privateKey);

        if (getConfigService().getBoolean(ESealConfig.Properties.JOB_ONLINE_REFRESH,
                ESealConfig.Defaults.JOB_ONLINE_REFRESH)) {
            this.getValidationJob().onlineRefresh();
        }

        // Create JAdES service for signature
        JAdESService service = new JAdESService(this.getCertificateVerifier());
        service.setTspSource(this.getOnlineTSPSource());
        // Get the SignedInfo XML segment that need to be signed.
        ToBeSigned dataToSign = service.getDataToSign(toSignDocument, parameters);
        // This function obtains the signature value for signed information using the rivate key and specified algorithm
        SignatureValue signatureValue = signingToken.sign(dataToSign, parameters.getDigestAlgorithm(), privateKey);
        // Sign the document
        DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);
        return signedDocument;
    }

    /**
     * Returns the needed Signature parameters generated from the properties and a privateKey
     *
     * @param privateKey private certificate key
     * @return JADES signature parameters
     */
    @Override
    public JAdESSignatureParameters fillParameters(DSSPrivateKeyEntry privateKey) {

        JAdESSignatureParameters parameters = new JAdESSignatureParameters();
        logger.debug("Calling fillParameters with DSSPrivateKeyEntry...");
        parameters.setSignatureLevel(SignatureLevel.valueByName(getConfigService().getString(ESealConfig.Properties.SIGNATURE_LEVEL,
                ESealConfig.Defaults.Jades.SIGNATURE_LEVEL)));
        parameters.setDigestAlgorithm(
                DigestAlgorithm.forName(getConfigService().getString(ESealConfig.Properties.DIGEST_ALGORITHM,
                        ESealConfig.Defaults.DIGEST_ALGORITHM_SIGN)));

        parameters.setSigningCertificate(privateKey.getCertificate());
        parameters.setCertificateChain(privateKey.getCertificateChain());
        parameters.setBase64UrlEncodedPayload(getConfigService().getBoolean(ESealConfig.Properties.PAYLOAD_BASE64,
                ESealConfig.Defaults.Jades.PAYLOAD_BASE64));

        parameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
        ((JAdESSignatureParameters)parameters).setJwsSerializationType(
                JWSSerializationType.valueOf(getConfigService().getString(ESealConfig.Properties.JWS_SERIALIZATION_TYPE,
                        ESealConfig.Defaults.Jades.JWS_SERIALIZATION_TYPE)));
        ((JAdESSignatureParameters)parameters).setBase64UrlEncodedPayload(false);
        ((JAdESSignatureParameters)parameters).setBase64UrlEncodedEtsiUComponents(true);

//        parameters.setSigDMechanism(SigDMechanism.NO_SIG_D); //Only used with Detached packaging
        logger.debug("fillParameters(DSSPrivateKeyEntry) - OK ");

        return parameters;
    }

    /**
     * Returns the needed Signature parameters generated from the properties and a given signature form
     *
     * @param form signature form
     * @return JADES signature parameters
     */
    @Override
    public JAdESSignatureParameters fillParameters(AbstractSignatureForm form) {

        JAdESSignatureParameters parameters = new JAdESSignatureParameters();
        logger.debug("Calling fillParameters with AbstractSignatureForm...");
        parameters.setSignaturePackaging(SignaturePackaging.valueOf(getConfigService().getString(ESealConfig.Properties.SIGNATURE_PACKAGING,
                ESealConfig.Defaults.Jades.SIGNATURE_PACKAGING)));
        parameters.setJwsSerializationType(JWSSerializationType.valueOf(getConfigService().getString(ESealConfig.Properties.JWS_SERIALIZATION_TYPE,
                ESealConfig.Defaults.Jades.JWS_SERIALIZATION_TYPE)));
        parameters.setBase64UrlEncodedPayload(getConfigService().getBoolean(ESealConfig.Properties.PAYLOAD_BASE64,
                ESealConfig.Defaults.Jades.PAYLOAD_BASE64));
        parameters.setSignatureLevel(form.getSignatureLevel());
        parameters.setDigestAlgorithm(
                DigestAlgorithm.forName(getConfigService().getString(ESealConfig.Properties.DIGEST_ALGORITHM,
                        ESealConfig.Defaults.DIGEST_ALGORITHM_SIGN)));
        parameters.bLevel().setSigningDate(form.getSigningDate());

        if (form.getContentTimestamp() != null) {
            parameters.setContentTimestamps(Arrays.asList(DSSSignatureUtils.toTimestampToken(form.getContentTimestamp())));
        }

        CertificateToken signingCertificate = DSSUtils.loadCertificateFromBase64EncodedString(form.getBase64Certificate());
        parameters.setSigningCertificate(signingCertificate);

        List<String> base64CertificateChain = form.getBase64CertificateChain();
        if (Utils.isCollectionNotEmpty(base64CertificateChain)) {
            List<CertificateToken> certificateChain = new LinkedList<CertificateToken>();
            for (String base64Certificate : base64CertificateChain) {
                certificateChain.add(DSSUtils.loadCertificateFromBase64EncodedString(base64Certificate));
            }
            parameters.setCertificateChain(certificateChain);
        }
        logger.debug("fillParameters(AbstractSignatureForm) - OK ");

        return parameters;
    }

    @Override
    public JAdESService getSignatureService() {
        return new JAdESService(certificateVerifier);
    }

    @Override
    public JAdESService getSignatureService(TSPSource onlineTSPSource) {
        JAdESService service = new JAdESService(getCertificateVerifier());
        service.setTspSource(onlineTSPSource);
        return service;
    }

    @Override
    public SignatureForm getSignatureForm() {
        return SignatureForm.valueOf(getConfigService().getString(ESealConfig.Properties.SIGNATURE_FORM,
                ESealConfig.Defaults.Jades.SIGNATURE_FORM));
    }

    @Override
    public SignatureLevel getSignatureLevel() {
        return SignatureLevel.valueByName(getConfigService().getString(ESealConfig.Properties.SIGNATURE_LEVEL,
                ESealConfig.Defaults.Jades.SIGNATURE_LEVEL));
    }

    @Override
    public SignaturePackaging getSignaturePackaging() {
        return SignaturePackaging.valueOf(getConfigService().getString(ESealConfig.Properties.SIGNATURE_PACKAGING,
                ESealConfig.Defaults.Jades.SIGNATURE_PACKAGING));
    }

    public OnlineTSPSource getOnlineTSPSource() {
        return onlineTSPSource;
    }

    public void setOnlineTSPSource(OnlineTSPSource onlineTSPSource) {
        this.onlineTSPSource = onlineTSPSource;
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
}