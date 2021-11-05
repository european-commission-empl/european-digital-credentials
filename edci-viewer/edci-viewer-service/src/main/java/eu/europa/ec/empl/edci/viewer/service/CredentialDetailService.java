package eu.europa.ec.empl.edci.viewer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.view.EuropassDiplomaDTO;
import eu.europa.ec.empl.edci.datamodel.view.VerificationCheckView;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.util.DiplomaUtils;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.util.WalletResourceUtil;
import eu.europa.ec.empl.edci.viewer.common.constants.Parameter;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class CredentialDetailService {
    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CredentialDetailService.class);

//    private final static Logger logger = LoggerFactory.getLogger(CredentialDetailService.class);

    @Autowired
    private ViewerConfigService viewerConfigService;

    @Autowired
    private WalletResourceUtil walletResourceUtils;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Inject
    DiplomaUtils europassCredentialViewerUtils;

    @Inject
    private Validator validator;

    public EuropassDiplomaDTO getWalletDiplomaHTML(String credentialUUID, String walletUserId, String locale) {
        CredentialHolderDTO europassCredentialDTO = null;
        String response = null;
        try {
            response = walletResourceUtils.doWalletGetRequest(viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_XML).replaceAll(Pattern.quote(Parameter.WALLET_USER_ID),
                    walletUserId).replaceAll(Pattern.quote(Parameter.UUID), credentialUUID).concat("?retrieveVP=true"), null, MediaType.APPLICATION_OCTET_STREAM, String.class, true);

            europassCredentialDTO = edciCredentialModelUtil.fromXML(response);

        } catch (JAXBException e) {
            logger.error(String.format("JAXBException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_INVALID_FORMAT, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).setCause(e);
        } catch (EDCIRestException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }

        return europassCredentialViewerUtils.extractEuropassDiplomaDTO(europassCredentialDTO, locale);
    }

    public CredentialHolderDTO getCredentialDetail(MultipartFile file) {
        CredentialHolderDTO europassCredentialDTO = null;
        try {
            europassCredentialDTO = edciCredentialModelUtil.fromByteArray(file.getBytes());
            europassCredentialDTO.getCredential().setDisplay(null);
            if (logger.isDebugEnabled()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                logger.debug(mapper.writeValueAsString(europassCredentialDTO));
            }
        } catch (IOException e) {
            logger.error(String.format("IOException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
        } catch (JAXBException e) {
            logger.error(String.format("JAXBException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
        }
        return europassCredentialDTO;
    }

    public EuropassDiplomaDTO getCredentialDiploma(MultipartFile file, String locale) {

        CredentialHolderDTO europassCredentialDTO = null;
        try {
           /* jaxbContext = JAXBContext.newInstance(EuropassCredentialDTO.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();*/

            /*europassCredentialDTO = (EuropassCredentialDTO) unmarshaller.unmarshal(inputStream);*/
            europassCredentialDTO = edciCredentialModelUtil.fromByteArray(file.getBytes());

            if (logger.isDebugEnabled()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                logger.debug(mapper.writeValueAsString(europassCredentialDTO.getCredential()));
            }
        } catch (IOException e) {
            logger.error(String.format("IOException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
        } catch (JAXBException e) {
            logger.error(String.format("JAXBException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
        }
        return europassCredentialViewerUtils.extractEuropassDiplomaDTO(europassCredentialDTO, locale);
    }

    public CredentialHolderDTO getCredentialDetail(String walletUserId, String credentialUUID) {
        CredentialHolderDTO europassCredentialDTO = null;
        String response = null;
        try {
            response = walletResourceUtils.doWalletGetRequest(viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_XML).replaceAll(Pattern.quote(Parameter.WALLET_USER_ID),
                    walletUserId).replaceAll(Pattern.quote(Parameter.UUID), credentialUUID).concat("?retrieveVP=true"), null, MediaType.APPLICATION_OCTET_STREAM, String.class, true);

            europassCredentialDTO = edciCredentialModelUtil.fromXML(response);
            if (logger.isDebugEnabled()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                logger.debug(mapper.writeValueAsString(europassCredentialDTO));
            }
        } catch (IOException e) {
            logger.error(String.format("IOException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_INVALID_FORMAT, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).setCause(e);
        } catch (JAXBException e) {
            logger.error(String.format("JAXBException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_INVALID_FORMAT, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).setCause(e);
        }
        return europassCredentialDTO;
    }

    public List<VerificationCheckView> getWalletCredentialVerification(String credentialUUID, String walletUserId) {
        String response = null;
        List<VerificationCheckView> verificationCheckViews = null;
        try {
            verificationCheckViews = Arrays.asList(walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_VERIFY_ID_URL)
                            .replaceAll(Pattern.quote(Parameter.WALLET_USER_ID), walletUserId)
                            .replaceAll(Pattern.quote(Parameter.UUID), credentialUUID),
                    MediaType.APPLICATION_JSON, null, VerificationCheckView[].class, true));

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Raw Response from wallet at getWalletCredentialVerification [%s]", response));
            }
        } catch (EDCIRestException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }

        return verificationCheckViews;
    }

    /*
     * Calls EDCI wallet for verification response from XML.
     *
     * */
    public List<VerificationCheckView> getCredentialVerification(MultipartFile file) {
        List<VerificationCheckView> verificationCheckViews = null;
        verificationCheckViews = Arrays.asList(walletResourceUtils.doWalletPostRequest(viewerConfigService.getString(ViewerConfig.Viewer.WALLET_VERIFY_XML_URL), file, EDCIParameter.WALLET_CREDENTIAL_FILE, VerificationCheckView[].class, MediaType.APPLICATION_JSON, false));
        return verificationCheckViews;
    }

}
