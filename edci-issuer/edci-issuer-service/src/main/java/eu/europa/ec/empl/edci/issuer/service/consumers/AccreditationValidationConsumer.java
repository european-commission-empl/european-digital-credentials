package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.QMSAccreditationsService;
import eu.europa.ec.empl.edci.util.EDCIValidationUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropeanDigitalCredentialDTO.class, priority = 7)
@Component
public class AccreditationValidationConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private QMSAccreditationsService qmsAccreditationsService;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EDCIValidationUtil edciValidationUtil;

    @Override
    public void accept(ConsumerContext context) {
        EuropeanDigitalCredentialDTO europassCredentialDTO = context.getCredential();
        //TODO ->  RESTORE
        /*ValidationResult validationResult = this.getQmsAccreditationsService().isCoveredCredential(europassCredentialDTO);
        //Check for any uncovered accreditation, the credential will result invalid in case of finding one, credentials with no accreditation return null
        if (validationResult != null && !validationResult.isValid()) {
            this.getEdciValidationUtil().loadLocalizedMessages(validationResult);
            europassCredentialDTO.setValid(false);
            for (ValidationError validationError : validationResult.getValidationErrors()) {
                europassCredentialDTO.getValidationErrors().add(validationError.getErrorMessage());
            }
        }*/
    }

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }

    public QMSAccreditationsService getQmsAccreditationsService() {
        return qmsAccreditationsService;
    }

    public void setQmsAccreditationsService(QMSAccreditationsService qmsAccreditationsService) {
        this.qmsAccreditationsService = qmsAccreditationsService;
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }

    public EDCIValidationUtil getEdciValidationUtil() {
        return edciValidationUtil;
    }

    public void setEdciValidationUtil(EDCIValidationUtil edciValidationUtil) {
        this.edciValidationUtil = edciValidationUtil;
    }
}
