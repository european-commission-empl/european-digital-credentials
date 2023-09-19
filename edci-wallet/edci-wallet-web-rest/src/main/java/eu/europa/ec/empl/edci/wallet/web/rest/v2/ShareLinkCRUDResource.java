package eu.europa.ec.empl.edci.wallet.web.rest.v2;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletEndpoint;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import eu.europa.ec.empl.edci.wallet.mapper.CycleAvoidingMappingContext;
import eu.europa.ec.empl.edci.wallet.mapper.ShareLinkMapper;
import eu.europa.ec.empl.edci.wallet.service.ShareLinkService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import eu.europa.ec.empl.edci.wallet.web.mapper.ShareLinkRestMapper;
import eu.europa.ec.empl.edci.wallet.web.model.ShareLinkInfoView;
import eu.europa.ec.empl.edci.wallet.web.model.ShareLinkView;
import io.swagger.annotations.*;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api(tags = {
        "V2" + WalletEndpoint.V2.SHARELINKS_BASE
}, description = "ShareLink CRUD API")
@Controller(value = "v2.ShareLinkCRUDResource")
@RequestMapping(value = EDCIConstants.Version.V2)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class ShareLinkCRUDResource implements CrudResource {

    private static final Logger logger = LogManager.getLogger(ShareLinkCRUDResource.class);

    @Autowired
    private ShareLinkRestMapper shareLinkRestMapper;

    @Autowired
    private ShareLinkService shareLinkService;

    @Autowired
    private ShareLinkMapper shareLinkMapper;

    @Autowired
    private WalletService walletService;

    @ApiOperation(value = "Get Share Link of a Credential")
    @GetMapping(value = WalletEndpoint.V2.SHARELINKS_BASE + Parameter.Path.SHARED_HASH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid or has expired"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<Resource<ShareLinkInfoView>> getShareLink(@PathVariable(Parameter.SHARED_HASH) String sharedHash,
                                                                    @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {


        ShareLinkInfoView shareResponse = shareLinkRestMapper.toResponseVO(shareLinkService.fetchShareLinkBySharedURL(sharedHash));
        return generateOkResponse(shareResponse, generateShareLinksHateoas(shareResponse));
    }

    @ApiOperation(value = "Get Share Link given a wallet address")
    @GetMapping(value = WalletEndpoint.V2.SHARELINKS_BASE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({ @ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    ) })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorizedAddress(#walletAddress)")
    public ResponseEntity<PagedResources<ShareLinkInfoView>> listShareLink(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SH_WALLET_ADDRESS, required = true) String walletAddress,
            @ApiParam() @RequestParam(value = Parameter.SH_EXPIRED, required = false) Boolean expired,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        walletService.validateWalletWalletAddressExists(walletAddress);

        PageParam pageParam = new PageParam(page, size, sort, direction);

        String expiredStr = "";

        if (expired != null) {
            expiredStr = ";expired:" + (expired ? "1" : "0");
        }

        Specification<ShareLinkDAO> specif = buildSearchSpecification("credential.wallet.walletAddress:" + walletAddress + expiredStr);

        Page<ShareLinkDAO> resultPage = shareLinkService.findAll(specif, pageParam.toPageRequest());

        return generateListResponse(resultPage.map(dao -> shareLinkRestMapper.toResponseVO(shareLinkMapper.toDTO(dao, new CycleAvoidingMappingContext()))), WalletEndpoint.V2.SHARELINKS_BASE);
    }

    @ApiOperation(value = "Delete Share Link of a Credential")
    @DeleteMapping(value = WalletEndpoint.V2.SHARELINKS_BASE + Parameter.Path.SHARED_HASH)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({ @ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    ) })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorizedSharelink(#sharedHash)")
    public ResponseEntity<Resource<ShareLinkInfoView>> deleteShareLink(
            @PathVariable(Parameter.SHARED_HASH) String sharedHash,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        ShareLinkDTO shareLink = shareLinkService.fetchShareLinkBySharedURL(sharedHash);

        shareLinkService.deleteShareLink(shareLink);

        return generateNoContentResponse();
    }

    @ApiOperation(value = "Update Share Link of a Credential")
    @PutMapping(value = WalletEndpoint.V2.SHARELINKS_BASE + Parameter.Path.SHARED_HASH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid"),
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "The link is not found"),
            @ApiResponse(code = 400, response = ApiErrorMessage.class, message = "Invalid body request"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    @ApiImplicitParams({ @ApiImplicitParam(name = HttpHeaders.AUTHORIZATION,
            value = "Oauth access token", dataType = "string",
            paramType = "header", required = true,
            defaultValue = "Bearer [accessToken]"
    ) })
    @PreAuthorize("@edciWalletAuthorizationService.isAuthorizedSharelink(#sharedHash)")
    public ResponseEntity<Resource<ShareLinkInfoView>> updateShareLink(
            @PathVariable(Parameter.SHARED_HASH) String sharedHash,
            @ApiParam(required = true, value = "Share link new expiration date in format: yyyy-MM-dd'T'HH:mm:ssXXX") @RequestBody ShareLinkView shareLinkView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        ShareLinkInfoView shareResponse = shareLinkRestMapper.toResponseVO(shareLinkService.updateShareLink(sharedHash, shareLinkRestMapper.toDTO(shareLinkView, null)));
        return generateOkResponse(shareResponse, generateShareLinksHateoas(shareResponse));
    }

    private Link[] generateShareLinksHateoas(ShareLinkInfoView shareResponse) {

        if (shareResponse != null) {

            Link hateoasSelf = ControllerLinkBuilder.linkTo(ShareLinkCRUDResource.class).slash(WalletEndpoint.V2.SHARELINKS_BASE)
                    .slash(shareResponse.getShareHash()).withSelfRel();

            Link hateoasViewer = new Link(shareLinkService.getShareLinkURL(shareResponse.getShareHash()), "view");

            Link hateoasPresentation = ControllerLinkBuilder.linkTo(ShareLinkCRUDResource.class).slash(WalletEndpoint.V2.SHARELINKS_BASE)
                    .slash(shareResponse.getShareHash()).slash(WalletEndpoint.V2.SHARELINK_PRESENTATION).withRel("presentation");

            return new Link[]{
                    hateoasSelf, hateoasViewer, hateoasPresentation
            };

        } else {
            return null;
        }
    }
}
