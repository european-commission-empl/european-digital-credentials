package eu.europa.ec.empl.edci.datamodel.validation;

import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationError {

    private String errorKey;
    private String errorMessage;
    private List<Identifiable> affectedAssets = new ArrayList<Identifiable>();
    private Map<String, Object> messageVariables = new HashMap<String, Object>();

    public ValidationError() {

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

    public Map<String, Object> getMessageVariables() {
        return messageVariables;
    }

    public void setMessageVariables(Map<String, Object> messageVariables) {
        this.messageVariables = messageVariables;
    }

    public void putMessageVariables(String key, Object value) {
        this.getMessageVariables().put(key, value);
    }
}
