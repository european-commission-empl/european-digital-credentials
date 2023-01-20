package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import org.eclipse.persistence.oxm.annotations.XmlIDExtension;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@EDCIIdentifier(prefix = "urn:epass:awardingProcess:")
@XmlRootElement(name = "awardingProcess")
@XmlType(propOrder = {"id", "identifier", "description", "additionalNote", "used", "learningAchievement", "awardingBody", "awardingLocation", "awardingDate"})
public class AwardingProcessDTO implements Identifiable, Nameable {

    @XmlID
    @XmlIDExtension
    @XmlAttribute
    private URI id; //1
    @XmlTransient
    private String pk;
    private List<Identifier> identifier; //*
    private Note description;
    private List<Note> additionalNote;
    private List<AssessmentDTO> used;
    //Transient or Gulag
    private List<LearningAchievementDTO> learningAchievement; // TODO 1..*
    @XmlIDREF
    @XmlPath("awardingBody/@idref")
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_AWARDINGPROCESS_AWARDINGBODY_NOTNULL)
    private OrganizationDTO awardingBody; //TODO 1..*
    @XmlElement(name = "location")
    private LocationDTO awardingLocation; //0..1

    @XmlElement(name = "date")
    private Date awardingDate; //0..1

    public AwardingProcessDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "identifier", "description", "additionalNote", "awardingLocation");
    }

    @Override
    public void setHashCodeSeed(String pk) {
        this.pk = pk;
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public Note getDescription() {
        return description;
    }

    public void setDescription(Note description) {
        this.description = description;
    }

    public List<Note> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<Note> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<AssessmentDTO> getUsed() {
        return used;
    }

    public void setUsed(List<AssessmentDTO> used) {
        this.used = used;
    }

    public List<LearningAchievementDTO> getLearningAchievement() {
        return learningAchievement;
    }

    public void setLearningAchievement(List<LearningAchievementDTO> learningAchievement) {
        this.learningAchievement = learningAchievement;
    }

    public OrganizationDTO getAwardingBody() {
        return awardingBody;
    }

    public void setAwardingBody(OrganizationDTO awardingBody) {
        this.awardingBody = awardingBody;
    }

    public LocationDTO getAwardingLocation() {
        return awardingLocation;
    }

    public void setAwardingLocation(LocationDTO awardingLocation) {
        this.awardingLocation = awardingLocation;
    }

    public Date getAwardingDate() {
        return awardingDate;
    }

    public void setAwardingDate(Date awardingDate) {
        this.awardingDate = awardingDate;
    }

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.isEquals(object);
    }


    public String getHashCodeSeed() {
        return pk;
    }

    //XML Getters
    public LocationDTO getLocation() {
        return this.awardingLocation;
    }

    public void setLocation(LocationDTO awardingLocation) {
        this.awardingLocation = awardingLocation;
    }

    public Date getDate() {
        return this.awardingDate;
    }

    public void setDate(Date awardingDate) {
        this.awardingDate = awardingDate;
    }

}