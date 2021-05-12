package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.EuropassCredentialSpecLiteView;

import javax.validation.constraints.NotNull;

public class EuropassCredentialSpecView extends EuropassCredentialSpecLiteView {

    @NotNull
    private TextDTView title; //1

    private NoteDTView description; //0..1

    @NotNull
    private CodeDTView type; //1

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String issuanceDate; //1

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String expirationDate; //0..1

    private SubresourcesOids relAchieved;

    private SubresourcesOids relPerformed;

    private SubresourcesOids relEntitledTo;

    private SubresourcesOids relIssuer;

    private SubresourcesOids relDiploma;

    public SubresourcesOids getRelAchieved() {
        relAchieved = (relAchieved == null ? new SubresourcesOids() : relAchieved);
        return relAchieved;
    }

    public void setRelAchieved(SubresourcesOids relAchieved) {
        this.relAchieved = relAchieved;
    }

    public SubresourcesOids getRelDiploma() {
        relDiploma = (relDiploma == null ? new SubresourcesOids() : relDiploma);
        return relDiploma;
    }

    public void setRelDiploma(SubresourcesOids relDiploma) {
        this.relDiploma = relDiploma;
    }

    public SubresourcesOids getRelPerformed() {
        relPerformed = (relPerformed == null ? new SubresourcesOids() : relPerformed);
        return relPerformed;
    }

    public void setRelPerformed(SubresourcesOids relPerformed) {
        this.relPerformed = relPerformed;
    }

    public SubresourcesOids getRelEntitledTo() {
        relEntitledTo = (relEntitledTo == null ? new SubresourcesOids() : relEntitledTo);
        return relEntitledTo;
    }

    public SubresourcesOids getRelIssuer() {
        relIssuer = (relIssuer == null ? new SubresourcesOids() : relIssuer);
        return relIssuer;
    }

    public void setRelIssuer(SubresourcesOids relIssuer) {
        this.relIssuer = relIssuer;
    }

    public void setRelEntitledTo(SubresourcesOids relEntitledTo) {
        this.relEntitledTo = relEntitledTo;
    }

    public CodeDTView getType() {
        return type;
    }

    public void setType(CodeDTView type) {
        this.type = type;
    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public TextDTView getTitle() {
        return title;
    }

    public void setTitle(TextDTView title) {
        this.title = title;
    }

    public NoteDTView getDescription() {
        return description;
    }

    public void setDescription(NoteDTView description) {
        this.description = description;
    }

}