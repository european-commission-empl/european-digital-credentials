package eu.europa.ec.empl.edci.issuer.common.model;

public class DeliveryDetailsDTO {

    private String walletAddress;

    public DeliveryDetailsDTO() {
    }

    public DeliveryDetailsDTO(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
