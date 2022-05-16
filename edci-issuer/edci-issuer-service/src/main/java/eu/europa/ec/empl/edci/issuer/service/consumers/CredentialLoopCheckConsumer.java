package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.ResourcesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropassCredentialDTO.class, preProcess = true, priority = 1)
@Component
public class CredentialLoopCheckConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    ResourcesUtil resourcesUtil;

    @Override
    public void accept(ConsumerContext context) {
        EuropassCredentialDTO europassCredentialDTO = context.getCredential().getCredential();

        resourcesUtil.checkContentClassLoopTree(europassCredentialDTO);

    }
}
