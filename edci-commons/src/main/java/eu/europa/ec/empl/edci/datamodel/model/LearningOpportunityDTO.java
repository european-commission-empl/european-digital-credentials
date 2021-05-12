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
import org.joda.time.Period;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "identifier", "title", "alternativeLabel", "description", "additionalNote", "homePage", "supplementaryDocument", "startedAtDate", "endedAtDate", "duration"
        , "providedBy", "providedAt", "learningSchedule", "scheduleInformation", "admissionProcedure", "priceDetails", "specifiedBy", "hasPart"})
@XmlRootElement(name = "learningOpportunity")
@EDCIIdentifier(prefix = "urn:epass:learningOpportunity:")
public class LearningOpportunityDTO implements Identifiable, Nameable {
    @XmlAttribute
    @XmlID
    @XmlIDExtension
    @NotNull(message = MessageKeys.Validation.VALIDATION_LEARNINGOPPORTUNITY_ID_NOTNULL)
    private URI id; //1
    @XmlTransient
    private String pk;
    @Valid
    private List<Identifier> identifier; //*
    @NotNull(message = MessageKeys.Validation.VALIDATION_LEARNINGOPPORTUNITY_TITLE_NOTNULL)
    @Valid
    private Text title; //1
    @Valid
    private List<Text> alternativeLabel; //*
    @Valid
    private Note description; //0..1
    @Valid
    private List<Note> additionalNote; //*
    @Valid
    private List<WebDocumentDTO> homePage; //*
    @Valid
    private List<WebDocumentDTO> supplementaryDocument; //*
    private Date startedAtDate; //0..1
    private Date endedAtDate; //0..1
    private Period duration; //0..1
    @XmlIDREF
    @XmlPath("providedBy/@idref")
    private OrganizationDTO providedBy; //*º
    private List<LocationDTO> providedAt; //*
    @Valid
    private Code learningSchedule; //0..1
    @Valid
    private Note scheduleInformation; //0..1
    @Valid
    private Note admissionProcedure; //0..1
    @Valid
    private List<PriceDetailsDTO> priceDetails; //*
    @XmlIDREF
    @XmlPath("specifiedBy/@idref")
    @NotNull(message = MessageKeys.Validation.VALIDATION_LEARNINGOPPORTUNITY_SPECIFIEDBY_NOTNULL)
    @Valid
    private LearningSpecificationDTO specifiedBy; //1
    @Valid
    private List<LearningOpportunityDTO> hasPart; //*

    public LearningOpportunityDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "identifier", "description", "additionalNote", "alternativeLabel", "specifiedBy", "id");
    }

    @Override
    public void setPk(String pk) {
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

    public Date getStartedAtDate() {
        return startedAtDate;
    }

    public void setStartedAtDate(Date startedAtDate) {
        this.startedAtDate = startedAtDate;
    }

    public Date getEndedAtDate() {
        return endedAtDate;
    }

    public void setEndedAtDate(Date endedAtDate) {
        this.endedAtDate = endedAtDate;
    }

    public Period getDuration() {
        return duration;
    }

    public void setDuration(Period duration) {
        this.duration = duration;
    }

    public OrganizationDTO getProvidedBy() {
        return providedBy;
    }

    public void setProvidedBy(OrganizationDTO providedBy) {
        this.providedBy = providedBy;
    }

    public List<LocationDTO> getProvidedAt() {
        return providedAt;
    }

    public void setProvidedAt(List<LocationDTO> providedAt) {
        this.providedAt = providedAt;
    }

    public Code getLearningSchedule() {
        return learningSchedule;
    }

    public void setLearningSchedule(Code learningSchedule) {
        this.learningSchedule = learningSchedule;
    }

    public Note getScheduleInformation() {
        return scheduleInformation;
    }

    public void setScheduleInformation(Note scheduleInformation) {
        this.scheduleInformation = scheduleInformation;
    }

    public Note getAdmissionProcedure() {
        return admissionProcedure;
    }

    public void setAdmissionProcedure(Note admissionProcedure) {
        this.admissionProcedure = admissionProcedure;
    }

    public List<PriceDetailsDTO> getPriceDetails() {
        return priceDetails;
    }

    public void setPriceDetails(List<PriceDetailsDTO> priceDetails) {
        this.priceDetails = priceDetails;
    }

    public LearningSpecificationDTO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningSpecificationDTO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<LearningOpportunityDTO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(List<LearningOpportunityDTO> hasPart) {
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