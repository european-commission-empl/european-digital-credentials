package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;

import java.util.List;


public class LocationDCView extends DataContainerView {

    private List<IdentifierDTView> identifier; //*

    private TextDTView geographicName; //0..1

    private List<CodeDTView> spatialCode; //*

    private NoteDTView description; //0..1

    private List<AddressDCView> hasAddress; //0..1

    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public TextDTView getGeographicName() {
        return geographicName;
    }

    public void setGeographicName(TextDTView geographicName) {
        this.geographicName = geographicName;
    }

    public List<CodeDTView> getSpatialCode() {
        return spatialCode;
    }

    public void setSpatialCode(List<CodeDTView> spatialCode) {
        this.spatialCode = spatialCode;
    }

    public NoteDTView getDescription() {
        return description;
    }

    public void setDescription(NoteDTView description) {
        this.description = description;
    }

    public List<AddressDCView> getHasAddress() {
        return hasAddress;
    }

    public void setHasAddress(List<AddressDCView> hasAddress) {
        this.hasAddress = hasAddress;
    }
}