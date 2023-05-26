package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningEntitlementSpec:")
public class QDRLearningEntitlementSpecificationDTO extends QDRSpecificationDTO {

    private List<QDRLearningAchievementSpecificationDTO> entitledBy = new ArrayList<>();
    private List<QDRLearningEntitlementSpecificationDTO> generalisationOf = new ArrayList<>();
    private List<QDRLearningEntitlementSpecificationDTO> hasPart = new ArrayList<>();
    private List<QDRLearningEntitlementSpecificationDTO> isPartOf = new ArrayList<>();
    private List<QDRConceptDTO> limitNationalOccupation = new ArrayList<>();
    @MandatoryConceptScheme("http://data.europa.eu/esco/concept-scheme/occupations")
    private List<QDRConceptDTO> limitOccupation = new ArrayList<>();
    private List<QDRLearningEntitlementSpecificationDTO> specialisationOf = new ArrayList<>();
    @NotNull
    @MandatoryConceptScheme("http://data.europa.eu/snb/entitlement-status/25831c2")
    private QDRConceptDTO entitlementStatus;
    private List<QDROrganisationDTO> limitOrganisation = new ArrayList<>();
    @MandatoryConceptScheme("http://publications.europa.eu/resource/authority/atu")
    private List<QDRConceptDTO> limitJurisdiction = new ArrayList<>();

    public List<QDRLearningAchievementSpecificationDTO> getEntitledBy() {
        return entitledBy;
    }

    public List<QDRLearningEntitlementSpecificationDTO> getHasPart() {
        return hasPart;
    }

    public List<QDRConceptDTO> getLimitNationalOccupation() {
        return limitNationalOccupation;
    }

    public List<QDRConceptDTO> getLimitOccupation() {
        return limitOccupation;
    }

    public List<QDRLearningEntitlementSpecificationDTO> getSpecialisationOf() {
        return specialisationOf;
    }

    public QDRConceptDTO getEntitlementStatus() {
        return entitlementStatus;
    }

    public void setEntitlementStatus(QDRConceptDTO entitlementStatus) {
        this.entitlementStatus = entitlementStatus;
    }

    public List<QDROrganisationDTO> getLimitOrganisation() {
        return limitOrganisation;
    }

    public List<QDRConceptDTO> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public List<QDRLearningEntitlementSpecificationDTO> getGeneralisationOf() {
        return generalisationOf;
    }

    public List<QDRLearningEntitlementSpecificationDTO> getIsPartOf() {
        return isPartOf;
    }

    public void setEntitledBy(List<QDRLearningAchievementSpecificationDTO> entitledBy) {
        this.entitledBy = entitledBy;
    }

    public void setGeneralisationOf(List<QDRLearningEntitlementSpecificationDTO> generalisationOf) {
        this.generalisationOf = generalisationOf;
    }

    public void setHasPart(List<QDRLearningEntitlementSpecificationDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setIsPartOf(List<QDRLearningEntitlementSpecificationDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setLimitNationalOccupation(List<QDRConceptDTO> limitNationalOccupation) {
        this.limitNationalOccupation = limitNationalOccupation;
    }

    public void setLimitOccupation(List<QDRConceptDTO> limitOccupation) {
        this.limitOccupation = limitOccupation;
    }

    public void setSpecialisationOf(List<QDRLearningEntitlementSpecificationDTO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }

    public void setLimitOrganisation(List<QDROrganisationDTO> limitOrganisation) {
        this.limitOrganisation = limitOrganisation;
    }

    public void setLimitJurisdiction(List<QDRConceptDTO> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRLearningEntitlementSpecificationDTO)) return false;
        if (!super.equals(o)) return false;
        QDRLearningEntitlementSpecificationDTO that = (QDRLearningEntitlementSpecificationDTO) o;
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
