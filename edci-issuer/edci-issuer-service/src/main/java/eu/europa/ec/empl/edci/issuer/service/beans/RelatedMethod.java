package eu.europa.ec.empl.edci.issuer.service.beans;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RelatedMethod {

    Method method = null;
    HashMap<Object, Boolean> values = new HashMap<>();

    public RelatedMethod(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public HashMap<Object, Boolean> getValues() {
        return values;
    }

    public void addValues(Object entity, Boolean include) {
        this.values.put(entity, include);
    }

    @Override
    public int hashCode() {
        return this.method.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RelatedMethod)) {
            return false;
        }
        return this.method.equals(((RelatedMethod)obj).getMethod());
    }
}
