package eu.europa.ec.empl.edci.viewer.service;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.model.external.VerificationCheckReport;
import eu.europa.ec.empl.edci.model.view.EuropassDiplomaDTO;
import eu.europa.ec.empl.edci.model.view.VerificationCheckView;
import eu.europa.ec.empl.edci.util.*;
import eu.europa.ec.empl.edci.viewer.common.constants.Parameter;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class CredentialDetailService {

    public static final Logger logger = LogManager.getLogger(CredentialDetailService.class);

    @Autowired
    private ViewerConfigService viewerConfigService;

    @Autowired
    private WalletResourceUtil walletResourceUtils;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private DiplomaUtil diplomaUtil;

    @Autowired
    private VerificationResourceUtil verificationResourceUtil;

    @Autowired
    private ExternalServicesUtil externalServicesUtil;


    public EuropassDiplomaDTO getWalletDiplomaHTML(String credentialUUID, String walletUserId, String locale) {
        EuropassDiplomaDTO response = null;
        try {
            response = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_DIPLOMA)
                            .replaceAll(Pattern.quote(Parameter.WALLET_USER_ID), walletUserId)
                            .replaceAll(Pattern.quote(Parameter.UUID), credentialUUID),
                    null,
                    MediaType.APPLICATION_JSON,
                    EuropassDiplomaDTO.class,
                    true);

        } catch (EDCIRestException | EDCIException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }

        return response;
    }

    public EuropeanDigitalCredentialDTO getCredentialDetail(MultipartFile file) {
        EuropeanDigitalCredentialDTO europassCredentialDTO = null;
        try {

            europassCredentialDTO = this.getCredentialUtil().unMarshallCredential(file.getBytes());

            //europassCredentialDTO.getCredential().setDisplay(null);

        } catch (IOException | ParseException e) {
            logger.error(String.format("Parsing exception: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
        }

        return europassCredentialDTO;
    }

    public EuropeanDigitalCredentialDTO getCredentialDetail(byte[] file) {
        EuropeanDigitalCredentialDTO europassCredentialDTO = null;
        try {

            europassCredentialDTO = this.getCredentialUtil().unMarshallCredential(file);

            //europassCredentialDTO.getCredential().setDisplay(null);

        } catch (IOException | ParseException e) {
            logger.error(String.format("Parsing exception: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
        }

        return europassCredentialDTO;
    }

    public EuropassDiplomaDTO getCredentialDiploma(MultipartFile file, String locale) {

        EuropeanDigitalCredentialDTO europassCredentialDTO = null;
        try {

            europassCredentialDTO = this.getCredentialUtil().unMarshallCredential(file.getBytes());

        } catch (IOException | ParseException e) {
            logger.error(String.format("Parsing exception: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
        }
        //TODO: new DiplomaUtil

        EuropassDiplomaDTO europassDiplomaDTO = new EuropassDiplomaDTO();
        europassDiplomaDTO.setBase64DiplomaImages(this.getDiplomaUtil().getBase64DiplomaImages(europassCredentialDTO, locale));
        return europassDiplomaDTO;
    }

    public EuropeanDigitalCredentialDTO getCredentialDetail(String walletUserId, String credentialUUID) {
        EuropeanDigitalCredentialDTO europassCredentialDTO = null;
        try {
            String response = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD)
                            .replaceAll(Pattern.quote(Parameter.WALLET_USER_ID), walletUserId)
                            .replaceAll(Pattern.quote(Parameter.UUID), credentialUUID).concat("?retrieveVP=false"),
                    null,
                    MediaType.APPLICATION_OCTET_STREAM,
                    String.class,
                    true);

            europassCredentialDTO = this.getCredentialUtil().unMarshallCredential(response);

        } catch (IOException | ParseException e) {
            logger.error(String.format("Parsing exception: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_INVALID_FORMAT, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).setCause(e);
        }
        return europassCredentialDTO;
    }

    public List<VerificationCheckView> getWalletCredentialVerification(String credentialUUID, String walletUserId) {
        List<VerificationCheckView> verificationCheckViews = null;
        try {
            verificationCheckViews = Arrays.asList(walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_VERIFY_ID_URL)
                            .replaceAll(Pattern.quote(Parameter.WALLET_USER_ID), walletUserId)
                            .replaceAll(Pattern.quote(Parameter.UUID), credentialUUID),
                    MediaType.APPLICATION_JSON,
                    null,
                    VerificationCheckView[].class,
                    true));

        } catch (EDCIRestException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }

        return verificationCheckViews;
    }

    /*
     * Calls EDCI external service for verification response from JSON-LD.
     *
     * */
    public List<VerificationCheckReport> getCredentialVerification(MultipartFile file) {
        return this.getExternalServicesUtil().verifyCredential(file);
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

    public VerificationResourceUtil getVerificationResourceUtil() {
        return verificationResourceUtil;
    }

    public void setVerificationResourceUtil(VerificationResourceUtil verificationResourceUtil) {
        this.verificationResourceUtil = verificationResourceUtil;
    }

    public ExternalServicesUtil getExternalServicesUtil() {
        return externalServicesUtil;
    }

    public void setExternalServicesUtil(ExternalServicesUtil externalServicesUtil) {
        this.externalServicesUtil = externalServicesUtil;
    }
}
