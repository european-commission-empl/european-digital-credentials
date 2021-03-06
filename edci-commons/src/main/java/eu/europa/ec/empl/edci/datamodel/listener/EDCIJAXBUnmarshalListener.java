package eu.europa.ec.empl.edci.datamodel.listener;


import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.MailboxDTO;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.Unmarshaller;
import java.net.URI;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCIJAXBUnmarshalListener extends Unmarshaller.Listener {

    @Override
    public void afterUnmarshal(Object target, Object parent) {
        if (target instanceof MailboxDTO) {
            MailboxDTO mailboxDTO = (MailboxDTO) target;
            String mailString = mailboxDTO.getId().toString();

            if (mailString.contains(EDCIConfig.Defaults.DEFAULT_MAILTO)) {
                mailString = mailString.replace(EDCIConfig.Defaults.DEFAULT_MAILTO, "");
                mailboxDTO.setId(URI.create(mailString));
            }
        }

        //Fix to avoid duplication of objects on reading/marshalling same dto
        //ToDo-> add more entities
        if (target instanceof EuropassCredentialDTO) {
            EuropassCredentialDTO europassCredentialDTO = (EuropassCredentialDTO) target;
            europassCredentialDTO.setLearningSpecificationReferences(null);
            europassCredentialDTO.setOrganisationReferences(null);
            europassCredentialDTO.setAccreditationReferences(null);
            europassCredentialDTO.setAssessmentSpecificationReferences(null);
            europassCredentialDTO.setAwardingProcessReferences(null);
            europassCredentialDTO.setEntitlementSpecificationReferences(null);
            europassCredentialDTO.setLearningOpportunityReferences(null);
            europassCredentialDTO.setLearningOutcomeReferences(null);
            europassCredentialDTO.setScoringSchemeReferences(null);
        }
        super.afterUnmarshal(target, parent);
    }
}
