package eu.europa.ec.empl.edci.issuer.web.model.signature;

import java.util.List;

public class SignatureNexuResponseView {
    private String signatureValue;
    private String certificate;
    private List<String> certificateChain;
    private String signatureAlgorithm;
    private String encryptionAlgorithm;

    public SignatureNexuResponseView(){}

    public String getSignatureValue() {
        return signatureValue;
    }

    public void setSignatureValue(String signatureValue) {
        this.signatureValue = signatureValue;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public List<String> getCertificateChain() {
        return certificateChain;
    }

    public void setCertificateChain(List<String> certificateChain) {
        this.certificateChain = certificateChain;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }
}
