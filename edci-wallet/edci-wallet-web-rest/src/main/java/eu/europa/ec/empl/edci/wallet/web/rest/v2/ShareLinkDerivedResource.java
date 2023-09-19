package eu.europa.ec.empl.edci.wallet.web.rest.v2;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.wallet.common.constants.Parameter;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletEndpoint;
import eu.europa.ec.empl.edci.wallet.service.ShareLinkService;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api(tags = {
        "V2" + WalletEndpoint.V2.SHARELINKS_BASE
}, description = "ShareLink other opertaions API")
@Controller(value = "v2.ShareLinkDerivedResource")
@RequestMapping(value = EDCIConstants.Version.V2)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class ShareLinkDerivedResource implements CrudResource {

    private static final Logger logger = LogManager.getLogger(ShareLinkDerivedResource.class);

    @Autowired
    private ShareLinkService shareLinkService;

    //TODO vp: why delete this?
    @ApiOperation(value = "Download Shared Link Credential")
    @GetMapping(value = WalletEndpoint.V2.SHARELINKS_BASE + Parameter.Path.SHARED_HASH + WalletEndpoint.V2.CREDENTIALS_BASE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid or has expired"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<byte[]> getCredentialFromSharelink(@PathVariable(Parameter.SHARED_HASH) String sharedURL,
                                                             @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return shareLinkService.downloadShareLinkCredential(sharedURL);
    }

    @ApiOperation(value = "Get Shared Presentation PDF")
    @GetMapping(value = WalletEndpoint.V2.SHARELINKS_BASE + Parameter.Path.SHARED_HASH + WalletEndpoint.V2.SHARELINK_PRESENTATION, produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid or has expired"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<ByteArrayResource> downloadShareLinkPresentationPDF(@PathVariable(Parameter.SHARED_HASH) String shareHash,
                                                                              @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        String pdfType = "diploma";
        return shareLinkService.downloadShareLinkPresentationPDF(shareHash, pdfType);
    }

}
