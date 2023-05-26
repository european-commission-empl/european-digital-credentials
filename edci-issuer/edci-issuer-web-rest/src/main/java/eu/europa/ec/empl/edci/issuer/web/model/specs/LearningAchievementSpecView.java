package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.AwardingProcessDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.LearningSpecificationDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.LearningAchievementSpecLiteView;

import javax.validation.constraints.NotNull;
import java.util.List;

public class LearningAchievementSpecView extends LearningAchievementSpecLiteView {

    private List<IdentifierDTView> identifier; //*

    @NotNull
    private TextDTView title; //1

    private TextDTView description; //0..1

    private List<NoteDTView> additionalNote; //*

    private AwardingProcessDCView awardedBy; //0..1

    private LearningSpecificationDCView specifiedBy; //0..1

    private SubresourcesOids relAwardingBody;

    private SubresourcesOids relProvenBy;

    private SubresourcesOids relInfluencedBy;

    private SubresourcesOids relEntitlesTo;

    private SubresourcesOids relSubAchievements;

    private SubresourcesOids relLearningOutcomes;

    private SubresourcesOids relAccreditation;

    public SubresourcesOids getRelAccreditation() {
        relAccreditation = (relAccreditation == null ? new SubresourcesOids() : relAccreditation);
        return relAccreditation;
    }

    public void setRelAccreditation(SubresourcesOids relAccreditation) {
        this.relAccreditation = relAccreditation;
    }

    public SubresourcesOids getRelAwardingBody() {
        relAwardingBody = (relAwardingBody == null ? new SubresourcesOids() : relAwardingBody);
        return relAwardingBody;
    }

    public void setRelAwardingBody(SubresourcesOids relAwardingBody) {
        this.relAwardingBody = relAwardingBody;
    }

    public SubresourcesOids getRelProvenBy() {
        relProvenBy = (relProvenBy == null ? new SubresourcesOids() : relProvenBy);
        return relProvenBy;
    }

    public void setRelProvenBy(SubresourcesOids relProvenBy) {
        this.relProvenBy = relProvenBy;
    }

    public SubresourcesOids getRelInfluencedBy() {
        relInfluencedBy = (relInfluencedBy == null ? new SubresourcesOids() : relInfluencedBy);
        return relInfluencedBy;
    }

    public void setRelInfluencedBy(SubresourcesOids relInfluencedBy) {
        this.relInfluencedBy = relInfluencedBy;
    }

    public SubresourcesOids getRelEntitlesTo() {
        relEntitlesTo = (relEntitlesTo == null ? new SubresourcesOids() : relEntitlesTo);
        return relEntitlesTo;
    }

    public void setRelEntitlesTo(SubresourcesOids relEntitlesTo) {
        this.relEntitlesTo = relEntitlesTo;
    }

    public SubresourcesOids getRelSubAchievements() {
        relSubAchievements = (relSubAchievements == null ? new SubresourcesOids() : relSubAchievements);
        return relSubAchievements;
    }

    public void setRelSubAchievements(SubresourcesOids relSubAchievements) {
        this.relSubAchievements = relSubAchievements;
    }

    public SubresourcesOids getRelLearningOutcomes() {
        relLearningOutcomes = (relLearningOutcomes == null ? new SubresourcesOids() : relLearningOutcomes);
        return relLearningOutcomes;
    }

    public void setRelLearningOutcomes(SubresourcesOids relLearningOutcomes) {
        this.relLearningOutcomes = relLearningOutcomes;
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

    public TextDTView getDescription() {
        return description;
    }

    public void setDescription(TextDTView description) {
        this.description = description;
    }

    public List<NoteDTView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public AwardingProcessDCView getAwardedBy() {
        return awardedBy;
    }

    public LearningSpecificationDCView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningSpecificationDCView specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public void setAwardedBy(AwardingProcessDCView awardedBy) {
        this.awardedBy = awardedBy;
    }
}