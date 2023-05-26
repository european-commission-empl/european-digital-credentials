package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningActivitySpec:")
public class QDRLearningActivitySpecificationDTO extends QDRSpecificationDTO {

    private List<String> contactHour = new ArrayList<>();
    private List<QDRLearningActivitySpecificationDTO> generalisationOf = new ArrayList<>();
    private List<QDRLearningActivitySpecificationDTO> hasPart = new ArrayList<>();
    private List<QDRLearningAchievementSpecificationDTO> influences = new ArrayList<>();
    private List<QDRLearningActivitySpecificationDTO> isPartOf = new ArrayList<>();
    @MandatoryConceptScheme("http://publications.europa.eu/resource/authority/language")
    private List<QDRConceptDTO> language = new ArrayList<>();
    private List<QDRConceptDTO> mode = new ArrayList<>();
    private List<QDRLearningActivitySpecificationDTO> specialisationOf = new ArrayList<>();
    private Period volumeOfLearning;

    public List<String> getContactHour() {
        return contactHour;
    }

    public List<QDRLearningActivitySpecificationDTO> getHasPart() {
        return hasPart;
    }

    public List<QDRLearningAchievementSpecificationDTO> getInfluences() {
        return influences;
    }

    public List<QDRConceptDTO> getLanguage() {
        return language;
    }

    public List<QDRConceptDTO> getMode() {
        return mode;
    }

    public List<QDRLearningActivitySpecificationDTO> getSpecialisationOf() {
        return specialisationOf;
    }

    public Period getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(Period volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
    }

    public List<QDRLearningActivitySpecificationDTO> getGeneralisationOf() {
        return generalisationOf;
    }

    public List<QDRLearningActivitySpecificationDTO> getIsPartOf() {
        return isPartOf;
    }

    public void setContactHour(List<String> contactHour) {
        this.contactHour = contactHour;
    }

    public void setGeneralisationOf(List<QDRLearningActivitySpecificationDTO> generalisationOf) {
        this.generalisationOf = generalisationOf;
    }

    public void setHasPart(List<QDRLearningActivitySpecificationDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setInfluences(List<QDRLearningAchievementSpecificationDTO> influences) {
        this.influences = influences;
    }

    public void setIsPartOf(List<QDRLearningActivitySpecificationDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setLanguage(List<QDRConceptDTO> language) {
        this.language = language;
    }

    public void setMode(List<QDRConceptDTO> mode) {
        this.mode = mode;
    }

    public void setSpecialisationOf(List<QDRLearningActivitySpecificationDTO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRLearningActivitySpecificationDTO)) return false;
        if (!super.equals(o)) return false;
        QDRLearningActivitySpecificationDTO that = (QDRLearningActivitySpecificationDTO) o;
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
