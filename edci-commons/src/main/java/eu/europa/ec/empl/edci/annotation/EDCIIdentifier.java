package eu.europa.ec.empl.edci.annotation;

import eu.europa.ec.empl.edci.constants.EDCIConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EDCIIdentifier {

    public String prefix() default EDCIConfig.Defaults.XML_IDENTIFIER_PREFIX;
}
