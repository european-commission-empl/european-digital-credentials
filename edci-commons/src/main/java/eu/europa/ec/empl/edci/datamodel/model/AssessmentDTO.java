package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.CustomizableEntityDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.adapter.DateTimeAdapter;
import eu.europa.ec.empl.edci.datamodel.model.base.Gradeable;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.base.RootEntity;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.*;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "assessment")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "identifier", "title", "description", "additionalNote", "grade", "shortenedGrading", "resultDistribution", "issuedDate", "idVerification", "assessedBy", "specifiedBy", "hasPart"})
@EDCIIdentifier(prefix = "urn:epass:assessment:")
@CustomizableEntityDTO(identifierField = "OCBID")
public class AssessmentDTO implements RootEntity, Gradeable, Nameable {

    @XmlTransient
    private String pk;
    @XmlTransient
    private String OCBID;
    @XmlAttribute
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ASSESSMENT_ID_NOTNULL)
    private URI id; //1
    @Valid
    private List<Identifier> identifier; //*
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ASSESSMENT_TITLE_NOTNULL)
    @Valid
    private Text title; //1
    @Valid
    private Text description; //0..1
    @Valid
    private List<Note> additionalNote; //*
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ASSESSMENT_GRADE_NOTNULL)
    @Valid
    private Score grade; //1
    @Valid
    private ShortenedGradingDTO shortenedGrading;
    @Valid
    private ResultDistributionDTO resultDistribution;
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date issuedDate;
    @Valid
    @XmlElement(name = "idVerificationMethod")
    private Code idVerification; //0..1
    @XmlIDREF
    @XmlPath("specifiedBy/@idref")
    @Valid
    private AssessmentSpecificationDTO specifiedBy; //0..1
    @Valid
    private List<AssessmentDTO> hasPart; //*
    @XmlIDREF
    @XmlPath("assessedBy/@idref")
    private OrganizationDTO assessedBy; //*

    public AssessmentDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @Override
    public URI getId() {
        return id;
    }

    @Override
    public void setId(URI id) {
        this.id = id;
    }

    public String getOCBID() {
        return OCBID;
    }

    public void setOCBID(String OCBID) {
        this.OCBID = OCBID;
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "description", "id", "additionalNote");
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

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public List<Note> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<Note> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public Score getGrade() {
        return grade;
    }

    public void setGrade(Score grade) {
        this.grade = grade;
    }

    public ShortenedGradingDTO getShortenedGrading() {
        return shortenedGrading;
    }

    public void setShortenedGrading(ShortenedGradingDTO shortenedGrading) {
        this.shortenedGrading = shortenedGrading;
    }

    public ResultDistributionDTO getResultDistribution() {
        return resultDistribution;
    }

    public void setResultDistribution(ResultDistributionDTO resultDistribution) {
        this.resultDistribution = resultDistribution;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Code getIdVerification() {
        return idVerification;
    }

    public void setIdVerification(Code idVerification) {
        this.idVerification = idVerification;
    }

    public AssessmentSpecificationDTO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(AssessmentSpecificationDTO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<AssessmentDTO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(List<AssessmentDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public OrganizationDTO getAssessedBy() {
        return assessedBy;
    }

    public void setAssessedBy(OrganizationDTO assessedBy) {
        this.assessedBy = assessedBy;
    }

    @Override
    public void graduate(String score) {
        if (this.getGrade() == null) this.setGrade(new Score());
        this.getGrade().setContent(score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }

    @Override
    public void setHashCodeSeed(String pk) {
        this.pk = pk;
    }

    public String getHashCodeSeed() {
        return pk;
    }

    //XML Getters
    public Code getIdVerificationMethod() {
        return this.idVerification;
    }

    public void setIdVerificationMethod(Code idVerification) {
        this.idVerification = idVerification;
    }
}