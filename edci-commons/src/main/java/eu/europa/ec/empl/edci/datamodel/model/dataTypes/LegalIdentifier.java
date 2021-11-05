package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class LegalIdentifier extends Identifier {

    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_LEGALIDENTIFIER_SPATIALID_NOTNULL)
    @XmlAttribute(name = "spatialID")
    private String spatialId; //1

    public String getSpatialId() {
        return spatialId;
    }

    public void setSpatialId(String spatialId) {
        this.spatialId = spatialId;
    }
}