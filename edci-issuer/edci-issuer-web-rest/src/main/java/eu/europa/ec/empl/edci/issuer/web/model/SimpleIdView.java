package eu.europa.ec.empl.edci.issuer.web.model;

import javax.validation.constraints.NotNull;

public class SimpleIdView {

    @NotNull
    private String id;

    public SimpleIdView() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
