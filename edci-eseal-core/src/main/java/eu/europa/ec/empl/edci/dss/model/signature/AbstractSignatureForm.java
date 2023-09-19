package eu.europa.ec.empl.edci.dss.model.signature;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureLevel;

import java.util.Date;
import java.util.List;


public abstract class AbstractSignatureForm {


    private Date signingDate;


    private boolean addContentTimestamp;

    private SignatureForm signatureForm;

    private SignatureLevel signatureLevel;

    private DigestAlgorithm digestAlgorithm;

    private String base64Certificate;

    private List<String> base64CertificateChain;

    private EncryptionAlgorithm encryptionAlgorithm;

    private String base64SignatureValue;

    private DSSTimestamp contentTimestamp;


    public Date getSigningDate() {
        return signingDate;
    }

    public void setSigningDate(Date signingDate) {
        this.signingDate = signingDate;
    }


    public boolean isAddContentTimestamp() {
        return addContentTimestamp;
    }

    public void setAddContentTimestamp(boolean addContentTimestamp) {
        this.addContentTimestamp = addContentTimestamp;
    }

    public SignatureForm getSignatureForm() {
        return signatureForm;
    }

    public void setSignatureForm(SignatureForm signatureForm) {
        this.signatureForm = signatureForm;
    }

    public SignatureLevel getSignatureLevel() {
        return signatureLevel;
    }

    public void setSignatureLevel(SignatureLevel signatureLevel) {
        this.signatureLevel = signatureLevel;
    }

    public DigestAlgorithm getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    public String getBase64Certificate() {
        return base64Certificate;
    }

    public void setBase64Certificate(String base64Certificate) {
        this.base64Certificate = base64Certificate;
    }

    public List<String> getBase64CertificateChain() {
        return base64CertificateChain;
    }

    public void setBase64CertificateChain(List<String> base64CertificateChain) {
        this.base64CertificateChain = base64CertificateChain;
    }

    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getBase64SignatureValue() {
        return base64SignatureValue;
    }

    public void setBase64SignatureValue(String base64SignatureValue) {
        this.base64SignatureValue = base64SignatureValue;
    }

    public DSSTimestamp getContentTimestamp() {
        return contentTimestamp;
    }

    public void setContentTimestamp(DSSTimestamp contentTimestamp) {
        this.contentTimestamp = contentTimestamp;
    }

}
