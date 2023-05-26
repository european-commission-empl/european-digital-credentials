package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import java.net.URI;

public class MailboxDTO extends JsonLdCommonDTO {

    @Override
    public String toString() {
        return this.getId().toString();
    }

    @Override
    public void setId(URI id) {
        if(id != null) {
            if(id.toString().startsWith(DataModelConstants.Defaults.DEFAULT_MAILTO)) {
                super.setId(id);
            } else {
                super.setId(URI.create(DataModelConstants.Defaults.DEFAULT_MAILTO.concat(id.toString())));
            }
        }
    }
}
