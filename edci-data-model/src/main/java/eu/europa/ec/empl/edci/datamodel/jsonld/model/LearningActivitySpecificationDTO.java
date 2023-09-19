package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningActivitySpec:")
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", visible = true, defaultImpl = LearningActivitySpecificationDTO.class)
public class LearningActivitySpecificationDTO extends SpecificationDTO {

    private List<String> contactHour = new ArrayList<>();
    private List<LearningActivitySpecificationDTO> generalisationOf = new ArrayList<>();
    private List<LearningActivitySpecificationDTO> hasPart = new ArrayList<>();
    private List<LearningAchievementSpecificationDTO> influences = new ArrayList<>();
    private List<LearningActivitySpecificationDTO> isPartOf = new ArrayList<>();
    private List<ConceptDTO> language = new ArrayList<>();
    private List<ConceptDTO> mode = new ArrayList<>();
    private List<LearningActivitySpecificationDTO> specialisationOf = new ArrayList<>();
    private Period volumeOfLearning;

    public LearningActivitySpecificationDTO() {
        super();
    }

    @JsonCreator
    public LearningActivitySpecificationDTO(String uri) {
        super(uri);
    }

    public List<String> getContactHour() {
        return contactHour;
    }

    public List<LearningActivitySpecificationDTO> getHasPart() {
        return hasPart;
    }

    public List<LearningAchievementSpecificationDTO> getInfluences() {
        return influences;
    }

    public List<ConceptDTO> getLanguage() {
        return language;
    }

    public List<ConceptDTO> getMode() {
        return mode;
    }

    public List<LearningActivitySpecificationDTO> getSpecialisationOf() {
        return specialisationOf;
    }

    public Period getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(Period volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
    }

    public List<LearningActivitySpecificationDTO> getGeneralisationOf() {
        return generalisationOf;
    }

    public List<LearningActivitySpecificationDTO> getIsPartOf() {
        return isPartOf;
    }

    public void setContactHour(List<String> contactHour) {
        this.contactHour = contactHour;
    }

    public void setGeneralisationOf(List<LearningActivitySpecificationDTO> generalisationOf) {
        this.generalisationOf = generalisationOf;
    }

    public void setHasPart(List<LearningActivitySpecificationDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setInfluences(List<LearningAchievementSpecificationDTO> influences) {
        this.influences = influences;
    }

    public void setIsPartOf(List<LearningActivitySpecificationDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setLanguage(List<ConceptDTO> language) {
        this.language = language;
    }

    public void setMode(List<ConceptDTO> mode) {
        this.mode = mode;
    }

    public void setSpecialisationOf(List<LearningActivitySpecificationDTO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningActivitySpecificationDTO)) return false;
        if (!super.equals(o)) return false;
        LearningActivitySpecificationDTO that = (LearningActivitySpecificationDTO) o;
        return Objects.equals(contactHour, that.contactHour) &&
                Objects.equals(generalisationOf, that.generalisationOf) &&
                Objects.equals(hasPart, that.hasPart) &&
                Objects.equals(influences, that.influences) &&
                Objects.equals(isPartOf, that.isPartOf) &&
                Objects.equals(language, that.language) &&
                Objects.equals(mode, that.mode) &&
                Objects.equals(specialisationOf, that.specialisationOf) &&
                Objects.equals(volumeOfLearning, that.volumeOfLearning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contactHour, generalisationOf, hasPart, influences, isPartOf, language, mode, specialisationOf, volumeOfLearning);
    }
}
