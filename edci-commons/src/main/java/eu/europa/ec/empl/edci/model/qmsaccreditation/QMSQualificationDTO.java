package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.Period;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSQualificationDTO {
    private URI uri;
    private List<QMSIdentifierDTO> identifiers;
    private List<QMSCodeDTO> learningOpportunityTypes;
    private QMSLabelDTO title;
    private List<QMSLabelDTO> alternativeLabels;
    private QMSNoteDTO definition;
    private QMSNoteDTO learningOutcomeDescription;
    private List<QMSNoteDTO> additionalNotes;
    private List<URL> homepages;
    private List<URL> supplementaryDocuments;
    private List<QMSCodeDTO> iscedFCodes;
    private List<QMSCodeDTO> educationSubjects;
    private Period volumeOfLearning;
    private List<QMSCodeDTO> educationLevels;
    private List<QMSCodeDTO> languages;
    private List<QMSCodeDTO> modes;
    private QMSCodeDTO learningSetting;
    private Period maximumDuration;
    private List<QMSCodeDTO> targetGroups;
    private QMSNoteDTO entryRequirementNote;
    private QMSCodeDTO eqfLevel;
    private List<QMSCodeDTO> nqfLevels;
    private boolean isPartialQualification;
    private List<QMSAccreditationDTO> accreditations;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public List<QMSIdentifierDTO> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<QMSIdentifierDTO> identifiers) {
        this.identifiers = identifiers;
    }

    public List<QMSCodeDTO> getLearningOpportunityTypes() {
        return learningOpportunityTypes;
    }

    public void setLearningOpportunityTypes(List<QMSCodeDTO> learningOpportunityTypes) {
        this.learningOpportunityTypes = learningOpportunityTypes;
    }

    public QMSLabelDTO getTitle() {
        return title;
    }

    public void setTitle(QMSLabelDTO title) {
        this.title = title;
    }

    public List<QMSLabelDTO> getAlternativeLabels() {
        return alternativeLabels;
    }

    public void setAlternativeLabels(List<QMSLabelDTO> alternativeLabels) {
        this.alternativeLabels = alternativeLabels;
    }

    public QMSNoteDTO getDefinition() {
        return definition;
    }

    public void setDefinition(QMSNoteDTO definition) {
        this.definition = definition;
    }

    public QMSNoteDTO getLearningOutcomeDescription() {
        return learningOutcomeDescription;
    }

    public void setLearningOutcomeDescription(QMSNoteDTO learningOutcomeDescription) {
        this.learningOutcomeDescription = learningOutcomeDescription;
    }

    public List<QMSNoteDTO> getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(List<QMSNoteDTO> additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public List<URL> getHomepages() {
        return homepages;
    }

    public void setHomepages(List<URL> homepages) {
        this.homepages = homepages;
    }

    public List<URL> getSupplementaryDocuments() {
        return supplementaryDocuments;
    }

    public void setSupplementaryDocuments(List<URL> supplementaryDocuments) {
        this.supplementaryDocuments = supplementaryDocuments;
    }

    public List<QMSCodeDTO> getIscedFCodes() {
        return iscedFCodes;
    }

    public void setIscedFCodes(List<QMSCodeDTO> iscedFCodes) {
        this.iscedFCodes = iscedFCodes;
    }

    public List<QMSCodeDTO> getEducationSubjects() {
        return educationSubjects;
    }

    public void setEducationSubjects(List<QMSCodeDTO> educationSubjects) {
        this.educationSubjects = educationSubjects;
    }

    public Period getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(Period volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
    }

    public List<QMSCodeDTO> getEducationLevels() {
        return educationLevels;
    }

    public void setEducationLevels(List<QMSCodeDTO> educationLevels) {
        this.educationLevels = educationLevels;
    }

    public List<QMSCodeDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<QMSCodeDTO> languages) {
        this.languages = languages;
    }

    public List<QMSCodeDTO> getModes() {
        return modes;
    }

    public void setModes(List<QMSCodeDTO> modes) {
        this.modes = modes;
    }

    public QMSCodeDTO getLearningSetting() {
        return learningSetting;
    }

    public void setLearningSetting(QMSCodeDTO learningSetting) {
        this.learningSetting = learningSetting;
    }

    public Period getMaximumDuration() {
        return maximumDuration;
    }

    public void setMaximumDuration(Period maximumDuration) {
        this.maximumDuration = maximumDuration;
    }

    public List<QMSCodeDTO> getTargetGroups() {
        return targetGroups;
    }

    public void setTargetGroups(List<QMSCodeDTO> targetGroups) {
        this.targetGroups = targetGroups;
    }

    public QMSNoteDTO getEntryRequirementNote() {
        return entryRequirementNote;
    }

    public void setEntryRequirementNote(QMSNoteDTO entryRequirementNote) {
        this.entryRequirementNote = entryRequirementNote;
    }

    public QMSCodeDTO getEqfLevel() {
        return eqfLevel;
    }

    public void setEqfLevel(QMSCodeDTO eqfLevel) {
        this.eqfLevel = eqfLevel;
    }

    public List<QMSCodeDTO> getNqfLevels() {
        return nqfLevels;
    }

    public void setNqfLevels(List<QMSCodeDTO> nqfLevels) {
        this.nqfLevels = nqfLevels;
    }

    public boolean getIsPartialQualification() {
        return isPartialQualification;
    }

    public void setPartialQualification(boolean partialQualification) {
        isPartialQualification = partialQualification;
    }

    public List<QMSAccreditationDTO> getAccreditations() {
        return accreditations;
    }

    public void setAccreditations(List<QMSAccreditationDTO> accreditations) {
        this.accreditations = accreditations;
    }

    public List<QMSIdentifierDTO> getAllAvailableIdentifiers() {
        List<QMSIdentifierDTO> availableIdentifiers = new ArrayList<>();
        if (this.getIdentifiers() != null) availableIdentifiers.addAll(this.getIdentifiers());
        return availableIdentifiers;
    }
}
