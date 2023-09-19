package eu.europa.ec.empl.edci.datamodel.validation;

import org.apache.jena.shacl.validation.ReportEntry;

public class SHACLValidationError extends ValidationError {

    private String path;
    private String constraint;
    private String nodeValue;

    public SHACLValidationError(String messageValue, ReportEntry entry) {
        this.setErrorMessage(messageValue);
        try {
            this.setNodeValue(entry.focusNode().toString());
            this.setConstraint(entry.constraint().toString());
            this.setPath(entry.resultPath().toString());
        } catch (Exception e) {
            //Silent exception
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }
}
