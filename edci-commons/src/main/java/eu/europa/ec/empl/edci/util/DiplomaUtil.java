package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.DisplayDetailDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.IndividualDisplayDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.MediaObjectDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component()
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DiplomaUtil {

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    public byte[] getThumbnailImage(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        String encodedThumbnail = this.getBase64EncodedThumbnailImage(europeanDigitalCredentialDTO);
        return encodedThumbnail == null ? null : Base64.getDecoder().decode(encodedThumbnail);
    }

    public String getBase64EncodedThumbnailImage(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        Locale locale = this.getCredentialUtil().guessPrimaryLanguage(europeanDigitalCredentialDTO);
        IndividualDisplayDTO individualDisplay = this.getLocalizedIndividualDisplay(europeanDigitalCredentialDTO, locale);
        if (individualDisplay != null) {
            DisplayDetailDTO displayDetail = individualDisplay.getDisplayDetail().stream().filter(
                    displayDetailDTO -> displayDetailDTO.getPage() == DataModelConstants.FIRST_DIPLOMA_PAGE)
                    .findFirst()
                    .orElse(null);
            return displayDetail != null ? displayDetail.getImage().getContent() : null;
        } else {
            return null;
        }
    }

    public List<String> getBase64DiplomaImages(EuropeanDigitalCredentialDTO credential, String locale) {

        //Work with the locale passed as a parameter or the one in the context if locale is null
        ConceptDTO language = controlledListCommonsService.searchLanguageByLang(
                StringUtils.isNotEmpty(locale) ? locale : LocaleContextHolder.getLocale().getLanguage());

        //Find the individual display for the locale passed
        Optional<IndividualDisplayDTO> individualDisplay = credential.getDisplayParameter().getIndividualDisplay().stream()
                .filter(individualDisplayDTO -> {
                    return individualDisplayDTO.getLanguage() != null && individualDisplayDTO.getLanguage().getId().equals(language.getId());
                }).findFirst();

        //If not found, retrieve the one from the primary language
        if (individualDisplay.isEmpty() && credential.getDisplayParameter().getPrimaryLanguage() != null) {
            individualDisplay = credential.getDisplayParameter().getIndividualDisplay().stream()
                    .filter(individualDisplayDTO -> {
                        return individualDisplayDTO.getLanguage() != null &&
                                credential.getDisplayParameter().getPrimaryLanguage().getId().equals(individualDisplayDTO.getLanguage().getId());
                    }).findFirst();
        }

        List<DisplayDetailDTO> diplomaList = null;

        //Get the display detail (if no locale or primary language can be retrieved, then the first indivual disaplay is used)
        if (individualDisplay.isEmpty()) {
            diplomaList = credential.getDisplayParameter().getIndividualDisplay().get(0).getDisplayDetail();
        } else {
            diplomaList = individualDisplay.get().getDisplayDetail();
        }

        //Order the images by page number
        List<MediaObjectDTO> orderedImgList = diplomaList.stream().sorted(Comparator.comparingInt(DisplayDetailDTO::getPage))
                .map(DisplayDetailDTO::getImage).collect(Collectors.toList());

        //Build a base64 string with it's "headers"
        return orderedImgList.stream().map(this::getDataUriScheme).collect(Collectors.toList());

    }

    public String getDataUriScheme(MediaObjectDTO mediaObjectDTO) {
        return "data:"
                .concat(mediaObjectDTO.getContentType().getPrefLabel().toString())
                .concat(";")
                .concat(mediaObjectDTO.getContentEncoding().getPrefLabel().toString())
                .concat(",")
                .concat(mediaObjectDTO.getContent());
    }

    private IndividualDisplayDTO getLocalizedIndividualDisplay(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, Locale locale) {
        //Get the individual display with the primary language, otherwise get any
        ConceptDTO localeConcept = this.getControlledListCommonsService().searchLanguageByLang(locale.toString());
        return europeanDigitalCredentialDTO.getDisplayParameter().getIndividualDisplay().stream().filter(
                individualDisplayDTO -> individualDisplayDTO.getLanguage().getId().equals(localeConcept.getId())
        ).findAny().orElse(
                europeanDigitalCredentialDTO.getDisplayParameter().getIndividualDisplay().stream().findAny().orElse(null)
        );
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }

    public ControlledListCommonsService getControlledListCommonsService() {
        return controlledListCommonsService;
    }

    public void setControlledListCommonsService(ControlledListCommonsService controlledListCommonsService) {
        this.controlledListCommonsService = controlledListCommonsService;
    }
}
