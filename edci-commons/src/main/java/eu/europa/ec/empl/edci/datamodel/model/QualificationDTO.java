package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "qualification")
@EDCIIdentifier(prefix = "urn:epass:qualification:")
@XmlType(name = "QualificationV2", propOrder = {"isPartialQualification", "eqfLevel", "nqfLevel", "hasAccreditation", "qualificationCode"})
public class QualificationDTO extends LearningSpecificationDTO {

    @Valid
    private Code eqfLevel; //0..1
    @Valid
    private List<Code> nqfLevel; //*
    private Boolean isPartialQualification; //0..1
    @XmlIDREF
    @XmlPath("hasAccreditation/@idref")
    @Valid
    @XmlElement(name = "hasAccreditation")
    private List<AccreditationDTO> hasAccreditation; //*
    private List<Code> qualificationCode; //*

    public QualificationDTO() {
        this.initIdentifiable();
    }

    public QualificationDTO(LearningSpecificationDTO learningSpecificationDTO) {
        this.setId(learningSpecificationDTO.getId());
        this.setIdentifier(learningSpecificationDTO.getIdentifier());
        this.setLearningOpportunityType(learningSpecificationDTO.getLearningOpportunityType());
        this.setTitle(learningSpecificationDTO.getTitle());
        this.setAlternativeLabel(learningSpecificationDTO.getAlternativeLabel());
        this.setDefinition(learningSpecificationDTO.getDefinition());
        this.setLearningOutcomeDescription(learningSpecificationDTO.getLearningOutcomeDescription());
        this.setAdditionalNote(learningSpecificationDTO.getAdditionalNote());
        this.setHomePage(learningSpecificationDTO.getHomePage());
        this.setSupplementaryDocument(learningSpecificationDTO.getSupplementaryDocument());
        this.setIscedFCode(learningSpecificationDTO.getIscedFCode());
        this.setEducationSubject(learningSpecificationDTO.getEducationSubject());
        this.setVolumeOfLearning(learningSpecificationDTO.getVolumeOfLearning());
        this.setEctsCreditPoints(learningSpecificationDTO.getEctsCreditPoints());
        this.setCreditPoints(learningSpecificationDTO.getCreditPoints());
        this.setEducationLevel(learningSpecificationDTO.getEducationLevel());
        this.setLanguage(learningSpecificationDTO.getLanguage());
        this.setMode(learningSpecificationDTO.getMode());
        this.setLearningSetting(learningSpecificationDTO.getLearningSetting());
        this.setMaximumDuration(learningSpecificationDTO.getMaximumDuration());
        this.setTargetGroup(learningSpecificationDTO.getTargetGroup());
        this.setEntryRequirementNote(learningSpecificationDTO.getEntryRequirementNote());
        this.setLearningOutcome(learningSpecificationDTO.getLearningOutcome());
        this.setAwardingOpportunity(learningSpecificationDTO.getAwardingOpportunity());
        this.setSpecializationOf(learningSpecificationDTO.getSpecializationOf());
        this.initIdentifiable();
    }

    public Code getEqfLevel() {
        return eqfLevel;
    }

    public void setEqfLevel(Code eqfLevel) {
        this.eqfLevel = eqfLevel;
    }

    public List<Code> getNqfLevel() {
        return nqfLevel;
    }

    public void setNqfLevel(List<Code> nqfLevel) {
        this.nqfLevel = nqfLevel;
    }

    public Boolean getIsPartialQualification() {
        return isPartialQualification;
    }

    public void setIsPartialQualification(Boolean partialQualification) {
        isPartialQualification = partialQualification;
    }

    public List<AccreditationDTO> getHasAccreditation() {
        return hasAccreditation;
    }

    public void setHasAccreditation(List<AccreditationDTO> hasAccreditation) {
        this.hasAccreditation = hasAccreditation;
    }

    public List<Code> getQualificationCode() {
        return qualificationCode;
    }

    public void setQualificationCode(List<Code> qualificationCode) {
        this.qualificationCode = qualificationCode;
    }

    public String getSpecificationType() {
        return "qualification";
    }


}