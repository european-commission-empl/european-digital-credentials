package eu.europa.ec.empl.edci.model;

import eu.europa.ec.empl.edci.annotation.EmptiableIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface Emptiable {

    default boolean isEmpty() {

        try {
            List<Field> fields = new ArrayList<Field>();

            fields.addAll(Arrays.asList(getClass().getDeclaredFields()));

            Class<?> current = getClass();
            while (current.getSuperclass() != null) {
                current = current.getSuperclass();
                fields.addAll(Arrays.asList(current.getDeclaredFields()));
            }

            for (Field f : fields) {
                if (f.getDeclaredAnnotation(EmptiableIgnore.class) == null && f.getDeclaredAnnotation(Id.class) == null) {
                    if (!java.lang.reflect.Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
                        Object fieldValue = FieldUtils.readField(f, this, true);
                        if (fieldValue != null) {
                            if (fieldValue instanceof Emptiable) {
                                if (!((Emptiable) fieldValue).isEmpty()) {
                                    return false;
                                }
                            } else if (fieldValue instanceof Collection) {
                                if (!((Collection) fieldValue).isEmpty()) {
                                    return false;
                                }
                            } else if (fieldValue instanceof String) {
                                if (!StringUtils.isEmpty(((String) fieldValue).trim())) {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
