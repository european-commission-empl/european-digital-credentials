package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.model.AccreditationDTO;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessageKeys;
import eu.europa.ec.empl.edci.mapper.accreditation.QMSAccreditationsMapper;
import eu.europa.ec.empl.edci.model.qmsaccreditation.QMSAccreditationDTO;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.QMSAccreditationsService;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropassCredentialDTO.class, preProcess = true, priority = 4)
@Component
public class AccreditationConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private QMSAccreditationsMapper qmsAccreditationsMapper;

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private QMSAccreditationsService qmsAccreditationsService;

    @Autowired
    private Validator validator;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Override
    public void accept(ConsumerContext context) {
        EuropassCredentialDTO europassCredentialDTO = context.getCredential().getCredential();
        Set<AccreditationDTO> accreditations = this.getReflectiveUtil().getUniqueInnerObjectsOfType(AccreditationDTO.class, europassCredentialDTO);
        List<String> errors = new ArrayList<>();
        //Loop through all existing accreditations in credential
        for (AccreditationDTO accreditationDTO : accreditations) {
            if (validator.notEmpty(accreditationDTO.getId())) {
                QMSAccreditationDTO qmsAccreditationDTO = this.getQmsAccreditationsService().getAccreditation(accreditationDTO.getId());
                //If Accreditation can be downloaded, fill required parameters from online source
                if (this.getValidator().notEmpty(qmsAccreditationDTO)) {
                    AccreditationDTO downloadedAcc = this.getQmsAccreditationsMapper().toAccreditationDTO(qmsAccreditationDTO);
                    Code accreditationType = new Code();
                    accreditationType.setUri(qmsAccreditationDTO.getType().toString());
                    accreditationType.setTargetFrameworkURI(ControlledList.ACCREDITATION.getUrl());
                    accreditationDTO.setAccreditationType(accreditationType);
                    accreditationDTO.setId(downloadedAcc.getId());
                    accreditationDTO.setIdentifier(downloadedAcc.getIdentifier());
                    accreditationDTO.setTitle(downloadedAcc.getTitle());
                    accreditationDTO.setDescription(downloadedAcc.getDescription());
                    accreditationDTO.setReport(downloadedAcc.getReport());
                    accreditationDTO.setOrganization(downloadedAcc.getOrganization());
                    accreditationDTO.setLimitQualification(downloadedAcc.getLimitQualification());
                    accreditationDTO.setLimitField(downloadedAcc.getLimitField());
                    accreditationDTO.setLimitEqfLevel(downloadedAcc.getLimitEqfLevel());
                    accreditationDTO.setLimitJurisdiction(downloadedAcc.getLimitJurisdiction());
                    accreditationDTO.setAccreditingAgent(downloadedAcc.getAccreditingAgent());
                    accreditationDTO.setIssueDate(downloadedAcc.getIssueDate());
                    accreditationDTO.setReviewDate(downloadedAcc.getReviewDate());
                    accreditationDTO.setExpiryDate(downloadedAcc.getExpiryDate());
                    accreditationDTO.setAdditionalNote(downloadedAcc.getAdditionalNote());
                    accreditationDTO.setHomePage(downloadedAcc.getHomePage());
                    accreditationDTO.setSupplementaryDocument(downloadedAcc.getSupplementaryDocument());
                } else {
                    errors.add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.REQUIRED_ACC_ITEM_NOTFOUND, accreditationDTO.getId()));
                }
            } else {
                errors.add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.REQUIRED_ID_ACC_DOWNLOAD, accreditationDTO.getIdentifiableName()));
            }
        }
        europassCredentialDTO.getValidationErrors().addAll(errors);
    }

    public QMSAccreditationsMapper getQmsAccreditationsMapper() {
        return qmsAccreditationsMapper;
    }

    public void setQmsAccreditationsMapper(QMSAccreditationsMapper qmsAccreditationsMapper) {
        this.qmsAccreditationsMapper = qmsAccreditationsMapper;
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

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }
}
