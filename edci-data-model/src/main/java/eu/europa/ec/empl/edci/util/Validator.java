package eu.europa.ec.empl.edci.util;


import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Util class for empty or null checks.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Validator {

    /**
     * Check if the object is Not empty.
     *
     * @param object the object
     * @return the boolean
     */
    public boolean notEmpty(Object object) {
        return !isEmpty(object);
    }

    /**
     * Check if the object is empty.
     *
     * @param object the object
     * @return the boolean
     */
    public boolean isEmpty(Object object) {

        if (object instanceof Optional) {
            return !((Optional) object).isPresent();
        }
        if (object == null) {
            return true;
        }

        if (object instanceof Long) {
            Long l = (Long) object;
            if (l == 0) {
                return true;
            }
        } else if (object instanceof CharSequence) {
            if (((CharSequence) object).length() == 0) {
                return true;
            }

            // If string is all whitespace, we consider it empty
            if (object instanceof String) {
                int i;
                String s = (String) object;

                for (i = 0; i < s.length(); i++) {
                    if (!Character.isWhitespace(s.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }

        if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        } else if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        } else if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }

        return false;
    }

    /**
     * Gets value or null.
     *
     * @param <T>      the type parameter
     * @param supplier the supplier
     * @return the value
     */
    public <T> T getValueNullSafe(Supplier<T> supplier) {

        return getValueNullSafe(supplier, null);
    }

    /**
     * Gets value, if null gets alternativeValue.
     *
     * @param <T>              the type parameter
     * @param supplier         the supplier
     * @param alternativeValue the alternative value
     * @return the value
     */
    public <T> T getValueNullSafe(Supplier<T> supplier, T alternativeValue) {

        T returnValue = null;

        try {
            returnValue = supplier.get();

            if (returnValue == null) {
                returnValue = alternativeValue;
            }

        } catch (Throwable e) {
            returnValue = null;
        }

        return returnValue;
    }

    /**
     * Returns true only if ALL suppliers returns a value different than null and no NullPointerException is thrown during the check
     *
     * @param <T>      supplier type
     * @param supplier fields to check.
     * @return True if no null is found, false otherwise
     */
    public <T> boolean isNotNull(Supplier<T>... supplier) {

        boolean returnValue = true;

        try {
            returnValue = Arrays.stream(supplier).noneMatch(s -> s.get() == null);

        } catch (Throwable e) {
            returnValue = false;
        }

        return returnValue;
    }

}