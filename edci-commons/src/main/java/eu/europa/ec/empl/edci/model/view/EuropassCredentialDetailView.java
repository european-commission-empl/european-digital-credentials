package eu.europa.ec.empl.edci.model.view;

import eu.europa.ec.empl.edci.model.view.base.AgentView;
import eu.europa.ec.empl.edci.model.view.fields.DisplayParametersFieldView;
import eu.europa.ec.empl.edci.model.view.fields.EvidenceFieldView;
import eu.europa.ec.empl.edci.model.view.fields.IdentifierFieldView;
import eu.europa.ec.empl.edci.model.view.fields.MediaObjectFieldView;
import eu.europa.ec.empl.edci.model.view.tabs.CredentialSubjectTabView;
import eu.europa.ec.empl.edci.model.view.tabs.OrganizationTabView;

import java.util.ArrayList;
import java.util.List;

//ToDo> Delete legacy class?
public class EuropassCredentialDetailView {
    
    private List<String> type; //credentialProfiles
    //    @JsonFormat(pattern = "dd-MM-YYYY")
    private String issuanceDate;
    //    @JsonFormat(pattern = "dd-MM-YYYY")
    private String expirationDate;
    private String issuanceLocation;
    private CredentialSubjectTabView credentialSubject;
    private OrganizationTabView issuer;
    private List<AgentView> holder;
    private List<String> proof;
    private DisplayParametersFieldView displayParameter;
    private List<IdentifierFieldView> identifier;
    private List<MediaObjectFieldView> attachment;
    private String xml;


    private List<String> validationErrors = new ArrayList<>();
    private List<String> credentialStatus = new ArrayList<>();
    private EvidenceFieldView evidence;
    private String issued;
    private List<String> termsOfUse = new ArrayList<>();
    private String validUntil;
    private String validFrom;
    
    public String getIssuanceLocation() {
        return issuanceLocation;
    }

    public void setIssuanceLocation(String issuanceLocation) {
        this.issuanceLocation = issuanceLocation;
    }

    public List<String> getProof() {
        return proof;
    }

    public void setProof(List<String> proof) {
        this.proof = proof;
    }
    
    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public CredentialSubjectTabView getCredentialSubject() {
        return credentialSubject;
    }

    public void setCredentialSubject(CredentialSubjectTabView credentialSubject) {
        this.credentialSubject = credentialSubject;
    }

    public OrganizationTabView getIssuer() {
        return issuer;
    }

    public void setIssuer(OrganizationTabView issuer) {
        this.issuer = issuer;
    }

    public List<AgentView> getHolder() {
        return holder;
    }

    public void setHolder(List<AgentView> holder) {
        this.holder = holder;
    }

    public DisplayParametersFieldView getDisplayParameter() {
        return displayParameter;
    }

    public void setDisplayParameter(DisplayParametersFieldView displayParameter) {
        this.displayParameter = displayParameter;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public List<MediaObjectFieldView> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<MediaObjectFieldView> attachment) {
        this.attachment = attachment;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public List<String> getCredentialStatus() {
        return credentialStatus;
    }

    public void setCredentialStatus(List<String> credentialStatus) {
        this.credentialStatus = credentialStatus;
    }

    public EvidenceFieldView getEvidence() {
        return evidence;
    }

    public void setEvidence(EvidenceFieldView evidence) {
        this.evidence = evidence;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public List<String> getTermsOfUse() {
        return termsOfUse;
    }

    public void setTermsOfUse(List<String> termsOfUse) {
        this.termsOfUse = termsOfUse;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }
}
