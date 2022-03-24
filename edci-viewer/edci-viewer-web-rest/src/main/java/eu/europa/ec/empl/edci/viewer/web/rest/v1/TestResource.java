package eu.europa.ec.empl.edci.viewer.web.rest.v1;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller(value = "v1.TestResource")
@Api(description = "TestResource", tags = {
        "V1"
})
@RequestMapping(EDCIConstants.Version.V1 + "/test")
@ResponseStatus(HttpStatus.OK)
public class TestResource {

    @ApiOperation(value = "Dummy test resource for Swagger implementation")
    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSomething() {
        return "hello eUI V1";
    }

}