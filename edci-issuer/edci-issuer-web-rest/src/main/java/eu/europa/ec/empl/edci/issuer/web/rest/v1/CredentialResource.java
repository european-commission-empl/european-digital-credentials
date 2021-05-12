package eu.europa.ec.empl.edci.issuer.web.rest.v1;

import eu.europa.ec.empl.edci.constants.Version;
import eu.europa.ec.empl.edci.issuer.common.constants.Endpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.issuer.service.CredentialService;
import eu.europa.ec.empl.edci.issuer.service.FileService;
import eu.europa.ec.empl.edci.issuer.service.spec.AssessmentSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.EuropassCredentialSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.LearningAchievementSpecService;
import eu.europa.ec.empl.edci.issuer.web.mapper.CredentialRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.FileRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.StatusRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialFileUploadResponseView;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialView;
import eu.europa.ec.empl.edci.issuer.web.model.StatusView;
import eu.europa.ec.empl.edci.issuer.web.model.data.IssueBuildCredentialView;
import eu.europa.ec.empl.edci.issuer.web.model.signature.SignatureBytesView;
import eu.europa.ec.empl.edci.issuer.web.model.signature.SignatureNexuView;
import eu.europa.ec.empl.edci.issuer.web.model.signature.SignatureParametersView;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.List;


@Api(tags = {
        "V1"
})
@Controller(value = "v1.CredentialResource")
@RequestMapping(value = Version.V1 + Endpoint.V1.CREDENTIALS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class CredentialResource { //extends AbstractBaseResource { //TODO: use datamodel class

    @Autowired
    private FileRestMapper fileRestMapper;

    @Autowired
    private FileService dynamicFileService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private CredentialRestMapper credentialRestMapper;

    @Autowired
    private StatusRestMapper statusRestMapper;

    @Autowired
    private CredentialMapper dynamicCredentialMapper;

    @Autowired
    private EuropassCredentialSpecService credentialSpecService;

    @Autowired
    private AssessmentSpecService assessmentSpecService;

    @Autowired
    private LearningAchievementSpecService learningAchievementSpecService;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;


    @ApiOperation(value = "Upload some credentials in XML")
    @PostMapping(value = Endpoint.V1.CREDENTIALS_UPLOAD,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CredentialFileUploadResponseView addCredentials(@ApiParam(required = true, value = "The XML file containing various the credentials") @RequestPart(Parameter.FILE) MultipartFile file,
                                                           @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return fileRestMapper.toFileUploadVO(credentialService.uploadCredentials(file, locale));
    }

    @ApiOperation(value = "Seal a list of credentials with nexu data")
    @PostMapping(value = Endpoint.V1.CREDENTIALS_SEAL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CredentialView> sealCredentials(@RequestBody List<SignatureNexuView> signaturesNexuView,
                                                @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialRestMapper.toVOList(credentialService.signCredential(credentialRestMapper.toDTOSignatureList(signaturesNexuView)));
    }

    @ApiOperation(value = "Delete credential based on uuid")
    @DeleteMapping(value = Endpoint.V1.ROOT + Parameter.Path.UUID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public StatusView deleteCredentials(@PathVariable(Parameter.UUID) String uuid,
                                        @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return statusRestMapper.toVO(credentialService.deleteCredentials(uuid));
    }

    @ApiOperation(value = "Download a credential XML file", response = File.class)
    @GetMapping(value = Endpoint.V1.ROOT + Parameter.Path.UUID,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> downloadCredential(@PathVariable(Parameter.UUID) String uuid,
                                                     @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialService.downloadFile(uuid);
    }

    @ApiOperation("Send credentials to owners via email and to their wallet addresses (TBD)")
    @PostMapping(value = Endpoint.V1.CREDENTIALS_SEND,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CredentialView> sendCredentials(@RequestBody List<CredentialView> credentialViewList,
                                                @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialRestMapper.toVOList(credentialService.sendCredentials(credentialRestMapper.toDTOList(credentialViewList)));
    }

    @ApiOperation("Get Bytes from Signature parameters")
    @PostMapping(value = Endpoint.V1.CREDENTIALS_BYTES,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SignatureBytesView> getSignatureBytes(@RequestBody SignatureParametersView signatureParametersView,
                                                      @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialRestMapper.toVOByteList(credentialService.getSignatureBytes(credentialRestMapper.toDTO(signatureParametersView)));
    }

    @ApiOperation(value = "Issue a credential")
    @PostMapping(value = Endpoint.V1.CREDENTIALS_ISSUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CredentialFileUploadResponseView issueCredential(@RequestBody @Valid IssueBuildCredentialView credentialView,
                                                            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        return fileRestMapper.toFileUploadVO(credentialService.issueCredentials(credentialRestMapper.toDTO(credentialView), locale));
    }

    @ApiOperation(value = "Get a Test Credential for Nexus Signature", response = File.class)
    @GetMapping(value = Endpoint.V1.TEST_CREDENTIAL, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> getTestCredential() {
        return new ResponseEntity<byte[]>(credentialService.getTestCredential(), prepareHttpHeadersForCredentialDownload("testCredential.xml", MediaType.APPLICATION_XML_VALUE), HttpStatus.OK);
    }

    public HttpHeaders prepareHttpHeadersForCredentialDownload(String fileName, String mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
        return httpHeaders;
    }
}
