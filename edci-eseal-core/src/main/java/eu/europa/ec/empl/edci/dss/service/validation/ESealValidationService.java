package eu.europa.ec.empl.edci.dss.service.validation;

import eu.europa.ec.empl.edci.dss.config.ESealCoreConfigService;
import eu.europa.ec.empl.edci.dss.constants.ESealConfig;
import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.validation.CertificateValidator;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.reports.CertificateReports;
import eu.europa.esig.dss.validation.reports.Reports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ESealValidationService {

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private ESealCoreConfigService configService;

    @Autowired
    @Qualifier("esealCommonsDataLoader")
    private CommonsDataLoader commonsDataLoader;

    @Autowired
    private JadesValidationService jsonValidationService;

    @Autowired
    private XadesValidationService xadesValidationService;

    public ESealValidationService() {
    }

    public boolean isSigned(byte[] jsonBytes) {
        return this.isSigned(jsonBytes, this.getCommonsDataLoader());
    }

    public boolean isSigned(byte[] jsonBytes, DataLoader dataLoader) {
        return getJsonValidationService().validateJson(jsonBytes, dataLoader,
                getConfigService().getBoolean(ESealConfig.Properties.JOB_ONLINE_REFRESH,
                        ESealConfig.Defaults.JOB_ONLINE_REFRESH)).getSimpleReport().getSignaturesCount() > 0;
    }

    public Reports validateJson(byte[] jsonBytes) {
        return this.validateJson(jsonBytes, this.getCommonsDataLoader());
    }

    public Reports validateJson(byte[] jsonBytes, DataLoader dataLoader) {
        return getJsonValidationService().validateJson(jsonBytes, dataLoader,
                getConfigService().getBoolean(ESealConfig.Properties.JOB_ONLINE_REFRESH,
                        ESealConfig.Defaults.JOB_ONLINE_REFRESH));
    }

    public Reports validateJson(String jsonFilePath) {
        return this.validateJson(jsonFilePath, this.getCommonsDataLoader());
    }

    public Reports validateJson(String jsonFilePath, DataLoader dataLoader) {
        return getJsonValidationService().validateJson(jsonFilePath, dataLoader,
                getConfigService().getBoolean(ESealConfig.Properties.JOB_ONLINE_REFRESH,
                        ESealConfig.Defaults.JOB_ONLINE_REFRESH));
    }

    public Reports validateXml(byte[] xmlBytes) {
        return getXadesValidationService().validateXml(xmlBytes,
                getConfigService().getBoolean(ESealConfig.Properties.JOB_ONLINE_REFRESH,
                        ESealConfig.Defaults.JOB_ONLINE_REFRESH));
    }


    public Reports validateXml(String xmlFilePath) {
        return getXadesValidationService().validateXml(xmlFilePath,
                getConfigService().getBoolean(ESealConfig.Properties.JOB_ONLINE_REFRESH,
                        ESealConfig.Defaults.JOB_ONLINE_REFRESH));
    }

    public CertificateReports validateCertificate(String base64Certificate, CertificateVerifier cv) {

        CertificateToken certificate = DSSUtils.loadCertificateFromBase64EncodedString(base64Certificate);
        CertificateValidator certificateValidator = CertificateValidator.fromCertificate(certificate);

        certificateValidator.setCertificateVerifier(cv);
        certificateValidator.setValidationTime(new Date());

        CertificateReports reports = certificateValidator.validate();
        return reports;

    }

    public CertificateQualification getCertificateQualification(String base64Certificate, CertificateVerifier cv) {
        CertificateReports reports = validateCertificate(base64Certificate, cv);
        return reports.getSimpleReport().getQualificationAtCertificateIssuance();
    }

    public CertificateVerifier getCertificateVerifier() {
        return certificateVerifier;
    }

    public void setCertificateVerifier(CertificateVerifier certificateVerifier) {
        this.certificateVerifier = certificateVerifier;
    }

    public List<SignatureLevel> getExpectedSignatureLevel() {
        List<SignatureLevel> signatureLevels = new ArrayList<>();

        String[] signatures = getConfigService().getStringArray(ESealConfig.Properties.SIGNATURE_LEVEL_LIST_JSON);

        if(signatures != null && signatures.length > 0) {
            for (String signature : signatures) {
                signatureLevels.add(SignatureLevel.valueByName(signature));
            }
        }

        return signatureLevels.isEmpty() ? ESealConfig.Defaults.SIGNATURE_LEVELS_JSON : signatureLevels;
    }

    public List<SignatureLevel> getExpectedXmlSignatureLevel() {
        List<SignatureLevel> signatureLevels = new ArrayList<>();

        String[] signatures = getConfigService().getStringArray(ESealConfig.Properties.SIGNATURE_LEVEL_LIST_XML);

        if(signatures != null && signatures.length > 0) {
            for(String signature : signatures) {
                signatureLevels.add(SignatureLevel.valueByName(signature));
            }
        }

        return signatureLevels.isEmpty() ? ESealConfig.Defaults.SIGNATURE_LEVELS_XML : signatureLevels;
    }


    public List<DigestAlgorithm> getExpectedDigestAlgorithm() {
        List<DigestAlgorithm> digestAlgorithms = new ArrayList<>();

        String[] acceptedDigestAlgArray = getConfigService().getStringArray(ESealConfig.Properties.DIGEST_ALGORITHM);

        if(acceptedDigestAlgArray != null && acceptedDigestAlgArray.length > 0) {
            for(String digestAlg : acceptedDigestAlgArray) {
                digestAlgorithms.add(DigestAlgorithm.forName(digestAlg));
            }
        }

        return digestAlgorithms.isEmpty() ? ESealConfig.Defaults.DIGEST_ALGORITHM_VALIDATION : digestAlgorithms;
    }

    public String getPayload(String signedDocument) {
        return this.getJsonValidationService().getPayload(signedDocument);
    }

    public String getPayload(byte[] signedDocument) {
        return this.getJsonValidationService().getPayload(signedDocument);
    }

    public ESealCoreConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ESealCoreConfigService configService) {
        this.configService = configService;
    }

    public JadesValidationService getJsonValidationService() {
        return jsonValidationService;
    }

    public void setJsonValidationService(JadesValidationService jsonValidationService) {
        this.jsonValidationService = jsonValidationService;
    }

    public CommonsDataLoader getCommonsDataLoader() {
        return commonsDataLoader;
    }

    public void setCommonsDataLoader(CommonsDataLoader commonsDataLoader) {
        this.commonsDataLoader = commonsDataLoader;
    }

    public XadesValidationService getXadesValidationService() {
        return xadesValidationService;
    }

    public void setXadesValidationService(XadesValidationService xadesValidationService) {
        this.xadesValidationService = xadesValidationService;
    }
}