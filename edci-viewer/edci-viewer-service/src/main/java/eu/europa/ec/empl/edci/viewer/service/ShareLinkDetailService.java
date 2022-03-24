package eu.europa.ec.empl.edci.viewer.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.view.EuropassDiplomaDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.util.DiplomaUtils;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.WalletResourceUtil;
import eu.europa.ec.empl.edci.viewer.common.constants.Parameter;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerConfig;
import eu.europa.ec.empl.edci.viewer.common.model.ShareLinkFetchResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.regex.Pattern;

@Service
public class ShareLinkDetailService {

    @Autowired
    private ViewerConfigService viewerConfigService;

    @Autowired
    private WalletResourceUtil walletResourceUtils;

    @Autowired
    private DiplomaUtils europassCredentialViewerUtils;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ShareLinkDetailService.class);
//    private final static Logger logger = LoggerFactory.getLogger(ShareLinkDetailService.class);


    public CredentialHolderDTO getSharedCredentialDetail(String shareHash) {

        CredentialHolderDTO europassCredentialDTO = null;
        String response = null;
        try {
            response = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_SHARED_XML).replaceAll(Pattern.quote(Parameter.SHARE_HASH), shareHash),
                    null, MediaType.APPLICATION_OCTET_STREAM, String.class, false);


            europassCredentialDTO = edciCredentialModelUtil.fromXML(response);
            if (logger.isTraceEnabled()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                logger.trace(mapper.writeValueAsString(europassCredentialDTO));
            }
        } catch (JAXBException e) {
            logger.error(String.format("JAXBException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_INVALID_FORMAT, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).setCause(e);
        } catch (EDCIRestException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }
        return europassCredentialDTO;
    }

    public EuropassDiplomaDTO getSharedCredentialDiploma(String shareHash, String locale) {
        CredentialHolderDTO europassCredentialDTO = null;
        EuropassDiplomaDTO europassDiplomaDTO = null;
        String response = null;
        String shareLinkResponse = null;
        try {
            response = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_SHARED_XML).replaceAll(Pattern.quote(Parameter.SHARE_HASH), shareHash),
                    null, MediaType.APPLICATION_OCTET_STREAM, String.class, false);

            europassCredentialDTO = edciCredentialModelUtil.fromXML(response);

            shareLinkResponse = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_SHARELINK_FETCH).replaceAll(Pattern.quote(Parameter.SHARE_HASH), shareHash),
                    null, MediaType.APPLICATION_JSON, String.class, false);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ShareLinkFetchResponseDTO shareLinkFetchResponseDTO = objectMapper.readValue(shareLinkResponse, ShareLinkFetchResponseDTO.class);
            europassDiplomaDTO = europassCredentialViewerUtils.extractEuropassDiplomaDTO(europassCredentialDTO, locale);
            europassDiplomaDTO.setExpirationDate(shareLinkFetchResponseDTO.getExpirationDate());

            logger.trace(String.format("ShareLink Expiration Date: [%s]", shareLinkFetchResponseDTO.getExpirationDate()));
        } catch (JAXBException e) {
            logger.error(String.format("JAXBException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_INVALID_FORMAT, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).setCause(e);
        } catch (EDCIRestException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }
        return europassDiplomaDTO;
    }
}
