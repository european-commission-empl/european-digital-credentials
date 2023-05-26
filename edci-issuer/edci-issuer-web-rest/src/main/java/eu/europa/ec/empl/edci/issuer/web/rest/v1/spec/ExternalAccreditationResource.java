package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.service.CredentialService;
import eu.europa.ec.empl.edci.issuer.web.mapper.CredentialRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.SimpleIdView;
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
@Controller(value = "v1.ExternalAccreditationResource")
@RequestMapping(value = EDCIConstants.Version.V1)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class ExternalAccreditationResource {

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private QMSAccreditationsService qmsAccreditationsService;

    @Autowired
    private CredentialRestMapper credentialRestMapper;

    @ApiOperation(value = "Returns true if an accreditation exists given an Id", response = Boolean.class)
    @ResponseBody
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "No accreditation exists with the given Id"),
            @ApiResponse(code = 503, response = ApiErrorMessage.class, message = "the service to retrieve the accreditations is not available at the moment")
    })
    @PostMapping(value = IssuerEndpoint.V1.ACCREDITATIONS_BASE + IssuerEndpoint.V1.ACCREDITATION_CHECK,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean isAccreditationValid(@ApiParam(value = "accreditation ID") @Valid @RequestBody SimpleIdView accreditationId,
                                                      @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        Boolean returnValue = false;
        if (qmsAccreditationsService.getAccreditation(URI.create(StringUtils.trim(accreditationId.getId()))) != null) {
            returnValue = true;
        } else {
            throw new EDCIException(HttpStatus.NOT_FOUND, ErrorCode.ACCREDITATION_NOT_FOUND);
        }/*else if (accreditationId.getId().contains("unavailable")) {
            throw new EDCIException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.UNDEFINED); //TODO: set Error code
        } else {
            throw new EDCINotFoundException(ErrorCode.UNDEFINED); //TODO: set Error code

        }*/
        return returnValue;
    }

    public CredentialService getCredentialService() {
        return credentialService;
    }

    public void setCredentialService(CredentialService credentialService) {
        this.credentialService = credentialService;
    }
}
