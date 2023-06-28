package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.EuropassCredentialSpecLiteView;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class EuropassCredentialSpecView extends EuropassCredentialSpecLiteView {

    @NotNull
    private TextDTView title; //1

    private NoteDTView description; //0..1

    @NotNull
    private CodeDTView type; //1

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String validFrom; //1

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String validUntil; //0..1

    private Set<IdentifierDTView> identifier; //*

    private String hasAccreditation; //*

    private SubresourcesOids relAchieved;

    private SubresourcesOids relPerformed;

    private SubresourcesOids relEntitledTo;

    private SubresourcesOids relIssuer;

    private SubresourcesOids relAssessed;

    private SubresourcesOids relDiploma;

    public SubresourcesOids getRelAchieved() {
        relAchieved = (relAchieved == null ? new SubresourcesOids() : relAchieved);
        return relAchieved;
    }

    public void setRelAchieved(SubresourcesOids relAchieved) {
        this.relAchieved = relAchieved;
    }

    public SubresourcesOids getRelAssessed() {
        relAssessed = (relAssessed == null ? new SubresourcesOids() : relAssessed);
        return relAssessed;
    }

    public void setRelAssessed(SubresourcesOids relAchieved) {
        this.relAssessed = relAchieved;
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

    public String getHasAccreditation() {
        return hasAccreditation;
    }

    public void setHasAccreditation(String hasAccreditation) {
        this.hasAccreditation = hasAccreditation;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
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

    public Set<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Set<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }
}