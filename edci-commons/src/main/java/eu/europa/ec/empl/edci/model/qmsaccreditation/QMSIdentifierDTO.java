package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSIdentifierDTO {

    private URI value;
    private String schemeID;
    private String schemeAgencyName;
    private String schemeAgencyID;
    private Date issuedDate;

    public URI getValue() {
        return value;
    }

    public void setValue(URI value) {
        this.value = value;
    }

    public String getSchemeID() {
        return schemeID;
    }

    public void setSchemeID(String schemeID) {
        this.schemeID = schemeID;
    }

    public String getSchemeAgencyName() {
        return schemeAgencyName;
    }

    public void setSchemeAgencyName(String schemeAgencyName) {
        this.schemeAgencyName = schemeAgencyName;
    }

    public String getSchemeAgencyID() {
        return schemeAgencyID;
    }

    public void setSchemeAgencyID(String schemeAgencyID) {
        this.schemeAgencyID = schemeAgencyID;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }
}
