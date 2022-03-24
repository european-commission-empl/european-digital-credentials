package eu.europa.ec.empl.edci.viewer.web.rest.v1;


import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.view.EuropassCredentialPresentationView;
import eu.europa.ec.empl.edci.datamodel.view.VerificationCheckView;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.mapper.EuropassCredentialPresentationMapper;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.viewer.common.constants.Parameter;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerEndpoint;
import eu.europa.ec.empl.edci.viewer.service.ShareLinkDetailService;
import eu.europa.ec.empl.edci.viewer.service.ShareLinkService;
import eu.europa.ec.empl.edci.viewer.web.mapper.EuropassCredentialDetailRestMapper;
import eu.europa.ec.empl.edci.viewer.web.model.EuropassDiplomaView;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Api(tags = {
        "V1"
})
@Controller(value = "v1.ShareLinkResource")
@RequestMapping(value = EDCIConstants.Version.V1 + ViewerEndpoint.V1.SHARELINKS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class ShareLinkResource {

    //    private final static Logger logger = LoggerFactory.getLogger(ShareLinkResource.class);
    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ShareLinkResource.class);

    @Autowired
    private EuropassCredentialPresentationMapper europassCredentialPresentationMapper;

    @Autowired
    private EuropassCredentialDetailRestMapper europassCredentialDetailRestMapper;

    @Autowired
    private ShareLinkDetailService shareLinkDetailService;

    @Autowired
    private ShareLinkService shareLinkService;

    @Autowired
    private Validator validator;

    @ApiOperation(value = "Get visual representation of a diploma from a ShareLink")
    @GetMapping(value = Parameter.Path.SHARE_HASH + ViewerEndpoint.V1.DIPLOMA,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public EuropassDiplomaView getSharedCredentialDiploma(@PathVariable(Parameter.SHARE_HASH) String sharedLink,
                                                          @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return europassCredentialDetailRestMapper.toVO(shareLinkDetailService.getSharedCredentialDiploma(sharedLink, locale));
    }


    @ApiOperation(value = "Get Details of a shared credential")
    @GetMapping(value = Parameter.Path.SHARE_HASH + ViewerEndpoint.V1.DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EuropassCredentialPresentationView getSharedCredentialDetails(@PathVariable(Parameter.SHARE_HASH) String shareHash,
                                                                         @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        if (validator.isEmpty(locale)) locale = EDCIConstants.DEFAULT_LOCALE;
        return europassCredentialPresentationMapper.toEuropassCredentialPresentationView(shareLinkDetailService.getSharedCredentialDetail(shareHash), false);
    }

    @ApiOperation(value = "Get Shared Presentation XML")
    @GetMapping(value = Parameter.Path.SHARE_HASH + ViewerEndpoint.V1.SHARELINK_PRESENTATION, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid or has expired"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<byte[]> downloadShareLinkPresentationXML(@PathVariable(Parameter.SHARE_HASH) String shareHash,
                                                                   @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return shareLinkService.downloadShareLinkPresentationXML(shareHash);
    }

    @ApiOperation(value = "Get Shared Presentation PDF")
    @GetMapping(value = Parameter.Path.SHARE_HASH + ViewerEndpoint.V1.SHARELINK_PRESENTATION, produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid or has expired"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public ResponseEntity<ByteArrayResource> downloadShareLinkPresentationPDF(@PathVariable(Parameter.SHARE_HASH) String shareHash,
                                                                              @ApiParam(required = false, value = "The information that we want into the PDF: full/diploma. By default Diploma", defaultValue = "diploma") @RequestParam(value = Parameter.PDF_TYPE, required = false) String pdfType,
                                                                              @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return shareLinkService.downloadShareLinkPresentationPDF(shareHash, pdfType);
    }

    @ApiOperation(value = "Get verification from a credential XML")
    @GetMapping(value = Parameter.Path.SHARE_HASH + ViewerEndpoint.V1.VERIFY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Wallet not Found"),
            @ApiResponse(code = 403, response = ApiErrorMessage.class, message = "The link is invalid or has expired"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "There's been an unexpected error")
    })
    public List<VerificationCheckView> getShareLinkCredentialVerification(@PathVariable(Parameter.SHARE_HASH) String shareHash,
                                                                          @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws IOException {

        return shareLinkService.getShareLinkVerification(shareHash);

    }

}
