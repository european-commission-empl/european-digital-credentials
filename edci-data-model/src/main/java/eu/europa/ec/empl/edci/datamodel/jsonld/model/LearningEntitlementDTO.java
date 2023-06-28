package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.empl.edci.annotation.CustomizableEntityDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:entitlement:")
@CustomizableEntityDTO(identifierField = "OCBID")
public class LearningEntitlementDTO extends ClaimDTO {

    @JsonIgnore
    private String OCBID;
    private ZonedDateTime dateIssued;
    private List<LearningAchievementDTO> entitledBy = new ArrayList<>();
    private ZonedDateTime expiryDate;
    private List<LearningEntitlementDTO> hasPart = new ArrayList<>();
    @JsonIgnore
    private List<LearningEntitlementDTO> isPartOf = new ArrayList<>();
    private LearningEntitlementSpecificationDTO specifiedBy;

    public String getOCBID() {
        return OCBID;
    }

    public void setOCBID(String OCBID) {
        this.OCBID = OCBID;
    }
    public ZonedDateTime getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(ZonedDateTime dateIssued) {
        this.dateIssued = dateIssued;
    }

    public List<LearningAchievementDTO> getEntitledBy() {
        return entitledBy;
    }

    public ZonedDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(ZonedDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<LearningEntitlementDTO> getHasPart() {
        return hasPart;
    }

    public LearningEntitlementSpecificationDTO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningEntitlementSpecificationDTO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<LearningEntitlementDTO> getIsPartOf() {
        return isPartOf;
    }

    public void setEntitledBy(List<LearningAchievementDTO> entitledBy) {
        this.entitledBy = entitledBy;
    }

    public void setHasPart(List<LearningEntitlementDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setIsPartOf(List<LearningEntitlementDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningEntitlementDTO)) return false;
        if (!super.equals(o)) return false;
        LearningEntitlementDTO that = (LearningEntitlementDTO) o;
        return Objects.equals(dateIssued, that.dateIssued) &&
                Objects.equals(entitledBy, that.entitledBy) &&
                Objects.equals(expiryDate, that.expiryDate) &&
                Objects.equals(hasPart, that.hasPart) &&
                Objects.equals(specifiedBy, that.specifiedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateIssued, entitledBy, expiryDate, hasPart, specifiedBy);
    }
}
