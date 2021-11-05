package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
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
public class CredentialValidationConsumer implements Consumer<EuropassCredentialDTO> {

    @Autowired
    private EDCIValidationService edciValidationService;

    @Override
    public void accept(EuropassCredentialDTO europassCredentialDTO) {
        ValidationResult validationResult = edciValidationService.validateAndLocalize(europassCredentialDTO, Default.class);

        if (!validationResult.isValid()) {
            europassCredentialDTO.setValid(false);
            List<String> errorMessages = validationResult.getDistinctErrorMessages();
            Collections.sort(errorMessages);
            europassCredentialDTO.getValidationErrors().addAll(errorMessages);
        }
    }
}
