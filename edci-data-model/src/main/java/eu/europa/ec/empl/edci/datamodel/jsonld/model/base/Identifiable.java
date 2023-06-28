package eu.europa.ec.empl.edci.datamodel.jsonld.model.base;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.Defaults;

import java.net.URI;

public interface Identifiable extends Nameable {

    public void setId(URI id);

    public URI getId();

    default String getIdPrefix(Object object) {
        if (object.getClass().getAnnotation(EDCIIdentifier.class) != null) {
            return object.getClass().getAnnotation(EDCIIdentifier.class).prefix();
        }
        return Defaults.IDENTIFIER_PREFIX;
    }

    public abstract boolean equals(Object o);

    public abstract int hashCode();

}
