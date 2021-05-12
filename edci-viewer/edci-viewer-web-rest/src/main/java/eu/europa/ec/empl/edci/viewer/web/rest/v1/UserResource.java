package eu.europa.ec.empl.edci.viewer.web.rest.v1;


import eu.europa.ec.empl.edci.constants.Version;
import eu.europa.ec.empl.edci.security.model.mapper.UserRestMapper;
import eu.europa.ec.empl.edci.security.model.view.UserDetailsView;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import eu.europa.ec.empl.edci.viewer.common.constants.Endpoint;
import eu.europa.ec.empl.edci.viewer.common.constants.Parameter;
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
@RequestMapping(value = Version.V1 + Endpoint.V1.USER_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class UserResource {

    @Autowired
    private EDCIUserService userService;

    @Autowired
    private UserRestMapper userRestMapper;

    @ApiOperation(value = "Gets user details or isAuthenticated:false if not authenticated")
    @GetMapping(value = Endpoint.V1.USER_DETAILS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserDetailsView getUserDetails(
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return userRestMapper.toVO(userService.getUserInfo());
    }

}
