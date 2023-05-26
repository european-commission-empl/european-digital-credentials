package eu.europa.ec.empl.edci.viewer.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.IndividualDisplayDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.model.view.EuropassDiplomaDTO;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.util.*;
import eu.europa.ec.empl.edci.viewer.common.constants.Parameter;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerConfig;
import eu.europa.ec.empl.edci.viewer.common.model.ShareLinkFetchResponseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ShareLinkDetailService {

    @Autowired
    private ViewerConfigService viewerConfigService;

    @Autowired
    private WalletResourceUtil walletResourceUtils;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private DiplomaUtil diplomaUtil;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    private static final Logger logger = LogManager.getLogger(ShareLinkDetailService.class);

    public EuropeanDigitalCredentialDTO getSharedCredentialDetail(String shareHash) {

        EuropeanDigitalCredentialDTO europassCredentialDTO = null;
        String response = null;
        try {
            response = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_SHARED_JSON).replaceAll(Pattern.quote(Parameter.SHARE_HASH), shareHash),
                    null,
                    MediaType.APPLICATION_OCTET_STREAM,
                    String.class,
                    false);


            europassCredentialDTO = this.getCredentialUtil().unMarshallCredential(response);

        } /*catch (JAXBException e) {
            logger.error(String.format("JAXBException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_INVALID_FORMAT, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).setCause(e);
        }*/ catch (EDCIRestException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }
        return europassCredentialDTO;
    }

    public EuropassDiplomaDTO getSharedCredentialDiploma(String shareHash, String locale) {
        EuropeanDigitalCredentialDTO europassCredentialDTO = null;
        EuropassDiplomaDTO europassDiplomaDTO = null;
        String response = null;
        String shareLinkResponse = null;
        try {

            response = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_SHARED_JSON).replaceAll(Pattern.quote(Parameter.SHARE_HASH), shareHash),
                    null,
                    MediaType.APPLICATION_OCTET_STREAM,
                    String.class,
                    false);

            shareLinkResponse = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_SHARELINK_FETCH).replaceAll(Pattern.quote(Parameter.SHARE_HASH), shareHash),
                    null,
                    MediaType.APPLICATION_JSON,
                    String.class,
                    false);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ShareLinkFetchResponseDTO shareLinkFetchResponseDTO = objectMapper.readValue(shareLinkResponse, ShareLinkFetchResponseDTO.class);


            europassCredentialDTO = this.getCredentialUtil().unMarshallCredential(response);

            europassDiplomaDTO = this.getCredentialDiploma(europassCredentialDTO, shareLinkFetchResponseDTO.getExpirationDate());

        } catch (EDCIRestException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }
        return europassDiplomaDTO;
    }

    public EuropassDiplomaDTO getCredentialDiploma(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, Date expirationDate) throws IOException {

        EuropassDiplomaDTO europassDiplomaDTO = new EuropassDiplomaDTO();
        europassDiplomaDTO.setBase64DiplomaImages(this.getDiplomaUtil().getBase64DiplomaImages(europeanDigitalCredentialDTO, null));
        byte[] diplomaImageFull = DatatypeConverter.parseBase64Binary(europassDiplomaDTO.getBase64DiplomaImages().get(0).split("base64,")[1]);
        europassDiplomaDTO.setLogo(new String(imageUtil.resizeImage(diplomaImageFull,
                ControlledListsUtil.MimeType.JPG.getExtension(), 750, 0)));
        europassDiplomaDTO.setId(europeanDigitalCredentialDTO.getId());
        europassDiplomaDTO.setAvailableLanguages(europeanDigitalCredentialDTO.getDisplayParameter().getIndividualDisplay().stream()
                .map(IndividualDisplayDTO::getLanguage)
                .map(displayLang -> controlledListCommonsService.searchLanguageISO639ByConcept(displayLang))
                .collect(Collectors.toList()));
        europassDiplomaDTO.setExpirationDate(expirationDate);

        return europassDiplomaDTO;
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }

    public DiplomaUtil getDiplomaUtil() {
        return diplomaUtil;
    }

    public void setDiplomaUtil(DiplomaUtil diplomaUtil) {
        this.diplomaUtil = diplomaUtil;
    }
}
