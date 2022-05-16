package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.QMSAccreditationsService;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@EDCIConsumer(applyTo = EuropassCredentialDTO.class, preProcess = false, priority = 9)
@Component
public class AccreditationValidationConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private QMSAccreditationsService qmsAccreditationsService;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Override
    public void accept(ConsumerContext context) {
        EuropassCredentialDTO europassCredentialDTO = context.getCredential().getCredential();
        ValidationResult validationResult = this.getQmsAccreditationsService().isCoveredCredential(europassCredentialDTO);
        //Check for any uncovered accreditation, the credential will result invalid in case of finding one, credentials with no accreditation return null
        if (validationResult != null && !validationResult.isValid()) {
            europassCredentialDTO.setValid(false);
            for (ValidationError validationError : validationResult.getValidationErrors()) {
                List<String> identifiableAffectedAssets = validationError.getAffectedAssets().stream().map(Identifiable::getIdentifiableName).collect(Collectors.toList());
                europassCredentialDTO.getValidationErrors().add(this.getEdciMessageService().getMessage(validationError.getErrorKey(), identifiableAffectedAssets.toArray()));
            }
        }
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
}
