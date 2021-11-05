package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import java.util.ArrayList;
import java.util.List;

public class CustomList<T> {


    private List<T> list;

    public CustomList() {
        this.list = new ArrayList<>();
    }

    public List<T> getList() {
        return list;
    }

    public void setList(java.util.List<T> list) {
        this.list = list;
    }

    public void addObject(T t) {
        this.list.add(t);
    }

    public T getObject(int i) {
        if (this.list.size() == i) {
            return null;
            //this.list.add(Class<T>.newInstance());
        }
        return this.list.get(i);
    }
}
