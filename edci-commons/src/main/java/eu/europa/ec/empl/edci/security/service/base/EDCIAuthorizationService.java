package eu.europa.ec.empl.edci.security.service.base;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.security.EDCISecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "edciWalletAuthorizationService")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public abstract class EDCIAuthorizationService implements IEDCIAuthorizationService {

    @Autowired
    public EDCISecurityContextHolder edciSecurityContextHolder;

    public IConfigService configService;

    public IConfigService getConfigService() {
        return configService;
    }

    public abstract void setConfigService(IConfigService configService);

    public EDCISecurityContextHolder getEdciSecurityContextHolder() {
        return edciSecurityContextHolder;
    }

    public void setEdciSecurityContextHolder(EDCISecurityContextHolder edciSecurityContextHolder) {
        this.edciSecurityContextHolder = edciSecurityContextHolder;
    }

    @Override
    public boolean isMockUserActive() {
        return this.getConfigService().getBoolean(EDCIConfig.Security.MOCK_USER_ACTIVE);
    }

    @Override
    public boolean isUser(String sub) {
        return this.getEdciSecurityContextHolder().getSub().equals(sub);
    }

}
