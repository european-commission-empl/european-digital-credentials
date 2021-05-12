package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.adapter.DurationAdapter;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.eclipse.persistence.oxm.annotations.XmlIDExtension;
import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.joda.time.Period;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.List;

@XmlRootElement(name = "learningActivitySpecification")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "identifier", "title", "alternativeLabel", "description", "additionalNote", "homePage", "supplementaryDocument", "learningActivityType", "workload", "language", "mode", "teaches", "specializationOf"})
@EDCIIdentifier(prefix = "urn:epass:learningactivityspec:")
public class LearningActivitySpecificationDTO implements Identifiable, Nameable {

    @XmlAttribute
    @XmlID
    @XmlIDExtension
    @NotNull(message = MessageKeys.Validation.VALIDATION_LEARNINGACTIVITYSPEC_ID_NOTNULL)
    private URI id; //1
    @XmlTransient
    private String pk;
    @Valid
    private List<Identifier> identifier; //*
    @XmlElement(name = "type")
    private List<Code> learningActivityType; //*
    @Valid
    private Text title; //0..1
    @Valid
    @XmlElement(name = "altLabel")
    private List<Text> alternativeLabel;
    @Valid
    private Note description; //0..1
    @Valid
    private List<Note> additionalNote; //*
    @Valid
    @XmlElement(name = "homepage")
    private List<WebDocumentDTO> homePage; //*
    @Valid
    @XmlElement(name = "supplementaryDoc")
    private List<WebDocumentDTO> supplementaryDocument; //*
    @XmlJavaTypeAdapter(DurationAdapter.class)
    private Period workload; //0..1
    @Valid
    private List<Code> language; //*
    @Valid
    private Code mode; //0..1
    @XmlIDREF
    @XmlPath("teaches/@idref")
    @Valid
    private List<LearningSpecificationDTO> teaches;
    @XmlIDREF
    @XmlPath("specializationOf/@idref")
    private List<LearningActivitySpecificationDTO> specializationOf; //0..1

    public LearningActivitySpecificationDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "identifier", "altenativeLabel", "description", "learningActivityType", "teaches", "id");
    }

    @Override
    public URI getId() {
        return id;
    }

    @Override
    public void setId(URI id) {
        this.id = id;
    }

    @Override
    public void setPk(String pk) {
        this.pk = pk;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public List<Code> getLearningActivityType() {
        return learningActivityType;
    }

    public void setLearningActivityType(List<Code> learningActivityType) {
        this.learningActivityType = learningActivityType;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public List<Text> getAlternativeLabel() {
        return alternativeLabel;
    }

    public void setAlternativeLabel(List<Text> alternativeLabel) {
        this.alternativeLabel = alternativeLabel;
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

    public List<WebDocumentDTO> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<WebDocumentDTO> homePage) {
        this.homePage = homePage;
    }

    public List<WebDocumentDTO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
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

    public List<Code> getLanguage() {
        return language;
    }

    public void setLanguage(List<Code> language) {
        this.language = language;
    }

    public Code getMode() {
        return mode;
    }

    public void setMode(Code mode) {
        this.mode = mode;
    }

    public List<LearningSpecificationDTO> getTeaches() {
        return teaches;
    }

    public void setTeaches(List<LearningSpecificationDTO> teaches) {
        this.teaches = teaches;
    }

    public List<LearningActivitySpecificationDTO> getSpecializationOf() {
        return specializationOf;
    }

    public void setSpecializationOf(List<LearningActivitySpecificationDTO> specializationOf) {
        this.specializationOf = specializationOf;
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
}