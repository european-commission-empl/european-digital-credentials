package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.service.EDCIValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.groups.Default;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropassCredentialDTO.class, priority = 10)
@Component
public class CredentialValidationConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private EDCIValidationService edciValidationService;

    @Override
    public void accept(ConsumerContext context) {
        EuropassCredentialDTO europassCredentialDTO = context.getCredential().getCredential();
        ValidationResult validationResult = this.getEdciValidationService().validateAndLocalizeCredentialHolder(context.getCredential(), Default.class);

        if (!validationResult.isValid()) {
            europassCredentialDTO.setValid(false);
            List<String> errorMessages = validationResult.getDistinctErrorMessages();
            Collections.sort(errorMessages);
            europassCredentialDTO.getValidationErrors().addAll(errorMessages);
        }
    }

    public EDCIValidationService getEdciValidationService() {
        return edciValidationService;
    }

    public void setEdciValidationService(EDCIValidationService edciValidationService) {
        this.edciValidationService = edciValidationService;
    }
}
