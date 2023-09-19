package eu.europa.ec.empl.edci.wallet.web.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class WalletCreateView {

    @NotNull
    private String userId;
    @Email
    @NotNull
    private String userEmail;

    public WalletCreateView() {
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
