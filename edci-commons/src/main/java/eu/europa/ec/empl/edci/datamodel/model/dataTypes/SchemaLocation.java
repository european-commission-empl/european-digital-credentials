package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public class SchemaLocation {
    private static final Logger logger = Logger.getLogger(SchemaLocation.class);
    private String namespace;
    private String location;

    public SchemaLocation() {
    }

    public SchemaLocation(String location) {
        this.location = location;
    }

    public SchemaLocation(String namespace, String location) {
        this.namespace = namespace;
        this.location = location;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String toString() {
        return this.getNamespace().concat(" ").concat(this.getLocation());
    }

    public URL getLocationURL() {
        URL url = null;
        try {
            url = new URL(this.getLocation());
        } catch (MalformedURLException e) {
            logger.error(e);
        }
        return url;
    }

    public boolean isValidURL() {
        return this.getLocationURL() != null;
    }
}
