package eu.europa.ec.empl.edci.issuer.task;

import eu.europa.ec.empl.edci.service.RDFsparqlBridgeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ControlledListsTask {
    private static final Logger logger = LogManager.getLogger(ControlledListsTask.class);

//    @Autowired
//    private ControlledListsService controlledListsService;
//
//    @Autowired
//    private ControlledListsMapper controlledListsMapper;
//
//    @Autowired
//    private IssuerConfigService issuerConfigService;

    @Autowired
    private RDFsparqlBridgeService rdfsparqlBridgeService;

//    @PostConstruct //When starting the server
//    @Deprecated
//    public void loadControlledListsOnStartup() {
//        if (issuerConfigService.getBoolean(Constant.LOAD_CONTROLLED_LISTS_STARTUP)) {
//            controlledListsService.loadControlledLists(false);
//        }
//    }

//    @Scheduled(cron = "0 0 0 1 1/1 *") //Monthly
//    @Deprecated
//    public void loadControlledListsMonthly() {
//        if (issuerConfigService.getBoolean(Constant.LOAD_CONTROLLED_LISTS_MONTHLY)) {
//            controlledListsService.loadControlledLists(false);
//        }
//    }

    @Scheduled(cron = "0 0 1 * * MON") //Every monday at 1 AM
    public void loadControlledListsMonthly() {
        rdfsparqlBridgeService.resetAllControlledListsConceptSchemes();
    }


}