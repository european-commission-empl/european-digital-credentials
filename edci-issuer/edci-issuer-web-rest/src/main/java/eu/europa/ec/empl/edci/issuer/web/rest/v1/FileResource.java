package eu.europa.ec.empl.edci.issuer.web.rest.v1;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.service.IssuerFileService;
import eu.europa.ec.empl.edci.issuer.web.mapper.CredentialRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.FileRestMapper;
import eu.europa.ec.empl.edci.util.MockFactoryUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;

@Api(tags = {
        "V1"
})
@Controller(value = "v1.FileResource")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.FILES_BASE)
//@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class FileResource {

    @Autowired
    private IssuerFileService fileService;

    @Autowired
    private FileRestMapper fileRestMapper;

    @Autowired
    private CredentialRestMapper credentialRestMapper;

    @Autowired
    private MockFactoryUtil mockFactoryUtil;

    private static final Logger logger = LogManager.getLogger(FileResource.class);


    @ApiOperation(value = "Downloads an XLS template to add credentials ", response = File.class)
    @GetMapping(value = IssuerEndpoint.V1.TEMPLATES + Parameter.Path.EXCEL_TYPE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getCredentialTemplate(@PathVariable(Parameter.EXCEL_TYPE) String type,
                                                        @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return fileService.downloadTemplate(type);
    }

}
