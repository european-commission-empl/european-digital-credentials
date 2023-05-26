package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.constants.DataModelConstants;

import java.net.URI;

public class QDRMailboxDTO extends QDRJsonLdCommonDTO {

    public QDRMailboxDTO(String uri) {
        super.setUri(URI.create(uri));
    }

    @Override
    public String toString() {
        return this.getUri().toString();
    }

    @Override
    public void setUri(URI uri) {
        if(uri != null) {
            if(uri.toString().startsWith(DataModelConstants.Defaults.DEFAULT_MAILTO)) {
                super.setUri(uri);
            } else {
                super.setUri(URI.create(DataModelConstants.Defaults.DEFAULT_MAILTO.concat(uri.toString())));
            }
        }
    }
}
