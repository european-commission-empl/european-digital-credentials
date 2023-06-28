package eu.europa.ec.empl.edci.wallet.service.service;

import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.security.service.base.EDCIAuthorizationService;
import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import eu.europa.ec.empl.edci.wallet.repository.ShareLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "edciWalletAuthorizationService")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCIWalletAuthorizationService extends EDCIAuthorizationService {

    @Autowired
    private ShareLinkRepository shareLinkRepository;

    @Autowired
    @Qualifier("walletConfigService")
    @Override
    public void setConfigService(BaseConfigService configService) {
        this.configService = configService;
    }

    @Override
    public boolean isAuthorized(String sub) {
        return this.isUser(sub) || this.isMockUserActive();
    }

    public boolean isAuthorizedSharelink(String sharelink) {
        ShareLinkDAO sharelinkDAO = shareLinkRepository.fetchBySharedURL(sharelink);
        return this.isAuthorized(sharelinkDAO.getCredential().getWallet().getUserId());
    }
}
