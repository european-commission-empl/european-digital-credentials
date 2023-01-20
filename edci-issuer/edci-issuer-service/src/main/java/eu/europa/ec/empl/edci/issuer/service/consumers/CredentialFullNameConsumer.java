package eu.europa.ec.empl.edci.issuer.service.consumers;


import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropassCredentialDTO.class, preProcess = true, priority = 3)
@Component
public class CredentialFullNameConsumer implements Consumer<ConsumerContext> {

    @Override
    public void accept(ConsumerContext consumerContext) {
        PersonDTO credentialSubject = consumerContext.getCredential().getCredential().getCredentialSubject();
        if (credentialSubject.getFullName() == null || credentialSubject.getFullName().getContents().isEmpty() || credentialSubject.getFullName().getAnyLanguageString().isEmpty()) {
            String locale = consumerContext.getCredential().getCredential().getPrimaryLanguage();
            String fullName = credentialSubject.getGivenNames().getLocalizedStringOrAny(locale)
                    .concat(EDCIConstants.StringPool.STRING_SPACE)
                    .concat(credentialSubject.getFamilyName().getLocalizedStringOrAny(locale));
            credentialSubject.setFullName(new Text(fullName, locale));
        }
    }
}
