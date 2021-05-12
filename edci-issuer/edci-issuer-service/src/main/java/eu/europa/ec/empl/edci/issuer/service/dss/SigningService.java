package eu.europa.ec.empl.edci.issuer.service.dss;

import eu.europa.ec.empl.edci.dss.validation.DSSValidationUtils;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerConstants;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DomUtils;
import eu.europa.esig.dss.asic.cades.ASiCWithCAdESSignatureParameters;
import eu.europa.esig.dss.asic.cades.signature.ASiCWithCAdESService;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESService;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.signature.MultipleDocumentsSignatureService;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class SigningService {

    private static final Logger logger = LoggerFactory.getLogger(SigningService.class);

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private ProxyConfig proxyConfig;

    @Autowired
    private OnlineTSPSource onlineTSPSource;

    @Autowired
    private IssuerConfigService issuerConfigService;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ToBeSigned getDataToSign(DSSSignatureDocumentForm form, boolean onlyQseals, String xPathLocationString) {
        logger.info("Start getDataToSign with one document");

        // Check if qualified
        DSSValidationUtils dssValidationUtils = new DSSValidationUtils();
        CertificateQualification qualification = dssValidationUtils.getCertificateQualification(form.getBase64Certificate(), certificateVerifier);
        if (onlyQseals && !qualification.toString().startsWith("QCERT_FOR_ESEAL")) {
            throw new EDCIException("certificate.not.qseal.error");
        }

        DocumentSignatureService service = getSignatureService(form.getContainerType(), form.getSignatureForm());

        AbstractSignatureParameters parameters = fillParameters(form, xPathLocationString);

        ToBeSigned toBeSigned = null;
        try {
            registerNamespaces();
            DSSDocument toSignDocument = DSSSignatureUtils.toDSSDocument(form.getDocumentToSign());
            toBeSigned = service.getDataToSign(toSignDocument, parameters);
        } catch (Exception e) {
            throw new EDCIException(e).addDescription("Unable to execute getDataToSign");
        }
        logger.info("End getDataToSign with one document");


        return toBeSigned;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public TimestampToken getContentTimestamp(DSSSignatureDocumentForm form, String xPathLocationString) {
        logger.info("Start getContentTimestamp with one document");

        DocumentSignatureService service = getSignatureService(form.getContainerType(), form.getSignatureForm());
        AbstractSignatureParameters parameters = fillParameters(form, xPathLocationString);
        DSSDocument toSignDocument = DSSSignatureUtils.toDSSDocument(form.getDocumentToSign());

        TimestampToken contentTimestamp = service.getContentTimestamp(toSignDocument, parameters);

        logger.info("End getContentTimestamp with one document");
        return contentTimestamp;
    }

    private AbstractSignatureParameters fillParameters(DSSSignatureDocumentForm form, String xPathLocationString) {
        AbstractSignatureParameters parameters = getSignatureParameters(form.getContainerType(), form.getSignatureForm(), xPathLocationString);
        parameters.setSignaturePackaging(form.getSignaturePackaging());

        fillParameters(parameters, form);

        return parameters;
    }

    private void fillParameters(AbstractSignatureParameters parameters, AbstractSignatureForm form) {
        parameters.setSignatureLevel(form.getSignatureLevel());
        parameters.setDigestAlgorithm(DigestAlgorithm.forName(issuerConfigService.getString(EDCIIssuerConstants.CONFIG_PROPERTY_DIGEST_ALGORITHM_NAME)));
        // parameters.setEncryptionAlgorithm(form.getEncryptionAlgorithm()); retrieved from certificate
        parameters.bLevel().setSigningDate(form.getSigningDate());

        //parameters.setSignWithExpiredCertificate(form.isSignWithExpiredCertificate());

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
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public DSSDocument signDocument(DSSSignatureDocumentForm form, String xPathLocationString) throws DSSException {
        logger.info("Start signDocument with one document");
        DocumentSignatureService service = getSignatureService(form.getContainerType(), form.getSignatureForm());

        AbstractSignatureParameters parameters = fillParameters(form, xPathLocationString);

        DSSDocument signedDocument = null;

        DSSDocument toSignDocument = DSSSignatureUtils.toDSSDocument(form.getDocumentToSign());
        SignatureAlgorithm sigAlgorithm = SignatureAlgorithm.getAlgorithm(form.getEncryptionAlgorithm(), DigestAlgorithm.forName(issuerConfigService.getString(EDCIIssuerConstants.CONFIG_PROPERTY_DIGEST_ALGORITHM_NAME)));
        SignatureValue signatureValue = new SignatureValue(sigAlgorithm, Utils.fromBase64(form.getBase64SignatureValue()));
        signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);

        logger.info("End signDocument with one document");
        return signedDocument;
    }

    @SuppressWarnings("rawtypes")
    private DocumentSignatureService getSignatureService(ASiCContainerType containerType, SignatureForm signatureForm) {
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
                    service = getXAdESSignatureService();
                    service.setTspSource(onlineTSPSource);
                    break;
                default:
                    logger.error("Unknow signature form : " + signatureForm);
            }
        }
        return service;
    }

    private AbstractSignatureParameters getSignatureParameters(ASiCContainerType containerType, SignatureForm signatureForm, String xPathLocationString) {
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
                    padesParams.setSignatureSize(9472 * 2); // double reserved space for signature
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
        return parameters;
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

    private void registerNamespaces() {
        DomUtils.registerNamespace("vpp", "http://data.europa.eu/snb/vp");
        DomUtils.registerNamespace("eup", "http://data.europa.eu/snb");
        DomUtils.registerNamespace("cred", "http://data.europa.eu/europass/model/credentials/w3c#");
    }
}
