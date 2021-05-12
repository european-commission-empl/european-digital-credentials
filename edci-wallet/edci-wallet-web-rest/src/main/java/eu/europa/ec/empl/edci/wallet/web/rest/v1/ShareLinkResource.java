package eu.europa.ec.empl.edci.wallet.web.rest.v1;

import eu.europa.ec.empl.edci.constants.Version;
import eu.europa.ec.empl.edci.datamodel.view.ShareLinkInfoView;
import eu.europa.ec.empl.edci.datamodel.view.ShareLinkView;
import eu.europa.ec.empl.edci.datamodel.view.VerificationCheckView;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.constants.Endpoint;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import eu.europa.ec.empl.edci.wallet.mapper.ShareLinkMapper;
import eu.europa.ec.empl.edci.wallet.service.ShareLinkService;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import eu.europa.ec.empl.edci.wallet.web.mapper.CredentialVerificationRestMapper;
import eu.europa.ec.empl.edci.wallet.web.mapper.ShareLinkRestMapper;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Api(tags = {
        "V1" + Endpoint.V1.SHARELINKS_BASE
}, description = "ShareLink API")
@Controller(value = "v1.ShareLinkResource")
@RequestMapping(value = Version.V1)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class ShareLinkResource implements CrudResource {

    private static final Logger logger = Logger.getLogger(ShareLinkResource.class);

    @Autowired
    private ShareLinkRestMapper shareLinkRestMapper;

    @Autowired
    private ShareLinkService shareLinkService;

    @Autowired
    private ShareLinkMapper shareLinkMapper;

    @Autowired
    private CredentialVerificationRestMapper credentialVerificationRestMapper;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private WalletService walletService;

    //TODO vp: why delete this?
    @ApiOperation(value = "Download Shared Link Credential")
    @GetMapping(value = Endpoint.V1.SHARELINKS_BASE + Parameter.Path.SHARED_HASH + Endpoint.V1.CREDENTIALS_BASE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid or has expired"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<byte[]> downloadShareLinkCredential(@PathVariable(Parameter.SHARED_HASH) String sharedURL,
                                                              @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return shareLinkService.downloadShareLinkCredential(sharedURL);
    }

    @ApiOperation(value = "Get Share Link of a Credential")
    @GetMapping(value = Endpoint.V1.SHARELINKS_BASE + Parameter.Path.SHARED_HASH,
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
    @GetMapping(value = Endpoint.V1.SHARELINKS_BASE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Wallet not Found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<PagedResources<ShareLinkInfoView>> listShareLink(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SH_USER_ID, required = true) String walletAddress,
            @ApiParam() @RequestParam(value = Parameter.SH_EXPIRED, required = false) Boolean expired,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        walletService.validateWalletExists(walletAddress);

        PageParam pageParam = new PageParam(page, size, sort, direction);

        String expiredStr = "";

        if (expired != null) {
            expiredStr = ";expired:" + (expired ? "1" : "0");
        }

        Specification<ShareLinkDAO> specif = buildSearchSpecification("credentialDAO.walletDAO.userId:" + walletAddress + expiredStr);

        Page<ShareLinkDAO> resultPage = shareLinkService.findAll(specif, pageParam.toPageRequest());

        return generateListResponse(resultPage.map(dao -> shareLinkRestMapper.toResponseVO(shareLinkMapper.toDTO(dao))), Endpoint.V1.SHARELINKS_BASE);
    }

    @ApiOperation(value = "Delete Share Link of a Credential")
    @DeleteMapping(value = Endpoint.V1.SHARELINKS_BASE + Parameter.Path.SHARED_HASH)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<Resource<ShareLinkInfoView>> deleteShareLink(
            @PathVariable(Parameter.SHARED_HASH) String sharedHash,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        ShareLinkDTO shareLink = shareLinkService.fetchShareLinkBySharedURL(sharedHash);

        shareLinkService.deleteShareLink(shareLink);

        return generateNoContentResponse();
    }

    @ApiOperation(value = "Update Share Link of a Credential")
    @PutMapping(value = Endpoint.V1.SHARELINKS_BASE + Parameter.Path.SHARED_HASH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid"),
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "The link is not found"),
            @ApiResponse(code = 400, response = ApiErrorMessage.class, message = "Invalid body request"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<Resource<ShareLinkInfoView>> updateShareLink(
            @PathVariable(Parameter.SHARED_HASH) String sharedHash,
            @ApiParam(required = true, value = "share link information") @RequestBody ShareLinkView shareLinkView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {


        ShareLinkInfoView shareResponse = shareLinkRestMapper.toResponseVO(shareLinkService.updateShareLink(sharedHash, shareLinkRestMapper.toDTO(shareLinkView, null)));
        return generateOkResponse(shareResponse, generateShareLinksHateoas(shareResponse));
    }

    @ApiOperation(value = "Get Shared Presentation XML")
    @GetMapping(value = Endpoint.V1.SHARELINKS_BASE + Parameter.Path.SHARED_HASH + Endpoint.V1.SHARELINK_PRESENTATION, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid or has expired"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<byte[]> downloadShareLinkPresentationXML(@PathVariable(Parameter.SHARED_HASH) String shareHash,
                                                                   @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return shareLinkService.downloadShareLinkPresentationXML(shareHash);
    }

    @ApiOperation(value = "Get Shared Presentation PDF")
    @GetMapping(value = Endpoint.V1.SHARELINKS_BASE + Parameter.Path.SHARED_HASH + Endpoint.V1.SHARELINK_PRESENTATION, produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid or has expired"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<ByteArrayResource> downloadShareLinkPresentationPDF(@PathVariable(Parameter.SHARED_HASH) String shareHash,
                                                                              @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return shareLinkService.downloadShareLinkPresentationPDF(shareHash);
    }


    @ApiOperation(value = "Get verification from a credential XML")
    @GetMapping(value = Endpoint.V1.SHARELINKS_BASE + Parameter.Path.SHARED_HASH + Endpoint.V1.VERIFY,
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


    private Link[] generateShareLinksHateoas(ShareLinkInfoView shareResponse) {

        if (shareResponse != null) {

            Link hateoasSelf = ControllerLinkBuilder.linkTo(ShareLinkResource.class).slash(Endpoint.V1.SHARELINKS_BASE)
                    .slash(shareResponse.getShareHash()).withSelfRel(); //TODO: MediaType ContentType

            Link hateoasViewer = new Link(walletConfigService.getString(EDCIWalletConstants.CONFIG_PROPERTY_VIEWER_SHARED_URL)
                    .replaceAll(Parameter.SHARED_HASH, shareResponse.getShareHash()), "view");

            Link hateoasPresentation = ControllerLinkBuilder.linkTo(ShareLinkResource.class).slash(Endpoint.V1.SHARELINKS_BASE)
                    .slash(shareResponse.getShareHash()).slash(Endpoint.V1.SHARELINK_PRESENTATION).withRel("presentation");

            return new Link[]{
                    hateoasSelf, hateoasViewer, hateoasPresentation
            };

        } else {
            return null;
        }
    }
}
