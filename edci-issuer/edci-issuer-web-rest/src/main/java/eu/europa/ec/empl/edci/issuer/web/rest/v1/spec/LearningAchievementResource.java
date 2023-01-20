package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.AwardingProcessDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LearningSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.*;
import eu.europa.ec.empl.edci.issuer.service.spec.*;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.*;
import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.specs.AssessmentSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.LearningAchievementSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.*;
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
@Controller(value = "v1.LearningAchievementSpecResource")
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.ACHIEVEMENTS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class LearningAchievementResource implements CrudResource {

    @Autowired
    private LearningAchievementSpecService learningAchievementService;

    @Autowired
    private LearningAchievementSpecRestMapper learningAchievementSpecRestMapper;

    @Autowired
    private OrganizationSpecService organizationSpecService;

    @Autowired
    private OrganizationSpecRestMapper organizationSpecRestMapper;

    @Autowired
    private AssessmentSpecService assessmentSpecService;

    @Autowired
    private AssessmentSpecRestMapper assessmentSpecRestMapper;

    @Autowired
    private LearningActivitySpecService learningActivitySpecService;

    @Autowired
    private LearningActivitySpecRestMapper learningActivitySpecRestMapper;

    @Autowired
    private EntitlementSpecService entitlementSpecService;

    @Autowired
    private EntitlementSpecRestMapper entitlementSpecRestMapper;

    @Autowired
    private LearningOutcomeSpecService learningOutcomeSpecService;

    @Autowired
    private LearningOutcomeSpecRestMapper learningOutcomeSpecRestMapper;

    @Autowired
    private EDCISecurityContextHolder edciUserHolder;

    @ApiOperation(value = "Create an learningAchievement spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningAchievementSpecView>> createLearningAchievement(@RequestBody @Valid LearningAchievementSpecView learningAchievementView,
                                                                                           @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        final LearningAchievementSpecDAO learningAchievementDAO = learningAchievementSpecRestMapper.toDAO(learningAchievementView);

        LearningAchievementSpecDAO learningAchievementCreatedDAO = learningAchievementService.save(learningAchievementDAO,
                () -> {
                    learningAchievementDAO.setWasAwardedBy(learningAchievementDAO.getWasAwardedBy() == null ? new AwardingProcessDCDAO() : learningAchievementDAO.getWasAwardedBy());
                    learningAchievementDAO.setSpecifiedBy(learningAchievementDAO.getSpecifiedBy() == null ? new LearningSpecificationDCDAO() : learningAchievementDAO.getSpecifiedBy());
                    learningAchievementDAO.getWasAwardedBy().setAwardingBody(organizationSpecService.retrieveEntities(false, learningAchievementView.getRelAwardingBody().getOid()));
                    learningAchievementDAO.setWasDerivedFrom(assessmentSpecService.retrieveEntities(false, learningAchievementView.getRelProvenBy().getOid()));
                    learningAchievementDAO.setWasInfluencedBy(learningActivitySpecService.retrieveEntities(false, learningAchievementView.getRelInfluencedBy().getOid()));
                    learningAchievementDAO.setEntitlesTo(entitlementSpecService.retrieveEntities(false, learningAchievementView.getRelEntitlesTo().getOid()));
                    learningAchievementDAO.setHasPart(learningAchievementService.retrieveEntities(false, learningAchievementView.getRelSubAchievements().getOid()));
                    learningAchievementDAO.getSpecifiedBy().setLearningOutcome(learningOutcomeSpecService.retrieveEntities(false, learningAchievementView.getRelLearningOutcomes().getOid()));
                });

        return generateOkResponse(learningAchievementCreatedDAO, learningAchievementSpecRestMapper, generateLearningAchievementHateoas(learningAchievementCreatedDAO));

    }

    @ApiOperation(value = "Duplicate an achievement spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningAchievementSpecView>> duplicateLearningAchievement(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.clone(oid, learningAchievementSpecRestMapper);

        return generateOkResponse(learningAchievementDAO, learningAchievementSpecRestMapper, generateLearningAchievementHateoas(learningAchievementDAO));
    }

    @ApiOperation(value = "Update an learningAchievement spec")
    @PutMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningAchievementSpecView>> updateLearningAchievement(
            @RequestBody @Valid LearningAchievementSpecView learningAchievementView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        if (!learningAchievementService.exists(learningAchievementView.getOid())) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with.OID [" + learningAchievementView.getOid() + "] not found");
        } else {
            learningAchievementView.setOid(learningAchievementView.getOid());
        }

        final LearningAchievementSpecDAO learningAchievementDAO = learningAchievementSpecRestMapper.toDAO(learningAchievementView);

        LearningAchievementSpecDAO learningAchievementCreatedDAO = learningAchievementService.save(learningAchievementDAO,
                () -> {
                    learningAchievementDAO.setWasAwardedBy(learningAchievementDAO.getWasAwardedBy() == null ? new AwardingProcessDCDAO() : learningAchievementDAO.getWasAwardedBy());
                    learningAchievementDAO.setSpecifiedBy(learningAchievementDAO.getSpecifiedBy() == null ? new LearningSpecificationDCDAO() : learningAchievementDAO.getSpecifiedBy());
                    learningAchievementDAO.getWasAwardedBy().setAwardingBody(organizationSpecService.retrieveEntities(false, learningAchievementView.getRelAwardingBody().getOid()));
                    learningAchievementDAO.setWasDerivedFrom(assessmentSpecService.retrieveEntities(false, learningAchievementView.getRelProvenBy().getOid()));
                    learningAchievementDAO.setWasInfluencedBy(learningActivitySpecService.retrieveEntities(false, learningAchievementView.getRelInfluencedBy().getOid()));
                    learningAchievementDAO.setEntitlesTo(entitlementSpecService.retrieveEntities(false, learningAchievementView.getRelEntitlesTo().getOid()));
                    learningAchievementDAO.setHasPart(learningAchievementService.retrieveEntities(false, learningAchievementView.getRelSubAchievements().getOid()));
                    learningAchievementDAO.getSpecifiedBy().setLearningOutcome(learningOutcomeSpecService.retrieveEntities(false, learningAchievementView.getRelLearningOutcomes().getOid()));
                });

        return generateOkResponse(learningAchievementCreatedDAO, learningAchievementSpecRestMapper, generateLearningAchievementHateoas(learningAchievementCreatedDAO));
    }

    @ApiOperation(value = "Delete an learningAchievement spec")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteLearningAchievement(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        boolean removed = learningAchievementService.delete(oid);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets an learningAchievement")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningAchievementSpecView>> getLearningAchievement(
            @ApiParam(required = true, value = "The LearningAchievement.OID") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }
        return generateOkResponse(learningAchievementDAO, learningAchievementSpecRestMapper, generateLearningAchievementHateoas(learningAchievementDAO));
    }

    @ApiOperation(value = "Gets a list of learningAchievements")
    @GetMapping(value = IssuerEndpoint.V1.SPECS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningAchievementSpecLiteView>> listLearningAchievement(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SEARCH, required = false) String search,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        PageParam pageParam = new PageParam(page, size, sort, direction);
        Specification specif = buildSearchSpecification(search, learningAchievementSpecRestMapper, edciUserHolder);

        Page<LearningAchievementSpecLiteView> products = learningAchievementService.findAll(specif, pageParam.toPageRequest(), learningAchievementSpecRestMapper);
        return generateListResponse(products, "/specs");
    }

    @ApiOperation(value = "Gets a list of subresources")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_AWARDING_BODY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> listAwardingBodies(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        if (learningAchievementDAO.getWasAwardedBy() == null) {
            learningAchievementDAO.setWasAwardedBy(new AwardingProcessDCDAO());
        }

        return generateListResponse(learningAchievementDAO.getWasAwardedBy().getAwardingBody(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing subresource")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_AWARDING_BODY,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> setAwardingBodies(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        if (learningAchievementDAO.getWasAwardedBy() == null) {
            learningAchievementDAO.setWasAwardedBy(new AwardingProcessDCDAO());
        }

        Set<OrganizationSpecDAO> entities = retrieveEntities(organizationSpecService, false, oids.getOid());

        learningAchievementDAO.getWasAwardedBy().setAwardingBody(entities);

        learningAchievementService.save(learningAchievementDAO);

        return generateListResponse(learningAchievementDAO.getWasAwardedBy().getAwardingBody(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Gets a subresource (Assessment)")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_PROVEN_BY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<AssessmentSpecView>> getProvenBy(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        AssessmentSpecDAO learnAss = null;

        if (learningAchievementDAO.getWasDerivedFrom() != null && !learningAchievementDAO.getWasDerivedFrom().isEmpty()) {
            learnAss = learningAchievementDAO.getWasDerivedFrom().iterator().next();
        }

        return generateOkResponse(learnAss, assessmentSpecRestMapper, generateLearningAchievementHateoas(learningAchievementDAO));

    }

    @ApiOperation(value = "Link an existing subresource  (Assessment)")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_PROVEN_BY,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<AssessmentSpecView>> setProvenBy(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        Set<AssessmentSpecDAO> entities = retrieveEntities(assessmentSpecService, true, oids.getOid());

        learningAchievementDAO.setWasDerivedFrom(entities);

        learningAchievementService.save(learningAchievementDAO);

        return generateOkResponse(learningAchievementDAO.getWasDerivedFrom().iterator().next(), assessmentSpecRestMapper, generateLearningAchievementHateoas(learningAchievementDAO));

    }

    @ApiOperation(value = "Deletes an existing subresource (Assessment)")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_PROVEN_BY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteProvenBy(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        if (learningAchievementDAO.getWasDerivedFrom() != null) {
            learningAchievementDAO.getWasDerivedFrom().clear();
            learningAchievementService.save(learningAchievementDAO);
        }

        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets a list of subresources")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_INFLUENCED_BY,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningActivitySpecLiteView>> listInfluencedBy(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        return generateListResponse(learningAchievementDAO.getWasInfluencedBy(), "/specs", learningActivitySpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing subresource")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_INFLUENCED_BY,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningActivitySpecLiteView>> setInfluencedBy(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        Set<LearningActivitySpecDAO> entities = retrieveEntities(learningActivitySpecService, false, oids.getOid());

        learningAchievementDAO.setWasInfluencedBy(entities);

        learningAchievementService.save(learningAchievementDAO);

        return generateListResponse(learningAchievementDAO.getWasInfluencedBy(), "/specs", learningActivitySpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Gets a list of subresources")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_ENTITLES_TO,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<EntitlementSpecLiteView>> listEntitlesTo(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        return generateListResponse(learningAchievementDAO.getEntitlesTo(), "/specs", entitlementSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing subresource")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_ENTITLES_TO,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<EntitlementSpecLiteView>> setEntitlesTo(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        Set<EntitlementSpecDAO> entities = retrieveEntities(entitlementSpecService, false, oids.getOid());

        learningAchievementDAO.setEntitlesTo(entities);

        learningAchievementService.save(learningAchievementDAO);

        return generateListResponse(learningAchievementDAO.getEntitlesTo(), "/specs", entitlementSpecRestMapper, LocaleContextHolder.getLocale().toString());
    }

    @ApiOperation(value = "Gets a list of subresources")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_SUB_ACHIEVEMENTS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningAchievementSpecLiteView>> listSubAchievements(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        return generateListResponse(learningAchievementDAO.getHasPart(), "/specs", learningAchievementSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing subresource")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_SUB_ACHIEVEMENTS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningAchievementSpecLiteView>> setSubAchievements(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        Set<LearningAchievementSpecDAO> entities = retrieveEntities(learningAchievementService, false, oids.getOid());

        learningAchievementDAO.setHasPart(entities);

        learningAchievementService.saveCheckingLoops(learningAchievementDAO, (a) -> a.getHasPart(), "LearningAchievement.hasPart");

        return generateListResponse(learningAchievementDAO.getHasPart(), "/specs", learningAchievementSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Gets a list of subresources")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_LEARNING_OUTCOMES,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningOutcomeSpecLiteView>> listLearningOutcomes(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        if (learningAchievementDAO.getSpecifiedBy() == null) {
            learningAchievementDAO.setSpecifiedBy(new LearningSpecificationDCDAO());
        }

        return generateListResponse(learningAchievementDAO.getSpecifiedBy().getLearningOutcome(), "/specs", learningOutcomeSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing subresource")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACH_LEARNING_OUTCOMES,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningOutcomeSpecLiteView>> setLearningOutcomes(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningAchievementSpecDAO learningAchievementDAO = learningAchievementService.find(oid);
        if (learningAchievementDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        if (learningAchievementDAO.getSpecifiedBy() == null) {
            learningAchievementDAO.setSpecifiedBy(new LearningSpecificationDCDAO());
        }

        Set<LearningOutcomeSpecDAO> entities = retrieveEntities(learningOutcomeSpecService, false, oids.getOid());

        learningAchievementDAO.getSpecifiedBy().setLearningOutcome(entities);

        learningAchievementService.save(learningAchievementDAO);

        return generateListResponse(learningAchievementDAO.getSpecifiedBy().getLearningOutcome(), "/specs", learningOutcomeSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    public Link[] generateLearningAchievementHateoas(LearningAchievementSpecDAO learningAchievementDAO) {

        if (learningAchievementDAO != null) {
            Link hateoasSelf = ControllerLinkBuilder.linkTo(LearningAchievementResource.class).slash(IssuerEndpoint.V1.SPECS).slash(learningAchievementDAO.getPk()).withSelfRel();

            Link hateoasAW = ControllerLinkBuilder.linkTo(LearningAchievementResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(learningAchievementDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACH_AWARDING_BODY).withRel("awardingBody");

            Link hateoasPB = ControllerLinkBuilder.linkTo(LearningAchievementResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(learningAchievementDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACH_PROVEN_BY).withRel("provenBy");

            Link hateoasIB = ControllerLinkBuilder.linkTo(LearningAchievementResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(learningAchievementDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACH_INFLUENCED_BY).withRel("influencedBy");

            Link hateoasET = ControllerLinkBuilder.linkTo(LearningAchievementResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(learningAchievementDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACH_ENTITLES_TO).withRel("entitlesTo");

            Link hateoasSA = ControllerLinkBuilder.linkTo(LearningAchievementResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(learningAchievementDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACH_SUB_ACHIEVEMENTS).withRel("subAchievements");

            Link hateoasLO = ControllerLinkBuilder.linkTo(LearningAchievementResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(learningAchievementDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACH_LEARNING_OUTCOMES).withRel("learningOutcomes");

            return new Link[]{
                    hateoasSelf, hateoasAW, hateoasPB, hateoasIB, hateoasET, hateoasSA, hateoasLO
            };

        } else {
            return null;
        }
    }

}


