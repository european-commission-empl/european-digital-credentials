package eu.europa.ec.empl.edci.viewer.common.model;

import java.util.Date;

public class ShareLinkFetchResponseDTO {
    private Date expirationDate;

    public ShareLinkFetchResponseDTO(){}

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
