package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import javax.validation.constraints.NotNull;
import java.util.List;

public class IdentifierDTView extends DataTypeView {

    @NotNull
    private String notation; //1

    private String creator; //0..1

    private TextDTView schemeAgency; //0..1

    private String schemeName; //0..1

    //    @JsonFormat(pattern = EuropassConstants.DATE_LOCAL)
    private String dateIssued; //0..1

    private List<CodeDTView> dcType; //*

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public TextDTView getSchemeAgency() {
        return schemeAgency;
    }

    public void setSchemeAgency(TextDTView schemeAgency) {
        this.schemeAgency = schemeAgency;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public List<CodeDTView> getDcType() {
        return dcType;
    }

    public void setDcType(List<CodeDTView> dcType) {
        this.dcType = dcType;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }
}