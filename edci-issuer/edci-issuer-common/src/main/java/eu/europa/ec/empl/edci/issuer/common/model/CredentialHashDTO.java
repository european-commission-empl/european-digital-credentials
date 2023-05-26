package eu.europa.ec.empl.edci.issuer.common.model;

public class CredentialHashDTO {
    private String signatureLevel;
    private String signaturePacking;
    private String digestAlgorithm;
    private String certificateResponse;
    private String toBeSigned;

    public CredentialHashDTO(){

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

    public String getToBeSigned() {
        return toBeSigned;
    }

    public void setToBeSigned(String toBeSigned) {
        this.toBeSigned = toBeSigned;
    }
}
