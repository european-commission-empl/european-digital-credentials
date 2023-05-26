package eu.europa.ec.empl.edci.model.external;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;

public class VerificationCheckReport {

    private ConceptReport type;
    private ConceptReport status;
    private LiteralMap description = new LiteralMap();
    private LiteralMap longDescription = new LiteralMap();
    private String internalInformation;

    public ConceptReport getType() {
        return type;
    }

    public void setType(ConceptReport type) {
        this.type = type;
    }

    public ConceptReport getStatus() {
        return status;
    }

    public void setStatus(ConceptReport status) {
        this.status = status;
    }

    public LiteralMap getDescription() {
        return description;
    }

    public LiteralMap getLongDescription() {
        return longDescription;
    }

    public String getInternalInformation() {
        return internalInformation;
    }

    public void setInternalInformation(String internalInformation) {
        this.internalInformation = internalInformation;
    }
}
