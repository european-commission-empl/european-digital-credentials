package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import java.net.URI;

public class ShaclValidator2017 extends JsonLdCommonDTO {

    public ShaclValidator2017() {
    }

    public ShaclValidator2017(URI id) {
        super.setId(id);
    }

    //inherited from parent
    public String getType() {
        return "ShaclValidator2017";
    }
}
