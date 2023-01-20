package eu.europa.ec.empl.edci.datamodel.model.base;

import eu.europa.ec.empl.edci.datamodel.model.dataTypes.IdentifiableName;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    default String getIdentifiableNameFromFieldList(Object object, boolean includePrefix, String... fieldNameList) {
        String identifiableName = "";
        for (String fieldName : fieldNameList) {
            if (fieldName.contains("/")) {
                identifiableName = Arrays.stream(fieldName.split("\\/"))
                    .map(fn -> getIdentifiableNameFromFieldList(object, includePrefix, fn))
                    .filter(fn -> !StringUtils.isBlank(fn))
                    .collect(Collectors.joining(" - "));
                if (!identifiableName.equals("")) break;
            } else {
                Field field = ReflectionUtils.findField(object.getClass(), fieldName);
                if (field != null) {
                    ReflectionUtils.makeAccessible(field);
                    Object fieldObject = ReflectionUtils.getField(field, object);

                    if (fieldObject != null && fieldObject.toString() != null && !fieldObject.toString().isEmpty()) {
                        String simpleClassName = object.getClass().getSimpleName().replaceAll(Pattern.quote("DTO"), "");
                        String prefix = includePrefix ? simpleClassName.concat(".").concat(fieldName.concat(" - ")) : "";
                        identifiableName = getFieldObjectIdentifiableName(fieldObject, prefix);
                        if (!identifiableName.equals("")) break;

                    }
                }
            }

        }
        //Labels can be empty, they are not mandatory and concatated to title always
        if (object != null && identifiableName.isEmpty() && object instanceof Identifiable
                && fieldNameList.length == 1 && !"label".equals(fieldNameList[0])) {
            identifiableName = ((Identifiable) object).getHashCodeSeed();
        }
        return identifiableName;
    }

    default String getIdentifiableNameFromFieldList(Object object, String... fieldNameList) {
        return this.getIdentifiableNameFromFieldList(object, true, fieldNameList);
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
        } else if (ILocalizable.class.isAssignableFrom(fieldObject.getClass())) {
            ILocalizable localizable = (ILocalizable) fieldObject;
            identifiableName += localizable.getLocalizedStringOrAny(LocaleContextHolder.getLocale().getLanguage());
        } else {
            // try {
            // identifiableName += new ObjectMapper().writeValueAsString(fieldObject);
            identifiableName += String.valueOf(fieldObject);
           /* } catch (JsonProcessingException e) {
                logger.error("could not parse JSON to get Identifiable name", e);
            }*/
        }
        return identifiableName;
    }
}
