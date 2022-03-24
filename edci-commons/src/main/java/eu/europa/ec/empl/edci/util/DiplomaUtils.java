package eu.europa.ec.empl.edci.util;


import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.DisplayParametersDTO;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.MediaObject;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.datamodel.view.EuropassDiplomaDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DiplomaUtils {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DiplomaUtils.class);

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

    public void setHTML(EuropassCredentialDTO europassCredentialDTO, EuropassDiplomaDTO europassDiplomaDTO) {

        String html = null;

        if (validator.isNotNull(() -> europassCredentialDTO.getDisplay().getHtml())) {
            html = htmlSanitizerUtil.processWildcardsHTML(europassCredentialDTO);
        } else {
            try {
                html = htmlSanitizerUtil.processWildcardsHTML(getClassPathResource(EDCIConstants.DEFAULT_VIEWER_DIPLOMA_PATH, false),
                        edciCredentialModelUtil.toXML(europassCredentialDTO));
            } catch (Exception e) {
                logger.error(e);
            }
        }

        europassDiplomaDTO.setHtml(html);

    }

    public void setLogo(EuropassCredentialDTO europassCredentialDTO, EuropassDiplomaDTO europassDiplomaDTO) {

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

        europassDiplomaDTO.setLogo(logo.toString());

    }

    public void setBackgroundImg(EuropassCredentialDTO europassCredentialDTO, EuropassDiplomaDTO europassDiplomaDTO) {

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

        europassDiplomaDTO.setBackgroundImage(backgroundImage.toString());

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

    public EuropassDiplomaDTO extractEuropassDiplomaDTO(CredentialHolderDTO credentialholderDTO, String locale) {

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

            setLogo(europassCredentialDTO, europassDiplomaDTO);
            setBackgroundImg(europassCredentialDTO, europassDiplomaDTO);
            setHTML(europassCredentialDTO, europassDiplomaDTO);

        } finally {
            LocaleContextHolder.setLocale(current);
        }

        return europassDiplomaDTO;
    }

    private byte[] getClassPathResource(String path) {
        try {
            Resource backgroundImageResource = new ClassPathResource(path);
            return StreamUtils.copyToByteArray(backgroundImageResource.getInputStream());
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
            throw new EDCIException().setCause(ioe).addDescription(String.format("IOException from %s", path));
        }
    }

    private String getClassPathResource(String path, boolean encode) {
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

    /*
     * Gets a JPEG image of a credential's diploma
     */
    public byte[] generateDiplomaImage(CredentialHolderDTO credentialHolderDTO) {

        byte[] bytes = null;

        try {

            EuropassDiplomaDTO diploma = extractEuropassDiplomaDTO(credentialHolderDTO, EDCIConstants.DEFAULT_LOCALE);

            bytes = imageUtil.htmlToImage(diploma.getHtml(), EDCIConfig.Defaults.DIPLOMA_PAGE_SIZE, EDCIConfig.Defaults.DIPLOMA_PAGE_MARGINS);

        } catch (Exception e) {
            logger.error(e);
            throw new EDCIException(e);
        }

        return bytes;
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
