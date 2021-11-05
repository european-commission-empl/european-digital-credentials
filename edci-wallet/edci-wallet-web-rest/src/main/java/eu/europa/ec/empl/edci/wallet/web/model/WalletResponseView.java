package eu.europa.ec.empl.edci.wallet.web.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class WalletResponseView {

    private String walletAddress;
    private String userId;
    private String userEmail;

    public WalletResponseView() {
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
