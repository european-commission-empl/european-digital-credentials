package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.AwardingProcessDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAssessmentSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.issuer.service.spec.*;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.AssessmentSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.LearningAchievementSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.LearningOutcomeSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.OrganizationSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.specs.AssessmentSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.AssessmentSpecLiteView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.OrganizationSpecLiteView;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import eu.europa.ec.empl.edci.security.EDCISecurityContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;


@Api(tags = {
        "V1"
})
@Controller(value = "v1.AssessmentSpecResource")
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.ASSESSMENTS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class AssessmentResource implements CrudResource {

    @Autowired
    private AssessmentSpecService assessmentService;

    @Autowired
    private AssessmentSpecRestMapper assessmentSpecRestMapper;

    @Autowired
    private LearningAchievementSpecService learningAchievementService;

    @Autowired
    private LearningAchievementSpecRestMapper learningAchievementSpecRestMapper;

    @Autowired
    private AssessmentSpecService assessmentSpecService;

    @Autowired
    private EntitlementSpecService entitlementSpecService;

    @Autowired
    private OrganizationSpecService organizationSpecService;

    @Autowired
    private OrganizationSpecRestMapper organizationSpecRestMapper;

    @Autowired
    private LearningOutcomeSpecService learningOutcomeSpecService;

    @Autowired
    private LearningOutcomeSpecRestMapper learningOutcomeSpecRestMapper;

    @Autowired
    private EDCISecurityContextHolder edciUserHolder;

    @ApiOperation(value = "Create an assessment spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<AssessmentSpecView>> createAssessment(
            @RequestBody @Valid AssessmentSpecView assessmentView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        final LearningAssessmentSpecDAO assessmentDAO = assessmentSpecRestMapper.toDAO(assessmentView);

        LearningAssessmentSpecDAO assessmentCreatedDAO = assessmentService.save(assessmentDAO,
                () -> {
                    assessmentDAO.setHasPart(assessmentService.retrieveEntities(false, assessmentView.getRelHasPart().getOid()));
                    assessmentDAO.setAssessedBy(organizationSpecService.retrieveEntities(false, assessmentView.getRelAssessedBy().getOid()));
                    assessmentDAO.setAwardedBy(assessmentDAO.getAwardedBy() == null ? new AwardingProcessDCDAO() : assessmentDAO.getAwardedBy());
                    assessmentDAO.getAwardedBy().setAwardingBody(organizationSpecService.retrieveEntities(false, assessmentView.getRelAwardingBody().getOid()));
                }
        );

        return generateOkResponse(assessmentCreatedDAO, assessmentSpecRestMapper, generateAssessmentHateoas(assessmentCreatedDAO));
    }

    @ApiOperation(value = "Duplicate an activity spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<AssessmentSpecView>> duplicateAssessment(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAssessmentSpecDAO assessmentDAO = assessmentService.clone(oid, assessmentSpecRestMapper);

        return generateOkResponse(assessmentDAO, assessmentSpecRestMapper, generateAssessmentHateoas(assessmentDAO));
    }

    @ApiOperation(value = "Update an assessment spec")
    @PutMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<AssessmentSpecView>> updateAssessment(
            @RequestBody @Valid AssessmentSpecView assessmentView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        if (!assessmentService.exists(assessmentView.getOid())) {
            throw new EDCINotFoundException().addDescription("Assessment with.OID [" + assessmentView.getOid() + "] not found");
        } else {
            assessmentView.setOid(assessmentView.getOid());
        }

        final LearningAssessmentSpecDAO assessmentDAO = assessmentSpecRestMapper.toDAO(assessmentView);

        LearningAssessmentSpecDAO assessmentCreatedDAO = assessmentService.save(assessmentDAO,
                () -> {
                    assessmentDAO.setHasPart(assessmentService.retrieveEntities(false, assessmentView.getRelHasPart().getOid()));
                    assessmentDAO.setAssessedBy(organizationSpecService.retrieveEntities(false, assessmentView.getRelAssessedBy().getOid()));
                    assessmentDAO.setAwardedBy(assessmentDAO.getAwardedBy() == null ? new AwardingProcessDCDAO() : assessmentDAO.getAwardedBy());
                    assessmentDAO.getAwardedBy().setAwardingBody(organizationSpecService.retrieveEntities(false, assessmentView.getRelAwardingBody().getOid()));
                }
        );

        return generateOkResponse(assessmentCreatedDAO, assessmentSpecRestMapper, generateAssessmentHateoas(assessmentCreatedDAO));
    }

    @ApiOperation(value = "Delete an assessment spec")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteAssessment(
            @ApiParam(required = true, value = "The Assessment oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        boolean removed = assessmentService.delete(oid);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets an assessment")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<AssessmentSpecView>> getAssessment(
            @ApiParam(required = true, value = "The Assessment.OID") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        LearningAssessmentSpecDAO assessmentDAO = assessmentService.find(oid);
        if (assessmentDAO == null) {
            throw new EDCINotFoundException().addDescription("Assessment with oid [" + oid + "] not found");
        }
        return generateOkResponse(assessmentDAO, assessmentSpecRestMapper, generateAssessmentHateoas(assessmentDAO));
    }

    @ApiOperation(value = "Gets a list of assessments")
    @GetMapping(value = IssuerEndpoint.V1.SPECS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<AssessmentSpecLiteView>> listAssessment(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SEARCH, required = false) String search,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        PageParam pageParam = new PageParam(page, size, sort, direction);
        Specification specif = buildSearchSpecification(search, assessmentSpecRestMapper, edciUserHolder);

        Page<AssessmentSpecLiteView> products = assessmentService.findAll(specif, pageParam.toPageRequest(), assessmentSpecRestMapper);
        return generateListResponse(products, "/specs");
    }

    @ApiOperation(value = "Gets a list of (hasPart) Assessments from assessments")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ASS_HAS_PART,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<AssessmentSpecLiteView>> listHasAssPart(
            @ApiParam(required = true, value = "The Assessment oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningAssessmentSpecDAO assessmentDAO = assessmentService.find(oid);
        if (assessmentDAO == null) {
            throw new EDCINotFoundException().addDescription("Assessment with oid [" + oid + "] not found");
        }

        return generateListResponse(assessmentDAO.getHasPart(), "/specs", assessmentSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing related (hasPart) Assessments to a assessment")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ASS_HAS_PART,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<AssessmentSpecLiteView>> setHasAssPart(
            @ApiParam(required = true, value = "The Assessment oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAssessmentSpecDAO assessmentDAO = assessmentService.find(oid);
        if (assessmentDAO == null) {
            throw new EDCINotFoundException().addDescription("Assessment with oid [" + oid + "] not found");
        }

        Set<LearningAssessmentSpecDAO> entities = retrieveEntities(assessmentService, false, oids.getOid());

        assessmentDAO.setHasPart(entities);

        assessmentService.save(assessmentDAO);

        return generateListResponse(assessmentDAO.getHasPart(), "/specs", assessmentSpecRestMapper, LocaleContextHolder.getLocale().toString());
    }

    @ApiOperation(value = "Gets a list of subresources")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_AWARDING_BODY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> listAwardingBodiesAsm(
            @ApiParam(required = true, value = "The Assessment oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningAssessmentSpecDAO assessmentDAO = assessmentService.find(oid);
        if (assessmentDAO == null) {
            throw new EDCINotFoundException().addDescription("Assessment with oid [" + oid + "] not found");
        }

        if (assessmentDAO.getAwardedBy() == null) {
            assessmentDAO.setAwardedBy(new AwardingProcessDCDAO());
        }

        return generateListResponse(assessmentDAO.getAwardedBy().getAwardingBody(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing subresource")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_AWARDING_BODY,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> setAwardingBodiesAsm(
            @ApiParam(required = true, value = "The Assessment oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAssessmentSpecDAO assessmentDAO = assessmentService.find(oid);
        if (assessmentDAO == null) {
            throw new EDCINotFoundException().addDescription("Assessment with oid [" + oid + "] not found");
        }

        if (assessmentDAO.getAwardedBy() == null) {
            assessmentDAO.setAwardedBy(new AwardingProcessDCDAO());
        }

        Set<OrganizationSpecDAO> entities = retrieveEntities(organizationSpecService, false, oids.getOid());

        assessmentDAO.getAwardedBy().setAwardingBody(entities);

        assessmentService.save(assessmentDAO);

        return generateListResponse(assessmentDAO.getAwardedBy().getAwardingBody(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Gets a list of (AssessedBy) Organization from assessments")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ASS_ASSESSED_BY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> listAssessedBy(
            @ApiParam(required = true, value = "The Assessment oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningAssessmentSpecDAO assessmentDAO = assessmentService.find(oid);
        if (assessmentDAO == null) {
            throw new EDCINotFoundException().addDescription("Assessment with oid [" + oid + "] not found");
        }

        return generateListResponse(assessmentDAO.getAssessedBy(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing related (AssessedBy) Organization to a assessment")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ASS_ASSESSED_BY,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> setAssessedBy(
            @ApiParam(required = true, value = "The Assessment oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAssessmentSpecDAO assessmentDAO = assessmentService.find(oid);
        if (assessmentDAO == null) {
            throw new EDCINotFoundException().addDescription("Assessment with oid [" + oid + "] not found");
        }

        Set<OrganizationSpecDAO> entities = retrieveEntities(organizationSpecService, false, oids.getOid());

        assessmentDAO.setAssessedBy(entities);

        assessmentService.save(assessmentDAO);

        return generateListResponse(assessmentDAO.getAssessedBy(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());
    }

    public Link[] generateAssessmentHateoas(LearningAssessmentSpecDAO assessmentDAO) {

        if (assessmentDAO != null) {

            Link hateoasSelf = ControllerLinkBuilder.linkTo(AssessmentResource.class).slash(IssuerEndpoint.V1.SPECS).slash(assessmentDAO.getPk()).withSelfRel();

            Link hateoasHasPart = ControllerLinkBuilder.linkTo(AssessmentResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(assessmentDAO.getPk())
                    .slash(IssuerEndpoint.V1.ASS_HAS_PART).withRel("hasPart");

//            Link hateoasSpecOf = ControllerLinkBuilder.linkTo(AssessmentResource.class)
//                    .slash(Endpoint.V1.SPECS).slash(assessmentDAO.getHashCodeSeed())
//                    .slash(Endpoint.V1.ASS_SPECIFICATION_OF).withRel("specializationOf");

            return new Link[]{
                    hateoasSelf, hateoasHasPart//, hateoasSpecOf
            };

        } else {
            return null;
        }
    }

}
