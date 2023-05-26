package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:qualification:")
public class QDRQualificationDTO extends QDRLearningAchievementSpecificationDTO {

    @MandatoryConceptScheme("http://data.europa.eu/snb/qdr/25831c2")
    private List<QDRConceptDTO> nqfLevel = new ArrayList<>();
    @MandatoryConceptScheme("http://data.europa.eu/snb/eqf/25831c2")
    private QDRConceptDTO eqfLevel;
    private List<QDRAccreditationDTO> accreditation = new ArrayList<>();
    private Boolean isPartialQualification;
    private List<QDRConceptDTO> qualificationCode = new ArrayList<>();

    public List<QDRConceptDTO> getNqfLevel() {
        return nqfLevel;
    }

    public void setNqfLevel(List<QDRConceptDTO> nqfLevel) {
        this.nqfLevel = nqfLevel;
    }

    public QDRConceptDTO getEqfLevel() {
        return eqfLevel;
    }

    public void setEqfLevel(QDRConceptDTO eqfLevel) {
        this.eqfLevel = eqfLevel;
    }

    public List<QDRAccreditationDTO> getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(List<QDRAccreditationDTO> accreditation) {
        this.accreditation = accreditation;
    }

    public List<QDRConceptDTO> getQualificationCode() {
        return qualificationCode;
    }

    public Boolean getPartialQualification() {
        return isPartialQualification;
    }

    public void setPartialQualification(Boolean partialQualification) {
        isPartialQualification = partialQualification;
    }

    public void setQualificationCode(List<QDRConceptDTO> qualificationCode) {
        this.qualificationCode = qualificationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRQualificationDTO)) return false;
        if (!super.equals(o)) return false;
        QDRQualificationDTO that = (QDRQualificationDTO) o;
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
