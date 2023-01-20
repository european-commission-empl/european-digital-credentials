package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.LearningActSpecificationDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.LearningSpecificationDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.LocationDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.LearningActivitySpecLiteView;

import java.util.List;

public class LearningActivitySpecView extends LearningActivitySpecLiteView {

    private List<IdentifierDTView> identifier; //*

    private TextDTView title; //1

    private NoteDTView description; //0..1

    private List<NoteDTView> additionalNote; //*

    private String workload; //0..1 in hours

    private List<LocationDCView> location; //*

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String startedAtTime; //0..1

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String endedAtTime; //0..1

    private LearningSpecificationDCView usedLearningOpportunity;

    private LearningActSpecificationDCView specifiedBy;

    //    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private SubresourcesOids relDirectedBy;

    //    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private SubresourcesOids relInfluenced;

    //    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private SubresourcesOids relHasPart;

    public SubresourcesOids getRelDirectedBy() {
        relDirectedBy = (relDirectedBy == null ? new SubresourcesOids() : relDirectedBy);
        return relDirectedBy;
    }

    public void setRelDirectedBy(SubresourcesOids relDirectedBy) {
        this.relDirectedBy = relDirectedBy;
    }

    public SubresourcesOids getRelInfluenced() {
        relInfluenced = (relInfluenced == null ? new SubresourcesOids() : relInfluenced);
        return relInfluenced;
    }

    public void setRelInfluenced(SubresourcesOids relInfluenced) {
        this.relInfluenced = relInfluenced;
    }

    public SubresourcesOids getRelHasPart() {
        relHasPart = (relHasPart == null ? new SubresourcesOids() : relHasPart);
        return relHasPart;
    }

    public void setRelHasPart(SubresourcesOids relHasPart) {
        this.relHasPart = relHasPart;
    }


    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    @Override
    public TextDTView getTitle() {
        return title;
    }

    @Override
    public void setTitle(TextDTView title) {
        this.title = title;
    }

    public NoteDTView getDescription() {
        return description;
    }

    public void setDescription(NoteDTView description) {
        this.description = description;
    }

    public List<NoteDTView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public String getWorkload() {
        return workload;
    }

    public void setWorkload(String workload) {
        this.workload = workload;
    }

    public List<LocationDCView> getLocation() {
        return location;
    }

    public void setLocation(List<LocationDCView> location) {
        this.location = location;
    }

    public String getStartedAtTime() {
        return startedAtTime;
    }

    public void setStartedAtTime(String startedAtTime) {
        this.startedAtTime = startedAtTime;
    }

    public String getEndedAtTime() {
        return endedAtTime;
    }

    public void setEndedAtTime(String endedAtTime) {
        this.endedAtTime = endedAtTime;
    }

    public LearningSpecificationDCView getUsedLearningOpportunity() {
        return usedLearningOpportunity;
    }

    public void setUsedLearningOpportunity(LearningSpecificationDCView usedLearningOpportunity) {
        this.usedLearningOpportunity = usedLearningOpportunity;
    }

    public LearningActSpecificationDCView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningActSpecificationDCView specifiedBy) {
        this.specifiedBy = specifiedBy;
    }
}