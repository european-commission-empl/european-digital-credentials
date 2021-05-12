package eu.europa.ec.empl.edci.datamodel.view;


import java.util.List;
import java.util.Set;

public class EuropassCredentialPresentationView implements ITabView {

    private CredentialMetadataTabView credentialMetadata;
    private CredentialSubjectTabView credentialSubject;
    private OrganizationTabView issuerPresentation;
    private OrganizationTabView issuerCredential;
    private List<EuropassCredentialPresentationLiteView> subCredentials;
    private List<AchievementTabView> achievements;
    private List<ActivityTabView> activities;
    private List<EntitlementTabView> entitlements;

    private Set<AchievementTabView> achievementsList;
    private Set<ActivityTabView> activitiesList;
    private Set<EntitlementTabView> entitlementsList;
    private Set<OrganizationTabView> organizationsList;
    private Set<AssessmentTabView> assessmentsList;

    public Set<AchievementTabView> getAchievementsList() {
        return achievementsList;
    }

    public void setAchievementsList(Set<AchievementTabView> achievementsList) {
        this.achievementsList = achievementsList;
    }

    public Set<ActivityTabView> getActivitiesList() {
        return activitiesList;
    }

    public void setActivitiesList(Set<ActivityTabView> activitiesList) {
        this.activitiesList = activitiesList;
    }

    public Set<EntitlementTabView> getEntitlementsList() {
        return entitlementsList;
    }

    public void setEntitlementsList(Set<EntitlementTabView> entitlementsList) {
        this.entitlementsList = entitlementsList;
    }

    public CredentialMetadataTabView getCredentialMetadata() {
        return credentialMetadata;
    }

    public void setCredentialMetadata(CredentialMetadataTabView credentialMetadata) {
        this.credentialMetadata = credentialMetadata;
    }

    public CredentialSubjectTabView getCredentialSubject() {
        return credentialSubject;
    }

    public void setCredentialSubject(CredentialSubjectTabView credentialSubject) {
        this.credentialSubject = credentialSubject;
    }

    public List<EuropassCredentialPresentationLiteView> getSubCredentials() {
        return subCredentials;
    }

    public void setSubCredentials(List<EuropassCredentialPresentationLiteView> subCredentials) {
        this.subCredentials = subCredentials;
    }

    public List<AchievementTabView> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<AchievementTabView> achievements) {
        this.achievements = achievements;
    }

    public List<ActivityTabView> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityTabView> activities) {
        this.activities = activities;
    }

    public List<EntitlementTabView> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(List<EntitlementTabView> entitlements) {
        this.entitlements = entitlements;
    }

    public OrganizationTabView getIssuerPresentation() {
        return issuerPresentation;
    }

    public void setIssuerPresentation(OrganizationTabView issuerPresentation) {
        this.issuerPresentation = issuerPresentation;
    }

    public OrganizationTabView getIssuerCredential() {
        return issuerCredential;
    }

    public void setIssuerCredential(OrganizationTabView issuerCredential) {
        this.issuerCredential = issuerCredential;
    }

    public Set<OrganizationTabView> getOrganizationsList() {
        return organizationsList;
    }

    public void setOrganizationsList(Set<OrganizationTabView> organizationsList) {
        this.organizationsList = organizationsList;
    }

    public Set<AssessmentTabView> getAssessmentsList() {
        return assessmentsList;
    }

    public void setAssessmentsList(Set<AssessmentTabView> assessmentsList) {
        this.assessmentsList = assessmentsList;
    }
}


