package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.empl.edci.annotation.CustomizableEntityDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.PeriodOfTimeDTO;
import org.joda.time.Period;

import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:activity:")
@CustomizableEntityDTO(identifierField = "OCBID")
public class LearningActivityDTO extends ClaimDTO {

    @JsonIgnore
    private String OCBID;
    private List<AgentDTO> directedBy = new ArrayList<>();
    private List<LearningActivityDTO> hasPart = new ArrayList<>();
    private List<LearningAchievementDTO> influences = new ArrayList<>();
    @JsonIgnore
    private List<LearningActivityDTO> isPartOf = new ArrayList<>();
    private LearningOpportunityDTO learningOpportunity;
    @Positive
    private Integer levelOfCompletion;
    private List<LocationDTO> location = new ArrayList<>();
    private LearningActivitySpecificationDTO specifiedBy;
    private List<PeriodOfTimeDTO> temporal = new ArrayList<>();
    private Period workload;

    public String getOCBID() {
        return OCBID;
    }

    public void setOCBID(String OCBID) {
        this.OCBID = OCBID;
    }
    public List<AgentDTO> getDirectedBy() {
        return directedBy;
    }

    public List<LearningActivityDTO> getHasPart() {
        return hasPart;
    }

    public List<LearningAchievementDTO> getInfluences() {
        return influences;
    }

    public LearningOpportunityDTO getLearningOpportunity() {
        return learningOpportunity;
    }

    public void setLearningOpportunity(LearningOpportunityDTO learningOpportunity) {
        this.learningOpportunity = learningOpportunity;
    }

    public Integer getLevelOfCompletion() {
        return levelOfCompletion;
    }

    public void setLevelOfCompletion(Integer levelOfCompletion) {
        this.levelOfCompletion = levelOfCompletion;
    }

    public List<LocationDTO> getLocation() {
        return location;
    }

    public LearningActivitySpecificationDTO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningActivitySpecificationDTO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<PeriodOfTimeDTO> getTemporal() {
        return temporal;
    }

    public Period getWorkload() {
        return workload;
    }

    public void setWorkload(Period workload) {
        this.workload = workload;
    }

    public List<LearningActivityDTO> getIsPartOf() {
        return isPartOf;
    }

    public void setDirectedBy(List<AgentDTO> directedBy) {
        this.directedBy = directedBy;
    }

    public void setHasPart(List<LearningActivityDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setInfluences(List<LearningAchievementDTO> influences) {
        this.influences = influences;
    }

    public void setIsPartOf(List<LearningActivityDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setLocation(List<LocationDTO> location) {
        this.location = location;
    }

    public void setTemporal(List<PeriodOfTimeDTO> temporal) {
        this.temporal = temporal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningActivityDTO)) return false;
        if (!super.equals(o)) return false;
        LearningActivityDTO that = (LearningActivityDTO) o;
        return Objects.equals(directedBy, that.directedBy) &&
                Objects.equals(hasPart, that.hasPart) &&
                Objects.equals(influences, that.influences) &&
                Objects.equals(learningOpportunity, that.learningOpportunity) &&
                Objects.equals(levelOfCompletion, that.levelOfCompletion) &&
                Objects.equals(location, that.location) &&
                Objects.equals(specifiedBy, that.specifiedBy) &&
                Objects.equals(temporal, that.temporal) &&
                Objects.equals(workload, that.workload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), directedBy, hasPart, influences, learningOpportunity, levelOfCompletion, location, specifiedBy, temporal, workload);
    }
}
