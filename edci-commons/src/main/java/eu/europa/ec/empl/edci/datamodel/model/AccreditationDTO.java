package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.adapter.DateAdapter;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.*;
import org.eclipse.persistence.oxm.annotations.XmlIDExtension;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "accreditation")
@EDCIIdentifier(prefix = "urn:epass:accreditationspec:")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "identifier", "accreditationType", "title", "description", "decision", "report", "limitQualification", "limitField", "limitEqfLevel", "limitJurisdiction"
        , "accreditingAgent", "issueDate", "reviewDate", "expiryDate", "additionalNote", "homePage", "supplementaryDocument"})
public class AccreditationDTO implements Identifiable, Nameable {

    @XmlAttribute
    @XmlID
    @XmlIDExtension
    private URI id; //0..1
    @XmlTransient
    private String pk;
    @Valid
    private List<Identifier> identifier; //*
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ACCREDITATION_ACCREDITATIONTYPE_NOTNULL)
    @Valid
    @XmlElement(name = "type")
    private Code accreditationType; //1º
    @Valid
    private Text title; //0..1
    @Valid
    private Note description; //*
    @Valid
    private Score decision; //0..1
    @Valid
    private WebDocumentDTO report; //0..1
    @XmlTransient
    @Valid
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ACCREDITAION_ORGANISATION_NOTNULL)
    private OrganizationDTO organization; //1
    @Valid
    private QualificationDTO limitQualification; //*
    @Valid
    private List<Code> limitField; //*
    @Valid
    @XmlElement(name = "limitEQFLevel")
    private List<Code> limitEqfLevel; //*
    @Valid
    private List<Code> limitJurisdiction; //*
    @XmlIDREF
    @XmlPath("accreditingAgent/@idRef")
    @Valid
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ACCREDITAION_ORGANISATION_NOTNULL)
    private OrganizationDTO accreditingAgent; //1
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date issueDate; //0..1
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date reviewDate; //0..1
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date expiryDate; //0..1
    @Valid
    private List<Note> additionalNote; //*
    @Valid
    @XmlElement(name = "homepage")
    private List<WebDocumentDTO> homePage; //*
    @Valid
    @XmlElement(name = "supplementaryDoc")
    private List<WebDocumentDTO> supplementaryDocument; //*

    public AccreditationDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "identifier", "description", "id");
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

    public Code getAccreditationType() {
        return accreditationType;
    }

    public void setAccreditationType(Code accreditationType) {
        this.accreditationType = accreditationType;
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

    public Score getDecision() {
        return decision;
    }

    public void setDecision(Score decision) {
        this.decision = decision;
    }

    public WebDocumentDTO getReport() {
        return report;
    }

    public void setReport(WebDocumentDTO report) {
        this.report = report;
    }

    public QualificationDTO getLimitQualification() {
        return limitQualification;
    }

    public void setLimitQualification(QualificationDTO limitQualification) {
        this.limitQualification = limitQualification;
    }

    public List<Code> getLimitField() {
        return limitField;
    }

    public void setLimitField(List<Code> limitField) {
        this.limitField = limitField;
    }

    public List<Code> getLimitEqfLevel() {
        return limitEqfLevel;
    }

    public void setLimitEqfLevel(List<Code> limitEqfLevel) {
        this.limitEqfLevel = limitEqfLevel;
    }

    public List<Code> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(List<Code> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public OrganizationDTO getAccreditingAgent() {
        return accreditingAgent;
    }

    public void setAccreditingAgent(OrganizationDTO accreditingAgent) {
        this.accreditingAgent = accreditingAgent;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
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