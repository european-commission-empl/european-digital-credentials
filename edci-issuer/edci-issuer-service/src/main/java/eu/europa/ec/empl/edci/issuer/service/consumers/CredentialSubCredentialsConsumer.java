package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropassCredentialDTO.class)
@Component
public class CredentialSubCredentialsConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    private Logger logger = LogManager.getLogger(CredentialSubCredentialsConsumer.class);

    @Override
    public void accept(ConsumerContext context) {
        EuropassCredentialDTO europassCredentialDTO = context.getCredential().getCredential();
        europassCredentialDTO.setSubCredentialsXML(edciCredentialModelUtil.generateXMLSubCreds(europassCredentialDTO));
    }
}
