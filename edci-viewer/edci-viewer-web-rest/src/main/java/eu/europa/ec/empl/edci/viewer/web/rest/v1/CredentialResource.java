package eu.europa.ec.empl.edci.viewer.web.rest.v1;


import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.view.*;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.mapper.EuropassCredentialPresentationMapper;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.viewer.common.constants.Parameter;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerEndpoint;
import eu.europa.ec.empl.edci.viewer.service.CredentialDetailService;
import eu.europa.ec.empl.edci.viewer.service.CredentialService;
import eu.europa.ec.empl.edci.viewer.web.mapper.EuropassCredentialDetailRestMapper;
import eu.europa.ec.empl.edci.viewer.web.model.EuropassDiplomaView;
import eu.europa.ec.empl.edci.viewer.web.rest.CrudResource;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;


@Api(tags = {
        "V1"
})
@Controller(value = "v1.CredentialResource")
@RequestMapping(value = EDCIConstants.Version.V1 + ViewerEndpoint.V1.CREDENTIALS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class CredentialResource implements CrudResource {

    //    private final static Logger logger = LoggerFactory.getLogger(CredentialResource.class);
    public static final Logger logger = LogManager.getLogger(CredentialResource.class);

    @Autowired
    private EuropassCredentialDetailRestMapper europassCredentialDetailRestMapper;

    @Autowired
    private EuropassCredentialPresentationMapper europassCredentialPresentationMapper;

    @Autowired
    private CredentialDetailService credentialDetailService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private Validator validator;

    @ApiOperation(value = "Get visual representation of a diploma from XML")
    @PostMapping(value = ViewerEndpoint.V1.DIPLOMA,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EuropassDiplomaView getCredentialDiploma(@RequestPart(value = "file") MultipartFile file,
                                                    @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return europassCredentialDetailRestMapper.toVO(credentialDetailService.getCredentialDiploma(file, locale));
    }

    @ApiOperation(value = "Get Visual representation of a diploma from Wallet Address and CredUUID")
    @GetMapping(value = Parameter.Path.WALLET_USER_ID + Parameter.Path.UUID + ViewerEndpoint.V1.DIPLOMA,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public EuropassDiplomaView getWalletDiplomaHTML(@PathVariable(Parameter.WALLET_USER_ID) String userId, @PathVariable(Parameter.UUID) String credentialUUID,
                                                    @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return europassCredentialDetailRestMapper.toVO(credentialDetailService.getWalletDiplomaHTML(credentialUUID, userId, locale));
    }

    @ApiOperation(value = "Get detailed representation of a diploma from XML")
    @PostMapping(value = ViewerEndpoint.V1.DETAILS,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EuropassCredentialPresentationView getCredentialDetail(@RequestPart(value = "file") MultipartFile file, @RequestParam(value = Parameter.LOCALE) String locale) {
        return europassCredentialPresentationMapper.toEuropassCredentialPresentationView(credentialDetailService.getCredentialDetail(file), false);
    }

    @ApiOperation(value = "Get detailed representation of a Diploma stored in a wallet")
    @GetMapping(value = Parameter.Path.WALLET_USER_ID + Parameter.Path.UUID + ViewerEndpoint.V1.DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public EuropassCredentialPresentationView getCredentialDetail(@PathVariable(Parameter.WALLET_USER_ID) String userId, @PathVariable(Parameter.UUID) String credentialUUID,
                                                                  @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return europassCredentialPresentationMapper.toEuropassCredentialPresentationView(credentialDetailService.getCredentialDetail(userId, credentialUUID), false);
    }

    @ApiOperation(value = "Get verification report from a credential")
    @PostMapping(value = ViewerEndpoint.V1.VERIFICATION,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<VerificationCheckView> getCredentialVerification(@RequestPart(value = "file") MultipartFile file,
                                                                 @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialDetailService.getCredentialVerification(file);
    }

    @ApiOperation(value = "Get verification report from wallet credential uuid")
    @GetMapping(value = Parameter.Path.WALLET_USER_ID + Parameter.Path.UUID + ViewerEndpoint.V1.VERIFY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public List<VerificationCheckView> getCredentialVerificationByWalletAddressID(
            @PathVariable(Parameter.WALLET_USER_ID) String userId,
            @PathVariable(Parameter.UUID) String uuidCredential,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialDetailService.getWalletCredentialVerification(uuidCredential, userId);

    }

    @ApiOperation(value = "Create Share Link of a Credential")
    @PostMapping(value = Parameter.Path.WALLET_USER_ID + ViewerEndpoint.V1.CREDENTIALS_BASE + Parameter.Path.UUID + ViewerEndpoint.V1.SHARELINKS_BASE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("isAuthenticated()")
    public Resource<ShareLinkInfoView> createShareLink(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.WALLET_USER_ID) String userId,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @Valid @RequestBody ShareLinkView shareLinkView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialService.createShareLink(shareLinkView, userId, uuid).getBody();
    }

    //Post or get?
    @ApiOperation(value = "Downloads a file containing verifiable presentation of the credential in XML format")
    @PostMapping(value = Parameter.Path.WALLET_USER_ID + ViewerEndpoint.V1.DOWNLOAD_VERIFIABLE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadVerificationXML(@RequestBody CredentialBaseView credentials,
                                                          @PathVariable(Parameter.WALLET_USER_ID) String userId,
                                                          @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        return credentialService.downloadVerifiablePresentationXML(credentials, locale, userId);
    }

    @ApiOperation(value = "Downloads a credential in XML format")
    @PostMapping(value = Parameter.Path.WALLET_USER_ID + ViewerEndpoint.V1.DOWNLOAD_CREDENTIAL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadCredentialXML(@RequestBody CredentialBaseView credentials,
                                                        @PathVariable(Parameter.WALLET_USER_ID) String userId,
                                                        @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        return credentialService.downloadCredentialXML(userId, credentials.getUuid());
    }


    @ApiOperation(value = "Downloads a file containing verifiable presentation of the credential in PDF format")
    @PostMapping(value = Parameter.Path.WALLET_USER_ID + ViewerEndpoint.V1.DOWNLOAD_VERIFIABLE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDF(@RequestBody CredentialBaseView credential,
                                                                               @PathVariable(Parameter.WALLET_USER_ID) String userId,
                                                                               @ApiParam(required = false, value = "The information that we want into the PDF: full/diploma. By default Diploma", defaultValue = "diploma") @RequestParam(value = Parameter.PDF_TYPE, required = false) String pdfType,
                                                                               @ApiParam(required = false, value = "The expiration date for the sharelink that will contain the PDF in format: " + EDCIConstants.DATE_LOCAL) @RequestParam(value = Parameter.PDF_EXP_DATE, required = false) @DateTimeFormat(pattern = EDCIConstants.DATE_LOCAL) Date expirationDate,
                                                                               @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialService.downloadVerifiablePresentationPDF(credential, locale, userId, pdfType, expirationDate);

    }

    @ApiOperation(value = "Downloads a file containing verifiable presentation of the credential")
    @PostMapping(value = ViewerEndpoint.V1.DOWNLOAD_PRESENTATION,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationPDFFromFile(@RequestPart(value = "file") MultipartFile file,
                                                                                       @ApiParam(required = false, value = "The information that we want into the PDF: full/diploma. By default Diploma", defaultValue = "diploma") @RequestParam(value = Parameter.PDF_TYPE, required = false) String pdfType,
                                                                                       @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        return credentialService.downloadVerifiablePresentationPDF(file, pdfType);

    }

    @ApiOperation(value = "Downloads a file containing verifiable presentation of the credential")
    @PostMapping(value = ViewerEndpoint.V1.DOWNLOAD_PRESENTATION,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<ByteArrayResource> downloadVerifiablePresentationXMLFromFile(@RequestPart(value = "file") MultipartFile file,
                                                                                       @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        return credentialService.downloadVerifiablePresentationXML(file);

    }


}
