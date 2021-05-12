package eu.europa.ec.empl.edci.issuer.service.dss;

import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.SignaturePackaging;

import java.io.File;

public class DSSSignatureDocumentForm extends AbstractSignatureForm {

    private File documentToSign;

    private SignaturePackaging signaturePackaging;

    private ASiCContainerType containerType;

    public File getDocumentToSign() {
        return documentToSign;
    }

    public void setDocumentToSign(File documentToSign) {
        this.documentToSign = documentToSign;
    }

    public SignaturePackaging getSignaturePackaging() {
        return signaturePackaging;
    }

    public void setSignaturePackaging(SignaturePackaging signaturePackaging) {
        this.signaturePackaging = signaturePackaging;
    }

    public ASiCContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(ASiCContainerType containerType) {
        this.containerType = containerType;
    }

    /*public boolean isDocumentToSign() {
        return (documentToSign != null) && (!documentToSign.isEmpty());
    }*/

}
