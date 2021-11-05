package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import javax.validation.constraints.NotNull;
import java.util.List;

public class IdentifierDTView extends DataTypeView {

    @NotNull
    private String content; //1

    private String identifierSchemeId; //0..1

    private String identifierSchemeAgencyName; //0..1

    //    @JsonFormat(pattern = EuropassConstants.DATE_LOCAL)
    private String issuedDate; //0..1

    private List<String> identifierType; //*

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdentifierSchemeId() {
        return identifierSchemeId;
    }

    public void setIdentifierSchemeId(String identifierSchemeId) {
        this.identifierSchemeId = identifierSchemeId;
    }

    public String getIdentifierSchemeAgencyName() {
        return identifierSchemeAgencyName;
    }

    public void setIdentifierSchemeAgencyName(String identifierSchemeAgencyName) {
        this.identifierSchemeAgencyName = identifierSchemeAgencyName;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public List<String> getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(List<String> identifierType) {
        this.identifierType = identifierType;
    }
}