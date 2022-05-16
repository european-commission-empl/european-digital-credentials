package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.issuer.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropassCredentialDTO.class, preProcess = true, priority = 4)
@Component
public class CredentialDownloadConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private DownloadService downloadService;

    @Override
    public void accept(ConsumerContext context) {
        List<String> errors = new ArrayList<String>();
        EuropassCredentialDTO europassCredentialDTO = context.getCredential().getCredential();
        errors = downloadService.downloadAssets(europassCredentialDTO);
        if (errors.size() != 0) {
            europassCredentialDTO.setValid(false);
            europassCredentialDTO.getValidationErrors().addAll(errors);
        }
    }
}

