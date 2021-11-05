package eu.europa.ec.empl.edci.wallet.web.rest.v1;

import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.view.CredentialBaseView;
import eu.europa.ec.empl.edci.datamodel.view.ShareLinkInfoView;
import eu.europa.ec.empl.edci.datamodel.view.ShareLinkView;
import eu.europa.ec.empl.edci.datamodel.view.VerificationCheckView;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.util.ControlledListsUtil;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.ImageUtil;
import eu.europa.ec.empl.edci.util.XmlUtil;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletEndpoint;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.service.CredentialService;
import eu.europa.ec.empl.edci.wallet.service.ShareLinkService;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialLocalizableInfoUtil;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialUtil;
import eu.europa.ec.empl.edci.wallet.web.mapper.CredentialRestMapper;
import eu.europa.ec.empl.edci.wallet.web.mapper.CredentialVerificationRestMapper;
import eu.europa.ec.empl.edci.wallet.web.mapper.ShareLinkRestMapper;
import eu.europa.ec.empl.edci.wallet.web.model.CredentialUploadView;
import eu.europa.ec.empl.edci.wallet.web.model.CredentialView;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Api(tags = {
        "V1" + WalletEndpoint.V1.CREDENTIALS_BASE
}, description = "Credential API")
@Controller(value = "v1.CredentialResource")
@RequestMapping(value = EDCIConstants.Version.V1 + WalletEndpoint.V1.WALLETS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class CredentialResource implements CrudResource {

    private static final Logger logger = Logger.getLogger(CredentialResource.class);

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private CredentialRestMapper credentialRestMapper;

    @Autowired
    private CredentialVerificationRestMapper credentialVerificationRestMapper;

    @Autowired
    private ShareLinkRestMapper shareLinkRestMapper;

    @Autowired
    private ShareLinkService shareLinkService;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private CredentialLocalizableInfoUtil credentialLocalizableInfoUtil;

    @Autowired
    private XmlUtil xmlUtil;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private ImageUtil imageUtil;


    @ApiOperation(value = "Add a credential XML to a existing wallet")
    @PostMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.ROOT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 400, response = ApiErrorMessage.class, message = "The file uploaded is not a valid Europass Digital Credential"),
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "No wallet exists with the given Wallet address"),
            @ApiResponse(code = 409, response = ApiErrorMessage.class, message = "A credential with the same ID already exists"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public CredentialView addCredential(
            @ApiParam(required = true, value = "The XML file containing the credential") @RequestPart(EDCIParameter.WALLET_ADD_CREDENTIAL_XML) MultipartFile file,
            @ApiParam(required = true, value = "The wallet Address where the credential will be added") @PathVariable(Parameter.USER_EMAIL) String userId,
            @ApiParam(value = "sendEmail", defaultValue = "false", allowableValues = "true/false") @RequestParam(value = Parameter.SEND_MAIL, required = false, defaultValue = "true") Boolean sendMail,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale)
            throws UnsupportedEncodingException {
        WalletDTO wallet = walletService.fetchWalletByUserId(userId);
        CredentialUploadView credentialUploadView = new CredentialUploadView();
        credentialUploadView.setWalletAddress(wallet.getWalletAddress());
        credentialUploadView.setUserId(wallet.getUserId());
        try {
            credentialUploadView.setCredentialXML(file.getBytes());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return credentialRestMapper.toVO(credentialService.createCredential(credentialRestMapper.toDTO(credentialUploadView), sendMail), walletConfigService, credentialLocalizableInfoUtil);
    }

    @ApiOperation(value = "Add a credential XML to a temporary or inexistent wallet (if this feature is enabled)")
    @PostMapping(value = WalletEndpoint.V1.EMAIL + Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.ROOT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 400, response = ApiErrorMessage.class, message = "The file uploaded is not a valid Europass Digital Credential"),
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "No wallet exists with the given Wallet address"),
            @ApiResponse(code = 409, response = ApiErrorMessage.class, message = "A credential with the same ID already exists"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public CredentialView addCredentialByEmail(
            @ApiParam(required = true, value = "The XML file containing the credential") @RequestPart(EDCIParameter.WALLET_ADD_CREDENTIAL_XML) MultipartFile file,
            @ApiParam(required = true, value = "The wallet Address where the credential will be added") @PathVariable(Parameter.USER_EMAIL) @Valid @Email String userEmail,
            @ApiParam(value = "sendEmail", defaultValue = "false", allowableValues = "true/false") @RequestParam(value = Parameter.SEND_MAIL, required = false, defaultValue = "true") Boolean sendMail,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale)
            throws UnsupportedEncodingException {

        CredentialDTO credentialDTO = new CredentialDTO();
        try {
            credentialDTO.setCredentialXML(file.getBytes());
        } catch (Exception e) {
            throw new EDCIBadRequestException();
        }

        return credentialRestMapper.toVO(this.credentialService.createCredentialByEmail(credentialDTO, userEmail, sendMail), walletConfigService, credentialLocalizableInfoUtil);

    }

    @ApiOperation(value = "CustomList the existing credentials on a wallet based on a locale")
    @GetMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.ROOT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "No wallet exists with the given Wallet address"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public List<CredentialView> listCredentials(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(value = " The desired locale for the credential's texts") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        walletService.validateWalletExists(userId);
        return credentialRestMapper.toVOList(credentialService.listCredentials(userId), walletConfigService, credentialLocalizableInfoUtil);
    }

    @ApiOperation(value = "Delete an existing credential")
    @DeleteMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.ROOT + Parameter.Path.UUID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public ResponseEntity deleteCredential(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        walletService.validateWalletExists(userId);
        credentialService.deleteCredential(userId, uuid);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Downloads a credential JSON file")
    @GetMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.ROOT + Parameter.Path.UUID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public EuropassCredentialDTO downloadJsonCredential(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        walletService.validateWalletExists(userId);
        byte[] byteCredential = credentialService.fetchCredentialByUUID(userId, uuid).getCredentialXML();
        EuropassCredentialDTO europassCredentialDTO = edciCredentialModelUtil.fromByteArray(byteCredential).getCredential();
        return europassCredentialDTO;

    }

    @ApiOperation(value = "Downloads a credential XML file")
    @GetMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.ROOT + Parameter.Path.UUID,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public ResponseEntity<byte[]> downloadCredential(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale,
            @ApiParam(value = "retrieveVP") @RequestParam(value = "retrieveVP", required = false, defaultValue = "false") boolean retrieveVP) {
        walletService.validateWalletExists(userId);
        return credentialService.downloadCredential(userId, uuid, retrieveVP);
    }

    @ApiOperation(value = "Get verification report from a credential ID")
    @GetMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + Parameter.Path.UUID + WalletEndpoint.V1.VERIFY,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public List<VerificationCheckView> verifyCredential(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(required = true, value = "The credential uuid") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialVerificationRestMapper.toVOList(credentialService.verifyCredential(userId, uuid), LocaleContextHolder.getLocale().getLanguage());
    }

    @ApiOperation(value = "Get verification from a credential XML")
    @PostMapping(value = WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.VERIFY_XML,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Wallet not Found"),
            @ApiResponse(code = 400, response = ApiErrorMessage.class, message = "The file uploaded is not a valid Europass Digital Credential"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public List<VerificationCheckView> getCredentialVerification(
            @ApiParam(required = true, value = "The XML file of the credential") @RequestPart(value = EDCIParameter.WALLET_CREDENTIAL_FILE) MultipartFile file,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws IOException {
        return credentialVerificationRestMapper.toVOList(credentialService.verifyCredential(file), locale);
    }

    @ApiOperation(value = "Create Share Link of a Credential")
    @PostMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + Parameter.Path.UUID + WalletEndpoint.V1.SHARELINK_SHARE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public ResponseEntity<Resource<ShareLinkInfoView>> createShareLink(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @Valid @RequestBody ShareLinkView shareLinkView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        walletService.validateWalletExists(userId);

        ShareLinkInfoView shareResponse = shareLinkRestMapper.toResponseVO(shareLinkService.createShareLink(userId,
                shareLinkRestMapper.toDTO(shareLinkView, uuid)));

        return generateOkResponse(shareResponse, generateShareLinksHateoas(shareResponse));
    }

    private Link[] generateShareLinksHateoas(ShareLinkInfoView shareResponse) {

        if (shareResponse != null) {

//            Link hateoasSelf = ControllerLinkBuilder.linkTo(ShareLinkResource.class).slash(Endpoint.V1.SHARELINKS_BASE)
//                    .slash(shareResponse.getShareHash()).withSelfRel(); //TODO: MediaType ContentType

            Link hateoasViewer = new Link(walletConfigService.getString(EDCIWalletConstants.CONFIG_PROPERTY_VIEWER_SHARED_URL)
                    .replaceAll(Parameter.SHARED_HASH, shareResponse.getShareHash()), "view");

//            Link hateoasPresentation = ControllerLinkBuilder.linkTo(ShareLinkResource.class).slash(Endpoint.V1.SHARELINKS_BASE)
//                    .slash(shareResponse.getShareHash()).slash(Endpoint.V1.SHARELINK_PRESENTATION).withRel("presentation");

            return new Link[]{
//                    hateoasSelf,
                    hateoasViewer
//                    , hateoasPresentation
            };

        } else {
            return null;
        }
    }

    //Post or get?
    @ApiOperation(value = "Downloads a file containing verifiable presentation of the credential", tags = "Credentials")
    @PostMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.DOWNLOAD_VERIFIABLE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public ResponseEntity<byte[]> downloadVerificationXML(@RequestBody CredentialBaseView credentials,
                                                          @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
                                                          @ApiParam(required = false, value = "The information that we want into the PDF: full/diploma. By default Diploma", defaultValue = "diploma") @RequestParam(value = Parameter.PDF_TYPE, required = false) String pdfType,
                                                          @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        return credentialService.downloadVerifiablePresentationXML(userId, credentials.getUuid(), null);

    }

    @ApiOperation(value = "Downloads a file containing verifiable presentation of the credential", tags = "Credentials")
    @PostMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.DOWNLOAD_VERIFIABLE,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(@RequestBody CredentialBaseView credential,
                                                                               @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
                                                                               @ApiParam(required = false, value = "The information that we want into the PDF: full/diploma. By default Diploma", defaultValue = "diploma") @RequestParam(value = Parameter.PDF_TYPE, required = false) String pdfType,
                                                                               @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        walletService.validateWalletExists(userId);

        return credentialService.downloadVerifiablePresentationPDF(credentialService.fetchCredentialByUUID(userId, credential.getUuid()), null, pdfType);

    }

    @ApiOperation(value = "Downloads a file containing verifiable presentation of the credential", tags = "Credentials")
    @PostMapping(value = WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.DOWNLOAD_PRESENTATION,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDFFromFile(@RequestPart(value = EDCIParameter.WALLET_CREDENTIAL_FILE) MultipartFile file,
                                                                                       @ApiParam(required = false, value = "The information that we want into the PDF: full/diploma. By default Diploma", defaultValue = "diploma") @RequestParam(value = Parameter.PDF_TYPE, required = false) String pdfType,
                                                                                       @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        EuropassPresentationDTO europassPresentationDTO = null;

        try {
            europassPresentationDTO = credentialUtil.buildEuropassVerifiablePresentation(file.getBytes());
        } catch (Exception e) {
            throw new EDCIException(e);
        }

        return credentialService.downloadVerifiablePresentationPDF(europassPresentationDTO, null, pdfType);

    }

    @ApiOperation(value = "Downloads a file containing verifiable presentation of the credential", tags = "Credentials")
    @PostMapping(value = WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.DOWNLOAD_PRESENTATION,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<byte[]> downloadVerifiablePresentationXMLFromFile(@RequestPart(value = EDCIParameter.WALLET_CREDENTIAL_FILE) MultipartFile file,
                                                                            @ApiParam(required = false, value = "The information that we want into the PDF: full/diploma. By default Diploma", defaultValue = "diploma") @RequestParam(value = Parameter.PDF_TYPE, required = false) String pdfType,
                                                                            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (Exception e) {
            throw new EDCIException(e);
        }

        return credentialService.downloadVerifiablePresentationXML(bytes, null);

    }

    @ApiOperation(value = "Downloads a credential's diploma thmumbnail")
    @GetMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.ROOT + Parameter.Path.UUID + WalletEndpoint.V1.THUMBNAIL,
            produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public ResponseEntity<byte[]> downloadDiploma(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws JAXBException, IOException {

        walletService.validateWalletExists(userId);
        byte[] diplomaJPG = imageUtil.resizeImage(credentialService.getDiplomaImage(userId, uuid), ControlledListsUtil.MimeType.JPG.getExtension(), 750, 0);

        return new ResponseEntity<byte[]>(diplomaJPG,
                credentialUtil.prepareHttpHeadersForFile(EDCIConfig.Defaults.DIPLOMA_DEFAULT_PREFIX.concat(uuid).concat(".jpg"),
                        MediaType.IMAGE_JPEG_VALUE), HttpStatus.OK);
    }

}
