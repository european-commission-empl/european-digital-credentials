package eu.europa.ec.empl.edci.wallet.web.model;

public class CredentialUploadView {
    private String userId;
    private String walletAddress;
    private byte[] credentialXML;

    public CredentialUploadView(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public byte[] getCredentialXML() {
        return credentialXML;
    }

    public void setCredentialXML(byte[] credentialXML) {
        this.credentialXML = credentialXML;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
