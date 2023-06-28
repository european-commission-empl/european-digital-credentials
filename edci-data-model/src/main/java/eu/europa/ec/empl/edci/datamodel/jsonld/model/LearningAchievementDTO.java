package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.empl.edci.annotation.CustomizableEntityDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.CreditPointDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningAchievement:")
@CustomizableEntityDTO(identifierField = "OCBID")
public class LearningAchievementDTO extends ClaimDTO {

    @JsonIgnore
    private String OCBID;
    private List<CreditPointDTO> creditReceived = new ArrayList<>();
    private List<LearningEntitlementDTO> entitlesTo = new ArrayList<>();
    private List<LearningAchievementDTO> hasPart = new ArrayList<>();
    private List<LearningActivityDTO> influencedBy = new ArrayList<>();
    @JsonIgnore
    private List<LearningAchievementDTO> isPartOf = new ArrayList<>();
    private LearningOpportunityDTO learningOpportunity;
    private List<LearningAssessmentDTO> provenBy = new ArrayList<>();
    private LearningAchievementSpecificationDTO specifiedBy;

    public String getOCBID() {
        return OCBID;
    }

    public void setOCBID(String OCBID) {
        this.OCBID = OCBID;
    }

    public List<LearningEntitlementDTO> getEntitlesTo() {
        return entitlesTo;
    }

    public List<LearningAchievementDTO> getHasPart() {
        return hasPart;
    }

    public List<LearningActivityDTO> getInfluencedBy() {
        return influencedBy;
    }

    public LearningOpportunityDTO getLearningOpportunity() {
        return learningOpportunity;
    }

    public void setLearningOpportunity(LearningOpportunityDTO learningOpportunity) {
        this.learningOpportunity = learningOpportunity;
    }

    public List<LearningAssessmentDTO> getProvenBy() {
        return provenBy;
    }

    public LearningAchievementSpecificationDTO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningAchievementSpecificationDTO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<LearningAchievementDTO> getIsPartOf() {
        return isPartOf;
    }

    public List<CreditPointDTO> getCreditReceived() {
        return creditReceived;
    }

    public void setCreditReceived(List<CreditPointDTO> creditReceived) {
        this.creditReceived = creditReceived;
    }

    public void setEntitlesTo(List<LearningEntitlementDTO> entitlesTo) {
        this.entitlesTo = entitlesTo;
    }

    public void setHasPart(List<LearningAchievementDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setInfluencedBy(List<LearningActivityDTO> influencedBy) {
        this.influencedBy = influencedBy;
    }

    public void setIsPartOf(List<LearningAchievementDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setProvenBy(List<LearningAssessmentDTO> provenBy) {
        this.provenBy = provenBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningAchievementDTO)) return false;
        if (!super.equals(o)) return false;
        LearningAchievementDTO that = (LearningAchievementDTO) o;
        return Objects.equals(creditReceived, that.creditReceived) &&
                Objects.equals(entitlesTo, that.entitlesTo) &&
                Objects.equals(hasPart, that.hasPart) &&
                Objects.equals(influencedBy, that.influencedBy) &&
                Objects.equals(learningOpportunity, that.learningOpportunity) &&
                Objects.equals(provenBy, that.provenBy) &&
                Objects.equals(specifiedBy, that.specifiedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), creditReceived, entitlesTo, hasPart, influencedBy, learningOpportunity, provenBy, specifiedBy);
    }
}
