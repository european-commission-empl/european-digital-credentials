package eu.europa.ec.empl.edci.issuer.service.consumers;


import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.upload.EuropeanDigitalCredentialUploadDTO;
import eu.europa.ec.empl.edci.issuer.service.DiplomaService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropeanDigitalCredentialDTO.class, priority = 8)
@Component
public class SummaryImagesConsumer implements Consumer<ConsumerContext> {

    private static final Logger logger = LogManager.getLogger(SummaryImagesConsumer.class);

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private DiplomaService diplomaService;

    @Override
    public void accept(ConsumerContext consumerContext) {
        logger.info("start DiplomaDisplayConsumer");
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        EuropeanDigitalCredentialUploadDTO europeanDigitalCredentialUploadDTO = consumerContext.getCredentialUpload();
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = consumerContext.getCredential();

        boolean hasDisplay = europeanDigitalCredentialDTO.getDisplayParameter() != null &&
                europeanDigitalCredentialDTO.getDisplayParameter().getIndividualDisplay() != null &&
                !europeanDigitalCredentialDTO.getDisplayParameter().getIndividualDisplay().isEmpty();

        boolean hasTemplate = europeanDigitalCredentialUploadDTO.getDeliveryDetails() != null &&
                europeanDigitalCredentialUploadDTO.getDeliveryDetails().getDisplayDetails() != null;

        if(hasTemplate || !hasDisplay) {
            europeanDigitalCredentialDTO.getDisplayParameter().setIndividualDisplay(new ArrayList<>());
            this.getDiplomaService().informDiplomaImage(europeanDigitalCredentialDTO, hasTemplate ? europeanDigitalCredentialUploadDTO.getDeliveryDetails().getDisplayDetails() : null);
        }

        if (logger.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            logger.debug(String.format("end DiplomaDisplayConsumer, took %d seconds", (end - start) / 1000));
        }
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }

    public DiplomaService getDiplomaService() {
        return diplomaService;
    }

    public void setDiplomaService(DiplomaService diplomaService) {
        this.diplomaService = diplomaService;
    }
}
