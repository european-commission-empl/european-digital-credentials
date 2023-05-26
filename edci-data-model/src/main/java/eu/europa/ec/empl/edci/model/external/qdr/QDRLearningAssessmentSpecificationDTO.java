package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningAssessmentSpec:")
public class QDRLearningAssessmentSpecificationDTO extends QDRSpecificationDTO {

    private List<QDRLearningAssessmentSpecificationDTO> generalisationOf = new ArrayList<>();
    private QDRGradingSchemeDTO gradingScheme;
    private List<QDRLearningAssessmentSpecificationDTO> hasPart = new ArrayList<>();
    private List<QDRLearningAssessmentSpecificationDTO> isPartOf = new ArrayList<>();
    @MandatoryConceptScheme("http://publications.europa.eu/resource/authority/language")
    private List<QDRConceptDTO> language = new ArrayList<>();
    private List<QDRConceptDTO> mode = new ArrayList<>();
    private List<QDRLearningAchievementSpecificationDTO> proves = new ArrayList<>();
    private List<QDRLearningAchievementSpecificationDTO> specialisationOf = new ArrayList<>();

    public QDRGradingSchemeDTO getGradingScheme() {
        return gradingScheme;
    }

    public void setGradingScheme(QDRGradingSchemeDTO gradingScheme) {
        this.gradingScheme = gradingScheme;
    }

    public List<QDRLearningAssessmentSpecificationDTO> getHasPart() {
        return hasPart;
    }

    public List<QDRConceptDTO> getMode() {
        return mode;
    }

    public List<QDRLearningAchievementSpecificationDTO> getProves() {
        return proves;
    }

    public List<QDRLearningAchievementSpecificationDTO> getSpecialisationOf() {
        return specialisationOf;
    }

    public List<QDRLearningAssessmentSpecificationDTO> getGeneralisationOf() {
        return generalisationOf;
    }

    public List<QDRLearningAssessmentSpecificationDTO> getIsPartOf() {
        return isPartOf;
    }

    public List<QDRConceptDTO> getLanguage() {
        return language;
    }

    public void setGeneralisationOf(List<QDRLearningAssessmentSpecificationDTO> generalisationOf) {
        this.generalisationOf = generalisationOf;
    }

    public void setHasPart(List<QDRLearningAssessmentSpecificationDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setIsPartOf(List<QDRLearningAssessmentSpecificationDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setLanguage(List<QDRConceptDTO> language) {
        this.language = language;
    }

    public void setMode(List<QDRConceptDTO> mode) {
        this.mode = mode;
    }

    public void setProves(List<QDRLearningAchievementSpecificationDTO> proves) {
        this.proves = proves;
    }

    public void setSpecialisationOf(List<QDRLearningAchievementSpecificationDTO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRLearningAssessmentSpecificationDTO)) return false;
        if (!super.equals(o)) return false;
        QDRLearningAssessmentSpecificationDTO that = (QDRLearningAssessmentSpecificationDTO) o;
        return Objects.equals(generalisationOf, that.generalisationOf) &&
                Objects.equals(gradingScheme, that.gradingScheme) &&
                Objects.equals(hasPart, that.hasPart) &&
                Objects.equals(isPartOf, that.isPartOf) &&
                Objects.equals(language, that.language) &&
                Objects.equals(mode, that.mode) &&
                Objects.equals(proves, that.proves) &&
                Objects.equals(specialisationOf, that.specialisationOf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), generalisationOf, gradingScheme, hasPart, isPartOf, language, mode, proves, specialisationOf);
    }
}
