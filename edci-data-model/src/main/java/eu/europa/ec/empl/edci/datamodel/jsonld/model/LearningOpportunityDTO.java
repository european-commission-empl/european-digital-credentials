package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.*;
import org.joda.time.Period;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningOpportunity:")
public class LearningOpportunityDTO extends JsonLdCommonDTO {

    private List<String> descriptionHTML = new ArrayList<>();
    private NoteDTO admissionProcedure;
    private List<ZonedDateTime> applicationDeadline = new ArrayList<>();
    private MediaObjectDTO bannerImage;
    private ConceptDTO defaultLanguage;
    private LiteralMap description;
    private Period duration;
    private List<PriceDetailDTO> priceDetail = new ArrayList<>();
    private List<GrantDTO> grant = new ArrayList<>();
    private List<LearningOpportunityDTO> hasPart = new ArrayList<>();
    private List<WebResourceDTO> homepage = new ArrayList<>();
    private List<Identifier> identifier = new ArrayList<>();
    @JsonIgnore
    private List<LearningOpportunityDTO> isPartOf = new ArrayList<>();
    private LearningAchievementSpecificationDTO learningAchievementSpecification;
    private LearningActivitySpecificationDTO learningActivitySpecification;
    private ConceptDTO learningSchedule;
    private List<LocationDTO> location = new ArrayList<>();
    private List<ConceptDTO> mode = new ArrayList<>();
    private List<NoteDTO> additionalNote = new ArrayList<>();
    private List<WebResourceDTO> supplementaryDocument = new ArrayList<>();
    @NotNull
    private List<OrganisationDTO> providedBy = new ArrayList<>();
    private NoteDTO scheduleInformation;
    private PeriodOfTimeDTO temporal;
    @NotNull
    private LiteralMap title;
    private List<ConceptDTO> dcType = new ArrayList<>();

    public NoteDTO getAdmissionProcedure() {
        return admissionProcedure;
    }

    public void setAdmissionProcedure(NoteDTO admissionProcedure) {
        this.admissionProcedure = admissionProcedure;
    }

    public List<ZonedDateTime> getApplicationDeadline() {
        return applicationDeadline;
    }

    public MediaObjectDTO getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(MediaObjectDTO bannerImage) {
        this.bannerImage = bannerImage;
    }

    public ConceptDTO getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(ConceptDTO defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public Period getDuration() {
        return duration;
    }

    public void setDuration(Period duration) {
        this.duration = duration;
    }

    public List<PriceDetailDTO> getPriceDetail() {
        return priceDetail;
    }

    public List<GrantDTO> getGrant() {
        return grant;
    }

    public List<LearningOpportunityDTO> getHasPart() {
        return hasPart;
    }

    public List<WebResourceDTO> getHomepage() {
        return homepage;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public LearningAchievementSpecificationDTO getLearningAchievementSpecification() {
        return learningAchievementSpecification;
    }

    public void setLearningAchievementSpecification(LearningAchievementSpecificationDTO learningAchievementSpecification) {
        this.learningAchievementSpecification = learningAchievementSpecification;
    }

    public LearningActivitySpecificationDTO getLearningActivitySpecification() {
        return learningActivitySpecification;
    }

    public void setLearningActivitySpecification(LearningActivitySpecificationDTO learningActivitySpecification) {
        this.learningActivitySpecification = learningActivitySpecification;
    }

    public ConceptDTO getLearningSchedule() {
        return learningSchedule;
    }

    public void setLearningSchedule(ConceptDTO learningSchedule) {
        this.learningSchedule = learningSchedule;
    }

    public List<LocationDTO> getLocation() {
        return location;
    }

    public List<ConceptDTO> getMode() {
        return mode;
    }

    public List<NoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public List<WebResourceDTO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public List<OrganisationDTO> getProvidedBy() {
        return providedBy;
    }

    public NoteDTO getScheduleInformation() {
        return scheduleInformation;
    }

    public void setScheduleInformation(NoteDTO scheduleInformation) {
        this.scheduleInformation = scheduleInformation;
    }

    public PeriodOfTimeDTO getTemporal() {
        return temporal;
    }

    public void setTemporal(PeriodOfTimeDTO temporal) {
        this.temporal = temporal;
    }

    public LiteralMap getTitle() {
        return title;
    }

    public void setTitle(LiteralMap title) {
        this.title = title;
    }

    public List<ConceptDTO> getDcType() {
        return dcType;
    }

    public List<String> getDescriptionHTML() {
        return descriptionHTML;
    }

    public List<LearningOpportunityDTO> getIsPartOf() {
        return isPartOf;
    }

    public void setDescriptionHTML(List<String> descriptionHTML) {
        this.descriptionHTML = descriptionHTML;
    }

    public void setApplicationDeadline(List<ZonedDateTime> applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public void setPriceDetail(List<PriceDetailDTO> priceDetail) {
        this.priceDetail = priceDetail;
    }

    public void setGrant(List<GrantDTO> grant) {
        this.grant = grant;
    }

    public void setHasPart(List<LearningOpportunityDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setHomepage(List<WebResourceDTO> homepage) {
        this.homepage = homepage;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void setIsPartOf(List<LearningOpportunityDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setLocation(List<LocationDTO> location) {
        this.location = location;
    }

    public void setMode(List<ConceptDTO> mode) {
        this.mode = mode;
    }

    public void setAdditionalNote(List<NoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setSupplementaryDocument(List<WebResourceDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public void setProvidedBy(List<OrganisationDTO> providedBy) {
        this.providedBy = providedBy;
    }

    public void setDcType(List<ConceptDTO> dcType) {
        this.dcType = dcType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningOpportunityDTO)) return false;
        if (!super.equals(o)) return false;
        LearningOpportunityDTO that = (LearningOpportunityDTO) o;
        return Objects.equals(descriptionHTML, that.descriptionHTML) &&
                Objects.equals(admissionProcedure, that.admissionProcedure) &&
                Objects.equals(applicationDeadline, that.applicationDeadline) &&
                Objects.equals(bannerImage, that.bannerImage) &&
                Objects.equals(defaultLanguage, that.defaultLanguage) &&
                Objects.equals(description, that.description) &&
                Objects.equals(duration, that.duration) &&
                Objects.equals(priceDetail, that.priceDetail) &&
                Objects.equals(grant, that.grant) &&
                Objects.equals(hasPart, that.hasPart) &&
                Objects.equals(homepage, that.homepage) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(learningAchievementSpecification, that.learningAchievementSpecification) &&
                Objects.equals(learningActivitySpecification, that.learningActivitySpecification) &&
                Objects.equals(learningSchedule, that.learningSchedule) &&
                Objects.equals(location, that.location) &&
                Objects.equals(mode, that.mode) &&
                Objects.equals(additionalNote, that.additionalNote) &&
                Objects.equals(supplementaryDocument, that.supplementaryDocument) &&
                Objects.equals(providedBy, that.providedBy) &&
                Objects.equals(scheduleInformation, that.scheduleInformation) &&
                Objects.equals(temporal, that.temporal) &&
                Objects.equals(title, that.title) &&
                Objects.equals(dcType, that.dcType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), descriptionHTML, admissionProcedure, applicationDeadline, bannerImage, defaultLanguage, description, duration, priceDetail, grant, hasPart, homepage, identifier, learningAchievementSpecification, learningActivitySpecification, learningSchedule, location, mode, additionalNote, supplementaryDocument, providedBy, scheduleInformation, temporal, title, dcType);
    }
}
