package eu.europa.ec.empl.edci.wallet.web.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class WalletCreateBulkElemView {

    @NotNull
    @Email
    private String userEmail;

    public WalletCreateBulkElemView() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
