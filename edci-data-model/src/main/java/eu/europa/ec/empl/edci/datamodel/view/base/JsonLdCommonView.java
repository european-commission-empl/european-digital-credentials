package eu.europa.ec.empl.edci.datamodel.view.base;


import javax.validation.constraints.NotNull;
import java.net.URI;

public class JsonLdCommonView {

    @NotNull
    public URI id;
    public String type;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
