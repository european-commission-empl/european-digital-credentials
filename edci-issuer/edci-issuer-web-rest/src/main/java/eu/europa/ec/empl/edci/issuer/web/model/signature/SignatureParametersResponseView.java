package eu.europa.ec.empl.edci.issuer.web.model.signature;

import java.util.List;

public class SignatureParametersResponseView {
    private SignatureParametersTokenIdView tokenId;
    private String keyId;
    private String certificate;
    private List<String> certificateChain;
    private String encryptionAlgorithm;

    public SignatureParametersResponseView(){}

    public SignatureParametersTokenIdView getTokenId() {
        return tokenId;
    }

    public void setTokenId(SignatureParametersTokenIdView tokenId) {
        this.tokenId = tokenId;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
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

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }
}
