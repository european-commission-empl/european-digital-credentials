package eu.europa.ec.empl.edci.wallet.web.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class WalletModifyView {

    @Email
    @NotNull
    private String userEmail;

    public WalletModifyView() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
