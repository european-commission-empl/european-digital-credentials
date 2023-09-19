package eu.europa.ec.empl.edci.issuer.service.consumers;


import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.DisplayParameterDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropeanDigitalCredentialDTO.class, priority = 3)
@Component
public class CredentialLanguageConsumer implements Consumer<ConsumerContext> {

    private static final Logger logger = LogManager.getLogger(CredentialLanguageConsumer.class);

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Override
    public void accept(ConsumerContext context) {
        logger.info("start CredentialLanguageConsumer");
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        //Initialize DisplayParameter if null
        EuropeanDigitalCredentialDTO europassCredentialDTO = context.getCredential();
        if (europassCredentialDTO.getDisplayParameter() == null) {
            europassCredentialDTO.setDisplayParameter(new DisplayParameterDTO());
        }
        //Get primary language or default
        ConceptDTO primaryLanguage = europassCredentialDTO.getDisplayParameter().getPrimaryLanguage() != null
                && europassCredentialDTO.getDisplayParameter().getPrimaryLanguage().getId() != null
                ? this.getControlledListCommonsService().searchConceptByUri(
                ControlledList.LANGUAGE.getUrl(), europassCredentialDTO.getDisplayParameter().getPrimaryLanguage().getId().toString())
                : this.getControlledListCommonsService().searchLanguageByLang(Defaults.LOCALE);
        //Add primary language to available languages if not present
        if (!europassCredentialDTO.getDisplayParameter().getLanguage().stream()
                .anyMatch(language -> language.getId().equals(primaryLanguage.getId()))) {
            europassCredentialDTO.getDisplayParameter().getLanguage().add(primaryLanguage);
        }
        if (logger.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            logger.debug(String.format("end CredentialLanguageConsumer, took %d seconds", (end - start) / 1000));
        }
    }

    public ControlledListCommonsService getControlledListCommonsService() {
        return controlledListCommonsService;
    }

    public void setControlledListCommonsService(ControlledListCommonsService controlledListCommonsService) {
        this.controlledListCommonsService = controlledListCommonsService;
    }
}
