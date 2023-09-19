package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningEntitlementSpec:")
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", visible = true, defaultImpl = LearningEntitlementSpecificationDTO.class)
public class LearningEntitlementSpecificationDTO extends SpecificationDTO {

    private List<LearningAchievementSpecificationDTO> entitledBy = new ArrayList<>();
    private List<LearningEntitlementSpecificationDTO> generalisationOf = new ArrayList<>();
    private List<LearningEntitlementSpecificationDTO> hasPart = new ArrayList<>();
    private List<LearningEntitlementSpecificationDTO> isPartOf = new ArrayList<>();
    private List<ConceptDTO> limitNationalOccupation = new ArrayList<>();
    private List<ConceptDTO> limitOccupation = new ArrayList<>();
    private List<LearningEntitlementSpecificationDTO> specialisationOf = new ArrayList<>();
    @NotNull
    private ConceptDTO entitlementStatus;
    private List<OrganisationDTO> limitOrganisation = new ArrayList<>();
    private List<ConceptDTO> limitJurisdiction = new ArrayList<>();

    public LearningEntitlementSpecificationDTO() {
        super();
    }

    @JsonCreator
    public LearningEntitlementSpecificationDTO(String uri) {
        super(uri);
    }

    public List<LearningAchievementSpecificationDTO> getEntitledBy() {
        return entitledBy;
    }

    public List<LearningEntitlementSpecificationDTO> getHasPart() {
        return hasPart;
    }

    public List<ConceptDTO> getLimitNationalOccupation() {
        return limitNationalOccupation;
    }

    public List<ConceptDTO> getLimitOccupation() {
        return limitOccupation;
    }

    public List<LearningEntitlementSpecificationDTO> getSpecialisationOf() {
        return specialisationOf;
    }

    public ConceptDTO getEntitlementStatus() {
        return entitlementStatus;
    }

    public void setEntitlementStatus(ConceptDTO entitlementStatus) {
        this.entitlementStatus = entitlementStatus;
    }

    public List<OrganisationDTO> getLimitOrganisation() {
        return limitOrganisation;
    }

    public List<ConceptDTO> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public List<LearningEntitlementSpecificationDTO> getGeneralisationOf() {
        return generalisationOf;
    }

    public List<LearningEntitlementSpecificationDTO> getIsPartOf() {
        return isPartOf;
    }

    public void setEntitledBy(List<LearningAchievementSpecificationDTO> entitledBy) {
        this.entitledBy = entitledBy;
    }

    public void setGeneralisationOf(List<LearningEntitlementSpecificationDTO> generalisationOf) {
        this.generalisationOf = generalisationOf;
    }

    public void setHasPart(List<LearningEntitlementSpecificationDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setIsPartOf(List<LearningEntitlementSpecificationDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setLimitNationalOccupation(List<ConceptDTO> limitNationalOccupation) {
        this.limitNationalOccupation = limitNationalOccupation;
    }

    public void setLimitOccupation(List<ConceptDTO> limitOccupation) {
        this.limitOccupation = limitOccupation;
    }

    public void setSpecialisationOf(List<LearningEntitlementSpecificationDTO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }

    public void setLimitOrganisation(List<OrganisationDTO> limitOrganisation) {
        this.limitOrganisation = limitOrganisation;
    }

    public void setLimitJurisdiction(List<ConceptDTO> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningEntitlementSpecificationDTO)) return false;
        if (!super.equals(o)) return false;
        LearningEntitlementSpecificationDTO that = (LearningEntitlementSpecificationDTO) o;
        return Objects.equals(entitledBy, that.entitledBy) &&
                Objects.equals(generalisationOf, that.generalisationOf) &&
                Objects.equals(hasPart, that.hasPart) &&
                Objects.equals(isPartOf, that.isPartOf) &&
                Objects.equals(limitNationalOccupation, that.limitNationalOccupation) &&
                Objects.equals(limitOccupation, that.limitOccupation) &&
                Objects.equals(specialisationOf, that.specialisationOf) &&
                Objects.equals(entitlementStatus, that.entitlementStatus) &&
                Objects.equals(limitOrganisation, that.limitOrganisation) &&
                Objects.equals(limitJurisdiction, that.limitJurisdiction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entitledBy, generalisationOf, hasPart, isPartOf, limitNationalOccupation, limitOccupation, specialisationOf, entitlementStatus, limitOrganisation, limitJurisdiction);
    }
}
