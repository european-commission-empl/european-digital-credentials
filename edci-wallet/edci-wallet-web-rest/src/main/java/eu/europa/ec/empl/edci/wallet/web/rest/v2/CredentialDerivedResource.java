package eu.europa.ec.empl.edci.wallet.web.rest.v2;

import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.IndividualDisplayDTO;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.util.ControlledListsUtil;
import eu.europa.ec.empl.edci.util.ImageUtil;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletEndpoint;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.EuropassDiplomaDTO;
import eu.europa.ec.empl.edci.wallet.service.CredentialService;
import eu.europa.ec.empl.edci.wallet.service.ShareLinkService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialLocalizableInfoUtil;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialStorageUtil;
import eu.europa.ec.empl.edci.wallet.web.mapper.ShareLinkRestMapper;
import eu.europa.ec.empl.edci.wallet.web.model.ShareLinkInfoView;
import eu.europa.ec.empl.edci.wallet.web.model.ShareLinkView;
import io.swagger.annotations.*;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

@Api(tags = {
        "V2" + WalletEndpoint.V2.CREDENTIALS_BASE
}, description = "Credential other operations API")
@Controller(value = "v2.CredentialDerivedResource")
@RequestMapping(value = EDCIConstants.Version.V2)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class CredentialDerivedResource implements CrudResource {

    private static final Logger logger = LogManager.getLogger(CredentialDerivedResource.class);

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private ShareLinkRestMapper shareLinkRestMapper;

    @Autowired
    private ShareLinkService shareLinkService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CredentialLocalizableInfoUtil credentialLocalizableInfoUtil;

    @Autowired
    private CredentialStorageUtil credentialStorageUtil;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @ApiOperation(value = "Create Share Link of a Credential")
    @PostMapping(value = WalletEndpoint.V2.CREDENTIALS_BASE + Parameter.Path.UUID + WalletEndpoint.V2.SHARELINK_SHARE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({@ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    )})
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorizedAddress(#walletAddress)")
    public ResponseEntity<Resource<ShareLinkInfoView>> createShareLink(
            @ApiParam(required = true, value = "The Wallet Address") @RequestParam(Parameter.WALLET_ADDRESS) String walletAddress,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(required = true, value = "Share link new expiration date in format: yyyy-MM-dd'T'HH:mm:ssXXX") @Valid @RequestBody ShareLinkView shareLinkView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        walletService.validateWalletWalletAddressExists(walletAddress);

        ShareLinkInfoView shareResponse = shareLinkRestMapper.toResponseVO(shareLinkService.createShareLinkWalletAddress(walletAddress,
                shareLinkRestMapper.toDTO(shareLinkView, uuid)));

        return generateOkResponse(shareResponse, generateShareLinksHateoas(shareResponse));
    }

    private Link[] generateShareLinksHateoas(ShareLinkInfoView shareResponse) {

        if (shareResponse != null) {

            Link hateoasSelf = ControllerLinkBuilder.linkTo(ShareLinkDerivedResource.class).slash(WalletEndpoint.V2.SHARELINKS_BASE)
                    .slash(shareResponse.getShareHash()).withSelfRel();

            Link hateoasViewer = new Link(shareLinkService.getShareLinkURL(shareResponse.getShareHash()), "view");

            Link hateoasPresentation = ControllerLinkBuilder.linkTo(ShareLinkDerivedResource.class).slash(WalletEndpoint.V2.SHARELINKS_BASE)
                    .slash(shareResponse.getShareHash()).slash(WalletEndpoint.V2.SHARELINK_PRESENTATION).withRel("presentation");

            return new Link[]{
                    hateoasSelf,
                    hateoasViewer,
                    hateoasPresentation
            };

        } else {
            return null;
        }
    }

    @ApiOperation(value = "Downloads a file containing verifiable presentation of the credential")
    @PostMapping(value = WalletEndpoint.V2.CREDENTIALS_BASE + Parameter.Path.UUID + WalletEndpoint.V2.DOWNLOAD_PRESENTATION,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({@ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    )})
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorizedAddress(#walletAddress)")
    public ResponseEntity<ByteArrayResource> createPDF(@ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
                                                       @ApiParam(required = true, value = "The Wallet Address") @RequestParam(Parameter.WALLET_ADDRESS) String walletAddress,
                                                       @ApiParam(required = false, value = "The expiration date for the sharelink that will contain the PDF in format: " + EDCIConstants.DATE_LOCAL)
                                                           @RequestParam(value = Parameter.PDF_EXP_DATE, required = false) @DateTimeFormat(pattern = EDCIConstants.DATE_LOCAL) Date expirationDate,
                                                       @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        String pdfType = "diploma";
        walletService.validateWalletWalletAddressExists(walletAddress);
        return credentialService.downloadVerifiablePresentationPDF(credentialService.fetchCredentialByUUID(walletAddress, uuid), expirationDate, pdfType);

    }

    @ApiOperation(value = "Downloads a file containing verifiable presentation of the credential")
    @PostMapping(value = WalletEndpoint.V2.CREDENTIALS_BASE + WalletEndpoint.V2.FILE_PATH + WalletEndpoint.V2.DOWNLOAD_PRESENTATION,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<ByteArrayResource> createPDF(@RequestPart(value = EDCIParameter.WALLET_CREDENTIAL_FILE) MultipartFile file,
                                                       @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        String pdfType = "diploma";
        try {
            return credentialService.downloadVerifiablePresentationPDF(file.getBytes(), null, pdfType, null);
        } catch (Exception e) {
            throw new EDCIBadRequestException().setCause(e);
        }

    }

    @ApiOperation(value = "Downloads a credential's diploma thmumbnail")
    @GetMapping(value = WalletEndpoint.V2.CREDENTIALS_BASE + Parameter.Path.UUID + WalletEndpoint.V2.THUMBNAIL,
            produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({@ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    )})
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorizedAddress(#walletAddress)")
    public ResponseEntity<byte[]> getThumbnail(
            @ApiParam(required = true, value = "The Wallet Address") @RequestParam(Parameter.WALLET_ADDRESS) String walletAddress,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws IOException {

        walletService.validateWalletWalletAddressExists(walletAddress);
        byte[] diplomaImageFull = DatatypeConverter.parseBase64Binary(new String(credentialService.getDiplomaImage(walletAddress, uuid, locale).get(0)).split("base64,")[1]);
        byte[] diplomaJPG = imageUtil.resizeImage(diplomaImageFull, ControlledListsUtil.MimeType.JPG.getExtension(), 750, 0);

        return new ResponseEntity<byte[]>(diplomaJPG,
                credentialStorageUtil.prepareHttpHeadersForFile(EDCIConfig.Defaults.DIPLOMA_DEFAULT_PREFIX.concat(uuid).concat(".jpg"),
                        MediaType.IMAGE_JPEG_VALUE), HttpStatus.OK);
    }

    @ApiOperation(value = "Downloads a credential's diploma")
    @GetMapping(value = WalletEndpoint.V2.CREDENTIALS_BASE + Parameter.Path.UUID + WalletEndpoint.V2.DIPLOMA,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({ @ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    ) })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorizedAddress(#walletAddress)")
    public ResponseEntity<Resource<EuropassDiplomaDTO>> downloadDiplomaMultipage(
            @ApiParam(required = true, value = "The Wallet Address") @RequestParam(Parameter.WALLET_ADDRESS) String walletAddress,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws IOException {

        walletService.validateWalletWalletAddressExists(walletAddress);

        CredentialDTO credentialDTO = credentialService.fetchCredentialByUUID(walletAddress, uuid);
        EuropeanDigitalCredentialDTO cred = credentialService.getEuropeanDigitalCredential(credentialDTO);

        EuropassDiplomaDTO diploma = new EuropassDiplomaDTO();
        diploma.setBase64DiplomaImages(credentialService.getDiplomaImage(walletAddress, uuid, locale).stream().map(String::new).collect(Collectors.toList()));
        byte[] diplomaImageFull = DatatypeConverter.parseBase64Binary(diploma.getBase64DiplomaImages().get(0).split("base64,")[1]);
        diploma.setLogo(new String(imageUtil.resizeImage(diplomaImageFull,
                ControlledListsUtil.MimeType.JPG.getExtension(), 750, 0)));
        diploma.setId(cred.getId());
        diploma.setAvailableLanguages(cred.getDisplayParameter().getIndividualDisplay().stream()
                .map(IndividualDisplayDTO::getLanguage)
                .map(displayLang -> controlledListCommonsService.searchLanguageISO639ByConcept(displayLang))
                .collect(Collectors.toList()));

        return generateOkResponse(diploma);
    }
}
