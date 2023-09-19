package eu.europa.ec.empl.edci.security.service.base;

public interface IEDCIAuthenticationService {

    public boolean isAuthenticated();

    public String getAccessToken();

    public String getRefreshToken();
}
