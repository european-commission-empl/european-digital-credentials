package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.service.QDRAccreditationValidationService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.EDCIValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropeanDigitalCredentialDTO.class, priority = 7)
@Component
public class AccreditationValidationConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private QDRAccreditationValidationService qdrAccreditationValidationService;
    @Autowired
    private EDCIValidationUtil edciValidationUtil;
    @Autowired
    private CredentialUtil credentialUtil;

    @Override
    public void accept(ConsumerContext context) {
        EuropeanDigitalCredentialDTO europassCredentialDTO = context.getCredential();
        //TODO ->  Move date generation to independent consumer?
        europassCredentialDTO.setIssued(ZonedDateTime.now());
        europassCredentialDTO.setIssuanceDate(ZonedDateTime.now());
        if (this.getCredentialUtil().isAccreditedCredential(europassCredentialDTO)) {
            ValidationResult validationResult = this.getQdrAccreditationValidationService().isCredentialCovered(europassCredentialDTO);
            //Check for any uncovered accreditation, the credential will result invalid in case of finding one, credentials with no accreditation return null
            if (validationResult != null && !validationResult.isValid()) {
                this.getEdciValidationUtil().loadLocalizedMessages(validationResult);
                europassCredentialDTO.setValid(false);
                validationResult.getValidationErrors().forEach(error -> europassCredentialDTO.getValidationErrors().add(error.getErrorMessage()));
            }
        }
    }

    public QDRAccreditationValidationService getQdrAccreditationValidationService() {
        return qdrAccreditationValidationService;
    }

    public void setQdrAccreditationValidationService(QDRAccreditationValidationService qdrAccreditationValidationService) {
        this.qdrAccreditationValidationService = qdrAccreditationValidationService;
    }

    public EDCIValidationUtil getEdciValidationUtil() {
        return edciValidationUtil;
    }

    public void setEdciValidationUtil(EDCIValidationUtil edciValidationUtil) {
        this.edciValidationUtil = edciValidationUtil;
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }
}
