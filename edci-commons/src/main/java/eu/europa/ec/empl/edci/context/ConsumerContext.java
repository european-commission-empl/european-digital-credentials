package eu.europa.ec.empl.edci.context;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;

import java.util.HashMap;
import java.util.Map;

public class ConsumerContext {

    EuropeanDigitalCredentialDTO credential = null;
    Map<String, Object> variables = new HashMap<>();

    public ConsumerContext() {
    }

    public ConsumerContext(EuropeanDigitalCredentialDTO credential) {
        if (credential == null) {
            throw new EDCIException().addDescription("Credential cannot be null");
        }
        this.credential = credential;
    }

    public ConsumerContext(EuropeanDigitalCredentialDTO credential, Map<String, Object> variables) {
        this(credential);
        if (variables != null) {
            variables.putAll(variables);
        }
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public Object getVariable(String key) {
        return variables.get(key);
    }

    public <T> T getVariable(String key, Class<T> type) {
        return (T) variables.get(key);
    }

    public ConsumerContext addVariable(String key, Object value) {
        this.variables.put(key, value);
        return this;
    }

    public EuropeanDigitalCredentialDTO getCredential() {
        return credential;
    }
    
}