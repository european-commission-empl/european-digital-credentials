package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.Evidence;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessageKeys;
import eu.europa.ec.empl.edci.mapper.accreditation.QMSAccreditationsMapper;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.QDRAccreditationExternalService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static eu.europa.ec.empl.edci.constants.EDCIMessageKeys.Acreditation.EVIDENCE_ACCREDITATION_NOT_FOUND;

@EDCIConsumer(applyTo = EuropeanDigitalCredentialDTO.class, priority = 5)
@Component
public class AccreditationConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private QMSAccreditationsMapper qmsAccreditationsMapper;

    @Autowired
    private QDRAccreditationExternalService accreditationExternalService;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Override
    public void accept(ConsumerContext context) {
        EuropeanDigitalCredentialDTO europassCredentialDTO = context.getCredential();
        //Check if it is accredited credential
        if (this.shouldDownloadAccreditation(europassCredentialDTO)) {
            List<Evidence> accreditedEvidences = context.getCredential().getEvidence().stream()
                    .filter(evd -> evd.getDcType() != null && evd.getDcType().getId().toString().equalsIgnoreCase(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getUrl()))
                    .collect(Collectors.toList());
            accreditedEvidences.forEach(accreditedEvidence -> {
                if (accreditedEvidence.getAccreditation() == null || accreditedEvidence.getAccreditation().getId() == null) {
                    europassCredentialDTO.setValid(false);
                    europassCredentialDTO.getValidationErrors().add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.REQUIRED_ID_ACC_DOWNLOAD, accreditedEvidence.getName()));
                } else {
                    AccreditationDTO qdrAccreditation = this.getAccreditationExternalService().retrieveAccreditationByUri(accreditedEvidence.getAccreditation().getId().toString(), europassCredentialDTO.getDisplayParameter().getPrimaryLanguage());
                    if (qdrAccreditation == null) {
                        europassCredentialDTO.setValid(false);
                        europassCredentialDTO.getValidationErrors().add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.REQUIRED_ACC_ITEM_NOTFOUND, accreditedEvidence.getId()));
                    } else {
                        accreditedEvidence.setAccreditation(qdrAccreditation);
                    }
                }
            });
        }
    }

    private boolean shouldDownloadAccreditation(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        if (this.getCredentialUtil().isAccreditedCredential(europeanDigitalCredentialDTO)) {
            if (europeanDigitalCredentialDTO.getEvidence() == null || europeanDigitalCredentialDTO.getEvidence().isEmpty()) {
                europeanDigitalCredentialDTO.setValid(false);
                europeanDigitalCredentialDTO.getValidationErrors().add(this.getEdciMessageService().getMessage(EVIDENCE_ACCREDITATION_NOT_FOUND));
            } else {
                return true;
            }
        } else if (europeanDigitalCredentialDTO.getEvidence() != null && !europeanDigitalCredentialDTO.getEvidence().isEmpty()) {
            return true;
        }
        return false;
    }

    public QMSAccreditationsMapper getQmsAccreditationsMapper() {
        return qmsAccreditationsMapper;
    }

    public void setQmsAccreditationsMapper(QMSAccreditationsMapper qmsAccreditationsMapper) {
        this.qmsAccreditationsMapper = qmsAccreditationsMapper;
    }

    public QDRAccreditationExternalService getAccreditationExternalService() {
        return accreditationExternalService;
    }

    public void setAccreditationExternalService(QDRAccreditationExternalService accreditationExternalService) {
        this.accreditationExternalService = accreditationExternalService;
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }
}
