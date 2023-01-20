package eu.europa.ec.empl.edci.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 lower priorities will be processed first
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EDCIConsumer {

    public Class applyTo();

    public boolean preProcess() default false;

    public int priority() default 10;

}
