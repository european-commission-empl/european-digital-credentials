package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:qualification:")
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", visible = true, defaultImpl = QualificationDTO.class)
public class QualificationDTO extends LearningAchievementSpecificationDTO {

    private List<ConceptDTO> nqfLevel = new ArrayList<>();
    private ConceptDTO eqfLevel;
    private List<AccreditationDTO> accreditation = new ArrayList<>();
    private Boolean isPartialQualification;
    private List<ConceptDTO> qualificationCode = new ArrayList<>();

    public QualificationDTO() {
        super();
    }

    @JsonCreator
    public QualificationDTO(String uri) {
        super(uri);
    }


    public List<ConceptDTO> getNqfLevel() {
        return nqfLevel;
    }

    public void setNqfLevel(List<ConceptDTO> nqfLevel) {
        this.nqfLevel = nqfLevel;
    }

    public ConceptDTO getEqfLevel() {
        return eqfLevel;
    }

    public void setEqfLevel(ConceptDTO eqfLevel) {
        this.eqfLevel = eqfLevel;
    }

    public List<AccreditationDTO> getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(List<AccreditationDTO> accreditation) {
        this.accreditation = accreditation;
    }

    public List<ConceptDTO> getQualificationCode() {
        return qualificationCode;
    }

    public Boolean getPartialQualification() {
        return isPartialQualification;
    }

    public void setPartialQualification(Boolean partialQualification) {
        isPartialQualification = partialQualification;
    }

    public void setQualificationCode(List<ConceptDTO> qualificationCode) {
        this.qualificationCode = qualificationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QualificationDTO)) return false;
        if (!super.equals(o)) return false;
        QualificationDTO that = (QualificationDTO) o;
        return Objects.equals(nqfLevel, that.nqfLevel) &&
                Objects.equals(eqfLevel, that.eqfLevel) &&
                Objects.equals(accreditation, that.accreditation) &&
                Objects.equals(isPartialQualification, that.isPartialQualification) &&
                Objects.equals(qualificationCode, that.qualificationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nqfLevel, eqfLevel, accreditation, isPartialQualification, qualificationCode);
    }
}
