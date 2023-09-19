package eu.europa.ec.empl.edci.wallet.web.rest.v2;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.mapper.CredentialVerificationRestMapper;
import eu.europa.ec.empl.edci.model.view.VerificationCheckView;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletEndpoint;
import eu.europa.ec.empl.edci.wallet.service.ShareLinkService;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Api(tags = {
        "V2" + WalletEndpoint.V2.SHARELINKS_BASE
}, description = "ShareLink verification API")
@Controller(value = "v2.ShareLinkVerificationResource")
@RequestMapping(value = EDCIConstants.Version.V2)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class ShareLinkVerificationResource implements CrudResource {

    private static final Logger logger = LogManager.getLogger(ShareLinkVerificationResource.class);

    @Autowired
    private ShareLinkService shareLinkService;

    @Autowired
    private CredentialVerificationRestMapper credentialVerificationRestMapper;

    @ApiOperation(value = "Get verification from a credential JSON-LD")
    @GetMapping(value = WalletEndpoint.V2.SHARELINKS_BASE + Parameter.Path.SHARED_HASH + WalletEndpoint.V2.VERIFY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Wallet not Found"),
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid or has expired"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public List<VerificationCheckView> getShareLinkCredentialVerification(@PathVariable(Parameter.SHARED_HASH) String shareHash,
                                                                          @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws IOException {

        return credentialVerificationRestMapper.toVOList(shareLinkService.verifyCredential(shareHash), locale);

    }

}
