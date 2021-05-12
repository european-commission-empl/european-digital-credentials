package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.eclipse.persistence.oxm.annotations.XmlIDExtension;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.List;

@EDCIIdentifier(prefix = "urn:epass:asssessmentspec:")
@XmlType(propOrder = {"id", "identifier", "title", "alternativeLabel", "description", "additionalNote", "homePage", "supplementaryDocument", "assessmentType", "language", "mode", "gradingScheme", "proves", "hasPart"})
@XmlRootElement(name = "assessmentSpecification")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssessmentSpecificationDTO implements Identifiable, Nameable {

    @XmlAttribute
    @XmlID
    @XmlIDExtension
    @NotNull(message = MessageKeys.Validation.VALIDATION_ASSESSMENT_ID_NOTNULL)
    private URI id; //1
    @XmlTransient
    private String pk;
    @Valid
    private List<Identifier> identifier; //*
    @Valid
    @XmlElement(name = "type")
    private List<Code> assessmentType; //*
    @Valid
    private Text title; //0..1
    @Valid
    @XmlElement(name = "altLabel")
    private List<Text> alternativeLabel; //*
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
    @Valid
    private List<Code> language; //*
    @Valid
    private Code mode; //0..1
    @XmlIDREF
    @XmlPath("gradingScheme/@idref")
    @Valid
    private ScoringSchemeDTO gradingScheme; //*
    @XmlIDREF
    @XmlPath("proves/@idref")
    @Valid
    private List<LearningSpecificationDTO> proves; //*
    @XmlIDREF
    @XmlPath("hasPart/@idref")
    @Valid
    private List<AssessmentSpecificationDTO> hasPart; //*

    public AssessmentSpecificationDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "description", "identifier", "additionalNote", "assessmentType", "mode");
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

    public List<Code> getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(List<Code> assessmentType) {
        this.assessmentType = assessmentType;
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

    public ScoringSchemeDTO getGradingScheme() {
        return gradingScheme;
    }

    public void setGradingScheme(ScoringSchemeDTO gradingSchemes) {
        this.gradingScheme = gradingSchemes;
    }

    public List<LearningSpecificationDTO> getProves() {
        return proves;
    }

    public void setProves(List<LearningSpecificationDTO> proves) {
        this.proves = proves;
    }

    public List<AssessmentSpecificationDTO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(List<AssessmentSpecificationDTO> hasPart) {
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


    public String getPk() {
        return pk;
    }

}