package eu.europa.ec.empl.edci.issuer.web.rest.v1;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientFileDTO;
import eu.europa.ec.empl.edci.issuer.service.IssuerFileService;
import eu.europa.ec.empl.edci.issuer.web.mapper.CredentialRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.FileRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.CLElementBasicView;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialFileUploadResponseView;
import eu.europa.ec.empl.edci.issuer.web.model.FileView;
import eu.europa.ec.empl.edci.issuer.web.model.RecipientFileUploadResponseView;
import eu.europa.ec.empl.edci.util.MockFactoryUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Set;

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

    private static final Logger logger = Logger.getLogger(FileResource.class);


    @ApiOperation(value = "Uploads credentials on XLS format")
    @PostMapping(value = IssuerEndpoint.V1.TEMPLATES + IssuerEndpoint.V1.CREDENTIALS,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CredentialFileUploadResponseView uploadCredential(@RequestPart(value = Parameter.FILE) MultipartFile file,
                                                             @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        FileView requestFileView = new FileView(file);
        return fileRestMapper.toFileUploadVO(fileService.uploadCredentialsExcelFile(fileRestMapper.toDTO(requestFileView)));
    }

    @ApiOperation(value = "Upload recipients on XLS format")
    @PostMapping(value = IssuerEndpoint.V1.TEMPLATES + IssuerEndpoint.V1.RECIPIENTS,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RecipientFileUploadResponseView uploadRecipients(@RequestPart(value = Parameter.FILE) MultipartFile file,
                                                            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        RecipientFileDTO recipientFileDTO = new RecipientFileDTO();
        recipientFileDTO.setFile(file);
        return fileRestMapper.toRecipientFileUploadVO(fileService.uploadRecipientsExcelFile(recipientFileDTO));
    }

    @ApiOperation(value = "Downloads an XLS template to add credentials ", response = File.class)
    @GetMapping(value = IssuerEndpoint.V1.TEMPLATES + Parameter.Path.EXCEL_TYPE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getCredentialTemplate(@PathVariable(Parameter.EXCEL_TYPE) String type,
                                                        @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return fileService.downloadTemplate(type);
    }

    @ApiOperation(value = "Lists available XLS templates")
    @GetMapping(value = IssuerEndpoint.V1.TEMPLATES, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<CLElementBasicView> getAvailableTemplates() {
        return fileRestMapper.toVOSet(fileService.getAvailableTemplates());
    }

}
