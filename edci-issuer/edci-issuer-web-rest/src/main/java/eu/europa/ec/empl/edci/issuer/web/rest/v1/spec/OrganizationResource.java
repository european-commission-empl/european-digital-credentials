package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.Version;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.Endpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.issuer.service.spec.EntitlementSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.LearningAchievementSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.LearningActivitySpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.OrganizationSpecService;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.OrganizationSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.specs.OrganizationSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.OrganizationSpecLiteView;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;


@Api(tags = {
        "V1"
})
@Controller(value = "v1.OrganizationSpecResource")
@RequestMapping(value = Version.V1 + Endpoint.V1.ORGANIZATIONS_BASE)
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class OrganizationResource implements CrudResource {

    @Autowired
    private OrganizationSpecService organizationService;

    @Autowired
    private OrganizationSpecRestMapper organizationSpecRestMapper;

    @Autowired
    private LearningAchievementSpecService learningAchievementSpecService;

    @Autowired
    private LearningActivitySpecService learningActivitySpecService;

    @Autowired
    private EntitlementSpecService entitlementSpecService;

    @Autowired
    private EDCISecurityContextHolder edciUserHolder;

    @ApiOperation(value = "Create an organization spec")
    @PostMapping(value = Endpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> createOrganization(@RequestBody @Valid OrganizationSpecView organizationView,
                                                                             @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        final OrganizationSpecDAO organizationDAO = organizationSpecRestMapper.toDAO(organizationView);

        OrganizationSpecDAO organizationCreatedDAO = organizationService.save(organizationDAO,
                () -> {
                    organizationDAO.setUnitOf(retrieveEntity(organizationService, false, organizationView.getRelUnitOf().getSingleOid()));
                });

        return generateOkResponse(organizationCreatedDAO, organizationSpecRestMapper, generateOrganizationHateoas(organizationCreatedDAO));
    }

    @ApiOperation(value = "Duplicate a activity spec")
    @PostMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> duplicateOrganization(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.clone(oid, organizationSpecRestMapper);

        return generateOkResponse(organizationDAO, organizationSpecRestMapper, generateOrganizationHateoas(organizationDAO));
    }

    @ApiOperation(value = "Update an organization spec")
    @PutMapping(value = Endpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> updateOrganization(
            @RequestBody @Valid OrganizationSpecView organizationView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        if (!organizationService.exists(organizationView.getOid())) {
            throw new EDCINotFoundException().addDescription("Organization with.OID [" + organizationView.getOid() + "] not found");
        } else {
            organizationView.setOid(organizationView.getOid());
        }

        final OrganizationSpecDAO organizationDAO = organizationSpecRestMapper.toDAO(organizationView);

        OrganizationSpecDAO organizationCreatedDAO = organizationService.save(organizationDAO,
                () -> {
                    organizationDAO.setUnitOf(retrieveEntity(organizationService, false, organizationView.getRelUnitOf().getSingleOid()));
                });

        return generateOkResponse(organizationCreatedDAO, organizationSpecRestMapper, generateOrganizationHateoas(organizationCreatedDAO));
    }

    @ApiOperation(value = "Upload a organization logo")
    @PostMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID + Endpoint.V1.LOGO,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity addLogo(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(required = true, value = "The Logo file") @RequestPart(Parameter.FILE) MultipartFile file) {

        organizationService.addLogo(oid, file);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Delete an organization spec")
    @DeleteMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteOrganization(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        boolean removed = organizationService.delete(oid);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets an organization")
    @GetMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> getOrganization(
            @ApiParam(required = true, value = "The Organization.OID") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }
        return generateOkResponse(organizationDAO, organizationSpecRestMapper, generateOrganizationHateoas(organizationDAO));
    }

    @ApiOperation(value = "Gets a list of organizations")
    @GetMapping(value = Endpoint.V1.SPECS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> listOrganization(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SEARCH, required = false) String search,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        PageParam pageParam = new PageParam(page, size, sort, direction);
        Specification specif = buildSearchSpecification(search, organizationSpecRestMapper, edciUserHolder);

        Page<OrganizationSpecLiteView> products = organizationService.findAll(specif, pageParam.toPageRequest(), organizationSpecRestMapper);
        return generateListResponse(products, "/specs");
    }

    @ApiOperation(value = "Gets a list of hasUnit organizations")
    @GetMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID + Endpoint.V1.ORG_HAS_UNITS_REL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> listHasUnit(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        return generateListResponse(organizationDAO.getHasUnit(), "/specs", organizationSpecRestMapper);

    }

    @ApiOperation(value = "Link an existing related organizations to a organization")
    @PostMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID + Endpoint.V1.ORG_UNIT_OF_REL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> setUnitOf(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        OrganizationSpecDAO entitiy = retrieveEntity(organizationService, false, oids.getSingleOid());

        organizationDAO.setUnitOf(entitiy);

        organizationService.save(organizationDAO);

        return generateOkResponse(organizationDAO.getUnitOf(), organizationSpecRestMapper, generateOrganizationHateoas(organizationDAO));
    }

    @ApiOperation(value = "Deletes an existing linked organization to a organization")
    @DeleteMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID + Endpoint.V1.ORG_UNIT_OF_REL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteUnitOf(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        organizationDAO.setUnitOf(null);

        organizationService.save(organizationDAO);

        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets a list of hasUnit organizations")
    @GetMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID + Endpoint.V1.ORG_UNIT_OF_REL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> getUnitOf(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        return generateOkResponse(organizationDAO.getUnitOf(), organizationSpecRestMapper, generateOrganizationHateoas(organizationDAO));

    }

    public Link[] generateOrganizationHateoas(OrganizationSpecDAO organizationDAO) {

        if (organizationDAO != null) {
            Link hateoasSelf = ControllerLinkBuilder.linkTo(OrganizationResource.class).slash(Endpoint.V1.SPECS).slash(organizationDAO.getPk()).withSelfRel();

            Link hateoasUnitOf = ControllerLinkBuilder.linkTo(OrganizationResource.class)
                    .slash(Endpoint.V1.SPECS).slash(organizationDAO.getPk())
                    .slash(Endpoint.V1.ORG_UNIT_OF_REL).withRel("unitOf");

            Link hateoasHasUnits = ControllerLinkBuilder.linkTo(OrganizationResource.class)
                    .slash(Endpoint.V1.SPECS).slash(organizationDAO.getPk())
                    .slash(Endpoint.V1.ORG_HAS_UNITS_REL).withRel("hasUnits");

            return new Link[]{
                    hateoasSelf, hateoasUnitOf, hateoasHasUnits
            };
        } else {
            return null;
        }
    }

}


