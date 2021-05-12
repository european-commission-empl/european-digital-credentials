package eu.europa.ec.empl.edci.issuer.common.model;

public class StatusDTO {
    private Boolean status;
    private String message;

    public StatusDTO(){

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
