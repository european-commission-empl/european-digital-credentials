package eu.europa.ec.empl.edci.util;


import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.*;
import eu.europa.ec.empl.edci.datamodel.model.DisplayParametersDTO;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.MediaObject;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.datamodel.view.EuropassDiplomaDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.sanitizer.EDCISanitizedHtml;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DiplomaUtils {

    public static final Logger logger = LogManager.getLogger(DiplomaUtils.class);

    @Autowired
    private ControlledListsUtil controlledListsUtil;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private HtmlSanitizerUtil htmlSanitizerUtil;

    @Autowired
    private Validator validator;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private ThymeleafUtil thymeleafUtil;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    IConfigService configService;

    public List<byte[]> getDiplomaFromCredential(CredentialHolderDTO credentialHolderDTO) {

        EDCISanitizedHtml html = null;
        boolean addBackground = true;

        EuropassCredentialDTO europassCredentialDTO = credentialHolderDTO.getCredential();
        try {
            if (validator.isNotNull(() -> europassCredentialDTO.getDisplay().getHtml())) {
                html = htmlSanitizerUtil.processHTML(europassCredentialDTO);
            } else if (validator.isNotNull(() -> europassCredentialDTO.getDisplay().getTemplate())) {
                String diplomaHTML = generateDiplomaHtmlFromTemplate(europassCredentialDTO);
                html = htmlSanitizerUtil.processTemplate(diplomaHTML);
                addBackground = false;
            } else {
                if (ControlledListConcept.CREDENTIAL_TYPE_DIPLOMA_SUPPLEMENT.getUrl().equals(europassCredentialDTO.getCredential().getType().getUri())) {
                    String diplomaHTML = generateDiplomaHtmlFromGenericTemplate(europassCredentialDTO);
                    html = htmlSanitizerUtil.processTemplate(diplomaHTML);
                    addBackground = false;
                } else {
                    html = htmlSanitizerUtil.processHTML(getClassPathResource(EDCIConstants.DEFAULT_VIEWER_DIPLOMA_HTML_PATH, false),
                            edciCredentialModelUtil.toXML(europassCredentialDTO));
                }
            }
        } catch (EDCIException e) {
            throw e;
        } catch (Exception e) {
            String messageParam = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new EDCIException(ErrorCode.DIPLOMA_BAD_FORMAT, messageParam);
        }

        List<byte[]> diplomaPagesList = null;
        if (addBackground) {
            diplomaPagesList = html.getHtml().stream().map(page -> addBackgroundImageAndGenerateImg(europassCredentialDTO, page)).collect(Collectors.toList());
        } else {
            diplomaPagesList = generateDiplomaImages(html.getHtml().stream().toArray(String[]::new));
        }

        return diplomaPagesList;

    }

    public EuropassDiplomaDTO generateEuropassDiplomaDTO(CredentialHolderDTO credentialholderDTO, String locale) {
        return generateEuropassDiplomaDTO(credentialholderDTO, null, locale);
    }

    public EuropassDiplomaDTO generateEuropassDiplomaDTO(CredentialHolderDTO credentialholderDTO, List<byte[]> preGeneratedDiploma, String locale) {

        Locale current = LocaleContextHolder.getLocale();

        EuropassDiplomaDTO europassDiplomaDTO = new EuropassDiplomaDTO();

        try {
            EuropassCredentialDTO europassCredentialDTO = credentialholderDTO.getCredential();

            //Setting the Locale for the diploma that will be retrieved
            if (locale == null) {
                locale = europassCredentialDTO.getCredential().getPrimaryLanguage();
                if (locale == null) {
                    locale = EDCIConstants.DEFAULT_LOCALE;
                }
                LocaleContextHolder.setLocale(LocaleUtils.toLocale(locale));
            }

            if (validator.isEmpty(europassCredentialDTO.getDisplay())) {
                europassCredentialDTO.setDisplay(new DisplayParametersDTO());
            }

            europassDiplomaDTO.setId(europassCredentialDTO.getId());
            europassDiplomaDTO.setType(edciCredentialModelUtil.isPresentation(credentialholderDTO) ? EDCIConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_PRESENTATION : EDCIConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_CREDENTIAL);
            europassDiplomaDTO.setPrimaryLanguage(europassCredentialDTO.getPrimaryLanguage());
            europassDiplomaDTO.setAvailableLanguages(europassCredentialDTO.getAvailableLanguages());

            europassDiplomaDTO.setLogo(getLogo(europassCredentialDTO));
            europassDiplomaDTO.setBackgroundImage(getBackgroundImg(europassCredentialDTO));
            if (preGeneratedDiploma != null && preGeneratedDiploma.size() > 0) {
                europassDiplomaDTO.setHtml(preGeneratedDiploma);
            } else {
                europassDiplomaDTO.setHtml(getDiplomaFromCredential(europassCredentialDTO));
            }

        } finally {
            LocaleContextHolder.setLocale(current);
        }

        return europassDiplomaDTO;
    }

    /*
     * Gets a JPEG image of a credential's diploma
     */
    protected List<byte[]> generateDiplomaImages(String... htmls) {

        List<byte[]> bytes = new ArrayList<>();

        try {

            for (String html : htmls) {
                bytes.add(imageUtil.htmlToImage(html, EDCIConfig.Defaults.DIPLOMA_PAGE_SIZE, EDCIConfig.Defaults.DIPLOMA_PAGE_MARGINS));
            }

        } catch (Exception e) {
            logger.error(e);
            throw new EDCIException(e);
        }

        return bytes;
    }

    protected String generateDiplomaHtmlFromTemplate(CredentialHolderDTO credentialHolderDTO) {

        Context context = new Context();
        context.setVariable("credential", credentialHolderDTO.getCredential());

        Properties labels = new Properties();

        Map<String, String> backEndProperties = edciMessageService.getMessages(LocaleContextHolder.getLocale());
        labels.putAll(backEndProperties);

        if (credentialHolderDTO.getCredential().getDisplay() != null
                && credentialHolderDTO.getCredential().getDisplay().getLabels() != null) {
            credentialHolderDTO.getCredential().getDisplay().getLabels().stream().forEach(label -> labels.put(label.getKey(), label.getStringContent()));
        }
        try {
            return thymeleafUtil.processTemplate(credentialHolderDTO.getCredential().getDisplay().getTemplate(), context, labels);
        } catch (TemplateInputException e) {
            String messageParam = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            if (e.getCol() != null && e.getLine() != null) {
                messageParam.concat(edciMessageService.getMessage(EDCIMessageKeys.LINE_MSG, e.getLine()))
                        .concat(EDCIConstants.StringPool.STRING_COMMA)
                        .concat(edciMessageService.getMessage(EDCIMessageKeys.COLUMN_MSG, e.getCol()));
            }
            throw new EDCIException(ErrorCode.DIPLOMA_BAD_FORMAT, messageParam);
        }
    }

    protected String generateDiplomaHtmlFromGenericTemplate(CredentialHolderDTO credentialHolderDTO) {

        Context context = new Context();
        context.setVariable("credential", credentialHolderDTO.getCredential());

        Properties labels = new Properties();

        Map<String, String> backEndProperties = edciMessageService.getMessages(LocaleContextHolder.getLocale());
        labels.putAll(backEndProperties);

        if (credentialHolderDTO.getCredential().getDisplay() != null
                && credentialHolderDTO.getCredential().getDisplay().getLabels() != null) {
            credentialHolderDTO.getCredential().getDisplay().getLabels().stream().forEach(label -> labels.put(label.getKey(), label.getStringContent()));
        }

        String diplomaHTML = thymeleafUtil.processTemplate(getClassPathResource(EDCIConstants.DEFAULT_VIEWER_DIPLOMA_THYMELEAF_PATH, false), context, labels);
        return diplomaHTML;

    }

    protected String getDefaultBackground(String pathResource) {

        byte[] bytes = getClassPathResource(pathResource);
        return addBase64Headers(ControlledListsUtil.MimeType.PNG.getExtension(), bytes);

    }

    protected String addBase64Headers(String format, byte[] imageBase64) {
        return "data:image/".concat(format).concat(";base64,").concat(Base64.getEncoder().encodeToString(imageBase64));
    }

    protected byte[] addBackgroundImageAndGenerateImg(EuropassCredentialDTO europassCredentialDTO, String europassDiplomaDTOFirstPage) {

        String background = "";
        String backgroundImg = new Validator().getValueNullSafe(
                () -> addBase64Headers(
                        europassCredentialDTO.getCredential().getDisplay().getBackground().getContentType().getTargetName().getStringContent().toLowerCase(),
                        europassCredentialDTO.getCredential().getDisplay().getBackground().getContent()));

        if (backgroundImg == null) {
            backgroundImg = getDefaultBackground(EDCIConstants.DEFAULT_VIEWER_DIPLOMA_BKG_IMG_PATH);
        }

        background = "background: url(" + backgroundImg + ") no-repeat center center; background-size: contain;";

        String cleanWrapped = "<div class=\"diplomaBG\" style=\"" +
                " overflow: hidden; " +
                " width: 21cm; " +
                " height: 29.7cm; " +
                " margin-left: auto; " +
                " margin-right: auto;\n" + background + " \">" + europassDiplomaDTOFirstPage + "</div>";

        return generateDiplomaImages(cleanWrapped).get(0);

    }

    protected String getLogo(EuropassCredentialDTO europassCredentialDTO) {

        StringBuilder logo = new StringBuilder();

        if (validator.isNotNull(() -> europassCredentialDTO.getIssuer().getLogo().getContent(), () -> europassCredentialDTO.getIssuer().getLogo().getContentType())) {
            String mimeType = controlledListsUtil.getMimeType(europassCredentialDTO.getIssuer().getLogo().getContentType().getUri());
            if (mimeType != null) {
                logo.append("data:").append(mimeType).append(";base64,").append(new String(Base64.getEncoder().encode(europassCredentialDTO.getIssuer().getLogo().getContent()), StandardCharsets.UTF_8));
            } else {
                // TODO EDCI_751 throw exception invalid mimetype
                MediaObject logoObject = getMediaObject(EDCIConstants.DEFAULT_VIEWER_LOGO_BKG_IMG_PATH);
                europassCredentialDTO.getIssuer().setLogo(logoObject);
                logo.append("data:image/png;base64,").append(new String(Base64.getEncoder().encode(logoObject.getContent()), StandardCharsets.UTF_8));

            }

        } else {
            MediaObject logoObject = getMediaObject(EDCIConstants.DEFAULT_VIEWER_LOGO_BKG_IMG_PATH);
            europassCredentialDTO.getIssuer().setLogo(logoObject);
            logo.append("data:image/png;base64,").append(new String(Base64.getEncoder().encode(logoObject.getContent()), StandardCharsets.UTF_8));
        }

        return logo.toString();

    }

    public String getBackgroundImg(EuropassCredentialDTO europassCredentialDTO) {

        StringBuilder backgroundImage = new StringBuilder();

        if (validator.isNotNull(() -> europassCredentialDTO.getDisplay().getBackground().getContent(), () -> europassCredentialDTO.getDisplay().getBackground().getContentType())) {
            String mimeType = controlledListsUtil.getMimeType(europassCredentialDTO.getDisplay().getBackground().getContentType().getUri());
            if (mimeType != null) {
                backgroundImage.append("data:").append(mimeType).append(";base64,").append(new String(Base64.getEncoder().encode(europassCredentialDTO.getDisplay().getBackground().getContent()), StandardCharsets.UTF_8));
            } else {
                // TODO EDCI_751 throw exception invalid mimetype
                MediaObject backgroundObject = getMediaObject(EDCIConstants.DEFAULT_VIEWER_DIPLOMA_BKG_IMG_PATH);
                europassCredentialDTO.getDisplay().setBackground(backgroundObject);
                backgroundImage.append("data:image/png;base64,").append(new String(Base64.getEncoder().encode(backgroundObject.getContent()), StandardCharsets.UTF_8));
            }
        } else {
            MediaObject backgroundObject = getMediaObject(EDCIConstants.DEFAULT_VIEWER_DIPLOMA_BKG_IMG_PATH);
            europassCredentialDTO.getDisplay().setBackground(backgroundObject);
            backgroundImage.append("data:image/png;base64,").append(new String(Base64.getEncoder().encode(backgroundObject.getContent()), StandardCharsets.UTF_8));
        }

        return backgroundImage.toString();

    }

    public MediaObject getMediaObject(String pathResource) {

        byte[] bytes = getClassPathResource(pathResource);
        MediaObject backgroundObject = new MediaObject();
        backgroundObject.setContent(bytes);
        Code contentType = new Code();
        contentType.setUri("http://publications.europa.eu/resource/authority/file-type/PNG");
        contentType.setTargetFrameworkURI("http://publications.europa.eu/resource/authority/file-type");
        contentType.setTargetName(new Text("PNG", "en"));
        backgroundObject.setContentType(contentType);

        return backgroundObject;

    }

    protected byte[] getClassPathResource(String path) {
        try {
            Resource backgroundImageResource = new ClassPathResource(path);
            return StreamUtils.copyToByteArray(backgroundImageResource.getInputStream());
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
            throw new EDCIException().setCause(ioe).addDescription(String.format("IOException from %s", path));
        }
    }

    protected String getClassPathResource(String path, boolean encode) {
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

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public void setControlledListsUtil(ControlledListsUtil controlledListsUtil) {
        this.controlledListsUtil = controlledListsUtil;
    }

    public void setEdciCredentialModelUtil(EDCICredentialModelUtil edciCredentialModelUtil) {
        this.edciCredentialModelUtil = edciCredentialModelUtil;
    }

    public void setHtmlSanitizerUtil(HtmlSanitizerUtil htmlSanitizerUtil) {
        this.htmlSanitizerUtil = htmlSanitizerUtil;
    }
}
