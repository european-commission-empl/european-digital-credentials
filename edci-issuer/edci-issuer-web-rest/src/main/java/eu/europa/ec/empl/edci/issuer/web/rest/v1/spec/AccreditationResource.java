package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.entity.specs.AccreditationSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.issuer.service.spec.*;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.AccreditationSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.LearningAchievementSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.LearningOutcomeSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.OrganizationSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.specs.AccreditationSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.OrganizationSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.AccreditationSpecLiteView;
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
@Controller(value = "v1.AccreditationResource")
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.ACCREDITATIONS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class AccreditationResource implements CrudResource {

    @Autowired
    private AccreditationSpecService accreditationSpecService;

    @Autowired
    private AccreditationSpecRestMapper accreditationSpecRestMapper;

    @Autowired
    private LearningAchievementSpecService learningAchievementService;

    @Autowired
    private LearningAchievementSpecRestMapper learningAchievementSpecRestMapper;

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

    @ApiOperation(value = "Create an accreditation spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<AccreditationSpecView>> createAccreditation(
            @RequestBody @Valid AccreditationSpecView accreditationView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        final AccreditationSpecDAO accreditationDAO = accreditationSpecRestMapper.toDAO(accreditationView);

        AccreditationSpecDAO accreditationCreatedDAO = accreditationSpecService.save(accreditationDAO,
                () -> {
                    accreditationDAO.setAccreditingAgent(retrieveEntity(organizationSpecService, false, accreditationView.getRelAccreditingAgent().getSingleOid()));
                    accreditationDAO.setOrganisation(retrieveEntity(organizationSpecService, false, accreditationView.getRelOrganisation().getSingleOid()));
                }
        );

        return generateOkResponse(accreditationCreatedDAO, accreditationSpecRestMapper, generateAccreditationHateoas(accreditationCreatedDAO));
    }

    @ApiOperation(value = "Duplicate an activity spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<AccreditationSpecView>> duplicateAccreditation(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        AccreditationSpecDAO accreditationDAO = accreditationSpecService.clone(oid, accreditationSpecRestMapper);

        return generateOkResponse(accreditationDAO, accreditationSpecRestMapper, generateAccreditationHateoas(accreditationDAO));
    }

    @ApiOperation(value = "Update an accreditation spec")
    @PutMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<AccreditationSpecView>> updateAccreditation(
            @RequestBody @Valid AccreditationSpecView accreditationView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        if (!accreditationSpecService.exists(accreditationView.getOid())) {
            throw new EDCINotFoundException().addDescription("accreditation with.OID [" + accreditationView.getOid() + "] not found");
        } else {
            accreditationView.setOid(accreditationView.getOid());
        }

        final AccreditationSpecDAO accreditationDAO = accreditationSpecRestMapper.toDAO(accreditationView);

        AccreditationSpecDAO accreditationCreatedDAO = accreditationSpecService.save(accreditationDAO,
                () -> {
                    accreditationDAO.setAccreditingAgent(retrieveEntity(organizationSpecService, false, accreditationView.getRelAccreditingAgent().getSingleOid()));
                    accreditationDAO.setOrganisation(retrieveEntity(organizationSpecService, false, accreditationView.getRelOrganisation().getSingleOid()));                }
        );

        return generateOkResponse(accreditationCreatedDAO, accreditationSpecRestMapper, generateAccreditationHateoas(accreditationCreatedDAO));
    }

    @ApiOperation(value = "Delete an accreditation spec")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteAccreditation(
            @ApiParam(required = true, value = "The accreditation oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        boolean removed = accreditationSpecService.delete(oid);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets an accreditation")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<AccreditationSpecView>> getAccreditation(
            @ApiParam(required = true, value = "The accreditation.OID") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        AccreditationSpecDAO accreditationDAO = accreditationSpecService.find(oid);
        if (accreditationDAO == null) {
            throw new EDCINotFoundException().addDescription("accreditation with oid [" + oid + "] not found");
        }
        return generateOkResponse(accreditationDAO, accreditationSpecRestMapper, generateAccreditationHateoas(accreditationDAO));
    }

    @ApiOperation(value = "Gets a list of accreditations")
    @GetMapping(value = IssuerEndpoint.V1.SPECS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<AccreditationSpecLiteView>> listAccreditation(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SEARCH, required = false) String search,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        PageParam pageParam = new PageParam(page, size, sort, direction);
        Specification specif = buildSearchSpecification(search, accreditationSpecRestMapper, edciUserHolder);

        Page<AccreditationSpecLiteView> products = accreditationSpecService.findAll(specif, pageParam.toPageRequest(), accreditationSpecRestMapper);
        return generateListResponse(products, "/specs");
    }

    @ApiOperation(value = "Link an existing related organizations to a Accreditation")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACC_ACCREDITING_AGENT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> setAccreditingAgent(
            @ApiParam(required = true, value = "The Accreditation oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        AccreditationSpecDAO accreditationSpecDAO = accreditationSpecService.find(oid);
        if (accreditationSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Accreditation with oid [" + oid + "] not found");
        }

        OrganizationSpecDAO entity = retrieveEntity(organizationSpecService, false, oids.getSingleOid());

        accreditationSpecDAO.setAccreditingAgent(entity);

        accreditationSpecService.save(accreditationSpecDAO);

        return generateOkResponse(accreditationSpecDAO.getAccreditingAgent(), organizationSpecRestMapper, generateAccreditationHateoas(accreditationSpecDAO));
    }

    @ApiOperation(value = "Deletes an existing linked organization to a Accreditation")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACC_ACCREDITING_AGENT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteAccreditingAgent(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        AccreditationSpecDAO accreditationSpecDAO = accreditationSpecService.find(oid);
        if (accreditationSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Accreditation with oid [" + oid + "] not found");
        }

        accreditationSpecDAO.setAccreditingAgent(null);

        accreditationSpecService.save(accreditationSpecDAO);

        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets a list of Accrediting Agents")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACC_ACCREDITING_AGENT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> getAccreditingAgent(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        AccreditationSpecDAO accreditationSpecDAO = accreditationSpecService.find(oid);
        if (accreditationSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Accreditation with oid [" + oid + "] not found");
        }

        return generateOkResponse(accreditationSpecDAO.getAccreditingAgent(), organizationSpecRestMapper, generateAccreditationHateoas(accreditationSpecDAO));

    }

    @ApiOperation(value = "Link an existing related organizations to a Accreditation")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACC_ORGANISATION,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> setOrganisation(
            @ApiParam(required = true, value = "The Accreditation oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        AccreditationSpecDAO accreditationSpecDAO = accreditationSpecService.find(oid);
        if (accreditationSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Accreditation with oid [" + oid + "] not found");
        }

        OrganizationSpecDAO entity = retrieveEntity(organizationSpecService, false, oids.getSingleOid());

        accreditationSpecDAO.setOrganisation(entity);

        accreditationSpecService.save(accreditationSpecDAO);

        return generateOkResponse(accreditationSpecDAO.getOrganisation(), organizationSpecRestMapper, generateAccreditationHateoas(accreditationSpecDAO));
    }

    @ApiOperation(value = "Deletes an existing linked organization to a Accreditation")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACC_ORGANISATION,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteOrganisation(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        AccreditationSpecDAO accreditationSpecDAO = accreditationSpecService.find(oid);
        if (accreditationSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Accreditation with oid [" + oid + "] not found");
        }

        accreditationSpecDAO.setOrganisation(null);

        accreditationSpecService.save(accreditationSpecDAO);

        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets a list of Organisations")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ACC_ORGANISATION,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> getOrganisation(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        AccreditationSpecDAO accreditationSpecDAO = accreditationSpecService.find(oid);
        if (accreditationSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Accreditation with oid [" + oid + "] not found");
        }

        return generateOkResponse(accreditationSpecDAO.getOrganisation(), organizationSpecRestMapper, generateAccreditationHateoas(accreditationSpecDAO));

    }

    public Link[] generateAccreditationHateoas(AccreditationSpecDAO accreditationDAO) {

        if (accreditationDAO != null) {

            Link hateoasSelf = ControllerLinkBuilder.linkTo(AccreditationResource.class).slash(IssuerEndpoint.V1.SPECS).slash(accreditationDAO.getPk()).withSelfRel();

            Link hateoasAccreditingAgent = ControllerLinkBuilder.linkTo(AccreditationResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(accreditationDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACC_ACCREDITING_AGENT).withRel("accreditingAgent");

            Link hateoasOrganisation = ControllerLinkBuilder.linkTo(AccreditationResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(accreditationDAO.getPk())
                    .slash(IssuerEndpoint.V1.ACC_ORGANISATION).withRel("organisation");

            return new Link[]{
                    hateoasSelf, hateoasAccreditingAgent, hateoasOrganisation
            };

        } else {
            return null;
        }
    }
}
