package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.base.RootEntity;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.eclipse.persistence.oxm.annotations.XmlIDExtension;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "learningOutcome")
@XmlType(propOrder = {"id", "identifier", "name", "description", "learningOutcomeType", "reusabilityLevel", "relatedSkill", "relatedESCOSkill"})
@EDCIIdentifier(prefix = "urn:epass:learningOutcome:")
public class LearningOutcomeDTO implements RootEntity, Nameable {
    @XmlTransient
    private String pk;
    @XmlAttribute
    @XmlID
    @XmlIDExtension
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_LEARNINGOPPORTUNITY_ID_NOTNULL)
    private URI id; //1
    @Valid
    private List<Identifier> identifier; //*
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_LEARNINGOUTCOME_NAME_NOTNULL)
    @Valid
    @XmlElement(name = "prefLabel")
    private Text name; //1
    @Valid
    private Note description; //0..1
    @Valid
    private Code learningOutcomeType; //0..1
    @Valid
    private Code reusabilityLevel; //0..1
    @Valid
    @XmlElement(name = "relatedSkill")
    private List<Code> relatedSkill;
    @Valid
    @XmlElement(name = "relatedEscoSkill")
    private List<Code> relatedESCOSkill; //*

    public LearningOutcomeDTO() {
        this.initIdentifiable();
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "name", "identifier", "description", "id");
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Note getDescription() {
        return description;
    }

    public void setDescription(Note description) {
        this.description = description;
    }

    public Code getLearningOutcomeType() {
        return learningOutcomeType;
    }

    public void setLearningOutcomeType(Code learningOutcomeType) {
        this.learningOutcomeType = learningOutcomeType;
    }

    public Code getReusabilityLevel() {
        return reusabilityLevel;
    }

    public void setReusabilityLevel(Code reusabilityLevel) {
        this.reusabilityLevel = reusabilityLevel;
    }

    public List<Code> getRelatedSkill() {
        return relatedSkill;
    }

    public void setRelatedSkill(List<Code> relatedSkill) {
        this.relatedSkill = relatedSkill;
    }

    public List<Code> getRelatedESCOSkill() {
        return relatedESCOSkill;
    }

    public void setRelatedESCOSkill(List<Code> relatedESCOSkill) {
        this.relatedESCOSkill = relatedESCOSkill;
    }

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.isEquals(object);
    }


    public String getPk() {
        return pk;
    }

    @Override
    public void setPk(String pk) {
        this.pk = pk;
    }

    //XML Getter
    public Text getPrefLabel() {
        return this.name;
    }

    public List<Code> getRelatedSkills() {
        return this.relatedSkill;
    }

    public List<Code> getRelatedEscoSkill() {
        return this.relatedESCOSkill;
    }

    public List<Code> getRelatedEscoSkills() {
        return this.relatedESCOSkill;
    }
}