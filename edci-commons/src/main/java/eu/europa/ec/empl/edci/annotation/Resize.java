package eu.europa.ec.empl.edci.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Resize {

    //Height in Px
    public int height();

    //Width in Px
    public int width() default 0;
}
