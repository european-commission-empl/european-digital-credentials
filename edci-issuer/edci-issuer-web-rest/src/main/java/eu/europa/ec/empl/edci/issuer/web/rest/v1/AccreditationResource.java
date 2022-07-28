package eu.europa.ec.empl.edci.issuer.web.rest.v1;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.service.CredentialService;
import eu.europa.ec.empl.edci.issuer.web.mapper.CredentialRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.SimpleIdView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.AccreditationDCView;
import eu.europa.ec.empl.edci.service.QMSAccreditationsService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;


@Api(tags = {
        "V1"
})
@Controller(value = "v1.AccreditationResource")
@RequestMapping(value = EDCIConstants.Version.V1)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class AccreditationResource {

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private QMSAccreditationsService qmsAccreditationsService;

    @Autowired
    private CredentialRestMapper credentialRestMapper;

    @ApiOperation(value = "Retrieves an accreditation given an Id", response = AccreditationDCView.class)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "No accreditation exists with the given Id"),
            @ApiResponse(code = 503, response = ApiErrorMessage.class, message = "the service to retrieve the accreditations is not available at the moment")
    })
    @PostMapping(value = IssuerEndpoint.V1.ACCREDITATION_BASE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AccreditationDCView getAccreditation(@ApiParam(value = "accreditation ID") @Valid @RequestBody SimpleIdView accreditationId,
                                                @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        if (qmsAccreditationsService.getAccreditation(URI.create(StringUtils.trim(accreditationId.getId()))) != null) {
            AccreditationDCView obj = new AccreditationDCView();
            obj.setId(URI.create(StringUtils.trim(accreditationId.getId())));
            return obj;
        } else {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.ACCREDITATION_NOT_FOUND);
        }/*else if (accreditationId.getId().contains("unavailable")) {
            throw new EDCIException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.UNDEFINED); //TODO: set Error code
        } else {
            throw new EDCINotFoundException(ErrorCode.UNDEFINED); //TODO: set Error code

        }*/
    }

    public CredentialService getCredentialService() {
        return credentialService;
    }

    public void setCredentialService(CredentialService credentialService) {
        this.credentialService = credentialService;
    }
}
