package eu.europa.ec.empl.edci.wallet.web.model;

public class CredentialUploadView {
    private String userId;
    private String walletAddress;
    private byte[] credential;

    public CredentialUploadView(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public byte[] getCredential() {
        return credential;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public void setCredential(byte[] credential) {
        this.credential = credential;
    }
}
