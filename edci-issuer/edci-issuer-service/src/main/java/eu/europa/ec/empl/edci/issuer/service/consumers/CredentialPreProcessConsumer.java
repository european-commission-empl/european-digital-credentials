package eu.europa.ec.empl.edci.issuer.service.consumers;


import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.MultilangFieldUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropeanDigitalCredentialDTO.class, priority = 4)
@Component
public class CredentialPreProcessConsumer implements Consumer<ConsumerContext> {

    private static final Logger logger = LogManager.getLogger(CredentialPreProcessConsumer.class);

    @Autowired
    private CredentialUtil credentialUtil;

    @Override
    public void accept(ConsumerContext consumerContext) {
        logger.info("start CredentialDownloadConsumer");
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }

        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = consumerContext.getCredential();
        europeanDigitalCredentialDTO.setIssuer(null);

        PersonDTO credentialSubject = europeanDigitalCredentialDTO.getCredentialSubject();
        LiteralMap fullName = credentialSubject.getFullName();


        if (credentialSubject.getFullName() == null || credentialSubject.getFullName().isEmpty()) {

            if (credentialSubject.getGivenName() != null && credentialSubject.getFamilyName() != null
                && !credentialSubject.getGivenName().isEmpty() && !credentialSubject.getFamilyName().isEmpty()) {
                String langKey = credentialSubject.getGivenName().entrySet().iterator().next().getKey();
                String fullNameString = MultilangFieldUtil.getLiteralStringOrAny(credentialSubject.getGivenName(), langKey)
                        .concat(EDCIConstants.StringPool.STRING_SPACE)
                        .concat(MultilangFieldUtil.getLiteralStringOrAny(credentialSubject.getFamilyName(), langKey));
                fullName = new LiteralMap();
                fullName.put(langKey, fullNameString);
            }

            credentialSubject.setFullName(fullName);

        }
        if (logger.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            logger.debug(String.format("end CredentialDownloadConsumer, took %d seconds", (end - start) / 1000));
        }
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }
}
