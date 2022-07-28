package eu.europa.ec.empl.edci.issuer.web.rest.v1.open;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicBatchSealingDTO;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicSealingDTO;
import eu.europa.ec.empl.edci.issuer.service.open.CredentialPublicService;
import eu.europa.ec.empl.edci.issuer.web.mapper.CredentialRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.specs.PublicSealAndSendView;
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

@Api(tags = {
        "V1"
})
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
@RequestMapping(EDCIConstants.Version.V1 + IssuerEndpoint.V1.PUBLIC + IssuerEndpoint.V1.CREDENTIALS)
@Controller
public class CredentialPublicResource {

    @Autowired
    private CredentialPublicService credentialPublicService;

    @Autowired
    private CredentialRestMapper credentialRestMapper;


    @ApiOperation(value = "Seal a set of credentials and send them to the configured wallet")
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_SEAL_BATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> batchSealAndSendCredentials(@ApiParam(required = true, value = "the XML files containing the credential") @RequestPart(Parameter.FILES) MultipartFile[] files,
                                                         @ApiParam(required = true, value = "the password for the local certificate") @RequestParam(value = Parameter.PASSWORD) String password,
                                                         @ApiParam(required = false, value = "indicates that the credential should be signed on behalf of another organization") @RequestParam(value = Parameter.SIGN_ON_BEHALF, required = false, defaultValue = "false") boolean signOnBehalf) {
        ApiErrorMessage apiErrorMessage = this.getCredentialPublicService().doBatchSealAndSendCredentials(new PublicBatchSealingDTO(files, password, signOnBehalf));
        if (apiErrorMessage != null) {
            apiErrorMessage.setPath("/api/" + EDCIConstants.Version.V1 + "/" + IssuerEndpoint.V1.PUBLIC + IssuerEndpoint.V1.CREDENTIALS + IssuerEndpoint.V1.CREDENTIALS_SEAL_BATCH);
        }
        return apiErrorMessage == null ? new ResponseEntity<>(HttpStatus.ACCEPTED) : new ResponseEntity<>(apiErrorMessage, this.prepareHttpHeadersForJSON(), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "Seal a credential in XML format and download resulting file. Requiers a configured local certificate.")
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_SEAL,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> sealAndDownloadCredential(@ApiParam(required = true, value = "The XML file containig the credential") @RequestPart(Parameter.FILE) MultipartFile file,
                                                            @ApiParam(required = true, value = "The password for the local certificate ") @RequestParam(value = Parameter.PASSWORD) String password,
                                                            @ApiParam(required = false, value = "indicates that the credential should be signed on behalf of another organization") @RequestParam(value = Parameter.SIGN_ON_BEHALF, required = false, defaultValue = "false") boolean signOnBehalf) {
        PublicSealingDTO publicSealingDTO = new PublicSealingDTO(file, password, signOnBehalf);
        return new ResponseEntity<>(this.getCredentialPublicService().doLocalSignAndDownloadCredential(publicSealingDTO),
                prepareHttpHeadersForCredentialDownload(file.getName(), MediaType.APPLICATION_XML_VALUE),
                HttpStatus.OK);

    }

    @ApiOperation(value = "Seal a credential in XML format, using the locally stored cert, and sent it to the wallet. Requires a configured local certificate.")
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_SEAL_SEND,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PublicSealAndSendView sealAndSendCredential(@ApiParam(required = true, value = "The XML file containing the credential") @RequestPart(Parameter.FILE) MultipartFile file,
                                                       @ApiParam(required = true, value = "The passworf for the local certificate") @RequestParam(value = Parameter.PASSWORD) String password,
                                                       @ApiParam(required = false, value = "indicates that the credential should be signed on behalf of another organization") @RequestParam(value = Parameter.SIGN_ON_BEHALF, required = false, defaultValue = "false") boolean signOnBehalf) {
        PublicSealingDTO publicSealingDTO = new PublicSealingDTO(file, password, signOnBehalf);
        return this.getCredentialRestMapper().toVO(this.getCredentialPublicService().doLocalSignAndSendCredential(publicSealingDTO));

    }

    public HttpHeaders prepareHttpHeadersForCredentialDownload(String fileName, String mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
        return httpHeaders;
    }

    public CredentialPublicService getCredentialPublicService() {
        return credentialPublicService;
    }

    public void setCredentialPublicService(CredentialPublicService credentialPublicService) {
        this.credentialPublicService = credentialPublicService;
    }

    public CredentialRestMapper getCredentialRestMapper() {
        return credentialRestMapper;
    }

    public void setCredentialRestMapper(CredentialRestMapper credentialRestMapper) {
        this.credentialRestMapper = credentialRestMapper;
    }

    protected HttpHeaders prepareHttpHeadersForJSON() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return httpHeaders;
    }
}