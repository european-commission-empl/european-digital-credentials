package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessageKeys;
import eu.europa.ec.empl.edci.issuer.mapper.ControlledListsMapper;
import eu.europa.ec.empl.edci.issuer.service.spec.EscoBridgeService;
import eu.europa.ec.empl.edci.issuer.utils.ecso.EscoElementPayload;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@EDCIConsumer(applyTo = EuropassCredentialDTO.class, preProcess = true, priority = 5)
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
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Override
    public void accept(ConsumerContext context) {
        EuropassCredentialDTO europassCredentialDTO = context.getCredential().getCredential();
        //get unique code references from europass cred
        Set<Code> controlledListValues = reflectiveUtil.getUniqueInnerObjectsOfType(Code.class, europassCredentialDTO);
        List<String> errors = new ArrayList<>();
        //guess primary language
        String primaryLanguage = edciCredentialModelUtil.guessCredentialLocale(europassCredentialDTO).toString();
        Set<String> availableLanguages = europassCredentialDTO.getAvailableLanguages().stream().collect(Collectors.toSet());
        availableLanguages.add("en"); //Always retrieve the CL in english
        for (Code controlledListValue : controlledListValues) {
            Code updatedValue = null;
            //Only process Codes that known as CL or Esco list
            if (ControlledList.contains(controlledListValue.getTargetFrameworkURI())) {
                //ControlledList
                updatedValue = controlledListCommonsService.searchConceptByUri(controlledListValue.getTargetFrameworkURI(), controlledListValue.getUri(), primaryLanguage, availableLanguages);
                if (ControlledList.isEuropassCl(controlledListValue.getTargetFrameworkURI()) && updatedValue == null) {
                    //If a required controlled list is null at this point, credential is not valid anymore
                    errors.add(edciMessageService.getMessage(EDCIIssuerMessageKeys.REQUIRED_CL_ITEM_NOTFOUND, controlledListValue.getUri(), controlledListValue.getTargetFrameworkURI()));
                    europassCredentialDTO.setValid(false);
                }

            } else if (escoBridgeService.isEscoList(controlledListValue.getTargetFrameworkURI())) {
                //Esco Lists
                try {
                    EscoBridgeService.EscoList escoList = escoBridgeService.getEscoList(controlledListValue.getTargetFrameworkURI());
                    updatedValue = controlledListsMapper.toCodeDTOESCO(escoBridgeService.searchEsco(EscoElementPayload.class, escoList.getType(), primaryLanguage, controlledListValue.getUri()), availableLanguages);
                } catch (Exception e) {
                    errors.add(edciMessageService.getMessage(EDCIIssuerMessageKeys.REQUIRED_CL_ITEM_NOTFOUND, controlledListValue.getUri(), controlledListValue.getTargetFrameworkURI()));
                }
            }

            if (updatedValue != null) {
                controlledListValue.setTargetFramework(updatedValue.getTargetFramework());
                controlledListValue.setTargetNotation(updatedValue.getTargetNotation());
                controlledListValue.setTargetName(updatedValue.getTargetName());
                controlledListValue.setTargetDescription(updatedValue.getTargetDescription());
            }
        }

        europassCredentialDTO.getValidationErrors().addAll(errors);
    }

}
