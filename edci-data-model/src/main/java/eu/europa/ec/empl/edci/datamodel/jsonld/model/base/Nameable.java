package eu.europa.ec.empl.edci.datamodel.jsonld.model.base;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface Nameable {

    default String getNameFromFieldList(Object object, boolean includePrefix, String... fieldNameList) {
        String name = "";
        for (String fieldName : fieldNameList) {
            if (fieldName.contains("/")) {
                name = Arrays.stream(fieldName.split("\\/"))
                        .map(fn -> getNameFromFieldList(object, includePrefix, fn))
                        .filter(fn -> !StringUtils.isBlank(fn))
                        .collect(Collectors.joining(" - "));
                if (!name.equals("")) break;
            } else {
                Field field = ReflectionUtils.findField(object.getClass(), fieldName);
                if (field != null) {
                    ReflectionUtils.makeAccessible(field);
                    Object fieldObject = ReflectionUtils.getField(field, object);

                    if (fieldObject != null && fieldObject.toString() != null && !fieldObject.toString().isEmpty()) {
                        String simpleClassName = object.getClass().getSimpleName().replaceAll(Pattern.quote("DTO"), "");
                        String prefix = includePrefix ? simpleClassName.concat(".").concat(fieldName.concat(" - ")) : "";
                        name = prefix.concat(fieldObject.toString());
                        if (!name.equals("")) break;

                    }
                }
            }
        }
        //Labels can be empty, they are not mandatory and concatenated to title always
       /* if (object != null && name.isEmpty() && object instanceof Identifiable
                && fieldNameList.length == 1 && !"label".equals(fieldNameList[0])) {
            name = ((Identifiable) object).getHashCodeSeed();
        }*/
        return name;
    }

    default String getNameFromFieldList(Object object, String... fieldNameList) {
        return this.getNameFromFieldList(object, true, fieldNameList);
    }

    abstract String getName();
}
