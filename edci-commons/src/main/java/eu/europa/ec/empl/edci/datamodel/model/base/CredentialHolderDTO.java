package eu.europa.ec.empl.edci.datamodel.model.base;

import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.OrganizationDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.List;

public interface CredentialHolderDTO extends Identifiable {

    @XmlTransient
    EuropassCredentialDTO getCredential();

    Date getExpirationDate();

    OrganizationDTO getIssuer();

    void setIssuer(OrganizationDTO org);

    Code getType();

    public List<String> getValidationErrors();

}