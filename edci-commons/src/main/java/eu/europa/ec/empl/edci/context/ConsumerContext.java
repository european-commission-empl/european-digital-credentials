package eu.europa.ec.empl.edci.context;

import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.util.Validate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConsumerContext {

    CredentialHolderDTO credential = null;
    Map<String, Object> variables = new HashMap<>();

    public ConsumerContext() {
    }

    public ConsumerContext(CredentialHolderDTO credential) {
        if (credential == null) {
            throw new EDCIException().addDescription("Credential cannot be null");
        }
        this.credential = credential;
    }

    public ConsumerContext(CredentialHolderDTO credential, Map<String, Object> variables) {
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

    public CredentialHolderDTO getCredential() {
        return credential;
    }

}