package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningAssessmentSpec:")
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", visible = true, defaultImpl = LearningAssessmentSpecificationDTO.class)
public class LearningAssessmentSpecificationDTO extends SpecificationDTO {

    private List<LearningAssessmentSpecificationDTO> generalisationOf = new ArrayList<>();
    private GradingSchemeDTO gradingScheme;
    private List<LearningAssessmentSpecificationDTO> hasPart = new ArrayList<>();
    private List<LearningAssessmentSpecificationDTO> isPartOf = new ArrayList<>();
    private List<ConceptDTO> language = new ArrayList<>();
    private List<ConceptDTO> mode = new ArrayList<>();
    private List<LearningAchievementSpecificationDTO> proves = new ArrayList<>();
    private List<LearningAchievementSpecificationDTO> specialisationOf = new ArrayList<>();

    public LearningAssessmentSpecificationDTO() {
        super();
    }

    @JsonCreator
    public LearningAssessmentSpecificationDTO(String uri) {
        super(uri);
    }

    public GradingSchemeDTO getGradingScheme() {
        return gradingScheme;
    }

    public void setGradingScheme(GradingSchemeDTO gradingScheme) {
        this.gradingScheme = gradingScheme;
    }

    public List<LearningAssessmentSpecificationDTO> getHasPart() {
        return hasPart;
    }

    public List<ConceptDTO> getMode() {
        return mode;
    }

    public List<LearningAchievementSpecificationDTO> getProves() {
        return proves;
    }

    public List<LearningAchievementSpecificationDTO> getSpecialisationOf() {
        return specialisationOf;
    }

    public List<LearningAssessmentSpecificationDTO> getGeneralisationOf() {
        return generalisationOf;
    }

    public List<LearningAssessmentSpecificationDTO> getIsPartOf() {
        return isPartOf;
    }

    public List<ConceptDTO> getLanguage() {
        return language;
    }

    public void setGeneralisationOf(List<LearningAssessmentSpecificationDTO> generalisationOf) {
        this.generalisationOf = generalisationOf;
    }

    public void setHasPart(List<LearningAssessmentSpecificationDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setIsPartOf(List<LearningAssessmentSpecificationDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setLanguage(List<ConceptDTO> language) {
        this.language = language;
    }

    public void setMode(List<ConceptDTO> mode) {
        this.mode = mode;
    }

    public void setProves(List<LearningAchievementSpecificationDTO> proves) {
        this.proves = proves;
    }

    public void setSpecialisationOf(List<LearningAchievementSpecificationDTO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningAssessmentSpecificationDTO)) return false;
        if (!super.equals(o)) return false;
        LearningAssessmentSpecificationDTO that = (LearningAssessmentSpecificationDTO) o;
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
