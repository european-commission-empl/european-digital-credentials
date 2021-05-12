package eu.europa.ec.empl.edci.datamodel.model.base;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.Defaults;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

public interface Identifiable extends Nameable {

    abstract URI getId();

    abstract void setId(URI id);

    abstract void setPk(String pk);

    abstract String getPk();

    default String getPrefix(Object object) {
        if (object.getClass().getAnnotation(EDCIIdentifier.class) != null) {
            return object.getClass().getAnnotation(EDCIIdentifier.class).prefix();
        }
        return Defaults.XML_IDENTIFIER_PREFIX;
    }

    default void initIdentifiable() {
        this.setPk(UUID.randomUUID().toString());
    }

    default int getHashCode() {
        return Objects.hash(getClass(), this.getPk());
    }

    default boolean isEquals(Object object) {
        // self check
        if (this == object) {
            return true;
        }
        // null check
        if (object == null) {
            return false;
        }
        // type check and cast
        if (getClass() != object.getClass()) {
            return false;
        }
        // field comparison
        if (object instanceof Identifiable) {
            Identifiable identifiable = (Identifiable) object;
            return this.getPk().equals(identifiable.getPk());
        }
        return false;
    }


}
