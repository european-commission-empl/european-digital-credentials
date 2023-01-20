package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.view.VerificationCheckFieldView;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.mapper.EuropassCredentialPresentationMapper;
import eu.europa.ec.empl.edci.util.DiplomaUtils;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.EDCIRestRequestBuilder;
import eu.europa.ec.empl.edci.util.ImageUtil;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialUtil;
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
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CredentialPDFService {
    private static final Logger logger = LogManager.getLogger(CredentialPDFService.class);

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private EuropassCredentialPresentationMapper europassCredentialPresentationMapper;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private DiplomaUtils diplomaUtils;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private ShareLinkService shareLinkService;

    protected String prepareVerificationRibbon(List<VerificationCheckFieldView> verifications) {
        String ribbon = "correct";
        try {
            for (VerificationCheckFieldView verification : verifications) {
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

    protected List<EuropassCredentialDTO> prepareSubCredentials(CredentialDTO credentialDTOS, EuropassPresentationDTO europassPresentationDTO) {

        List<EuropassCredentialDTO> subCredentials = new ArrayList<EuropassCredentialDTO>();
        try {
            subCredentials = edciCredentialModelUtil.parseSubCredentials(europassPresentationDTO.getCredential().getSubCredentialsXML());
        } catch (JAXBException e) {
            throw new EDCIException("wallet.credential.parsing.subcredential.error").setCause(e);
        }

        return subCredentials;

    }

    protected List<StringBuffer> prepareDiploma(CredentialDTO credentialDTOS, EuropassPresentationDTO europassPresentationDTO) {

        List<byte[]> diplomaList = null;

        if (credentialDTOS != null && credentialDTOS.getWalletDTO() != null) {
            diplomaList = credentialService.getDiplomaImage(credentialDTOS.getWalletDTO().getUserId(), credentialDTOS.getUuid());
        } else {
            diplomaList = diplomaUtils.getDiplomaFromCredential(europassPresentationDTO);
        }

        return diplomaList.stream().map(diploma -> new StringBuffer("data:image/jpeg;base64,").append(new String(Base64.getEncoder().encode(diploma),
                StandardCharsets.UTF_8).toString())).collect(Collectors.toList());

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
    public ResponseEntity<ByteArrayResource> buildCredentialPDF(EuropassPresentationDTO europassPresentationDTO, CredentialDTO credentialDTOS, String shareLink, String pdfType, Date expireDate) {

        ResponseEntity<ByteArrayResource> returnValue = null;
        try {

            //By default we'll always return the Diploma PDF (Diploma + verification checks)
            if (StringUtils.isEmpty(pdfType)) {
                pdfType = EDCIWalletConstants.CREDENTIAL_PDF_TYPE_DIPLOMA;
            }

            if (expireDate == null) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 1);
                expireDate = cal.getTime();
            }

            Context context = new Context();

            String b64 = Base64.getEncoder().encodeToString(getLogo(LocaleContextHolder.getLocale().getLanguage()).getBytes());
            List<VerificationCheckFieldView> verifications = europassCredentialPresentationMapper.toVerificationCheckFieldViewList(europassPresentationDTO.getVerifications());

            context.setLocale(LocaleContextHolder.getLocale());

            context.setVariable("indexCount", new AtomicInteger(1));
            context.setVariable("europassCredentialDetailView", europassCredentialPresentationMapper.toEuropassCredentialPresentationView(europassPresentationDTO, true));
            context.setVariable("europassCredential", europassPresentationDTO.getCredential());
            context.setVariable("europassSubCredentials", prepareSubCredentials(credentialDTOS, europassPresentationDTO));
            context.setVariable("diplomaList", prepareDiploma(credentialDTOS, europassPresentationDTO));
            context.setVariable("europassVerifications", verifications);
            context.setVariable("ribbon", prepareVerificationRibbon(verifications));
            context.setVariable("europeLogo", "data:image/svg+xml;base64," + b64);

            if (!StringUtils.isEmpty(shareLink)) {
                ShareLinkDTO sl = shareLinkService.fetchShareLinkBySharedURL(shareLink);
                String shareLinkUrl = shareLinkService.getShareLinkURL(sl.getShareHash());
                context.setVariable("shareLink", shareLinkUrl);
                context.setVariable("shareLinkQR", prepareSharelinkQR(shareLinkUrl));
                context.setVariable("shareLinkExpireDate", new SimpleDateFormat(EDCIConstants.DATE_FRONT_LOCAL).format(sl.getExpirationDate()));
            } else if (credentialDTOS != null && credentialDTOS.getWalletDTO() != null) {
                ShareLinkDTO sl = shareLinkService.createShareLink(credentialDTOS.getWalletDTO().getUserId(), credentialDTOS.getUuid(), expireDate);
                String shareLinkUrl = shareLinkService.getShareLinkURL(sl.getShareHash());
                context.setVariable("shareLink", shareLinkUrl);
                context.setVariable("shareLinkQR", prepareSharelinkQR(shareLinkUrl));
                context.setVariable("shareLinkExpireDate", new SimpleDateFormat(EDCIConstants.DATE_FRONT_LOCAL).format(sl.getExpirationDate()));
            }

            String html = null;
            if (EDCIWalletConstants.CREDENTIAL_PDF_TYPE_FULL.equalsIgnoreCase(pdfType)) {
                html = processTemplate("verifiable_presentation_template", context);
            } else {
                html = processTemplate("verifiable_diploma_template", context);
            }

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
                        credentialUtil.prepareHttpHeadersForFile(fileName.concat(".pdf"), MediaType.APPLICATION_PDF_VALUE), HttpStatus.OK);

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

    @Transactional(propagation = Propagation.REQUIRED)
    protected boolean credentialExists(String userId, String UUID) {
        return credentialRepository.countByUUID(userId, UUID) > 0;
    }

    protected String processTemplate(String templateFile, Context context) {
        return templateEngine.process(templateFile, context);
    }

    protected String getLogo(String locale) {
        if (locale == null) {
            locale = EDCIConfig.Defaults.DEFAULT_LOCALE;
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
