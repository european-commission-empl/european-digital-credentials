package eu.europa.ec.empl.edci.wallet.web.model;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class WalletCreateBulkView {

    @Valid
    @NotEmpty
    private List<WalletCreateBulkElemView> wallets;

    public WalletCreateBulkView() {
    }

    public List<WalletCreateBulkElemView> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletCreateBulkElemView> wallets) {
        this.wallets = wallets;
    }
}
