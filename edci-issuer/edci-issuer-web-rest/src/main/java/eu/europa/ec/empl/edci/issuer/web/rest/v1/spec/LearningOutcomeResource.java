package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningOutcomeSpecDAO;
import eu.europa.ec.empl.edci.issuer.service.spec.LearningOutcomeSpecService;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.LearningOutcomeSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.specs.LearningOutcomeSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.LearningOutcomeSpecLiteView;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import eu.europa.ec.empl.edci.security.EDCISecurityContextHolder;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {
        "V1"
})
@Controller(value = "v1.LearningOutcomeSpecResource")
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.LEARNING_OUTCOMES_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class LearningOutcomeResource implements CrudResource {

    @Autowired
    private LearningOutcomeSpecService learningOutcomeService;

    @Autowired
    private LearningOutcomeSpecRestMapper learningOutcomeSpecRestMapper;

    @Autowired
    private EDCISecurityContextHolder edciUserHolder;

    @ApiOperation(value = "Create an learningOutcome spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningOutcomeSpecView>> createLearningOutcome(@RequestBody @Valid LearningOutcomeSpecView learningOutcomeView,
                                                                                   @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        final LearningOutcomeSpecDAO learningOutcomeDAO = learningOutcomeSpecRestMapper.toDAO(learningOutcomeView);

        LearningOutcomeSpecDAO learningOutcomeCreatedDAO = learningOutcomeService.save(learningOutcomeDAO);

        return generateOkResponse(learningOutcomeCreatedDAO, learningOutcomeSpecRestMapper, generateLearningOutcomeHateoas(learningOutcomeCreatedDAO));
    }

    @ApiOperation(value = "Duplicate a activity spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningOutcomeSpecView>> duplicateLearningOutcome(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        LearningOutcomeSpecDAO learningOutcomeDAO = learningOutcomeService.clone(oid, learningOutcomeSpecRestMapper);

        return generateOkResponse(learningOutcomeDAO, learningOutcomeSpecRestMapper, generateLearningOutcomeHateoas(learningOutcomeDAO));
    }


    @ApiOperation(value = "Update an learningOutcome spec")
    @PutMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningOutcomeSpecView>> updateLearningOutcome(
            @RequestBody @Valid LearningOutcomeSpecView learningOutcomeView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        if (!learningOutcomeService.exists(learningOutcomeView.getOid())) {
            throw new EDCINotFoundException().addDescription("LearningOutcome with.OID [" + learningOutcomeView.getOid() + "] not found");
        } else {
            learningOutcomeView.setOid(learningOutcomeView.getOid());
        }

        final LearningOutcomeSpecDAO learningOutcomeDAO = learningOutcomeSpecRestMapper.toDAO(learningOutcomeView);

        LearningOutcomeSpecDAO learningOutcomeCreatedDAO = learningOutcomeService.save(learningOutcomeDAO);

        return generateOkResponse(learningOutcomeCreatedDAO, learningOutcomeSpecRestMapper, generateLearningOutcomeHateoas(learningOutcomeCreatedDAO));
    }

    @ApiOperation(value = "Delete an learningOutcome spec")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteLearningOutcome(
            @ApiParam(required = true, value = "The LearningOutcome oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        boolean removed = learningOutcomeService.delete(oid);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets an learningOutcome")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<LearningOutcomeSpecView>> getLearningOutcome(
            @ApiParam(required = true, value = "The LearningOutcome.OID") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        LearningOutcomeSpecDAO learningOutcomeDAO = learningOutcomeService.find(oid);
        if (learningOutcomeDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningOutcome with oid [" + oid + "] not found");
        }
        return generateOkResponse(learningOutcomeDAO, learningOutcomeSpecRestMapper, generateLearningOutcomeHateoas(learningOutcomeDAO));
    }

    @ApiOperation(value = "Gets a list of learningOutcomes")
    @GetMapping(value = IssuerEndpoint.V1.SPECS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<LearningOutcomeSpecLiteView>> listLearningOutcome(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SEARCH, required = false) String search,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        PageParam pageParam = new PageParam(page, size, sort, direction);
        Specification specif = buildSearchSpecification(search, learningOutcomeSpecRestMapper, edciUserHolder);

        Page<LearningOutcomeSpecLiteView> products = learningOutcomeService.findAll(specif, pageParam.toPageRequest(), learningOutcomeSpecRestMapper);
        return generateListResponse(products, "/specs");
    }

    public Link[] generateLearningOutcomeHateoas(LearningOutcomeSpecDAO learningOutcomeDAO) {

        if (learningOutcomeDAO != null) {
            Link hateoasSelf = ControllerLinkBuilder.linkTo(LearningOutcomeResource.class).slash(IssuerEndpoint.V1.SPECS).slash(learningOutcomeDAO.getPk()).withSelfRel();

            return new Link[]{
                    hateoasSelf
            };
        } else {
            return null;
        }
    }
}
