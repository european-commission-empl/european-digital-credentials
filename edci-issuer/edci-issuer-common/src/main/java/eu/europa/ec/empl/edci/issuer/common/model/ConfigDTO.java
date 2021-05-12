package eu.europa.ec.empl.edci.issuer.common.model;

import eu.europa.ec.empl.edci.constants.Defaults;

public class ConfigDTO {

    private String pk;

    private Defaults.Environment environment;

    private String key;

    private String value;

    public ConfigDTO() {

    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public Defaults.Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Defaults.Environment environment) {
        this.environment = environment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
