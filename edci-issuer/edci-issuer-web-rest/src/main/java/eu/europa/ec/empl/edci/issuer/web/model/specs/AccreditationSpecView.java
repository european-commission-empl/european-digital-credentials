package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.WebDocumentDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.AccreditationSpecLiteView;

import java.util.List;
import java.util.Set;

public class AccreditationSpecView extends AccreditationSpecLiteView {

    private CodeDTView dcType; //1

    private WebDocumentDCView report; //0..1

    private CodeDTView status;

    private List<CodeDTView> limitField; //*

    private List<CodeDTView> limitEQFLevel; //*

    private List<CodeDTView> limitJurisdiction; //*

    private String dateIssued; //0..1

    private Set<String> languages;

    private String expiryDate; //0..1

    private List<WebDocumentDCView> supplementaryDocument; //*

    private SubresourcesOids relAccreditingAgent;

    private SubresourcesOids relOrganisation;

    public SubresourcesOids getRelAccreditingAgent() {
        relAccreditingAgent = (relAccreditingAgent == null ? new SubresourcesOids() : relAccreditingAgent);
        return relAccreditingAgent;
    }

    public void setRelAccreditingAgent(SubresourcesOids relAccreditingAgent) {
        this.relAccreditingAgent = relAccreditingAgent;
    }

    public SubresourcesOids getRelOrganisation() {
        relOrganisation = (relOrganisation == null ? new SubresourcesOids() : relOrganisation);
        return relOrganisation;
    }

    public void setRelOrganisation(SubresourcesOids relOrganisation) {
        this.relOrganisation = relOrganisation;
    }

    public CodeDTView getDcType() {
        return dcType;
    }

    public void setDcType(CodeDTView dcType) {
        this.dcType = dcType;
    }

    public WebDocumentDCView getReport() {
        return report;
    }

    public void setReport(WebDocumentDCView report) {
        this.report = report;
    }

    public List<CodeDTView> getLimitField() {
        return limitField;
    }

    public void setLimitField(List<CodeDTView> limitField) {
        this.limitField = limitField;
    }

    public List<CodeDTView> getLimitEQFLevel() {
        return limitEQFLevel;
    }

    public void setLimitEQFLevel(List<CodeDTView> limitEQFLevel) {
        this.limitEQFLevel = limitEQFLevel;
    }

    public List<CodeDTView> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(List<CodeDTView> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<WebDocumentDCView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDCView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public CodeDTView getStatus() {
        return status;
    }

    public void setStatus(CodeDTView status) {
        this.status = status;
    }
}