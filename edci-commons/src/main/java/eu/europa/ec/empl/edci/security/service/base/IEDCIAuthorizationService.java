package eu.europa.ec.empl.edci.security.service.base;


import eu.europa.ec.empl.edci.config.service.BaseConfigService;

public interface IEDCIAuthorizationService {

    boolean isAuthorized(String sub);

    boolean isUser(String sub);

    boolean isMockUserActive();

    void setConfigService(BaseConfigService configService);
}
