package eu.europa.ec.empl.edci.datamodel.jsonld.model.util;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;

public class HashCodeObjectIdResolver extends SimpleObjectIdResolver {

    @Override
    public void bindItem(ObjectIdGenerator.IdKey id, Object ob) {
        super.bindItem(id, ob);
        /*if (_items == null) {
            _items = new HashMap<>();
        } else if (_items.containsKey(id)
                && _items.get(id).hashCode() != ob.hashCode()) {
            //throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
            System.out.println("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
        }
        _items.put(id, ob);*/
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        return new HashCodeObjectIdResolver();
    }
}
