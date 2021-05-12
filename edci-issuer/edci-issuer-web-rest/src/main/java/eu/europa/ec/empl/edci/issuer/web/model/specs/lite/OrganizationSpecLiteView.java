package eu.europa.ec.empl.edci.issuer.web.model.specs.lite;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.SpecView;

public class OrganizationSpecLiteView extends SpecView {

    private TextDTView preferredName; //1

    public TextDTView getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(TextDTView preferredName) {
        this.preferredName = preferredName;
    }
}