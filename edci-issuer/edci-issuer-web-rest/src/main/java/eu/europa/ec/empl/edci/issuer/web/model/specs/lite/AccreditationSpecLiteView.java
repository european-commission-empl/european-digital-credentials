package eu.europa.ec.empl.edci.issuer.web.model.specs.lite;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.SpecView;

public class AccreditationSpecLiteView extends SpecView {

    private TextDTView title; //1

    public TextDTView getTitle() {
        return title;
    }

    public void setTitle(TextDTView title) {
        this.title = title;
    }

}