package eu.europa.ec.empl.edci.datamodel.model.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.IdentifiableName;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;

public interface Nameable {
    public static final Logger logger = LogManager.getLogger(ReflectiveUtil.class);

    abstract String getIdentifiableName();

    default IdentifiableName getParsedIdentifiableName() {
        String identifiableNameString = this.getIdentifiableName();
        IdentifiableName identifiableName;

        if (!identifiableNameString.equals("")) {
            String className = identifiableNameString.substring(0, identifiableNameString.indexOf("."));
            String fieldName = identifiableNameString.substring(identifiableNameString.indexOf(".") + 1, identifiableNameString.indexOf("-") - 1);
            String fieldValue = identifiableNameString.substring(identifiableNameString.indexOf("-") + 2);
            identifiableName = new IdentifiableName(className, fieldName, fieldValue);
        } else {
            identifiableName = new IdentifiableName();
        }

        return identifiableName;
    }

    default String getIdentifiableNameFromFieldList(Object object, String... fieldNameList) {
        String identifiableName = "";
        for (String fieldName : fieldNameList) {
            Field field = ReflectionUtils.findField(object.getClass(), fieldName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                Object fieldObject = ReflectionUtils.getField(field, object);

                if (fieldObject != null && fieldObject.toString() != null && !fieldObject.toString().isEmpty()) {
                    String simpleClassName = object.getClass().getSimpleName().replaceAll(Pattern.quote("DTO"), "");
                    String prefix = simpleClassName.concat(".").concat(fieldName.concat(" - "));
                    identifiableName = getFieldObjectIdentifiableName(fieldObject, prefix);
                    if (!identifiableName.equals("")) break;

                }
            }

        }
        if (object != null && identifiableName.isEmpty() && object instanceof Identifiable) {
            identifiableName = ((Identifiable) object).getPk();
        }
        return identifiableName;
    }

    default String getFieldObjectIdentifiableName(Object fieldObject, String prefix) {
        String identifiableName = "";
        if (List.class.isAssignableFrom(fieldObject.getClass())) {
            List listField = (List<Object>) fieldObject;
            if (!listField.isEmpty()) {
                identifiableName = getFieldObjectListIdentifiableName(listField, prefix);
            }
        } else {
            identifiableName = getFieldObjectSingleIdentifiableName(fieldObject, prefix);
        }
        return identifiableName;
    }

    default String getFieldObjectListIdentifiableName(List<Object> fieldObject, String prefix) {
        return getFieldObjectSingleIdentifiableName(fieldObject.get(0), prefix);
    }

    default String getFieldObjectSingleIdentifiableName(Object fieldObject, String prefix) {
        String identifiableName = prefix;
        if (ClassUtils.isPrimitiveOrWrapper(fieldObject.getClass())) {
            identifiableName += fieldObject.toString();
        } else if (Nameable.class.isAssignableFrom(fieldObject.getClass())) {
            Nameable nameable = (Nameable) fieldObject;
            identifiableName += nameable.getIdentifiableName();
        } else if (Localizable.class.isAssignableFrom(fieldObject.getClass())) {
            Localizable localizable = (Localizable) fieldObject;
            identifiableName += localizable.getLocalizedStringOrAny(LocaleContextHolder.getLocale().getLanguage());
        } else {
            try {
                identifiableName += new ObjectMapper().writeValueAsString(fieldObject);
            } catch (JsonProcessingException e) {
                logger.error("could not parse JSON to get Identifiable name", e);
            }
        }
        return identifiableName;
    }
}
