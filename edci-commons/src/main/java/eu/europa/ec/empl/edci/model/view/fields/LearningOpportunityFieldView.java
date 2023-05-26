package eu.europa.ec.empl.edci.model.view.fields;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.model.view.tabs.AchievementSpecTabView;
import eu.europa.ec.empl.edci.model.view.tabs.ActivitySpecTabView;
import eu.europa.ec.empl.edci.model.view.tabs.OrganizationTabView;

import java.util.List;

public class LearningOpportunityFieldView {
    private List<String> descriptionHTML;
    private NoteFieldView admissionProcedure;
    private List<String> applicationDeadline;
    private MediaObjectFieldView bannerImage;
    private String defaultLanguage;
    private String description;
    private String duration;
    private List<PriceDetailFieldView> priceDetail;
    private List<GrantFieldView> grant;
    private List<LearningOpportunityFieldView> hasPart;
    private List<LinkFieldView> homepage;
    private List<Identifier> identifier;
    private List<LearningOpportunityFieldView> isPartOf;
    private AchievementSpecTabView learningAchievementSpecification;
    private ActivitySpecTabView learningActivitySpecification;
    private String learningSchedule;
    private List<LocationFieldView> location;
    private List<String> mode;
    private List<NoteFieldView> additionalNote;
    private List<LinkFieldView> supplementaryDocument;
    private List<OrganizationTabView> providedBy;
    private NoteFieldView scheduleInformation;
    private PeriodOfTimeFieldView temporal;
    private String title;
    private List<String> dcType;

    public List<String> getDescriptionHTML() {
        return descriptionHTML;
    }

    public void setDescriptionHTML(List<String> descriptionHTML) {
        this.descriptionHTML = descriptionHTML;
    }

    public NoteFieldView getAdmissionProcedure() {
        return admissionProcedure;
    }

    public void setAdmissionProcedure(NoteFieldView admissionProcedure) {
        this.admissionProcedure = admissionProcedure;
    }

    public List<String> getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(List<String> applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public MediaObjectFieldView getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(MediaObjectFieldView bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<PriceDetailFieldView> getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(List<PriceDetailFieldView> priceDetail) {
        this.priceDetail = priceDetail;
    }

    public List<GrantFieldView> getGrant() {
        return grant;
    }

    public void setGrant(List<GrantFieldView> grant) {
        this.grant = grant;
    }

    public List<LearningOpportunityFieldView> getHasPart() {
        return hasPart;
    }

    public void setHasPart(List<LearningOpportunityFieldView> hasPart) {
        this.hasPart = hasPart;
    }

    public List<LinkFieldView> getHomepage() {
        return homepage;
    }

    public void setHomepage(List<LinkFieldView> homepage) {
        this.homepage = homepage;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public List<LearningOpportunityFieldView> getIsPartOf() {
        return isPartOf;
    }

    public void setIsPartOf(List<LearningOpportunityFieldView> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public AchievementSpecTabView getLearningAchievementSpecification() {
        return learningAchievementSpecification;
    }

    public void setLearningAchievementSpecification(AchievementSpecTabView learningAchievementSpecification) {
        this.learningAchievementSpecification = learningAchievementSpecification;
    }

    public ActivitySpecTabView getLearningActivitySpecification() {
        return learningActivitySpecification;
    }

    public void setLearningActivitySpecification(ActivitySpecTabView learningActivitySpecification) {
        this.learningActivitySpecification = learningActivitySpecification;
    }

    public String getLearningSchedule() {
        return learningSchedule;
    }

    public void setLearningSchedule(String learningSchedule) {
        this.learningSchedule = learningSchedule;
    }

    public List<LocationFieldView> getLocation() {
        return location;
    }

    public void setLocation(List<LocationFieldView> location) {
        this.location = location;
    }

    public List<String> getMode() {
        return mode;
    }

    public void setMode(List<String> mode) {
        this.mode = mode;
    }

    public List<NoteFieldView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteFieldView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<LinkFieldView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<LinkFieldView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public List<OrganizationTabView> getProvidedBy() {
        return providedBy;
    }

    public void setProvidedBy(List<OrganizationTabView> providedBy) {
        this.providedBy = providedBy;
    }

    public NoteFieldView getScheduleInformation() {
        return scheduleInformation;
    }

    public void setScheduleInformation(NoteFieldView scheduleInformation) {
        this.scheduleInformation = scheduleInformation;
    }

    public PeriodOfTimeFieldView getTemporal() {
        return temporal;
    }

    public void setTemporal(PeriodOfTimeFieldView temporal) {
        this.temporal = temporal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getDcType() {
        return dcType;
    }

    public void setDcType(List<String> dcType) {
        this.dcType = dcType;
    }
}
