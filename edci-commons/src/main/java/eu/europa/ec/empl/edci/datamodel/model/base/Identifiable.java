package eu.europa.ec.empl.edci.datamodel.model.base;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIConfig;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

public interface Identifiable extends Nameable {

    abstract URI getId();

    abstract void setId(URI id);

    abstract void setHashCodeSeed(String pk);

    abstract String getHashCodeSeed();

    default String getPrefix(Object object) {
        if (object.getClass().getAnnotation(EDCIIdentifier.class) != null) {
            return object.getClass().getAnnotation(EDCIIdentifier.class).prefix();
        }
        return EDCIConfig.Defaults.XML_IDENTIFIER_PREFIX;
    }

    default boolean ignoreProcessing() {
        if (this.getClass().getAnnotation(EDCIIdentifier.class) != null) {
            return this.getClass().getAnnotation(EDCIIdentifier.class).ignoreProcessing();
        }
        return false;
    }

    default void initIdentifiable() {
        this.setHashCodeSeed(UUID.randomUUID().toString());
    }

    default int getHashCode() {
        return Objects.hash(getClass(), this.getHashCodeSeed());
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
            return this.getHashCodeSeed().equals(identifiable.getHashCodeSeed());
        }
        return false;
    }


}
