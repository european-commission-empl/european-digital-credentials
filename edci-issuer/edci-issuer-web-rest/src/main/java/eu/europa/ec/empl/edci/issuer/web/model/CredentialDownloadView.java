package eu.europa.ec.empl.edci.issuer.web.model;

import eu.europa.ec.empl.edci.datamodel.view.DeliveryDetailsView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CredentialDownloadView {

    public List<String> uuid = new ArrayList<>();

    public Map<String, DeliveryDetailsView> deliveryDetailsMap;

    public List<String> getUuid() {
        return uuid;
    }

    public void setUuid(List<String> uuid) {
        this.uuid = uuid;
    }

    public Map<String, DeliveryDetailsView> getDeliveryDetailsMap() {
        return deliveryDetailsMap;
    }

    public void setDeliveryDetailsMap(Map<String, DeliveryDetailsView> deliveryDetailsMap) {
        this.deliveryDetailsMap = deliveryDetailsMap;
    }
}