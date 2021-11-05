package eu.europa.ec.empl.edci.wallet.task;

import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WalletTask {
    private static final Logger logger = Logger.getLogger(WalletTask.class);

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletConfigService walletConfigService;

    @Scheduled(cron = "0 0 5 * * ?") //At 05:00
    @Transactional(propagation = Propagation.REQUIRED)
    public void loadControlledListsMonthly() {

        walletService.deleteOldTempWallets();

    }


}