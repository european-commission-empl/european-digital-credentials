package eu.europa.ec.empl.edci.datamodel.upload;

import java.util.ArrayList;
import java.util.List;

public class DeliveryDetailsDTO {

    private List<String> deliveryAddress = new ArrayList<>();
    private DisplayDetailsDTO displayDetails;

    public DeliveryDetailsDTO() {
    }

    public DeliveryDetailsDTO(List<String> deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<String> getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(List<String> deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public DisplayDetailsDTO getDisplayDetails() {
        return displayDetails;
    }

    public void setDisplayDetails(DisplayDetailsDTO displayDetails) {
        this.displayDetails = displayDetails;
    }
}
