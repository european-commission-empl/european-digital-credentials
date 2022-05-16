package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.adapter.DurationAdapter;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.base.RootEntity;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.joda.time.Period;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "activity")
@XmlType(name = "LearningActivityV2", propOrder = {"id", "identifier", "title", "description", "additionalNote", "workload", "startedAtTime", "endedAtTime", "directedBy",
        "location", "usedLearningOpportunity", "specifiedBy", "hasPart"})
@XmlAccessorType(XmlAccessType.FIELD)
@EDCIIdentifier(prefix = "urn:epass:activity:")
public class LearningActivityDTO implements RootEntity, Nameable {

    @XmlTransient
    private String pk;
    @XmlAttribute
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_LEARNINGACTIVITY_ID_NOTNULL)
    private URI id; //1
    @Valid
    private List<Identifier> identifier; //*
    @Valid
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_LEARNINGACTIVITY_TITLE_NOTNULL)
    private Text title; //1
    @Valid
    private Note description; //0..1
    @Valid
    private List<Note> additionalNote; //*
    @XmlJavaTypeAdapter(DurationAdapter.class)
    private Period workload; //0..1Q
    @Valid
    private Date startedAtTime; //0..1
    @Valid
    private Date endedAtTime; //0..1
    @XmlIDREF
    @XmlPath("directedBy/@idref")
    private OrganizationDTO directedBy; //*
    @Valid
    private List<LocationDTO> location; //*
    @XmlIDREF
    @XmlPath("usedLearningOpportunity/@idref")
    @Valid
    private LearningOpportunityDTO usedLearningOpportunity; //0..1
    @XmlIDREF
    @XmlPath("specifiedBy/@idref")
    @Valid
    private LearningActivitySpecificationDTO specifiedBy; //1
    @Valid
    private List<LearningActivityDTO> hasPart; //*

    public LearningActivityDTO() {
        this.initIdentifiable();
    }

    @Override
    public URI getId() {
        return id;
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "description", "influnced", "directedBy", "id");
    }

    @Override
    public void setId(URI id) {
        this.id = id;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
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

    public Period getWorkload() {
        return workload;
    }

    public void setWorkload(Period workload) {
        this.workload = workload;
    }

    public void parseWorkload(Object hours) {
        if (!StringUtils.isEmpty(hours)) {
            workload = Period.parse("PT" + Double.valueOf(hours.toString()).intValue() + "H");
        }
    }

    public Date getStartedAtTime() {
        return startedAtTime;
    }

    public void setStartedAtTime(Date startedAtTime) {
        this.startedAtTime = startedAtTime;
    }

    public Date getEndedAtTime() {
        return endedAtTime;
    }

    public void setEndedAtTime(Date endedAtTime) {
        this.endedAtTime = endedAtTime;
    }

    public OrganizationDTO getDirectedBy() {
        return directedBy;
    }

    public void setDirectedBy(OrganizationDTO directedBy) {
        this.directedBy = directedBy;
    }

    public List<LocationDTO> getLocation() {
        return location;
    }

    public void setLocation(List<LocationDTO> location) {
        this.location = location;
    }

    public LearningActivitySpecificationDTO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningActivitySpecificationDTO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<LearningActivityDTO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(List<LearningActivityDTO> hasPart) {
        this.hasPart = hasPart;
    }

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.isEquals(object);
    }

    @Override
    public String getPk() {
        return pk;
    }

    @Override
    public void setPk(String pk) {
        this.pk = pk;
    }

}