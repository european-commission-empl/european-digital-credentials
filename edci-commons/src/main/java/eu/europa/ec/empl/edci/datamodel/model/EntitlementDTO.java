package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.adapter.DateAdapter;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.base.RootEntity;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "entitlement")
@XmlType(propOrder = {"id", "identifier", "title", "description", "issuedDate", "expiryDate", "additionalNote", "specifiedBy", "wasDerivedFrom", "hasPart"})
@EDCIIdentifier(prefix = "urn:epass:entitlement:")
public class EntitlementDTO implements RootEntity, Nameable {

    @XmlTransient
    private String pk;
    @XmlAttribute
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ENTITLEMENT_ID_NOTNULL)
    private URI id; //1
    @Valid
    private List<Identifier> identifier; //*
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ENTITLEMENT_TITLE_NOTNULL)
    @Valid
    private Text title; //1
    @Valid
    private Note description; //0..1
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date issuedDate; //0..1
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date expiryDate; //0..1
    @Valid
    private List<Note> additionalNote; //0..1
    @XmlIDREF
    @XmlPath("specifiedBy/@idref")
    @Valid
    private EntitlementSpecificationDTO specifiedBy; //*
    @Valid
    private List<LearningAchievementDTO> wasDerivedFrom; //*
    @Valid
    private List<EntitlementDTO> hasPart; //*

    public EntitlementDTO() {
        this.initIdentifiable();
    }

    public URI getId() {
        return id;
    }

    @Override
    public void setId(URI id) {
        this.id = id;
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "description", "additionalNote", "id");
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

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
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

    public EntitlementSpecificationDTO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(EntitlementSpecificationDTO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<LearningAchievementDTO> getWasDerivedFrom() {
        return wasDerivedFrom;
    }

    public void setWasDerivedFrom(List<LearningAchievementDTO> wasDerivedFrom) {
        this.wasDerivedFrom = wasDerivedFrom;
    }

    public List<EntitlementDTO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(List<EntitlementDTO> hasPart) {
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