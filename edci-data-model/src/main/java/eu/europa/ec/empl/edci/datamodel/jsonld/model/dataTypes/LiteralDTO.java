package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LiteralDTO {

    @JsonProperty("@value")
    private String value;
    @JsonProperty("@type")
    private String type;

    public LiteralDTO() {
    }
    
    @JsonCreator()
    public LiteralDTO(String value) {
        this.setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
