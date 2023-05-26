package eu.europa.ec.empl.edci.viewer.web.rest.v1;


import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.security.model.mapper.UserRestMapper;
import eu.europa.ec.empl.edci.security.model.view.UserDetailsView;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import eu.europa.ec.empl.edci.viewer.common.constants.Parameter;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerEndpoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api(tags = {
        "V1"
})
@Controller(value = "v1.UserResource")
@RequestMapping(value = EDCIConstants.Version.V1 + ViewerEndpoint.V1.USER_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@Deprecated
public class UserResource {

    @Autowired
    private EDCIUserService userService;

    @Autowired
    private UserRestMapper userRestMapper;

    @ApiOperation(value = "Gets user details or isAuthenticated:false if not authenticated")
    @GetMapping(value = ViewerEndpoint.V1.USER_DETAILS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserDetailsView getUserDetails(
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return userRestMapper.toVO(userService.getUserInfo());
    }

}
