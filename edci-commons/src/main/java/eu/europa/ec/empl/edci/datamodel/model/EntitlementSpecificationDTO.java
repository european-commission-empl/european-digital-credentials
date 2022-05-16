package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
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

@XmlRootElement(name = "entitlementSpecification")
@EDCIIdentifier(prefix = "urn:epass:entitlementspec:")
@XmlType(propOrder = {"id", "identifier", "title", "alternativeLabel", "description", "additionalNote", "homePage", "supplementaryDocument", "entitlementType", "status", "limitOrganization", "limitJurisdiction", "limitOccupation", "limitNationalOccupation", "mayResultFrom"})
@XmlAccessorType(XmlAccessType.FIELD)
public class EntitlementSpecificationDTO implements Identifiable, Nameable {

    @XmlAttribute
    @XmlID
    @XmlIDExtension
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ENTITLEMENTSPEC_ID_NOTNULL)
    private URI id; //1
    @XmlTransient
    private String pk;
    @Valid
    private List<Identifier> identifier; //*
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ENTITLEMENTSPEC_TYPE_NOTNULL)
    @Valid
    @XmlElement(name = "type")
    private Code entitlementType;
    @Valid
    private Text title; //0..1
    @Valid
    @XmlElement(name = "altLabel")
    private List<Text> alternativeLabel;
    @Valid
    private Note description; //0..1
    @Valid
    private List<Note> additionalNote;
    @Valid
    @XmlElement(name = "homepage")
    private List<WebDocumentDTO> homePage;
    @Valid
    @XmlElement(name = "supplementaryDoc")
    private List<WebDocumentDTO> supplementaryDocument;
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ENTITLEMENTSPEC_STATUS_NOTNULL)
    @Valid
    private Code status; //1
    @XmlIDREF
    @XmlPath("limitOrganization/@idref")
    private List<OrganizationDTO> limitOrganization; //?
    private List<Code> limitJurisdiction; //*
    private List<Code> limitOccupation; //*
    private List<Code> limitNationalOccupation; //*
    @XmlIDREF
    @XmlPath("mayResultFrom/@idref")
    @Valid
    private List<LearningSpecificationDTO> mayResultFrom; //*
    @XmlTransient
    @XmlPath("specializationOf/@idref")
    private List<EntitlementSpecificationDTO> specializationOf;

    public EntitlementSpecificationDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "alternativeLabel", "description", "additionalNote", "entitlementType", "status", "id");
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

    public Code getEntitlementType() {
        return entitlementType;
    }

    public void setEntitlementType(Code entitlementType) {
        this.entitlementType = entitlementType;
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

    public Code getStatus() {
        return status;
    }

    public void setStatus(Code status) {
        this.status = status;
    }

    public List<OrganizationDTO> getLimitOrganization() {
        return limitOrganization;
    }

    public void setLimitOrganization(List<OrganizationDTO> limitOrganization) {
        this.limitOrganization = limitOrganization;
    }

    public List<Code> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(List<Code> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public List<Code> getLimitOccupation() {
        return limitOccupation;
    }

    public void setLimitOccupation(List<Code> limitOccupation) {
        this.limitOccupation = limitOccupation;
    }

    public List<Code> getLimitNationalOccupation() {
        return limitNationalOccupation;
    }

    public void setLimitNationalOccupation(List<Code> limitNationalOccupation) {
        this.limitNationalOccupation = limitNationalOccupation;
    }

    public List<LearningSpecificationDTO> getMayResultFrom() {
        return mayResultFrom;
    }

    public void setMayResultFrom(List<LearningSpecificationDTO> mayResultFrom) {
        this.mayResultFrom = mayResultFrom;
    }

    public List<EntitlementSpecificationDTO> getSpecializationOf() {
        return specializationOf;
    }

    public void setSpecializationOf(List<EntitlementSpecificationDTO> specializationOf) {
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

    //XML Getters
    public Code getType() {
        return this.entitlementType;
    }

    public List<Text> getAltLabel() {
        return this.alternativeLabel;
    }

    public List<Text> getAltLabels() {
        return this.alternativeLabel;
    }

    public List<WebDocumentDTO> getHomepage() {
        return this.homePage;
    }

    public List<WebDocumentDTO> getHomepages() {
        return this.homePage;
    }

    public List<WebDocumentDTO> getSupplementaryDoc() {
        return this.supplementaryDocument;
    }

    public List<WebDocumentDTO> getSupplementaryDocs() {
        return this.supplementaryDocument;
    }
}