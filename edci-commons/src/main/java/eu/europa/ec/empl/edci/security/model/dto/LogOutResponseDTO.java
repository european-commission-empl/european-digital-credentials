package eu.europa.ec.empl.edci.security.model.dto;

public class LogOutResponseDTO {

    private String redirectUrl;

    public LogOutResponseDTO(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
