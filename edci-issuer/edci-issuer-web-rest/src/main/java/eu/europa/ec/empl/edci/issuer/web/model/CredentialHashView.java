package eu.europa.ec.empl.edci.issuer.web.model;

public class CredentialHashView {
    private String signatureLevel;
    private String signaturePacking;
    private String digestAlgorithm;
    private String certificateResponse;

    public CredentialHashView(){

    }

    public String getSignatureLevel() {
        return signatureLevel;
    }

    public void setSignatureLevel(String signatureLevel) {
        this.signatureLevel = signatureLevel;
    }

    public String getSignaturePacking() {
        return signaturePacking;
    }

    public void setSignaturePacking(String signaturePacking) {
        this.signaturePacking = signaturePacking;
    }

    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    public String getCertificateResponse() {
        return certificateResponse;
    }

    public void setCertificateResponse(String certificateResponse) {
        this.certificateResponse = certificateResponse;
    }
}
