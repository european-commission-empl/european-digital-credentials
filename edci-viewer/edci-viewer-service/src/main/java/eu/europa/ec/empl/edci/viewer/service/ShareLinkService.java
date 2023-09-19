package eu.europa.ec.empl.edci.viewer.service;

import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.exception.OIDCException;
import eu.europa.ec.empl.edci.exception.security.EDCIUnauthorizedException;
import eu.europa.ec.empl.edci.model.view.VerificationCheckView;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.WalletResourceUtil;
import eu.europa.ec.empl.edci.viewer.common.constants.Parameter;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ShareLinkService {

    public static final Logger logger = LogManager.getLogger(ShareLinkService.class);

    @Autowired
    private WalletResourceUtil walletResourceUtils;

    @Autowired
    private ViewerConfigService viewerConfigService;

    @Autowired
    private CredentialUtil credentialUtil;


    public ResponseEntity<byte[]> downloadShareLinkPresentation(String shareHash) {
        byte[] json = null;

        try {
            json = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_SHARED_VERIFIABLE_PRESENTATION_URL).replaceAll(Parameter.SHARE_HASH, shareHash),
                    null,
                    MediaType.APPLICATION_PDF,
                    byte[].class,
                    false,
                    null);
        } catch (OIDCException e) {
            logger.error("OIDC Error downloading share link presentation", e);
            throw new EDCIUnauthorizedException();
        }
        return new ResponseEntity<byte[]>(json,
                prepareHttpHeadersForCredentialDownload(EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".json"), MediaType.APPLICATION_OCTET_STREAM_VALUE),
                HttpStatus.OK);

    }

    public ResponseEntity<byte[]> downloadShareLinkCredential(String shareHash) {

        byte[] response = null;

        try {
            response = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_SHARED_JSON).replaceAll(Pattern.quote(Parameter.SHARE_HASH), shareHash),
                    null,
                    MediaType.APPLICATION_OCTET_STREAM,
                    byte[].class,
                    false,
                    null);
        } catch (OIDCException e) {
            logger.error("OIDC Error downloading share link credential", e);
            throw new EDCIUnauthorizedException();
        }
        return new ResponseEntity<byte[]>(response,
                prepareHttpHeadersForCredentialDownload(EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".json"), MediaType.APPLICATION_OCTET_STREAM_VALUE),
                HttpStatus.OK);
    }

    public ResponseEntity<ByteArrayResource> downloadShareLinkPresentationPDF(String shareHash, String pdfType) {
        ByteArrayResource byteArrayResource = null;

        try {
            byteArrayResource = walletResourceUtils.doWalletGetRequest(viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_SHARED_VERIFIABLE_PRESENTATION_URL)
                            .replaceAll(Parameter.SHARE_HASH, shareHash)
                            .concat("?" + Parameter.PDF_TYPE + "=" + pdfType),
                    null,
                    MediaType.APPLICATION_PDF,
                    ByteArrayResource.class,
                    false,
                    null);
        } catch (OIDCException e) {
            logger.error("OIDC error downloading share presentation PDF ", e);
            throw new EDCIUnauthorizedException();
        }
        return new ResponseEntity<ByteArrayResource>(byteArrayResource,
                prepareHttpHeadersForCredentialDownload(EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).concat(".pdf"), MediaType.APPLICATION_PDF_VALUE),
                HttpStatus.OK);
    }

    public List<VerificationCheckView> getShareLinkVerification(String shareHash) {
        List<VerificationCheckView> verification = null;

        try {
            verification = walletResourceUtils.doWalletGetRequest(
                    viewerConfigService.getString(ViewerConfig.Viewer.WALLET_DOWNLOAD_SHARED_VERIFICATION_URL).replaceAll(Parameter.SHARE_HASH, shareHash),
                    null,
                    MediaType.APPLICATION_JSON,
                    List.class,
                    false,
                    null);
        } catch (OIDCException e) {
            logger.error("OIDC error gettink link verification", e);
            throw new EDCIUnauthorizedException();
        }
        return verification;
    }

    private HttpHeaders prepareHttpHeadersForCredentialDownload(String fileName, String mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
        return httpHeaders;
    }
}
