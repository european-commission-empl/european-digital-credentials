package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.entity.specs.*;
import eu.europa.ec.empl.edci.issuer.service.IssuerCustomizableModelService;
import eu.europa.ec.empl.edci.issuer.service.IssuerFileService;
import eu.europa.ec.empl.edci.issuer.service.spec.*;
import eu.europa.ec.empl.edci.issuer.web.mapper.CustomizationRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.*;
import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.customization.CustomizableInstanceSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.customization.CustomizableSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.AssessmentsListIssueView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.DiplomaSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.EuropassCredentialSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.OrganizationSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.EntitlementSpecLiteView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.EuropassCredentialSpecLiteView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.LearningAchievementSpecLiteView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.LearningActivitySpecLiteView;
import eu.europa.ec.empl.edci.issuer.web.util.ResourceUtil;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import eu.europa.ec.empl.edci.security.EDCISecurityContextHolder;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.Set;


@Api(tags = {
        "V1"
})
@Controller(value = "v1.CredentialSpecResource")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.CREDENTIALS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class EuropassCredentialResource implements CrudResource {

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EuropassCredentialSpecService credentialService;

    @Autowired
    private EuropassCredentialSpecRestMapper credentialSpecRestMapper;

    @Autowired
    private LearningAchievementSpecService learningAchievementSpecService;

    @Autowired
    private DiplomaSpecService diplomaSpecService;

    @Autowired
    private AssessmentSpecRestMapper assessmentSpecRestMapper;

    @Autowired
    private LearningAchievementSpecRestMapper learningAchievementSpecRestMapper;

    @Autowired
    private LearningActivitySpecService learningActivitySpecService;

    @Autowired
    private OrganizationSpecService organizationSpecService;

    @Autowired
    private LearningActivitySpecRestMapper learningActivitySpecRestMapper;

    @Autowired
    private EntitlementSpecService entitlementSpecService;

    @Autowired
    private EntitlementSpecRestMapper entitlementSpecRestMapper;

    @Autowired
    private OrganizationSpecRestMapper organizationSpecRestMapper;

    @Autowired
    private DiplomaSpecRestMapper diplomaSpecRestMapper;

    @Autowired
    private EDCISecurityContextHolder edciUserHolder;

    @Autowired
    private IssuerFileService fileService;

    @Autowired
    private IssuerCustomizableModelService issuerCustomizableModelService;

    @Autowired
    private CustomizationRestMapper customizationMapper;

    @Autowired
    private ResourceUtil resourceUtil;

    @ApiOperation(value = "Create a credential spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<EuropassCredentialSpecView>> createCredential(@RequestBody @Valid EuropassCredentialSpecView credentialView,
                                                                                 @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        final EuropassCredentialSpecDAO credentialDAO = credentialSpecRestMapper.toDAO(credentialView);

        EuropassCredentialSpecDAO credentialCreatedDAO = credentialService.save(credentialDAO,
                () -> {
                    credentialDAO.setAchieved(learningAchievementSpecService.retrieveEntities(false, credentialView.getRelAchieved().getOid()));
                    credentialDAO.setPerformed(learningActivitySpecService.retrieveEntities(false, credentialView.getRelPerformed().getOid()));
                    credentialDAO.setEntitledTo(entitlementSpecService.retrieveEntities(false, credentialView.getRelEntitledTo().getOid()));
                    credentialDAO.setIssuer(retrieveEntity(organizationSpecService, false, credentialView.getRelIssuer().getSingleOid()));
                    credentialDAO.setDisplay(retrieveEntity(diplomaSpecService, false, credentialView.getRelDiploma().getSingleOid()));
                }
        );

        return generateOkResponse(credentialCreatedDAO, credentialSpecRestMapper, generateCredentialHateoas(credentialCreatedDAO));
    }

    @ApiOperation(value = "Update a credential spec")
    @PutMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<EuropassCredentialSpecView>> updateCredential(
            @RequestBody @Valid EuropassCredentialSpecView credentialView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        if (!credentialService.exists(credentialView.getOid())) {
            throw new EDCINotFoundException().addDescription("Credential with.OID [" + credentialView.getOid() + "] not found");
        } else {
            credentialView.setOid(credentialView.getOid());
        }

        final EuropassCredentialSpecDAO credentialDAO = credentialSpecRestMapper.toDAO(credentialView);

        EuropassCredentialSpecDAO credentialCreatedDAO = credentialService.save(credentialDAO,
                () -> {
                    credentialDAO.setAchieved(learningAchievementSpecService.retrieveEntities(false, credentialView.getRelAchieved().getOid()));
                    credentialDAO.setPerformed(learningActivitySpecService.retrieveEntities(false, credentialView.getRelPerformed().getOid()));
                    credentialDAO.setEntitledTo(entitlementSpecService.retrieveEntities(false, credentialView.getRelEntitledTo().getOid()));
                    credentialDAO.setIssuer(retrieveEntity(organizationSpecService, false, credentialView.getRelIssuer().getSingleOid()));
                    credentialDAO.setDisplay(retrieveEntity(diplomaSpecService, false, credentialView.getRelDiploma().getSingleOid()));
                });

        return generateOkResponse(credentialCreatedDAO, credentialSpecRestMapper, generateCredentialHateoas(credentialCreatedDAO));
    }

    @ApiOperation(value = "Duplicate a credential spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<EuropassCredentialSpecView>> duplicateCredential(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EuropassCredentialSpecDAO credentialCreatedDAO = credentialService.clone(oid, credentialSpecRestMapper);

        return generateOkResponse(credentialCreatedDAO, credentialSpecRestMapper, generateCredentialHateoas(credentialCreatedDAO));
    }

    @ApiOperation(value = "Delete a credential spec")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteCredential(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        boolean removed = credentialService.delete(oid);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets a credential")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<EuropassCredentialSpecView>> getCredential(
            @ApiParam(required = true, value = "The Credential.OID") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        return generateOkResponse(credentialDAO, credentialSpecRestMapper, generateCredentialHateoas(credentialDAO));
    }

    @ApiOperation(value = "Gets a list of credentials")
    @GetMapping(value = IssuerEndpoint.V1.SPECS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResources<EuropassCredentialSpecLiteView>> listCredential(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SEARCH, required = false) String search,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
//        public ResponseEntity<Page> listCredential (@ModelAttribute PageParam pageParam) throws Exception { //Swagger expects pageParam to be in body. don't be like Swagger
        PageParam pageParam = new PageParam(page, size, sort, direction);
        Specification specif = buildSearchSpecification(search, credentialSpecRestMapper, edciUserHolder);

        Page<EuropassCredentialSpecLiteView> products = credentialService.findAll(specif, pageParam.toPageRequest(), credentialSpecRestMapper);
        return generateListResponse(products, "/specs");
    }


    @ApiOperation(value = "Gets a list of subresources")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_ACHIEVED,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResources<LearningAchievementSpecLiteView>> listAchieved(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        return generateListResponse(credentialDAO.getAchieved(), "/specs", learningAchievementSpecRestMapper);

    }

    @ApiOperation(value = "Link an existing subresource")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_ACHIEVED,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResources<LearningAchievementSpecLiteView>> setAchieved(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        Set<LearningAchievementSpecDAO> entities = retrieveEntities(learningAchievementSpecService, false, oids.getOid());

        credentialDAO.setAchieved(entities);

        credentialService.save(credentialDAO);

        return generateListResponse(credentialDAO.getAchieved(), "/specs", learningAchievementSpecRestMapper);

    }

    @ApiOperation(value = "Gets a list of subresources")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_PERFORMED,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResources<LearningActivitySpecLiteView>> listPerformed(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        return generateListResponse(credentialDAO.getPerformed(), "/specs", learningActivitySpecRestMapper);

    }

    @ApiOperation(value = "Link an existing subresource")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_PERFORMED,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResources<LearningActivitySpecLiteView>> setPerformed(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        Set<LearningActivitySpecDAO> entities = retrieveEntities(learningActivitySpecService, false, oids.getOid());

        credentialDAO.setPerformed(entities);

        credentialService.save(credentialDAO);

        return generateListResponse(credentialDAO.getPerformed(), "/specs", learningActivitySpecRestMapper);

    }

    @ApiOperation(value = "Gets a list of subresources")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_ENTITLED_TO,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResources<EntitlementSpecLiteView>> listEntitledTo(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        return generateListResponse(credentialDAO.getEntitledTo(), "/specs", entitlementSpecRestMapper);

    }

    @ApiOperation(value = "Link an existing subresource")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_ENTITLED_TO,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResources<EntitlementSpecLiteView>> setEntitledTo(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        Set<EntitlementSpecDAO> entities = retrieveEntities(entitlementSpecService, false, oids.getOid());

        credentialDAO.setEntitledTo(entities);

        credentialService.save(credentialDAO);

        return generateListResponse(credentialDAO.getEntitledTo(), "/specs", entitlementSpecRestMapper);

    }

    @ApiOperation(value = "Link an existing related organization to a credential")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_ISSUER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<OrganizationSpecView>> setIssuer(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        OrganizationSpecDAO entitiy = retrieveEntity(organizationSpecService, false, oids.getSingleOid());

        credentialDAO.setIssuer(entitiy);

        credentialService.save(credentialDAO);

        return generateOkResponse(credentialDAO.getIssuer(), organizationSpecRestMapper, generateCredentialHateoas(credentialDAO));
    }

    @ApiOperation(value = "Deletes the credential issuer")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_ISSUER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteIssuer(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        credentialDAO.setIssuer(null);

        credentialService.save(credentialDAO);

        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets the credential issuer")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_ISSUER,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<OrganizationSpecView>> getIssuer(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        return generateOkResponse(credentialDAO.getIssuer(), organizationSpecRestMapper, generateCredentialHateoas(credentialDAO));

    }

    @ApiOperation(value = "Gets all the assessments for the issue form")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_ISSUE_ASSESSMENTS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<AssessmentsListIssueView>> getIssuerAssessmentGrades(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        Set<AssessmentSpecDAO> assessments = credentialService.getCredentialAssessments(oid);
        AssessmentsListIssueView assmView = assessmentSpecRestMapper.toVOListIssueView(assessments);
        return generateOkResponse(assmView);

    }

    @ApiOperation(value = "Link an existing diploma to a credential")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_DIPLOMA,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<DiplomaSpecView>> setDiploma(
            @ApiParam(required = true, value = "The Diploma oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        DiplomaSpecDAO entitiy = retrieveEntity(diplomaSpecService, false, oids.getSingleOid());

        credentialDAO.setDisplay(entitiy);

        credentialService.save(credentialDAO);

        return generateOkResponse(credentialDAO.getDisplay(), diplomaSpecRestMapper, generateCredentialHateoas(credentialDAO));
    }

    @ApiOperation(value = "Deletes an existing linked diploma to a credential")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_DIPLOMA,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteDiploma(
            @ApiParam(required = true, value = "The Diploma oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        credentialDAO.setDisplay(null);

        credentialService.save(credentialDAO);

        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets the credential's diploma")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.CRED_DIPLOMA,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<DiplomaSpecView>> getDiploma(
            @ApiParam(required = true, value = "The Diploma oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        return generateOkResponse(credentialDAO.getDisplay(), diplomaSpecRestMapper, generateCredentialHateoas(credentialDAO));

    }

    @ApiOperation(value = "Generates and returns a list of the current available customizable fields")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + IssuerEndpoint.V1.RECIPIENTS + IssuerEndpoint.V1.FIELDS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<CustomizableSpecView>> getFullCustomizableSpec(
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        return generateOkResponse(customizationMapper.toVO(issuerCustomizableModelService.getFullCustomizableSpecList(), edciMessageService));
    }


    @ApiOperation(value = "Generate the customizable spec for a particular credential and Customizable spec selection")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + IssuerEndpoint.V1.RECIPIENTS + IssuerEndpoint.V1.FIELDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource<CustomizableInstanceSpecView>> getCredentialSpec(
            @ApiParam(value = "oid") @RequestParam(value = Parameter.OID, required = true) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale,
            @RequestBody CustomizableSpecView customizableSpecView) {
        return generateOkResponse(customizationMapper.toVO(issuerCustomizableModelService.getCustomizableInstanceSpec(customizationMapper.toDTO(customizableSpecView), oid), edciMessageService));
    }

    @ApiOperation(value = "Downloads an XLS Customizable templat", response = File.class)
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.RECIPIENTS + IssuerEndpoint.V1.TEMPLATES,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getCredentialSpecXLSTemplate(@PathVariable(Parameter.OID) Long oid,
                                                               @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale,
                                                               @RequestBody CustomizableSpecView customizableSpecView) {

        EuropassCredentialSpecDAO credentialDAO = credentialService.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        String fileName = credentialDAO.getIdentifiableName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_").concat(".xls");
        return this.generateResponse(
                issuerCustomizableModelService.getCustomizableXLSTemplate(customizationMapper.toDTO(customizableSpecView), oid)
                , HttpStatus.OK
                , this.resourceUtil.prepareHttpHeadersForXLSFileDownload(fileName));
    }


    public Link[] generateCredentialHateoas(EuropassCredentialSpecDAO credentialDAO) {

        Link hateoasSelf = ControllerLinkBuilder.linkTo(EuropassCredentialResource.class).slash(IssuerEndpoint.V1.SPECS).slash(credentialDAO.getHashCodeSeed()).withSelfRel();

        Link hateoasLearningAchievements = ControllerLinkBuilder.linkTo(EuropassCredentialResource.class)
                .slash(IssuerEndpoint.V1.SPECS).slash(credentialDAO.getHashCodeSeed())
                .slash(IssuerEndpoint.V1.CRED_ACHIEVED).withRel("achieved");

        Link hateoasLearningActivities = ControllerLinkBuilder.linkTo(EuropassCredentialResource.class)
                .slash(IssuerEndpoint.V1.SPECS).slash(credentialDAO.getHashCodeSeed())
                .slash(IssuerEndpoint.V1.CRED_PERFORMED).withRel("performed");

        Link hateoasLearningAEntitlements = ControllerLinkBuilder.linkTo(EuropassCredentialResource.class)
                .slash(IssuerEndpoint.V1.SPECS).slash(credentialDAO.getHashCodeSeed())
                .slash(IssuerEndpoint.V1.CRED_ENTITLED_TO).withRel("entitledTo");

        Link hateoasIssuer = ControllerLinkBuilder.linkTo(EuropassCredentialResource.class)
                .slash(IssuerEndpoint.V1.SPECS).slash(credentialDAO.getHashCodeSeed())
                .slash(IssuerEndpoint.V1.CRED_ISSUER).withRel("issuer");

        Link hateoasDiploma = ControllerLinkBuilder.linkTo(EuropassCredentialResource.class)
                .slash(IssuerEndpoint.V1.SPECS).slash(credentialDAO.getHashCodeSeed())
                .slash(IssuerEndpoint.V1.CRED_DIPLOMA).withRel("diploma");

        return new Link[]{
                hateoasSelf, hateoasLearningAchievements, hateoasLearningActivities, hateoasLearningAEntitlements, hateoasIssuer, hateoasDiploma
        };
    }
}


