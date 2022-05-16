package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSAccreditationDTO {

    private UUID id;
    private List<QMSIdentifierDTO> identifiers;
    //This Used to be a CODE, it might be again in the future. private QMSCodeDTO type;
    private URI type;
    private List<QMSLabelDTO> titles;
    private QMSScoreDTO decision;
    private QMSOrganizationDTO accreditedOrganization;
    private QMSQualificationDTO limitQualification;
    private List<QMSCodeDTO> limitFields;
    private List<QMSCodeDTO> limitEQFLevels;
    private List<QMSCodeDTO> limitJurisdictions;
    private QMSOrganizationDTO accreditingAgent;
    private Date issuedDate;
    private Date expiryDate;
    //toDo -> missing accrediting agent for DTO filling.

    public QMSAccreditationDTO() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<QMSIdentifierDTO> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<QMSIdentifierDTO> identifiers) {
        this.identifiers = identifiers;
    }

    public URI getType() {
        return type;
    }

    public void setType(URI type) {
        this.type = type;
    }

    public List<QMSLabelDTO> getTitles() {
        return titles;
    }

    public void setTitles(List<QMSLabelDTO> titles) {
        this.titles = titles;
    }

    public QMSScoreDTO getDecision() {
        return decision;
    }

    public void setDecision(QMSScoreDTO decision) {
        this.decision = decision;
    }

    public QMSOrganizationDTO getAccreditedOrganization() {
        return accreditedOrganization;
    }

    public void setAccreditedOrganization(QMSOrganizationDTO accreditedOrganization) {
        this.accreditedOrganization = accreditedOrganization;
    }

    public QMSQualificationDTO getLimitQualification() {
        return limitQualification;
    }

    public void setLimitQualification(QMSQualificationDTO limitQualification) {
        this.limitQualification = limitQualification;
    }

    public List<QMSCodeDTO> getLimitFields() {
        return limitFields;
    }

    public void setLimitFields(List<QMSCodeDTO> limitFields) {
        this.limitFields = limitFields;
    }

    public List<QMSCodeDTO> getLimitEQFLevels() {
        return limitEQFLevels;
    }

    public void setLimitEQFLevels(List<QMSCodeDTO> limitEQFLevels) {
        this.limitEQFLevels = limitEQFLevels;
    }

    public List<QMSCodeDTO> getLimitJurisdictions() {
        return limitJurisdictions;
    }

    public void setLimitJurisdictions(List<QMSCodeDTO> limitJurisdictions) {
        this.limitJurisdictions = limitJurisdictions;
    }

    public QMSOrganizationDTO getAccreditingAgent() {
        return accreditingAgent;
    }

    public void setAccreditingAgent(QMSOrganizationDTO accreditingAgent) {
        this.accreditingAgent = accreditingAgent;
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

    public List<QMSIdentifierDTO> getOrganizationalIdentifiers() {
        return this.getAccreditedOrganization().getAllAvailableIdentifiers();
    }

    public List<QMSIdentifierDTO> getAllAvailableIdentifiers() {
        List<QMSIdentifierDTO> identifiers = new ArrayList<>();
        if (this.getOrganizationalIdentifiers() != null) {
            identifiers.addAll(this.getOrganizationalIdentifiers());
        }
        if (this.getIdentifiers() != null) {
            identifiers.addAll(this.getIdentifiers());
        }
        return identifiers;
    }
}
