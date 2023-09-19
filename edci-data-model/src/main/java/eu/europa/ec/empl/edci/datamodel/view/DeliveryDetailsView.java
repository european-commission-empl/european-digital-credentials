package eu.europa.ec.empl.edci.datamodel.view;

import java.util.ArrayList;
import java.util.List;

public class DeliveryDetailsView {

    private List<String> deliveryAddress = new ArrayList<>();
    private DisplayDetailsView displayDetails;

    public DeliveryDetailsView() {
    }

    public DeliveryDetailsView(List<String> deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<String> getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(List<String> deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public DisplayDetailsView getDisplayDetails() {
        return displayDetails;
    }

    public void setDisplayDetails(DisplayDetailsView displayDetails) {
        this.displayDetails = displayDetails;
    }
}
