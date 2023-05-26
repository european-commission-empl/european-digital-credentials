package eu.europa.ec.empl.edci.datamodel.validation;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.Identifiable;

import java.util.*;

public class ValidationError {

    private String path;
    private String constraint;
    private String nodeValue;
    private String errorKey;
    private String errorMessage;
    private List<Identifiable> affectedAssets = new ArrayList<Identifiable>();
    private Map<String, Object> messageVariables = new HashMap<String, Object>();

    public ValidationError() {

    }

    public ValidationError(String errorKey, Identifiable... affectedAssets) {
        this.setErrorKey(errorKey);
        this.setAffectedAssets(Arrays.asList(affectedAssets));
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

    public ValidationError(String errorKey) {
        this.setErrorKey(errorKey);
    }

    public String getErrorKey() {
        return errorKey;
    }

    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<Identifiable> getAffectedAssets() {
        return affectedAssets;
    }

    public void setAffectedAssets(List<Identifiable> affectedAssets) {
        this.affectedAssets = affectedAssets;
    }

    public void addAffectedAsset(Identifiable asset) {
        this.affectedAssets.add(asset);
    }

    public void addAffectedAssets(Identifiable... assets) {
        this.affectedAssets.addAll(Arrays.asList(assets));
    }

    public Map<String, Object> getMessageVariables() {
        return messageVariables;
    }

    public void setMessageVariables(Map<String, Object> messageVariables) {
        this.messageVariables = messageVariables;
    }

    public void putMessageVariables(String key, Object value) {
        this.getMessageVariables().put(key, value);
    }

    @Override
    public String toString() {
        return getErrorMessage();
    }
}
