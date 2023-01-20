package eu.europa.ec.empl.edci.viewer.web.model;

import eu.europa.ec.empl.edci.datamodel.model.DisplayParametersDTO;
import eu.europa.ec.empl.edci.datamodel.model.OrganizationDTO;
import eu.europa.ec.empl.edci.datamodel.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.AgentDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;

import java.util.List;

//ToDo -> Delete legacy class?
public class EuropassCredentialDetailView {

    private String title;
    private String description;
    private Code type;
    //    @JsonFormat(pattern = "dd-MM-YYYY")
    private String issuanceDate;
    //    @JsonFormat(pattern = "dd-MM-YYYY")
    private String expirationDate;
    private String issuanceLocation;
    private PersonDTO credentialSubject;
    private OrganizationDTO issuer;
    private List<AgentDTO> agentReferences;
    private String proof;
    private List<EuropassCredentialDetailView> contains;
    private DisplayParametersDTO displayParams;
    private String xml;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssuanceLocation() {
        return issuanceLocation;
    }

    public void setIssuanceLocation(String issuanceLocation) {
        this.issuanceLocation = issuanceLocation;
    }

    public PersonDTO getCredentialSubject() {
        return credentialSubject;
    }

    public void setCredentialSubject(PersonDTO credentialSubject) {
        this.credentialSubject = credentialSubject;
    }

    public OrganizationDTO getIssuer() {
        return issuer;
    }

    public void setIssuer(OrganizationDTO issuer) {
        this.issuer = issuer;
    }

    public List<AgentDTO> getAgentReferences() {
        return agentReferences;
    }

    public void setAgentReferences(List<AgentDTO> agentReferences) {
        this.agentReferences = agentReferences;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public List<EuropassCredentialDetailView> getContains() {
        return contains;
    }

    public void setContains(List<EuropassCredentialDetailView> contains) {
        this.contains = contains;
    }

    public DisplayParametersDTO getDisplayParams() {
        return displayParams;
    }

    public void setDisplayParams(DisplayParametersDTO displayParams) {
        this.displayParams = displayParams;
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

    public Code getType() {
        return type;
    }

    public void setType(Code type) {
        this.type = type;
    }
}
