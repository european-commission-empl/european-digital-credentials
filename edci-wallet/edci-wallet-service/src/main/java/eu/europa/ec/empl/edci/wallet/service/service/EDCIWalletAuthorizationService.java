package eu.europa.ec.empl.edci.wallet.service.service;

import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.security.service.base.EDCIAuthorizationService;
import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.repository.ShareLinkRepository;
import eu.europa.ec.empl.edci.wallet.repository.WalletRepository;
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
    private WalletRepository walletRepository;

    @Autowired
    @Qualifier("walletConfigService")
    @Override
    public void setConfigService(BaseConfigService configService) {
        this.configService = configService;
    }

    /**
     * Returns true if the userID provided is the same as the one found in the token.
     *
     * @param sub userId
     * @return true if both user ids match or if mockUser configuration parameter is set to true
     */
    @Override
    public boolean isAuthorized(String sub) {
        return this.isUser(sub);
    }

    /**
     * Retrieves the userID linked to the wallet associated with the wallet address and validates
     * if it's the same as the one provided in the authetication token
     *
     * @param walletAddress wallet's wallet address
     * @return true if the wallet address belongs to the user's trying to perform the operation
     */
    public boolean isAuthorizedAddress(String walletAddress) {
        WalletDAO walletDAO = walletRepository.fetchByWalletAddress(walletAddress);
        if (walletDAO == null) {
            throw new EDCINotFoundException();
        }
        return isAuthorized(walletDAO.getUserId());
    }

    /**
     * Retrieves the userID linked to the wallet associated with the sharelink's credential and validates
     * if it's the same as the one provided in the authetication token
     *
     * @param sharelink sharelink's hash
     * @return true if the sharehash belongs to the user's trying to perform the operation
     */
    public boolean isAuthorizedSharelink(String sharelink) {
        ShareLinkDAO sharelinkDAO = shareLinkRepository.fetchBySharedURL(sharelink);
        if (sharelinkDAO == null) {
            throw new EDCINotFoundException();
        }
        return this.isAuthorized(sharelinkDAO.getCredential().getWallet().getUserId());
    }
}
