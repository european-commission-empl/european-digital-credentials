package eu.europa.ec.empl.edci.wallet.web.rest.v1;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.mapper.CredentialVerificationRestMapper;
import eu.europa.ec.empl.edci.model.view.VerificationCheckView;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletEndpoint;
import eu.europa.ec.empl.edci.wallet.service.CredentialService;
import io.swagger.annotations.*;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {
        "V1" + WalletEndpoint.V1.CREDENTIALS_BASE
}, description = "Credential verification API")
@Controller(value = "v1.CredentialVerificationResource")
@RequestMapping(value = EDCIConstants.Version.V1 + WalletEndpoint.V1.WALLETS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class CredentialVerificationResource implements CrudResource {

    private static final Logger logger = LogManager.getLogger(CredentialVerificationResource.class);

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private CredentialVerificationRestMapper credentialVerificationRestMapper;

    @ApiOperation(value = "Get verification report from a credential ID")
    @GetMapping(value = Parameter.Path.USER_ID + WalletEndpoint.V1.CREDENTIALS_BASE + Parameter.Path.UUID + WalletEndpoint.V1.VERIFY,
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
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public List<VerificationCheckView> verifyCredential(
            @ApiParam(required = true, value = "The Wallet Address where the credentials are stored") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(required = true, value = "The credential uuid") @PathVariable(Parameter.UUID) String uuid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialVerificationRestMapper.toVOList(credentialService.verifyCredential(userId, uuid), locale);
    }

}
