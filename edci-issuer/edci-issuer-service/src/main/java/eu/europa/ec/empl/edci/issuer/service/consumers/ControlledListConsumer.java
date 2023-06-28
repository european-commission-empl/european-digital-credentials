package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessageKeys;
import eu.europa.ec.empl.edci.issuer.mapper.ControlledListsMapper;
import eu.europa.ec.empl.edci.issuer.service.spec.EscoBridgeService;
import eu.europa.ec.empl.edci.issuer.utils.ecso.EscoElementPayload;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropeanDigitalCredentialDTO.class, priority = 6)
@Component
public class ControlledListConsumer implements Consumer<ConsumerContext> {

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private EscoBridgeService escoBridgeService;

    @Autowired
    private ControlledListsMapper controlledListsMapper;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private CredentialUtil credentialUtil;

    private static final Logger logger = LogManager.getLogger(ControlledListConsumer.class);

    @Override
    public void accept(ConsumerContext context) {
        logger.info("start ControlledListConsumer");
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        EuropeanDigitalCredentialDTO europassCredentialDTO = context.getCredential();
        //get unique code references from europass cred
        List<ConceptDTO> controlledListValues = reflectiveUtil.getInnerObjectsOfType(ConceptDTO.class, europassCredentialDTO);
        List<String> errors = new ArrayList<>();
        //guess primary language
        String primaryLanguage = this.getCredentialUtil().guessPrimaryLanguage(europassCredentialDTO).toString();
        Set<String> availableLanguages = this.getCredentialUtil().getAvailableLanguages(europassCredentialDTO);
        availableLanguages.add("en"); //Always retrieve the CL in english
        for (ConceptDTO controlledListValue : controlledListValues) {
            ConceptDTO updatedValue = null;
            //Only process Codes that known as CL or Esco list
            String targetFrameworkURI = controlledListValue.getInScheme() != null
                    && controlledListValue.getInScheme().getId() != null ? controlledListValue.getInScheme().getId().toString() : null;
            if (ControlledList.contains(targetFrameworkURI)) {
                //ControlledList
                updatedValue = controlledListCommonsService.searchConceptByUri(targetFrameworkURI, controlledListValue.getId().toString(), primaryLanguage, availableLanguages);
                if (ControlledList.isEuropassCl(targetFrameworkURI) && updatedValue == null) {
                    //If a required controlled list is null at this point, credential is not valid anymore
                    errors.add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.REQUIRED_CL_ITEM_NOTFOUND, controlledListValue.getId(), targetFrameworkURI));
                    europassCredentialDTO.setValid(false);
                }

            } else if (this.getEscoBridgeService().isEscoList(targetFrameworkURI)) {
                //Esco Lists
                try {
                    EscoBridgeService.EscoList escoList = this.getEscoBridgeService().getEscoList(targetFrameworkURI);
                    EscoElementPayload escoElementPayload = this.getEscoBridgeService().searchEsco(EscoElementPayload.class, escoList.getType(), primaryLanguage, controlledListValue.getId().toString());
                    updatedValue = this.getControlledListsMapper().toConceptDTOESCO(escoElementPayload, escoList.getTargetFrameWorkUrl(), availableLanguages);
                } catch (Exception e) {
                    errors.add(edciMessageService.getMessage(EDCIIssuerMessageKeys.REQUIRED_CL_ITEM_NOTFOUND, controlledListValue.getId().toString(), targetFrameworkURI));
                }
            }

            if (updatedValue != null) {
                controlledListValue.setInScheme(updatedValue.getInScheme());
                controlledListValue.setId(updatedValue.getId());
                controlledListValue.setNotation(updatedValue.getNotation());
                controlledListValue.setPrefLabel(updatedValue.getPrefLabel());
            }
        }

        europassCredentialDTO.getValidationErrors().addAll(errors);
        if (logger.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            logger.debug(String.format("end ControlledListConsumer, took %d seconds", (end - start) / 1000));
        }
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }

    public EscoBridgeService getEscoBridgeService() {
        return escoBridgeService;
    }

    public void setEscoBridgeService(EscoBridgeService escoBridgeService) {
        this.escoBridgeService = escoBridgeService;
    }

    public ControlledListsMapper getControlledListsMapper() {
        return controlledListsMapper;
    }

    public void setControlledListsMapper(ControlledListsMapper controlledListsMapper) {
        this.controlledListsMapper = controlledListsMapper;
    }
}
