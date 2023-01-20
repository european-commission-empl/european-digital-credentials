package eu.europa.ec.empl.edci.wallet.web.model;

public class WalletCreateBulkResponseElemView {

    private String userEmail;
    private String errorCode;
    private String errorMsg;

    public WalletCreateBulkResponseElemView() {
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
