package eu.europa.ec.empl.edci.issuer.web.rest.v1;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.dss.service.certificate.ESealCertificateService;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessageKeys;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.issuer.common.model.EuropeanDigitalCredentialUploadDTO;
import eu.europa.ec.empl.edci.issuer.common.model.EuropeanDigitalCredentialUploadResultDTO;
import eu.europa.ec.empl.edci.issuer.common.model.LocalSignatureRequestDTO;
import eu.europa.ec.empl.edci.issuer.common.model.customization.CustomizedRecipientsDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.issuer.service.CredentialService;
import eu.europa.ec.empl.edci.issuer.service.IssuerCustomizableModelService;
import eu.europa.ec.empl.edci.issuer.service.IssuerCustomizedModelService;
import eu.europa.ec.empl.edci.issuer.service.IssuerFileService;
import eu.europa.ec.empl.edci.issuer.service.spec.AssessmentSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.EuropassCredentialSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.LearningAchievementSpecService;
import eu.europa.ec.empl.edci.issuer.util.CertificateUtils;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.issuer.web.mapper.*;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialDownloadView;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialFileUploadResponseView;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialView;
import eu.europa.ec.empl.edci.issuer.web.model.StatusView;
import eu.europa.ec.empl.edci.issuer.web.model.customization.CustomizedRecipientsView;
import eu.europa.ec.empl.edci.issuer.web.model.data.AccreditationLiteView;
import eu.europa.ec.empl.edci.issuer.web.model.signature.LocalSignatureRequestView;
import eu.europa.ec.empl.edci.issuer.web.model.signature.SignatureBytesView;
import eu.europa.ec.empl.edci.issuer.web.model.signature.SignatureNexuView;
import eu.europa.ec.empl.edci.issuer.web.model.signature.SignatureParametersView;
import eu.europa.ec.empl.edci.model.external.mapper.QDRAccreditationMapper;
import eu.europa.ec.empl.edci.model.external.qdr.QDRAccreditationDTO;
import eu.europa.ec.empl.edci.service.AccreditationExternalService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Api(tags = {
        "V1"
})
@Controller(value = "v1.CredentialResource")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.CREDENTIALS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class CredentialResource { //extends AbstractBaseResource { //TODO: use datamodel class

    @Autowired
    private FileRestMapper fileRestMapper;

    @Autowired
    private AccreditationExternalService accreditationExternalService;

    @Autowired
    private AccreditationRestMapper accreditationRestMapper;

    @Autowired
    private QDRAccreditationMapper qdrAccreditationMapper;

    @Autowired
    private IssuerFileService dynamicFileService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private CredentialRestMapper credentialRestMapper;

    @Autowired
    private StatusRestMapper statusRestMapper;

    @Autowired
    private CredentialMapper dynamicCredentialMapper;

    @Autowired
    private CustomizationRestMapper customizationRestMapper;

    @Autowired
    private EuropassCredentialSpecService credentialSpecService;

    @Autowired
    private AssessmentSpecService assessmentSpecService;

    @Autowired
    private LearningAchievementSpecService learningAchievementSpecService;


    @Autowired
    private IssuerCustomizableModelService issuerCustomizableModelService;

    @Autowired
    private IssuerCustomizedModelService issuerCustomizedModelService;

    @Autowired
    private ESealCertificateService dssedciCertificateService;

    @Autowired
    private CertificateUtils certificateUtils;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private EDCIMessageService edciMessageService;

    @ApiOperation(value = "Upload some credentials in JSON")
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_UPLOAD,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CredentialFileUploadResponseView addCredentials(@ApiParam(required = true, value = "the Json file containing the EuropeanDigitalCredentialUpload") @RequestPart(Parameter.FILES) MultipartFile[] files,
                                                           @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        List<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadDTOS = credentialService.obtainCredentials(files);

        if (europeanDigitalCredentialUploadDTOS.stream().anyMatch(EuropeanDigitalCredentialUploadResultDTO::isBadFormat)) {
            throw new EDCIBadRequestException(EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIALS_BAD_FORMAT,
                    europeanDigitalCredentialUploadDTOS.stream()
                            .filter(EuropeanDigitalCredentialUploadResultDTO::isBadFormat)
                            .map(EuropeanDigitalCredentialUploadResultDTO::getFileName).collect(Collectors.joining(",")))
                    .addDescription(europeanDigitalCredentialUploadDTOS.stream()
                            .filter(EuropeanDigitalCredentialUploadResultDTO::isBadFormat)
                            .map(EuropeanDigitalCredentialUploadResultDTO::getBadFormatDesc).collect(Collectors.joining(", ")));
        }


        if (europeanDigitalCredentialUploadDTOS.stream().anyMatch(EuropeanDigitalCredentialUploadResultDTO::isSigned)) {
            throw new EDCIBadRequestException(EDCIIssuerMessageKeys.Sealing.CREDENTIALS_ALREADY_SIGNED,
                    europeanDigitalCredentialUploadDTOS.stream()
                            .filter(EuropeanDigitalCredentialUploadResultDTO::isBadFormat)
                            .map(EuropeanDigitalCredentialUploadResultDTO::getFileName).collect(Collectors.joining(",")));
        }

        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

        return fileRestMapper.toFileUploadVO(credentialService.uploadParsedCredentials(
                europeanDigitalCredentialUploadDTOS.stream().map(edcu -> (EuropeanDigitalCredentialUploadDTO) edcu).collect(Collectors.toList()),
                fileUtil.getCredentialPrivateFolderName(sessionId), locale));
    }

    @ApiOperation(value = "Seal a list of credentials using locally stored cert")
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_SEAL_LOCAL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public List<CredentialView> sealCredentialsLocalCertificate(@RequestBody LocalSignatureRequestView localSignatureRequestViews,
                                                                @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {

        LocalSignatureRequestDTO req = credentialRestMapper.toDTO(localSignatureRequestViews);

        List<CredentialDTO> credentialDTO = req.getCredentialDTO();
        String signOnBehalf = req.getSignOnBehalf();
        String certPassword = req.getCertPassword();
        String sessionId = fileUtil.getCredentialPrivateFolderName(RequestContextHolder.currentRequestAttributes().getSessionId());
        String batchId = UUID.randomUUID().toString().replaceAll("[^A-Za-z0-9]", "");

        return credentialRestMapper.toVOList(this.getCredentialService().signFromLocalCert(credentialDTO, certPassword, signOnBehalf,
                sessionId, batchId, localSignatureRequestViews.getMandatedIssue()));
    }

    @ApiOperation(value = "Seal a list of credentials with nexu data")
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_SEAL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CredentialView> sealCredentials(@RequestBody List<SignatureNexuView> signaturesNexuView,
                                                @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialRestMapper.toVOList(credentialService.signCredential(credentialRestMapper.toDTOSignatureList(signaturesNexuView)));
    }

    @ApiOperation(value = "Delete credential based on uuid")
    @DeleteMapping(value = IssuerEndpoint.V1.ROOT + Parameter.Path.UUID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public StatusView deleteCredentials(@PathVariable(Parameter.UUID) String uuid,
                                        @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return statusRestMapper.toVO(credentialService.deleteCredentials(uuid));
    }

    @ApiOperation(value = "Download a credential XML file", response = File.class)
    @GetMapping(value = IssuerEndpoint.V1.ROOT + Parameter.Path.UUID,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> downloadCredential(@PathVariable(Parameter.UUID) String uuid,
                                                     @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialService.downloadFile(uuid);
    }

    @ApiOperation("Send credentials to owners via email and to their wallet addresses")
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_SEND,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CredentialView> sendCredentials(@RequestBody List<CredentialView> credentialViewList,
                                                @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialRestMapper.toVOList(credentialService.sendCredentials(credentialRestMapper.toDTOList(credentialViewList)));
    }

    @ApiOperation(value = "Download a credential XML file", response = File.class)
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_DOWNLOAD,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadTempCredentials(@RequestBody CredentialDownloadView credentialViewList,
                                                          @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {
        return credentialService.downloadZipFile(credentialViewList.getUuid());
    }

    @ApiOperation("Get Bytes from Signature parameters")
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_BYTES,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SignatureBytesView> getSignatureBytes(@RequestBody SignatureParametersView signatureParametersView,
                                                      @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) {


        return credentialRestMapper.toVOByteList(credentialService.getSignatureBytes(signatureParametersView.getUuids(),
                credentialRestMapper.toDTO(signatureParametersView).getResponse().getCertificate(),
                credentialRestMapper.toDTO(signatureParametersView).getResponse().getCertificateChain(), signatureParametersView.getMandatedIssue()));


    }

    @ApiOperation(value = "Issue a credential from a json object")
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_ISSUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public CredentialFileUploadResponseView issueCredentialsFromRecipientsForm(@RequestBody @Valid CustomizedRecipientsView recipients,
                                                                               @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        CustomizedRecipientsDTO recipientsDTO = customizationRestMapper.toDTO(recipients);

        String ocbId = issuerCustomizedModelService.getCredentialIdFromRecipients(recipientsDTO);

        if (!credentialSpecService.existsByOCBID(ocbId)) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_CREDENTIAL_NOT_FOUND, ocbId);
        }

        EuropassCredentialSpecDAO credentialDAO = credentialSpecService.findByOCBID(ocbId);

        List<EuropeanDigitalCredentialUploadDTO> credentialsDTO = issuerCustomizedModelService.fromCustomToDTO(credentialDAO, recipientsDTO);

        return fileRestMapper.toFileUploadVO(credentialService.issueCredentials(credentialsDTO, locale));
    }

    @ApiOperation(value = "Issue a credential from a xls template file")
    @PostMapping(value = IssuerEndpoint.V1.CREDENTIALS_ISSUE + IssuerEndpoint.V1.TEMPLATES,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public CredentialFileUploadResponseView issueCredentialsFromRecipientsXLS(@ApiParam(required = true, value = "the XLS file with the recipìents information") @RequestPart(Parameter.FILE) MultipartFile file,
                                                                              @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        CustomizedRecipientsDTO recipients = issuerCustomizedModelService.getRecipientsFromXLS(file);

        String ocbID = issuerCustomizedModelService.getCredentialIdFromRecipients(recipients);

        if (!credentialSpecService.existsByOCBID(ocbID)) {
            throw new EDCIException(ErrorCode.CUSTOMIZABLE_CREDENTIAL_NOT_FOUND, ocbID);
        }

        EuropassCredentialSpecDAO credentialDAO = credentialSpecService.findByOCBID(ocbID);

        List<EuropeanDigitalCredentialUploadDTO> credentialsDTO = issuerCustomizedModelService.fromCustomToDTO(credentialDAO, recipients);

        return fileRestMapper.toFileUploadVO(credentialService.issueCredentials(credentialsDTO, locale));

    }

    @ApiOperation(value = "Get accreditation")
    @GetMapping(value = IssuerEndpoint.V1.CREDENTIALS_ACCREDITATION, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 404, response = ApiErrorMessage.class, message = "Accreditation not found"),
            @ApiResponse(code = 500, response = ApiErrorMessage.class, message = "The external service is not responding")
    })
    @ResponseBody
    public List<AccreditationLiteView> getAccreditations(@RequestParam(value = Parameter.URI) String uri, @RequestParam(value = Parameter.LANG) String lang) {
        QDRAccreditationDTO qdrAccreditationDTO = accreditationExternalService.retrieveAccreditationByUri(uri, lang);

        AccreditationDTO accreditationDTO = qdrAccreditationMapper.toAccreditationDTO(qdrAccreditationDTO, qdrAccreditationDTO.getMetadata().getLanguage().getValue());

        return Arrays.asList(accreditationRestMapper.toAccreditationLiteView(accreditationDTO));
    }

    @ApiOperation(value = "Get a Test Credential for Nexus Signature", response = File.class)
    @GetMapping(value = IssuerEndpoint.V1.TEST_CREDENTIAL, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> getTestCredential() {
        return new ResponseEntity<byte[]>(credentialService.getTestCredential(), prepareHttpHeadersForCredentialDownload("testCredential.json", MediaType.APPLICATION_XML_VALUE), HttpStatus.OK);
    }

    public HttpHeaders prepareHttpHeadersForCredentialDownload(String fileName, String mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
        return httpHeaders;
    }

    public CredentialService getCredentialService() {
        return credentialService;
    }

    public void setCredentialService(CredentialService credentialService) {
        this.credentialService = credentialService;
    }
}
