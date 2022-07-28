package eu.europa.ec.empl.edci.issuer.web.model;

public class StatusView {
    private Boolean status;
    private String message;

    public StatusView() {

    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
