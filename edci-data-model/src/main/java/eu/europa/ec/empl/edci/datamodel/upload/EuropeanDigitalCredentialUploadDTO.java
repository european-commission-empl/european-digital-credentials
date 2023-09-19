package eu.europa.ec.empl.edci.datamodel.upload;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;

public class EuropeanDigitalCredentialUploadDTO {

    private EuropeanDigitalCredentialDTO credential;
    private DeliveryDetailsDTO deliveryDetails;

    public EuropeanDigitalCredentialDTO getCredential() {
        return credential;
    }

    public void setCredential(EuropeanDigitalCredentialDTO credential) {
        this.credential = credential;
    }

    public DeliveryDetailsDTO getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(DeliveryDetailsDTO deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }
}
