package eu.europa.ec.empl.edci.issuer.web.rest.v2;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.issuer.web.mapper.FileRestMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller(value = "model.TestResource")
@RequestMapping(EDCIConstants.Version.V2 + "/test")
@ResponseStatus(HttpStatus.OK)
public class TestResource {
    private final Log logger = LogFactory.getLog(this.getClass());
    private final String fromMail = "xxx@yyy.com";
    private final String fromPass = "zzz";

    @Autowired
    private FileRestMapper fileRestMapper;

    @RequestMapping(value = "/",
            method = RequestMethod.GET)
    @ResponseBody
    public String getSomething() {
        this.logger.trace("entrooooooooo!");
        System.out.println("entro!");
        /*Gson gson = new Gson();
        return gson.toJson(new Hello("fcuk!"));*/
        return "{ \"msg\": \"hello yo!\"}";
    }

}
