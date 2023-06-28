package eu.europa.ec.empl.edci.datamodel.jsonld.model.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.util.ClassUtil;

import java.io.IOException;

@Deprecated()
public class JsonLdResolver extends TypeIdResolverBase {
    private JavaType superType;
    private static final String SUFFIX_DTO = "DTO";

    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }

    @Override
    public String idFromValue(Object value) {
        return idFromValueAndType(value, value.getClass());
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> clazz) {
        String name = clazz.getName();
        String packageName = clazz.getPackageName() + ".";

        name = name.replace(packageName, "");

        if (name.endsWith(SUFFIX_DTO)) {
            return name.replace(SUFFIX_DTO, "");
        }

        return name;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        Class<?> clazz;
        String packageName = superType.getRawClass().getPackageName() + ".";
        try {
            clazz = Class.forName(packageName + id);
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName(packageName + id + SUFFIX_DTO);
            } catch (ClassNotFoundException classNotFoundException) {
                throw new IllegalStateException("cannot find class '" + id + "'");
            }
        }

        return context.constructSpecializedType(superType, clazz);
    }

}