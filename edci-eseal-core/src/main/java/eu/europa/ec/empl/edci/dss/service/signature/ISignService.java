package eu.europa.ec.empl.edci.dss.service.signature;

import eu.europa.ec.empl.edci.dss.model.signature.AbstractSignatureForm;
import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.jades.signature.JAdESService;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import org.springframework.stereotype.Service;

@Service
public interface ISignService {

    DSSDocument signDocument(String toSignDocumentPath, String certPath, String password);

    AbstractSignatureParameters fillParameters(DSSPrivateKeyEntry privateKey);
    AbstractSignatureParameters fillParameters(AbstractSignatureForm form);

    AbstractSignatureService getSignatureService();
    AbstractSignatureService getSignatureService(TSPSource onlineTSPSource);

    SignatureForm getSignatureForm();
    SignatureLevel getSignatureLevel();
    SignaturePackaging getSignaturePackaging();

}