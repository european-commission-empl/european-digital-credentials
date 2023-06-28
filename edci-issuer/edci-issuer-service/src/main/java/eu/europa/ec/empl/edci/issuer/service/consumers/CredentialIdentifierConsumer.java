package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.UUID;
import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropeanDigitalCredentialDTO.class, priority = 2)
@Component
public class CredentialIdentifierConsumer implements Consumer<ConsumerContext> {

    private static final Logger logger = LogManager.getLogger(CredentialLanguageConsumer.class);

    @Autowired
    private CredentialUtil credentialUtil;

    @Override
    public void accept(ConsumerContext context) {
        logger.info("start CredentialIdentifierConsumer");
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        EuropeanDigitalCredentialDTO europassCredentialDTO = context.getCredential();
        //TODO ->  RESTORE
        europassCredentialDTO.setId(URI.create(europassCredentialDTO.getIdPrefix(europassCredentialDTO).concat(UUID.randomUUID().toString())));

        this.getCredentialUtil().doAddMissingIdentifiers(europassCredentialDTO);

        if (logger.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            logger.debug(String.format("end CredentialIdentifierConsumer, took %d seconds", (end - start) / 1000));
        }
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }
}
