package eu.europa.ec.empl.edci.wallet.web.rest.v1;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletEndpoint;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import eu.europa.ec.empl.edci.wallet.web.mapper.WalletRestMapper;
import eu.europa.ec.empl.edci.wallet.web.model.*;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {
        "V1" + WalletEndpoint.V1.WALLETS_BASE
}, description = "Wallet API")
@Controller(value = "v1.WalletResource")
@RequestMapping(value = EDCIConstants.Version.V1)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class WalletResource {

    @Autowired
    private WalletRestMapper walletRestMapper;

    @Autowired
    private WalletService walletService;

    @ApiOperation(value = "Create a Wallet from a userID and email")
    @PostMapping(value = WalletEndpoint.V1.WALLETS_BASE + WalletEndpoint.V1.ROOT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 409, response = ApiErrorMessage.class, message = "A Wallet already exists for this User mail/Id"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public WalletCreateResponseView createWallet(
            @ApiParam(value = "The wallet create request, all fields are required") @Valid @RequestBody WalletCreateView walletCreateView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) {

        if (StringUtils.isEmpty(walletCreateView.getUserId())) {
            throw new EDCIBadRequestException();
        }

        return walletRestMapper.toVO(walletService.createWallet(walletRestMapper.toDTO(walletCreateView)));
    }

    @ApiOperation(value = "Modify a Wallet's information")
    @PutMapping(value = WalletEndpoint.V1.WALLETS_BASE + Parameter.Path.USER_ID,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "No wallet exists with the given User Id"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({ @ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    ) })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public WalletResponseView modifyWallet(
            @ApiParam(required = true, value = "The User Id to be modified") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(value = "The wallet update request") @Valid @RequestBody WalletModifyView walletModifyView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) {

        if (StringUtils.isEmpty(walletModifyView.getUserEmail())) {
            throw new EDCIBadRequestException();
        }

        WalletDTO wallet = walletRestMapper.toDTO(walletModifyView);
        wallet.setUserId(userId);

        return walletRestMapper.toVOResponse(walletService.updateWallet(wallet));
    }

    @ApiOperation(value = "Returns a wallet's information")
    @GetMapping(value = WalletEndpoint.V1.WALLETS_BASE + Parameter.Path.USER_ID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "No wallet exists with the given User Id"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({ @ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    ) })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public WalletResponseView getWallet(
            @ApiParam(required = true, value = "The User Id to be retrieved") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) {

        return walletRestMapper.toVOResponse(walletService.fetchWalletByUserId(userId));
    }

    @ApiOperation(value = "Create a temporary Wallet from an email")
    @PostMapping(value = WalletEndpoint.V1.WALLETS_BASE + WalletEndpoint.V1.WALLETS_BULK,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public WalletCreateBulkResponseView createBulkWallet(
            @ApiParam(value = "The wallet create request, all fields are required") @Valid @RequestBody WalletCreateBulkView walletCreateBulkView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) {
        return walletRestMapper.toVOErrors(walletService.createBulkWallet(walletRestMapper.toDTO(walletCreateBulkView.getWallets())));
    }

    @ApiOperation(value = "Deletes a existing wallet")
    @DeleteMapping(value = WalletEndpoint.V1.WALLETS_BASE + Parameter.Path.USER_ID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "No wallet exists with the given User Id"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({ @ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    ) })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorized(#userId)")
    public ResponseEntity deleteWallet(
            @ApiParam(required = true, value = "The User Id to be deleted") @PathVariable(Parameter.USER_ID) String userId,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        walletService.deleteWallet(userId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
