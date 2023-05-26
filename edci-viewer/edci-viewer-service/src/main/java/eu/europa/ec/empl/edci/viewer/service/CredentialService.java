package eu.europa.ec.empl.edci.viewer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import eu.europa.ec.empl.edci.util.WalletResourceUtil;
import eu.europa.ec.empl.edci.viewer.common.constants.Parameter;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerConfig;
import eu.europa.ec.empl.edci.viewer.common.model.CredentialBaseView;
import eu.europa.ec.empl.edci.viewer.common.model.ShareLinkInfoView;
import eu.europa.ec.empl.edci.viewer.common.model.ShareLinkView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

@Service
public class CredentialService {

    @Autowired
    private WalletResourceUtil walletResourceUtils;

    @Autowired
    private ViewerConfigService viewerConfigService;

    private Logger logger = LogManager.getLogger(CredentialService.class);

    public ResponseEntity<Resource<ShareLinkInfoView>> createShareLink(ShareLinkView shareLinkView, String walletUserId, String uuid) {
        ResponseEntity<Resource<ShareLinkInfoView>> shareLinkResponseView = null;
        try {
            String body = new ObjectMapper().writeValueAsString(shareLinkView);

            shareLinkResponseView = walletResourceUtils.doWalletPostRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_SHARELINK_CREATE)
                            .replaceAll(Pattern.quote(Parameter.WALLET_USER_ID), walletUserId)
                            .replaceAll(Pattern.quote(Parameter.UUID), uuid),
                    body
                    , new ParameterizedTypeReference<Resource<ShareLinkInfoView>>() {
                    },
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_JSON,
                    true);

        } catch (JsonProcessingException e) {
            logger.error("Error sending sharelink creation request to wallet", e);
        }
        return shareLinkResponseView;
    }

    public ResponseEntity<byte[]> downloadVerifiablePresentation(CredentialBaseView credentialBaseViewList, String locale, String walletUserId) {
        byte[] json = null;
        try {
            String body = new ObjectMapper().writeValueAsString(credentialBaseViewList);

            json = walletResourceUtils.doWalletPostRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_VERIFIABLE_PRESENTATION_URL)
                            .replaceAll(Pattern.quote(Parameter.WALLET_USER_ID),
                                    walletUserId),
                    body,
                    byte[].class,
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_PDF,
                    true);

        } catch (JsonProcessingException e) {
            logger.error("Error download verifiable JSON-LD from wallet", e);
        }
        return new ResponseEntity<byte[]>(json, prepareHttpHeadersForCredentialDownload(EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".json"), MediaType.APPLICATION_PDF_VALUE), HttpStatus.OK);
    }

    public ResponseEntity<byte[]> downloadCredential(String walletUserId, String credentialUUID) {

        byte[] json = walletResourceUtils.doWalletGetRequest(viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD).replaceAll(Pattern.quote(Parameter.WALLET_USER_ID),
                walletUserId).replaceAll(Pattern.quote(Parameter.UUID), credentialUUID).concat("?retrieveVP=false"),
                null,
                MediaType.APPLICATION_OCTET_STREAM,
                byte[].class,
                true);

        return new ResponseEntity<byte[]>(json, prepareHttpHeadersForCredentialDownload(EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".json"), MediaType.APPLICATION_PDF_VALUE), HttpStatus.OK);

    }

    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(MultipartFile file, String pdfType) {

        ByteArrayResource byteArrayResource = null;

        byteArrayResource = walletResourceUtils.doWalletPostRequest(
                viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_VERIFIABLE_FROM_FILE_PRESENTATION_URL)
                        .concat("?" + Parameter.PDF_TYPE + "=" + pdfType),
                file,
                EDCIParameter.WALLET_CREDENTIAL_FILE,
                ByteArrayResource.class,
                MediaType.APPLICATION_PDF,
                false);

        return new ResponseEntity<ByteArrayResource>(byteArrayResource, prepareHttpHeadersForCredentialDownload(
                EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".pdf"),
                MediaType.APPLICATION_PDF_VALUE), HttpStatus.OK);
    }

    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentation(MultipartFile file) {

        ByteArrayResource byteArrayResource = null;

        byteArrayResource = walletResourceUtils.doWalletPostRequest(
                viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_VERIFIABLE_FROM_FILE_PRESENTATION_URL),
                file,
                EDCIParameter.WALLET_CREDENTIAL_FILE,
                ByteArrayResource.class,
                MediaType.APPLICATION_OCTET_STREAM,
                false
        );

        return new ResponseEntity<ByteArrayResource>(byteArrayResource, prepareHttpHeadersForCredentialDownload(
                EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".json"),
                MediaType.APPLICATION_OCTET_STREAM_VALUE), HttpStatus.OK);
    }

    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(CredentialBaseView credentialBaseView, String locale, String walletUserId, String pdfType, Date expirationDate) {
        ByteArrayResource byteArrayResource = null;

        try {
            String body = new ObjectMapper().writeValueAsString(credentialBaseView);

            byteArrayResource = walletResourceUtils.doWalletPostRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_VERIFIABLE_PRESENTATION_URL)
                            .replaceAll(Pattern.quote(Parameter.WALLET_USER_ID), walletUserId)
                            .concat("?" + Parameter.PDF_TYPE + "=" + pdfType)
                            .concat(expirationDate != null ? "&" + Parameter.PDF_EXP_DATE + "=" + new SimpleDateFormat(EDCIConstants.DATE_LOCAL).format(expirationDate) : ""),
                    body,
                    ByteArrayResource.class,
                    MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_PDF,
                    true
            );
        } catch (JsonProcessingException e) {
            logger.error("Error downloading verifiable pdf from wallet");
        }
        return new ResponseEntity<ByteArrayResource>(byteArrayResource, prepareHttpHeadersForCredentialDownload(EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".pdf"), MediaType.APPLICATION_PDF_VALUE), HttpStatus.OK);
    }


    private HttpHeaders prepareHttpHeadersForCredentialDownload(String fileName, String mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
        return httpHeaders;
    }
}
