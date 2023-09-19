package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.mapper.CredentialVerificationRestMapper;
import eu.europa.ec.empl.edci.mapper.EuropassCredentialPresentationMapper;
import eu.europa.ec.empl.edci.model.view.VerificationCheckView;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.*;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialStorageUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CredentialPDFService {
    private static final Logger logger = LogManager.getLogger(CredentialPDFService.class);

    @Autowired
    private CredentialStorageUtil credentialStorageUtil;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private EuropassCredentialPresentationMapper europassCredentialPresentationMapper;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private DiplomaUtil diplomaUtil;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private ExternalServicesUtil externalServicesUtil;

    @Autowired
    private ThymeleafUtil thymeleafUtil;

    @Autowired
    private ShareLinkService shareLinkService;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private CredentialVerificationRestMapper credentialVerificationRestMapper;

    protected String prepareVerificationRibbon(List<VerificationCheckView> verifications) {
        String ribbon = "correct";
        try {
            for (VerificationCheckView verification : verifications) {
                if (ControlledListConcept.VERIFICATION_STATUS_ERROR.getUrl().equals(verification.getStatus().getLink().toString())) {
                    ribbon = "incorrect";
                    break;
                }
                if (ControlledListConcept.VERIFICATION_STATUS_SKIPPED.getUrl().equals(verification.getStatus().getLink().toString())
                        && ControlledListConcept.VERIFICATION_CHECKS_SEAL.getUrl().equals(verification.getType().getLink().toString())) {
                    ribbon = "warning";
                }
            }
        } catch (Exception e) {
            logger.error("Error iterating verifications to check the ribbon status", e);
            ribbon = "incorrect";
        }
        return ribbon;
    }

    protected StringBuffer prepareSharelinkQR(String shareLink) throws Exception {
        return new StringBuffer("data:image/png;base64,").append(new String(Base64.getEncoder().encode(
                imageUtil.generateQRCodeImageBytes(shareLink, "png")), StandardCharsets.UTF_8).toString());
    }

    protected byte[] signPDF(byte[] pdfBytes) {
        return pdfBytes; //TODO: sign pdf
    }

    /*
     * Gets a verifiable presentation of the credential in PDF format (signed)
     */
    public ResponseEntity<ByteArrayResource> buildCredentialPDF(byte[] credentialBytes, String pdfType, CredentialDTO credentialDTOS, String shareLink, Date expireDate) {

        ResponseEntity<ByteArrayResource> returnValue = null;
        try {

            EuropeanDigitalCredentialDTO credential = credentialUtil.unMarshallCredential(credentialBytes);

            //By default we'll always return the Diploma PDF (Diploma + verification checks)
            if (StringUtils.isEmpty(pdfType)) {
                pdfType = EDCIWalletConstants.CREDENTIAL_PDF_TYPE_DIPLOMA;
            }

            //If no expiration date is passed, by default we'll add one month to today
            if (expireDate == null) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 1);
                expireDate = cal.getTime();
            }

            Context context = new Context();

            String b64 = Base64.getEncoder().encodeToString(getLogo(LocaleContextHolder.getLocale().getLanguage()).getBytes());
           List<VerificationCheckView> verifications = credentialVerificationRestMapper.toVOList(externalServicesUtil.verifyCredential(credentialBytes), LocaleContextHolder.getLocale().getLanguage());

            context.setLocale(LocaleContextHolder.getLocale());

            context.setVariable("indexCount", new AtomicInteger(1));
            context.setVariable("europassCredentialDetailView", europassCredentialPresentationMapper.toEuropassCredentialPresentationView(credential, true));
            context.setVariable("europassCredential", credential);
            context.setVariable("diplomaList", diplomaUtil.getBase64DiplomaImages(credential, null));
            context.setVariable("europassVerifications", verifications);
            context.setVariable("ribbon", prepareVerificationRibbon(verifications));
            context.setVariable("expireDate", expireDate);
            context.setVariable("europeLogo", "data:image/svg+xml;base64," + b64);

            if (!StringUtils.isEmpty(shareLink)) {
                ShareLinkDTO sl = shareLinkService.fetchShareLinkBySharedURL(shareLink);
                String shareLinkUrl = shareLinkService.getShareLinkURL(sl.getShareHash());
                context.setVariable("shareLink", shareLinkUrl);
                context.setVariable("shareLinkQR", prepareSharelinkQR(shareLinkUrl));
                context.setVariable("shareLinkExpireDate", new SimpleDateFormat(EDCIConstants.DATE_FRONT_LOCAL).format(sl.getExpirationDate()));
            } else if (credentialDTOS != null && credentialDTOS.getWallet() != null) {
                ShareLinkDTO sl = shareLinkService.createShareLink(credentialDTOS.getWallet().getWalletAddress(), credentialDTOS.getUuid(), expireDate);
                String shareLinkUrl = shareLinkService.getShareLinkURL(sl.getShareHash());
                context.setVariable("shareLink", shareLinkUrl);
                context.setVariable("shareLinkQR", prepareSharelinkQR(shareLinkUrl));
                context.setVariable("shareLinkExpireDate", new SimpleDateFormat(EDCIConstants.DATE_FRONT_LOCAL).format(sl.getExpirationDate()));
            }

            Properties labelsProp = new Properties();
            Map<String, String> backEndProperties = edciMessageService.getMessages(LocaleContextHolder.getLocale());
            labelsProp.putAll(backEndProperties);

            String html = thymeleafUtil.processTemplateFromWebInf(EDCIWalletConstants.PDF_THYMELEAF_TEMPLATE, context, labelsProp);

            String pdfDownloadURL = getPdfDownloadUrl();

            if (pdfDownloadURL != null && !pdfDownloadURL.isEmpty()) {
                byte[] bytes = new EDCIRestRequestBuilder(HttpMethod.POST, pdfDownloadURL)
                        .addHeaderRequestedWith()
                        .addHeaders(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_PDF)
                        .addBody(EDCIRestRequestBuilder.prepareMultiPartStringBody("html", html, new HashMap<>()))
                        .buildRequest(byte[].class)
                        .execute();

                // Sign PDF
                //TODO: see EDCI-752
                byte[] pdfSigned = signPDF(bytes);

                ByteArrayResource resource = new ByteArrayResource(pdfSigned);

                String fileName = "";
                try {
                    fileName = EDCIConfig.Defaults.CREDENTIAL_DEFAULT_PREFIX.concat(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()));
                } catch (Exception e) {
                    logger.error(e);
                    fileName = "Credential";
                }

                returnValue = new ResponseEntity<ByteArrayResource>(resource,
                        credentialStorageUtil.prepareHttpHeadersForFile(fileName.concat(".pdf"), MediaType.APPLICATION_PDF_VALUE), HttpStatus.OK);

            }

        } catch (Exception e) {
            logger.error(e);
            throw new EDCIException(e);
        }

        return returnValue;
    }

    protected String getPdfDownloadUrl() {
        return walletConfigService.getString("pdf.download.url");
    }

    protected String getLogo(String locale) {
        if (locale == null) {
            locale = EDCIConstants.DEFAULT_LOCALE;
        }
        ClassPathResource template = new ClassPathResource(
                "logo/".concat("logo")
                        .concat("--")
                        .concat(locale)
                        .concat(".svg"));
        String logoSource;
        try (InputStream inputStream = template.getInputStream()) {
            logoSource = IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            throw new EDCIException().addDescription(String.format("Could not get logo %s", template.getPath()));
        }
        return logoSource;
    }

}
