package eu.europa.ec.empl.edci.dss.model.signature;

import java.util.List;

public class SignatureParametersResponseDTO {
    private SignatureParametersTokenIdDTO tokenId;
    private String keyId;
    private String certificate;
    private List<String> certificateChain;
    private String encryptionAlgorithm;

    public SignatureParametersResponseDTO() {
    }

    public SignatureParametersTokenIdDTO getTokenId() {
        return tokenId;
    }

    public void setTokenId(SignatureParametersTokenIdDTO tokenId) {
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
