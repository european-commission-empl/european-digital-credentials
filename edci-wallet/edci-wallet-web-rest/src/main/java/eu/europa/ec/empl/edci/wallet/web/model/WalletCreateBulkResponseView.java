package eu.europa.ec.empl.edci.wallet.web.model;

import java.util.ArrayList;
import java.util.List;

public class WalletCreateBulkResponseView {

    private List<WalletCreateBulkResponseElemView> errors = new ArrayList<>();

    public WalletCreateBulkResponseView() {
    }

    public List<WalletCreateBulkResponseElemView> getErrors() {
        return errors;
    }

    public void setErrors(List<WalletCreateBulkResponseElemView> errors) {
        this.errors = errors;
    }
}
