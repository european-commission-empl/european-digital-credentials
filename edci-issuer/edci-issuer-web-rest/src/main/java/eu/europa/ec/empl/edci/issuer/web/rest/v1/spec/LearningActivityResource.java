package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.AwardingProcessDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAchievementSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningActivitySpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.issuer.service.spec.*;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.LearningAchievementSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.LearningActivitySpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.LearningOutcomeSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.OrganizationSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.specs.LearningActivitySpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.LearningAchievementSpecLiteView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.LearningActivitySpecLiteView;
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
@Controller(value = "v1.LearningActivitySpecResource")
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.ACTIVITIES_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class LearningActivityResource implements CrudResource {

    @Autowired
    private LearningActivitySpecService learningActivityService;

    @Autowired
    private LearningActivitySpecRestMapper learningActivitySpecRestMapper;

    @Autowired
    private LearningAchievementSpecService learningAchievementService;

    @Autowired
    private LearningAchievementSpecRestMapper learningAchievementSpecRestMapper;

    @Autowired
    private LearningActivitySpecService learningActivitySpecService;

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

    @ApiOperation(value = "Create an learningActivity spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningActivitySpecView>> createLearningActivity(@RequestBody @Valid LearningActivitySpecView learningActivityView,
                                                                                     @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        final LearningActivitySpecDAO learningActivityDAO = learningActivitySpecRestMapper.toDAO(learningActivityView);

        LearningActivitySpecDAO learningActivityCreatedDAO = learningActivityService.save(learningActivityDAO,
                () -> {
                    learningActivityDAO.setDirectedBy(organizationSpecService.retrieveEntities(false, learningActivityView.getRelDirectedBy().getOid()));
                    learningActivityDAO.setInfluenced(learningAchievementService.retrieveEntities(false, learningActivityView.getRelInfluenced().getOid()));
                    learningActivityDAO.setHasPart(learningActivityService.retrieveEntities(false, learningActivityView.getRelHasPart().getOid()));
                    learningActivityDAO.setAwardedBy(learningActivityDAO.getAwardedBy() == null ? new AwardingProcessDCDAO() : learningActivityDAO.getAwardedBy());
                    learningActivityDAO.getAwardedBy().setAwardingBody(organizationSpecService.retrieveEntities(false, learningActivityView.getRelAwardingBody().getOid()));
                });

        return generateOkResponse(learningActivityCreatedDAO, learningActivitySpecRestMapper, generateLearningActivityHateoas(learningActivityCreatedDAO));
    }

    @ApiOperation(value = "Duplicate an activity spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningActivitySpecView>> duplicateLearningActivity(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningActivitySpecDAO learningActivityDAO = learningActivityService.clone(oid, learningActivitySpecRestMapper);

        return generateOkResponse(learningActivityDAO, learningActivitySpecRestMapper, generateLearningActivityHateoas(learningActivityDAO));
    }

    @ApiOperation(value = "Update an learningActivity spec")
    @PutMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningActivitySpecView>> updateLearningActivity(
            @RequestBody @Valid LearningActivitySpecView learningActivityView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        if (!learningActivityService.exists(learningActivityView.getOid())) {
            throw new EDCINotFoundException().addDescription("LearningActivity with.OID [" + learningActivityView.getOid() + "] not found");
        } else {
            learningActivityView.setOid(learningActivityView.getOid());
        }

        final LearningActivitySpecDAO learningActivityDAO = learningActivitySpecRestMapper.toDAO(learningActivityView);

        LearningActivitySpecDAO learningActivityCreatedDAO = learningActivityService.save(learningActivityDAO,
                () -> {
                    learningActivityDAO.setDirectedBy(organizationSpecService.retrieveEntities(false, learningActivityView.getRelDirectedBy().getOid()));
                    learningActivityDAO.setInfluenced(learningAchievementService.retrieveEntities(false, learningActivityView.getRelInfluenced().getOid()));
                    learningActivityDAO.setHasPart(learningActivityService.retrieveEntities(false, learningActivityView.getRelHasPart().getOid()));
                    learningActivityDAO.setAwardedBy(learningActivityDAO.getAwardedBy() == null ? new AwardingProcessDCDAO() : learningActivityDAO.getAwardedBy());
                    learningActivityDAO.getAwardedBy().setAwardingBody(organizationSpecService.retrieveEntities(false, learningActivityView.getRelAwardingBody().getOid()));
                });

        return generateOkResponse(learningActivityCreatedDAO, learningActivitySpecRestMapper, generateLearningActivityHateoas(learningActivityCreatedDAO));
    }

    @ApiOperation(value = "Delete an learningActivity spec")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteLearningActivity(
            @ApiParam(required = true, value = "The LearningActivity oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        boolean removed = learningActivityService.delete(oid);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets an learningActivity")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningActivitySpecView>> getLearningActivity(
            @ApiParam(required = true, value = "The LearningActivity.OID") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        LearningActivitySpecDAO learningActivityDAO = learningActivityService.find(oid);
        if (learningActivityDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningActivity with oid [" + oid + "] not found");
        }
        return generateOkResponse(learningActivityDAO, learningActivitySpecRestMapper, generateLearningActivityHateoas(learningActivityDAO));
    }

    @ApiOperation(value = "Gets a list of learningActivities")
    @GetMapping(value = IssuerEndpoint.V1.SPECS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningActivitySpecLiteView>> listLearningActivity(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SEARCH, required = false) String search,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        PageParam pageParam = new PageParam(page, size, sort, direction);
        Specification specif = buildSearchSpecification(search, learningActivitySpecRestMapper, edciUserHolder);

        Page<LearningActivitySpecLiteView> products = learningActivityService.findAll(specif, pageParam.toPageRequest(), learningActivitySpecRestMapper);
        return generateListResponse(products, "/specs");
    }

    @ApiOperation(value = "Gets a list of (DirectedBy) Organizations from learningActivities")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACT_DIRECTED_BY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> listDirectedBy(
            @ApiParam(required = true, value = "The LearningActivity oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningActivitySpecDAO learningActivityDAO = learningActivityService.find(oid);
        if (learningActivityDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningActivity with oid [" + oid + "] not found");
        }

        return generateListResponse(learningActivityDAO.getDirectedBy(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing related (DirectedBy) Organization to a learningActivity")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACT_DIRECTED_BY,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> setDirectedBy(
            @ApiParam(required = true, value = "The LearningActivity oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningActivitySpecDAO learningActivityDAO = learningActivityService.find(oid);
        if (learningActivityDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        Set<OrganizationSpecDAO> entities = retrieveEntities(organizationSpecService, false, oids.getOid());

        learningActivityDAO.setDirectedBy(entities);

        learningActivityService.save(learningActivityDAO);

        return generateListResponse(learningActivityDAO.getDirectedBy(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());
    }

    @ApiOperation(value = "Gets a list of subresources")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACT_AWARDING_BODY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> listAwardingBodiesAct(
            @ApiParam(required = true, value = "The LearningActivity oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningActivitySpecDAO learningActivityDAO = learningActivityService.find(oid);
        if (learningActivityDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningActivity with oid [" + oid + "] not found");
        }

        if (learningActivityDAO.getAwardedBy() == null) {
            learningActivityDAO.setAwardedBy(new AwardingProcessDCDAO());
        }

        return generateListResponse(learningActivityDAO.getAwardedBy().getAwardingBody(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing subresource")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACT_AWARDING_BODY,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> setAwardingBodiesAct(
            @ApiParam(required = true, value = "The LearningActivity oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningActivitySpecDAO learningActivityDAO = learningActivityService.find(oid);
        if (learningActivityDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningActivity with oid [" + oid + "] not found");
        }

        if (learningActivityDAO.getAwardedBy() == null) {
            learningActivityDAO.setAwardedBy(new AwardingProcessDCDAO());
        }

        Set<OrganizationSpecDAO> entities = retrieveEntities(organizationSpecService, false, oids.getOid());

        learningActivityDAO.getAwardedBy().setAwardingBody(entities);

        learningActivityService.save(learningActivityDAO);

        return generateListResponse(learningActivityDAO.getAwardedBy().getAwardingBody(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }


    @ApiOperation(value = "Gets a list of (Influenced) LearningAchievement from learningActivities")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACT_INFLUENCED,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningAchievementSpecLiteView>> listInfluenced(
            @ApiParam(required = true, value = "The LearningActivity oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningActivitySpecDAO learningActivityDAO = learningActivityService.find(oid);
        if (learningActivityDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningActivity with oid [" + oid + "] not found");
        }
        return generateListResponse(learningActivityDAO.getInfluenced(), "/specs", learningAchievementSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing related (Influenced) LearningAchievement to a learningActivity")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACT_INFLUENCED,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningAchievementSpecLiteView>> setInfluenced(
            @ApiParam(required = true, value = "The LearningActivity oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningActivitySpecDAO learningActivityDAO = learningActivityService.find(oid);
        if (learningActivityDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningActivity with oid [" + oid + "] not found");
        }

        Set<LearningAchievementSpecDAO> entities = retrieveEntities(learningAchievementService, false, oids.getOid());

        learningActivityDAO.setInfluenced(entities);

        learningActivityService.save(learningActivityDAO);

        return generateListResponse(learningActivityDAO.getInfluenced(), "/specs", learningAchievementSpecRestMapper, LocaleContextHolder.getLocale().toString());
    }

    @ApiOperation(value = "Gets a list of (hasPart) LearningActivity from learningActivities")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACT_HAS_PART,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningActivitySpecLiteView>> listHasActPart(
            @ApiParam(required = true, value = "The LearningActivity oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningActivitySpecDAO learningActivityDAO = learningActivityService.find(oid);
        if (learningActivityDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningActivity with oid [" + oid + "] not found");
        }
        return generateListResponse(learningActivityDAO.getHasPart(), "/specs", learningActivitySpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing related (hasPart) LearningActivity to a learningActivity")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACT_HAS_PART,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningActivitySpecLiteView>> setHasActPart(
            @ApiParam(required = true, value = "The LearningActivity oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningActivitySpecDAO learningActivityDAO = learningActivityService.find(oid);
        if (learningActivityDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningActivity with oid [" + oid + "] not found");
        }

        Set<LearningActivitySpecDAO> entities = retrieveEntities(learningActivityService, false, oids.getOid());

        learningActivityDAO.setHasPart(entities);

        learningActivityService.save(learningActivityDAO);

        return generateListResponse(learningActivityDAO.getHasPart(), "/specs", learningActivitySpecRestMapper, LocaleContextHolder.getLocale().toString());
    }

    public Link[] generateLearningActivityHateoas(LearningActivitySpecDAO learningActivityDAO) {

        if (learningActivityDAO != null) {
            Link hateoasSelf = ControllerLinkBuilder.linkTo(LearningActivityResource.class).slash(IssuerEndpoint.V1.SPECS).slash(learningActivityDAO.getPk()).withSelfRel();

            Link hateoasDir = ControllerLinkBuilder.linkTo(LearningActivityResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(learningActivityDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACT_DIRECTED_BY).withRel("directedBy");

            Link hateoasInf = ControllerLinkBuilder.linkTo(LearningActivityResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(learningActivityDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACT_INFLUENCED).withRel("influenced");

            Link hateoasLO = ControllerLinkBuilder.linkTo(LearningActivityResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(learningActivityDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACT_HAS_PART).withRel("hasPart");

            return new Link[]{
                    hateoasSelf, hateoasDir, hateoasInf, hateoasLO
            };
        } else {
            return null;
        }
    }

}


