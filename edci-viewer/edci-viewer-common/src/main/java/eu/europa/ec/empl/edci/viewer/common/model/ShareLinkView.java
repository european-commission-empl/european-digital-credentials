package eu.europa.ec.empl.edci.viewer.common.model;

import javax.validation.constraints.NotNull;

public class ShareLinkView {

    @NotNull
    private String expirationDate;

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

}
