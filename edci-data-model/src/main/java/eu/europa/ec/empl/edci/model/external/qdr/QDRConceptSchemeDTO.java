package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import java.net.URI;

@EDCIIdentifier(prefix = "urn:epass:conceptScheme:")
public class QDRConceptSchemeDTO extends QDRJsonLdCommonDTO {

    public QDRConceptSchemeDTO(URI id) {
        super.setUri(id);
    }

    public QDRConceptSchemeDTO(String id) {
        super.setUri(URI.create(id));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
