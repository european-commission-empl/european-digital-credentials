package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.PeriodOfTimeDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:awardingOpportunity:")
public class AwardingOpportunityDTO extends JsonLdCommonDTO {

    @NotNull
    @JsonBackReference
    private LearningAchievementSpecificationDTO learningAchievementSpecification;
    @NotNull
    private List<AgentDTO> awardingBody = new ArrayList<>();
    private List<Identifier> identifier = new ArrayList<>();
    private LocationDTO location;
    private PeriodOfTimeDTO temporal;

    public LearningAchievementSpecificationDTO getLearningAchievementSpecification() {
        return learningAchievementSpecification;
    }

    public void setLearningAchievementSpecification(LearningAchievementSpecificationDTO learningAchievementSpecification) {
        this.learningAchievementSpecification = learningAchievementSpecification;
    }

    public List<AgentDTO> getAwardingBody() {
        return awardingBody;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public PeriodOfTimeDTO getTemporal() {
        return temporal;
    }

    public void setTemporal(PeriodOfTimeDTO temporal) {
        this.temporal = temporal;
    }

    public void setAwardingBody(List<AgentDTO> awardingBody) {
        this.awardingBody = awardingBody;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AwardingOpportunityDTO)) return false;
        if (!super.equals(o)) return false;
        AwardingOpportunityDTO that = (AwardingOpportunityDTO) o;
        return Objects.equals(awardingBody, that.awardingBody) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(location, that.location) &&
                Objects.equals(temporal, that.temporal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), awardingBody, identifier, location, temporal);
    }
}
