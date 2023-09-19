package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.ResourcesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropeanDigitalCredentialDTO.class, priority = 1)
@Component
public class CredentialLoopCheckConsumer implements Consumer<ConsumerContext> {

    private static final Logger logger = LogManager.getLogger(CredentialLoopCheckConsumer.class);

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    ResourcesUtil resourcesUtil;

    @Override
    public void accept(ConsumerContext context) {
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        EuropeanDigitalCredentialDTO europassCredentialDTO = context.getCredential();
        //TODO ->  RESTORE
        resourcesUtil.checkContentClassLoopTree(europassCredentialDTO);
        if (logger.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            logger.debug(String.format("end CredentialLoopCheckConsumer, took %d seconds", (end - start) / 1000));
        }
    }
}
