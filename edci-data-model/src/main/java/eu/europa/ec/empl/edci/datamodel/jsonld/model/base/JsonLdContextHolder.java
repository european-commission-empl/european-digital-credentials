package eu.europa.ec.empl.edci.datamodel.jsonld.model.base;

import jakarta.json.JsonValue;

/**
 * Implement this interface and use @JsonIgnore Annotation on jsonLdContext field, it will be treated in postDeserialize actions
 */
public interface JsonLdContextHolder {

    JsonValue getJsonLdContext();

    void setJsonLdContext(JsonValue jsonLdContext);
}
