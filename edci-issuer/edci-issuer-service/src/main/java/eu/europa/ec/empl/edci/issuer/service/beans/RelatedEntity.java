package eu.europa.ec.empl.edci.issuer.service.beans;

import java.util.HashSet;
import java.util.Set;

public class RelatedEntity {

    Object entity = null;
    Set<RelatedMethod> relatedMethods = new HashSet<>();

    public RelatedEntity(Object entity) {
        this.entity = entity;
    }

    public Object getEntity() {
        return entity;
    }

    public Set<RelatedMethod> getRelatedMethods() {
        return relatedMethods;
    }

    public void addMethod(RelatedMethod relatedMethods) {
        this.relatedMethods.add(relatedMethods);
    }

    @Override
    public int hashCode() {
        return this.entity.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Object temp = null;
        if (!(obj instanceof RelatedEntity)) {
            temp = ((RelatedEntity)obj).getEntity();
        } else {
            temp = obj;
        }
        return this.entity.equals(temp);
    }
}
