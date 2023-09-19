package eu.europa.ec.empl.edci.issuer.util;


import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.DisplayDetailDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.IndividualDisplayDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.MediaObjectDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConstants;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.LabelDTDAO;
import eu.europa.ec.empl.edci.sanitizer.EDCISanitizedHtml;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DiplomaUtils {

    public static final Logger logger = LogManager.getLogger(DiplomaUtils.class);

    public static final String TRANSPARENT_IMG = "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAAnElEQVR42u3RAQ0AAAgDoJvc6FrDOahAJdPhjBIiBCFCECIEIUIQIkSIEIQIQYgQhAhBiBCEIEQIQoQgRAhChCAEIUIQIgQhQhAiBCEIEYIQIQgRghAhCEGIEIQIQYgQhAhBCEKEIEQIQoQgRAhCECIEIUIQIgQhQhCCECEIEYIQIQgRghCECEGIEIQIQYgQhAgRIgQhQhAiBCHfLQGKlZ3aNUP0AAAAAElFTkSuQmCC";

    @Autowired
    private ControlledListsUtil controlledListsUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private HtmlSanitizerUtil htmlSanitizerUtil;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private ThymeleafUtil thymeleafUtil;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private CredentialUtil credentialUtil;

    public byte[] getThumbnailImage(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        String encodedThumbnail = this.getBase64EncodedThumbnailImage(europeanDigitalCredentialDTO);
        return encodedThumbnail == null ? null : Base64.getDecoder().decode(encodedThumbnail);
    }

    public String getBase64EncodedThumbnailImage(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        String locale = this.getCredentialUtil().guessPrimaryLanguage(europeanDigitalCredentialDTO).toString();
        ConceptDTO primaryLanguage = this.getControlledListCommonsService().searchLanguageByLang(locale);
        //Gert the individual display with the primary language, otherwise get any
        IndividualDisplayDTO individualDisplay = europeanDigitalCredentialDTO.getDisplayParameter().getIndividualDisplay().stream().filter(
                individualDisplayDTO -> individualDisplayDTO.getLanguage().getId().equals(primaryLanguage.getId())
        ).findAny().orElse(
                europeanDigitalCredentialDTO.getDisplayParameter().getIndividualDisplay().stream().findAny().orElse(null)
        );

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

    public List<String> getAllBase64EncodedThumbnailImage(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        List<String> pages = new ArrayList<>();

        for (IndividualDisplayDTO individualDisplayDTO : europeanDigitalCredentialDTO.getDisplayParameter().getIndividualDisplay()) {
            for (int i = 0; i < individualDisplayDTO.getDisplayDetail().size(); ++i) {
                pages.add(individualDisplayDTO.getDisplayDetail().get(i).getImage().getContent());
            }

        }

        return pages;
    }

    public void informDiplomaFromTemplate(EuropeanDigitalCredentialDTO credential,
                                          String template, Set<LabelDTDAO> labels, MediaObjectDTO background, boolean isDiplomaSupplement) {
        Context context = new Context();
        context.setVariable("credential", credential);

        Properties labelsProp = new Properties();

        Map<String, ConceptDTO> availableLangs = credential.getDisplayParameter().getLanguage().stream()
                .collect(Collectors.toMap(availableLang -> this.getControlledListCommonsService().searchLanguageISO639ByConcept(availableLang), availableLang -> availableLang));

        //For each available language
        int i = 0;
        for (String currentLang : availableLangs.keySet()) {

            //Let's retrieve all the labels and add them to the ones that we already have in our messages.properties
            Map<String, String> backEndProperties = this.getEdciMessageService().getMessages(new Locale(currentLang));
            labelsProp.putAll(backEndProperties);

            if (labels != null && !labels.isEmpty()) {
                labels.stream().forEach(label -> labelsProp.put(label.getKey(), MultilangFieldUtil.getLiteralStringOrAny(label.getContents(), currentLang)));
            }

            //If html is blank, let's retrieve the default template
            if (StringUtils.isBlank(template)) {
                if (isDiplomaSupplement) {
                    template = getResource(IssuerConstants.DEFAULT_VIEWER_DIPLOMA_SUPPLEMENT_PATH, false);
                } else {
                    template = getResource(IssuerConstants.DEFAULT_VIEWER_DIPLOMA_GENERIC_PATH, false);
                }
            }
            //If there's no logo in the organization's logo field, we'll put the default there in order to generate the diploma with one
            if (credential.getIssuer() != null && (credential.getIssuer().getLogo() == null || StringUtils.isBlank(credential.getIssuer().getLogo().getContent()))) {
                MediaObjectDTO logo = new MediaObjectDTO();
                logo.setContent(getResource(IssuerConstants.DEFAULT_VIEWER_DIPLOMA_LOGO_IMG_PATH, true));
                logo.setContentEncoding(this.getControlledListCommonsService().searchConceptByConcept(ControlledListConcept.ENCODING_BASE64, Locale.ENGLISH.getLanguage()));
                logo.setContentType(this.getControlledListCommonsService().searchConceptByConcept(ControlledListConcept.FILE_TYPE_PNG, Locale.ENGLISH.getLanguage()));
                credential.getIssuer().setLogo(logo);
            }

            //Let's generate the html from the template
            String diplomaHTML = getThymeleafUtil().processTemplate(template, context, currentLang, labelsProp);

            //Let's sanitize the html and split it into pages
            EDCISanitizedHtml sanitizedHTML = getHtmlSanitizerUtil().processTemplate(diplomaHTML);

            //Retrieve the default background or blank
            String backgroundBase64 = background != null ? buildImageBase64(background) : getDefaultImageBase64(isDiplomaSupplement ? IssuerConstants.DEFAULT_VIEWER_DIPLOMA_BKG_BLANK_IMG_PATH : IssuerConstants.DEFAULT_VIEWER_DIPLOMA_BKG_IMG_PATH, ControlledListsUtil.MimeType.JPG);

            //Call the service to generate the images
            List<DisplayDetailDTO> diplomaPages = generateImagesFromHTML(sanitizedHTML.getHtml(), backgroundBase64, availableLangs.keySet());

            IndividualDisplayDTO display = new IndividualDisplayDTO();
            display.setId(URI.create(display.getIdPrefix(display).concat(UUID.randomUUID().toString())));
            display.setLanguage(availableLangs.get(currentLang));
            display.getDisplayDetail().addAll(diplomaPages);

            credential.getDisplayParameter().getIndividualDisplay().add(display);

        }

    }

    protected List<DisplayDetailDTO> generateImagesFromHTML(List<String> htmlArray, String backgroundBase64, Collection<String> availableLangs) {

        List<DisplayDetailDTO> pages = new ArrayList<>();

        try {

            Set<String> availableLangsSet = new HashSet<>(availableLangs);
            availableLangsSet.add(Locale.ENGLISH.getLanguage());

            ConceptDTO conceptType = this.getControlledListCommonsService().searchConceptByUri(ControlledListConcept.FILE_TYPE_JPEG.getControlledList().getUrl(),
                    ControlledListConcept.FILE_TYPE_JPEG.getUrl(), Locale.ENGLISH.getLanguage(), availableLangsSet);
            ConceptDTO conceptEncoding = this.getControlledListCommonsService().searchConceptByUri(ControlledListConcept.ENCODING_BASE64.getControlledList().getUrl(),
                    ControlledListConcept.ENCODING_BASE64.getUrl(), Locale.ENGLISH.getLanguage(), availableLangsSet);

            int i = 0;
            for (String html : htmlArray) {
                DisplayDetailDTO displayDetailDTO = new DisplayDetailDTO();
                displayDetailDTO.setId(URI.create(displayDetailDTO.getIdPrefix(displayDetailDTO).concat(UUID.randomUUID().toString())));
                displayDetailDTO.setPage(++i);
                MediaObjectDTO mediaObjectDTO = new MediaObjectDTO();
                mediaObjectDTO.setId(URI.create(mediaObjectDTO.getIdPrefix(mediaObjectDTO).concat(UUID.randomUUID().toString())));
                mediaObjectDTO.setContent(new String(Base64.getEncoder().encode(getImageUtil().htmlToImage(html, backgroundBase64)), StandardCharsets.UTF_8));
                mediaObjectDTO.setContentEncoding(conceptEncoding);
                mediaObjectDTO.setContentType(conceptType);
                displayDetailDTO.setImage(mediaObjectDTO);
                pages.add(displayDetailDTO);
            }

        } catch (Exception e) {
            logger.error(e);
            throw new EDCIException(e);
        }

        return pages;

    }

    protected String getResource(String path, boolean encode) {
        try {
            Resource backgroundImageResource = new ClassPathResource(path);
            byte[] unencoded = StreamUtils.copyToByteArray(backgroundImageResource.getInputStream());
            if (encode) {
                return new String(Base64.getEncoder().encode(unencoded), StandardCharsets.UTF_8);
            }
            return new String(unencoded, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
            throw new EDCIException().setCause(ioe).addDescription(String.format("IOException from %s", path));
        }
    }

    protected String buildImageBase64(MediaObjectDTO mediaObjectElement) {
        String value = null;
        try {
            String base64Content = mediaObjectElement.getContent();
            String extension = controlledListsUtil.getMimeType(mediaObjectElement.getContentType().getId().toString());
            if (extension != null && base64Content != null) {
                value = "data:".concat(extension).concat(";base64,").concat(base64Content);
            }

        } catch (Exception e) {
            logger.error(e);
            value = "data:image/png;base64,".concat(TRANSPARENT_IMG);
        }

        return value;
    }

    protected String buildImageBase64(String format, String imageBase64) {
        return "data:image/".concat(format).concat(";base64,").concat(imageBase64);
    }

    protected String getDefaultImageBase64(String pathResource, ControlledListsUtil.MimeType mimeType) {
        String image = getResource(pathResource, true);
        return buildImageBase64(mimeType.getExtension(), image);
    }

    public ControlledListCommonsService getControlledListCommonsService() {
        return controlledListCommonsService;
    }

    public void setControlledListCommonsService(ControlledListCommonsService controlledListCommonsService) {
        this.controlledListCommonsService = controlledListCommonsService;
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }

    public HtmlSanitizerUtil getHtmlSanitizerUtil() {
        return htmlSanitizerUtil;
    }

    public void setHtmlSanitizerUtil(HtmlSanitizerUtil htmlSanitizerUtil) {
        this.htmlSanitizerUtil = htmlSanitizerUtil;
    }

    public ThymeleafUtil getThymeleafUtil() {
        return thymeleafUtil;
    }

    public void setThymeleafUtil(ThymeleafUtil thymeleafUtil) {
        this.thymeleafUtil = thymeleafUtil;
    }

    public ImageUtil getImageUtil() {
        return imageUtil;
    }

    public void setImageUtil(ImageUtil imageUtil) {
        this.imageUtil = imageUtil;
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }
}
