package eu.europa.ec.empl.edci.datamodel.listener;

import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCIJAXBMarshalListener extends Marshaller.Listener {

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    private Logger logger = Logger.getLogger(EDCIJAXBMarshalListener.class);

    @Override
    public void beforeMarshal(Object source) {
        //Operations forEuropassCredentialDTOS
        if (source instanceof EuropassCredentialDTO) {

            //SubCred Treatment
            EuropassCredentialDTO europassCredentialDTO = (EuropassCredentialDTO) source;

            europassCredentialDTO.setValidationErrors(null);


            /*europassCredentialDTO.setValid(null);
            europassCredentialDTO.setValidationErrors(null);*/
            List<String> subCredentials = europassCredentialDTO.getSubCredentialsXML();
            if (subCredentials == null) subCredentials = new ArrayList<String>();
            subCredentials.addAll(this.getEdciCredentialModelUtil().generateXMLSubCreds(europassCredentialDTO));
            europassCredentialDTO.setSubCredentialsXML(subCredentials);
            europassCredentialDTO.setContains(null);


            //References
            Set<LearningSpecificationDTO> learningSpecificationDTOS = this.getReflectiveUtil().getUniqueInnerObjectsOfType(LearningSpecificationDTO.class, europassCredentialDTO, null);
            Set<LearningOutcomeDTO> learningOutcomeDTOS = this.getReflectiveUtil().getUniqueInnerObjectsOfType(LearningOutcomeDTO.class, europassCredentialDTO, null);
            Set<LearningActivitySpecificationDTO> learningActivitySpecificationDTOS = this.getReflectiveUtil().getUniqueInnerObjectsOfType(LearningActivitySpecificationDTO.class, europassCredentialDTO, null);
            Set<AssessmentSpecificationDTO> assessmentSpecificationDTOS = this.getReflectiveUtil().getUniqueInnerObjectsOfType(AssessmentSpecificationDTO.class, europassCredentialDTO, null);
            Set<EntitlementSpecificationDTO> entitlementSpecificationDTOS = this.getReflectiveUtil().getUniqueInnerObjectsOfType(EntitlementSpecificationDTO.class, europassCredentialDTO, null);
            Set<LearningOpportunityDTO> learningOpportunityDTOS = this.getReflectiveUtil().getUniqueInnerObjectsOfType(LearningOpportunityDTO.class, europassCredentialDTO, null);
            Set<OrganizationDTO> organizationDTOS = this.getReflectiveUtil().getUniqueInnerObjectsOfType(OrganizationDTO.class, europassCredentialDTO, null);
            Set<AccreditationDTO> accreditationDTOS = this.getReflectiveUtil().getUniqueInnerObjectsOfType(AccreditationDTO.class, europassCredentialDTO, null);
            Set<ScoringSchemeDTO> scoringSchemeDTOS = this.getReflectiveUtil().getUniqueInnerObjectsOfType(ScoringSchemeDTO.class, europassCredentialDTO, null);
            Set<AwardingProcessDTO> awardingProcessDTOS = this.getReflectiveUtil().getUniqueInnerObjectsOfType(AwardingProcessDTO.class, europassCredentialDTO, null);

            if (!learningSpecificationDTOS.isEmpty()) {
                europassCredentialDTO.setLearningSpecificationReferences(learningSpecificationDTOS);
            } else {
                europassCredentialDTO.setLearningSpecificationReferences(null);
            }
            if (!learningOutcomeDTOS.isEmpty()) {
                europassCredentialDTO.setLearningOutcomeReferences(learningOutcomeDTOS);
            } else {
                europassCredentialDTO.setLearningOutcomeReferences(null);
            }
            if (!learningActivitySpecificationDTOS.isEmpty()) {
                europassCredentialDTO.setLearningActivitySpecificationReferences(learningActivitySpecificationDTOS);
            } else {
                europassCredentialDTO.setLearningActivitySpecificationReferences(null);
            }
            if (!assessmentSpecificationDTOS.isEmpty()) {
                europassCredentialDTO.setAssessmentSpecificationReferences(assessmentSpecificationDTOS);
            } else {
                europassCredentialDTO.setAssessmentSpecificationReferences(null);
            }
            if (!entitlementSpecificationDTOS.isEmpty()) {
                europassCredentialDTO.setEntitlementSpecificationReferences(entitlementSpecificationDTOS);
            } else {
                europassCredentialDTO.setEntitlementSpecificationReferences(null);
            }
            if (!learningOpportunityDTOS.isEmpty()) {
                europassCredentialDTO.setLearningOpportunityReferences(learningOpportunityDTOS);
            } else {
                europassCredentialDTO.setLearningOpportunityReferences(null);
            }
            if (!organizationDTOS.isEmpty()) {
                europassCredentialDTO.setOrganisationReferences(organizationDTOS);
            } else {
                europassCredentialDTO.setOrganisationReferences(null);
            }
            if (!accreditationDTOS.isEmpty()) {
                europassCredentialDTO.setAccreditationReferences(accreditationDTOS);
            } else {
                europassCredentialDTO.setAccreditationReferences(null);
            }
            if (!scoringSchemeDTOS.isEmpty()) {
                europassCredentialDTO.setScoringSchemeReferences(scoringSchemeDTOS);
            } else {
                europassCredentialDTO.setScoringSchemeReferences(null);
            }
            if (!awardingProcessDTOS.isEmpty()) {
                europassCredentialDTO.setAwardingProcessReferences(awardingProcessDTOS);
            } else {
                europassCredentialDTO.setAwardingProcessReferences(null);
            }
        }

        //Operations for mailBoxDTO
        if (source instanceof MailboxDTO) {
            MailboxDTO mailboxDTO = (MailboxDTO) source;
            String mailToString = mailboxDTO.getId().toString();
            if (!mailToString.contains(EDCIConfig.Defaults.DEFAULT_MAILTO)) {
                mailToString = EDCIConfig.Defaults.DEFAULT_MAILTO.concat(mailToString);
                mailboxDTO.setId(URI.create(mailToString));
            }
        }

        //Avoid self-closing tags on empty collections
        List<Field> fields = this.getReflectiveUtil().getListFields(source).stream().filter(field -> field.getAnnotation(XmlElementWrapper.class) != null).collect(Collectors.toList());

        if (fields != null && !fields.isEmpty()) {
            for (Field field : fields) {
                Collection<Object> listObject = (Collection<Object>) this.getReflectiveUtil().getField(field, source);
                if (listObject != null && listObject.isEmpty()) {
                    ReflectionUtils.makeAccessible(field);
                    ReflectionUtils.setField(field, source, null);
                }
            }
        }

        super.beforeMarshal(source);
    }


    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }

    public EDCICredentialModelUtil getEdciCredentialModelUtil() {
        return edciCredentialModelUtil;
    }

    public void setEdciCredentialModelUtil(EDCICredentialModelUtil edciCredentialModelUtil) {
        this.edciCredentialModelUtil = edciCredentialModelUtil;
    }
}
