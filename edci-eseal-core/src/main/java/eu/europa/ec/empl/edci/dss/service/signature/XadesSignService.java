package eu.europa.ec.empl.edci.dss.service.signature;

import eu.europa.ec.empl.edci.dss.config.ESealCoreConfigService;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XadesSignService {

    @Autowired
    private ESealCoreConfigService configService;

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private OnlineTSPSource onlineTSPSource;

    protected DSSDocument extendDocumentSignature(DSSDocument toSignDocument) {
        // Create JAdES service for signature
        XAdESSignatureParameters parameters = new XAdESSignatureParameters();
        parameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_LTA);

        XAdESService service = new XAdESService(this.getCertificateVerifier());
        service.setTspSource(this.getOnlineTSPSource());
        return service.extendDocument(toSignDocument, parameters);
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

    public OnlineTSPSource getOnlineTSPSource() {
        return onlineTSPSource;
    }

    public void setOnlineTSPSource(OnlineTSPSource onlineTSPSource) {
        this.onlineTSPSource = onlineTSPSource;
    }
}
