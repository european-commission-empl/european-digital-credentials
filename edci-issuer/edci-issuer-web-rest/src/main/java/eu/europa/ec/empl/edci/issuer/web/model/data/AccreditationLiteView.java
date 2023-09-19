package eu.europa.ec.empl.edci.issuer.web.model.data;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;

public class AccreditationLiteView {

    private String id;
    private TextDTView title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TextDTView getTitle() {
        return title;
    }

    public void setTitle(TextDTView title) {
        this.title = title;
    }
}
