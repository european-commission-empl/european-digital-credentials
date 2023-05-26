package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.AwardingProcessDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.EntitlemSpecificationDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.EntitlementSpecLiteView;

import java.util.List;

public class EntitlementSpecView extends EntitlementSpecLiteView {

    private List<IdentifierDTView> identifier; //*

    private TextDTView title; //1

    //@NotNull
    private List<CodeDTView> dcType; //1

    private NoteDTView description; //0..1

    //    @JsonFormat(pattern = EuropassConstants.DATE_LOCAL)
    private String dateIssued; //0..1

    //    @JsonFormat(pattern = EuropassConstants.DATE_LOCAL)
    private String expiryDate; //0..1

    private List<NoteDTView> additionalNote; //*

    private AwardingProcessDCView awardedBy; //0..1

    private EntitlemSpecificationDCView specifiedBy; //0..1

    private SubresourcesOids relHasPart;

    private SubresourcesOids relValidWith;

    private SubresourcesOids relAwardingBody;

    public SubresourcesOids getRelAwardingBody() {
        relAwardingBody = (relAwardingBody == null ? new SubresourcesOids() : relAwardingBody);
        return relAwardingBody;
    }

    public void setRelAwardingBody(SubresourcesOids relAwardingBody) {
        this.relAwardingBody = relAwardingBody;
    }

    public SubresourcesOids getRelHasPart() {
        relHasPart = (relHasPart == null ? new SubresourcesOids() : relHasPart);
        return relHasPart;
    }

    public void setRelHasPart(SubresourcesOids relHasPart) {
        this.relHasPart = relHasPart;
    }

    public SubresourcesOids getRelValidWith() {
        relValidWith = (relValidWith == null ? new SubresourcesOids() : relValidWith);
        return relValidWith;
    }

    public void setRelValidWith(SubresourcesOids relValidWith) {
        this.relValidWith = relValidWith;
    }

    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
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

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<NoteDTView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public EntitlemSpecificationDCView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(EntitlemSpecificationDCView specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<CodeDTView> getDcType() {
        return dcType;
    }

    public void setDcType(List<CodeDTView> dcType) {
        this.dcType = dcType;
    }

    public AwardingProcessDCView getAwardedBy() {
        return awardedBy;
    }

    public void setAwardedBy(AwardingProcessDCView awardedBy) {
        this.awardedBy = awardedBy;
    }
}

