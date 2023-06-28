package eu.europa.ec.empl.edci.issuer.web.model.specs.lite;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.SpecView;

public class OrganizationSpecLiteView extends SpecView {

    private TextDTView legalName; //1

    public TextDTView getLegalName() {
        return legalName;
    }

    public void setLegalName(TextDTView legalName) {
        this.legalName = legalName;
    }
}