package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;

import java.net.URI;
import java.util.List;

public class AddressDCView extends DataContainerView {

    private URI id; //0..1

    private List<IdentifierDTView> identifier; //*

    private NoteDTView fullAddress; //0..1

    private CodeDTView countryCode; //1

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public NoteDTView getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(NoteDTView fullAddress) {
        this.fullAddress = fullAddress;
    }

    public CodeDTView getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(CodeDTView countryCode) {
        this.countryCode = countryCode;
    }
}