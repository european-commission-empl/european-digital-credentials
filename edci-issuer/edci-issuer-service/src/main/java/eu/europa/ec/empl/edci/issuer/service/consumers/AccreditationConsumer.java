package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.mapper.accreditation.QMSAccreditationsMapper;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.QMSAccreditationsService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropeanDigitalCredentialDTO.class, priority = 5)
@Component
public class AccreditationConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private QMSAccreditationsMapper qmsAccreditationsMapper;

    @Autowired
    private QMSAccreditationsService qmsAccreditationsService;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Override
    public void accept(ConsumerContext context) {
        EuropeanDigitalCredentialDTO europassCredentialDTO = context.getCredential();
        //Check if it is accredited credential
       /* if (this.getCredentialUtil().isAccreditedCredential(europassCredentialDTO)) {
            Evidence evidence = context.getCredential().getEvidence();
            AccreditationDTO accreditationDTO = evidence != null && evidence.getAccreditation() != null ? evidence.getAccreditation() : null;
            //Check if accreditation exists and has an ID
            if (accreditationDTO != null && accreditationDTO.getId() != null) {
                //Download accreditation
                QMSAccreditationDTO qmsAccreditationDTO = this.getQmsAccreditationsService().getAccreditation(accreditationDTO.getId());
                if (qmsAccreditationDTO != null) {
                    //set fields in current credential's accreditation
                    AccreditationDTO downloadedAcc = this.getQmsAccreditationsMapper().toAccreditationDTO(qmsAccreditationDTO);
                    ConceptDTO accreditationType = downloadedAcc.getDcType();
                    accreditationDTO.setDcType(accreditationType);
                    accreditationDTO.setId(downloadedAcc.getId());
                    accreditationDTO.setIdentifier(downloadedAcc.getIdentifier());
                    accreditationDTO.setTitle(downloadedAcc.getTitle());
                    accreditationDTO.setDescription(downloadedAcc.getDescription());
                    accreditationDTO.setReport(downloadedAcc.getReport());
                    accreditationDTO.setOrganisation(downloadedAcc.getOrganisation());
                    accreditationDTO.setLimitQualification(downloadedAcc.getLimitQualification());
                    accreditationDTO.setLimitField(downloadedAcc.getLimitField());
                    accreditationDTO.setLimitEQFLevel(downloadedAcc.getLimitEQFLevel());
                    accreditationDTO.setAccreditedInJurisdiction(downloadedAcc.getAccreditedInJurisdiction());
                    accreditationDTO.setAccreditingAgent(downloadedAcc.getAccreditingAgent());
                    accreditationDTO.setDateIssued(downloadedAcc.getDateIssued());
                    accreditationDTO.setReviewDate(downloadedAcc.getReviewDate());
                    accreditationDTO.setExpiryDate(downloadedAcc.getExpiryDate());
                    accreditationDTO.setAdditionalNote(downloadedAcc.getAdditionalNote());
                    accreditationDTO.setHomepage(downloadedAcc.getHomepage());
                    accreditationDTO.setSupplementaryDocument(downloadedAcc.getSupplementaryDocument());
                } else {
                    //Accreditation not found
                    europassCredentialDTO.getValidationErrors().add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.REQUIRED_ACC_ITEM_NOTFOUND, accreditationDTO.getId()));
                }

            } else {
                //Accreditation without ID
                europassCredentialDTO.getValidationErrors().add(
                        this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.REQUIRED_ID_ACC_DOWNLOAD,
                                accreditationDTO != null ? accreditationDTO.getName() : EDCIConstants.StringPool.STRING_EMPTY));
            }
        }*/
    }

    public QMSAccreditationsMapper getQmsAccreditationsMapper() {
        return qmsAccreditationsMapper;
    }

    public void setQmsAccreditationsMapper(QMSAccreditationsMapper qmsAccreditationsMapper) {
        this.qmsAccreditationsMapper = qmsAccreditationsMapper;
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

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }
}
