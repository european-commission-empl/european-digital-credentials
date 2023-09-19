package eu.europa.ec.empl.edci.wallet.web.rest.v1;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletEndpoint;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.service.CredentialService;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialLocalizableInfoUtil;
import eu.europa.ec.empl.edci.wallet.web.mapper.CredentialRestMapper;
import eu.europa.ec.empl.edci.wallet.web.model.CredentialView;
import io.swagger.annotations.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Api(tags = {
        "V1" + WalletEndpoint.V1.CREDENTIALS_BASE
}, description = "Credential CRUD API")
@Controller(value = "v1.CredentialCRUDResource")
@RequestMapping(value = EDCIConstants.Version.V1 + WalletEndpoint.V1.WALLETS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class CredentialCrudResource implements CrudResource {

    private static final Logger logger = LogManager.getLogger(CredentialCrudResource.class);

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private CredentialRestMapper credentialRestMapper;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CredentialLocalizableInfoUtil credentialLocalizableInfoUtil;

    @ApiOperation(value = "Add a credential to a existing wallet")
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
            @ApiParam(required = false, value = "The XML file containing the credential", hidden = true) @RequestPart(value = EDCIParameter.WALLET_ADD_CREDENTIAL_XML, required = false) MultipartFile fileOld,
            @ApiParam(required = false, value = "The file containing the credential") @RequestPart(value = EDCIParameter.WALLET_ADD_CREDENTIAL, required = false) MultipartFile file,
            @ApiParam(required = true, value = "The wallet Address where the credential will be added") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(value = "sendEmail", defaultValue = "false", allowableValues = "true/false") @RequestParam(value = Parameter.SEND_MAIL, required = false, defaultValue = "true") Boolean sendMail,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale)
            throws UnsupportedEncodingException {

        byte[] credentialBytes = null;
        boolean needsConversion = false;

        try {
            if (file != null) {
                credentialBytes = file.getBytes();
                if (EDCIConstants.XML.XML_VALUE.equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()))) {
                    needsConversion = true;
                }
            } else if (fileOld != null) {
                credentialBytes = fileOld.getBytes();
                if (EDCIConstants.XML.XML_VALUE.equalsIgnoreCase(FilenameUtils.getExtension(fileOld.getOriginalFilename()))) {
                    needsConversion = true;
                }
            } else {
                throw new EDCIBadRequestException("wallet.error.no.credential.provided");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new EDCIException(e);
        }

        WalletDTO wallet = walletService.fetchWalletByUserId(userId);

        CredentialDTO newCredentialDTO = credentialService.createCredential(wallet, credentialBytes, sendMail, needsConversion);

        return credentialRestMapper.toVO(newCredentialDTO, walletConfigService, credentialLocalizableInfoUtil);
    }

    @ApiOperation(value = "Add a credential XML to a temporary or inexistent wallet (if this feature is enabled)")
    @PostMapping(value = WalletEndpoint.V1.EMAIL + Parameter.Path.USER_EMAIL + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.ROOT,
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
            @ApiParam(required = false, value = "The XML file containing the credential", hidden = true) @RequestPart(value = EDCIParameter.WALLET_ADD_CREDENTIAL_XML, required = false) MultipartFile fileOld,
            @ApiParam(required = false, value = "The file containing the credential") @RequestPart(value = EDCIParameter.WALLET_ADD_CREDENTIAL, required = false) MultipartFile file,
            @ApiParam(required = true, value = "The wallet's email or the email used to create the temporary wallet if it's not registered yet") @PathVariable(Parameter.USER_EMAIL) @Valid @Email String userEmail,
            @ApiParam(value = "sendEmail", defaultValue = "false", allowableValues = "true/false") @RequestParam(value = Parameter.SEND_MAIL, required = false, defaultValue = "true") Boolean sendMail,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale)
            throws UnsupportedEncodingException {

        byte[] credentialBytes = null;
        boolean needsConversion = false;

        try {
            if (file != null) {
                credentialBytes = file.getBytes();
                if (EDCIConstants.XML.XML_VALUE.equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()))) {
                    needsConversion = true;
                }
            } else if (fileOld != null) {
                credentialBytes = fileOld.getBytes();
                if (EDCIConstants.XML.XML_VALUE.equalsIgnoreCase(FilenameUtils.getExtension(fileOld.getOriginalFilename()))) {
                    needsConversion = true;
                }
            } else {
                throw new EDCIBadRequestException("wallet.error.no.credential.provided");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new EDCIException(e);
        }

        WalletDAO wallet = walletService.createOrRetrieveWalletByEmail(userEmail);

        CredentialDTO newCredentialDTO = credentialService.createCredentialByEmail(wallet, credentialBytes, needsConversion, sendMail);
        return credentialRestMapper.toVO(newCredentialDTO, walletConfigService, credentialLocalizableInfoUtil);

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
        WalletDTO walletDTO = walletService.fetchWalletByUserId(userId);
        return credentialRestMapper.toVOList(credentialService.listCredentials(walletDTO.getWalletAddress()), walletConfigService, credentialLocalizableInfoUtil);
    }

    @ApiOperation(value = "Delete an existing credential")
    @DeleteMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.ROOT + Parameter.Path.UUID,
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
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public ResponseEntity deleteCredential(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        credentialService.deleteCredential(walletService.fetchWalletByUserId(userId).getWalletAddress(), uuid);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Downloads a credential JSON-LD file")
    @GetMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + WalletEndpoint.V1.ROOT + Parameter.Path.UUID,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Credential or Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({@ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    )})
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public ResponseEntity<byte[]> getCredential(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(required = true, value = "The ID of the credential") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        WalletDTO walletDTO = walletService.fetchWalletByUserId(userId);
        return credentialService.downloadCredential(walletDTO.getWalletAddress(), uuid);
    }

}
