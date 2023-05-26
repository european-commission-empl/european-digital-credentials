package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.entity.specs.AccreditationSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.issuer.service.spec.*;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.AccreditationSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.OrganizationSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.specs.OrganizationSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.AccreditationSpecLiteView;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Set;


@Api(tags = {
        "V1"
})
@Controller(value = "v1.OrganizationSpecResource")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.ORGANIZATIONS_BASE)
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class OrganizationResource implements CrudResource {

    @Autowired
    private OrganizationSpecService organizationService;

    @Autowired
    private AccreditationSpecService accreditationSpecService;

    @Autowired
    private AccreditationSpecRestMapper accreditationSpecRestMapper;

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
    @PostMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> createOrganization(@RequestBody @Valid OrganizationSpecView organizationView,
                                                                             @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        final OrganizationSpecDAO organizationDAO = organizationSpecRestMapper.toDAO(organizationView);
        Set<AccreditationSpecDAO> accreditationSpecDAOS = retrieveEntities(accreditationSpecService, false, organizationView.getRelAccreditation().getOid());
        OrganizationSpecDAO organizationCreatedDAO = organizationService.save(organizationDAO,
                () -> {
                    organizationDAO.setAccreditation(retrieveEntities(accreditationSpecService, false, organizationView.getRelAccreditation().getOid()));
                    organizationDAO.setSubOrganizationOf(retrieveEntity(organizationService, false, organizationView.getRelSubOrganizationOf().getSingleOid()));
                });

        return generateOkResponse(organizationCreatedDAO, organizationSpecRestMapper, generateOrganizationHateoas(organizationCreatedDAO));
    }

    @ApiOperation(value = "Duplicate a activity spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> duplicateOrganization(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.clone(oid, organizationSpecRestMapper);

        return generateOkResponse(organizationDAO, organizationSpecRestMapper, generateOrganizationHateoas(organizationDAO));
    }

    @ApiOperation(value = "Update an organization spec")
    @PutMapping(value = IssuerEndpoint.V1.SPECS,
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
                    organizationDAO.setAccreditation(retrieveEntities(accreditationSpecService, false, organizationView.getRelAccreditation().getOid()));
                    organizationDAO.setSubOrganizationOf(retrieveEntity(organizationService, false, organizationView.getRelSubOrganizationOf().getSingleOid()));
                });

        return generateOkResponse(organizationCreatedDAO, organizationSpecRestMapper, generateOrganizationHateoas(organizationCreatedDAO));
    }

    @ApiOperation(value = "Upload a organization logo")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.LOGO,
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
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteOrganization(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        boolean removed = organizationService.delete(oid);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets an organization")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
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
    @GetMapping(value = IssuerEndpoint.V1.SPECS,
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
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ORG_HAS_UNITS_REL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Deprecated
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> listHasUnit(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        return generateListResponse(organizationDAO.getChildOrganisation(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing related organizations to a organization")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ORG_UNIT_OF_REL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Deprecated
    public ResponseEntity<Resource<OrganizationSpecView>> setUnitOf(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        OrganizationSpecDAO entitiy = retrieveEntity(organizationService, false, oids.getSingleOid());

        organizationDAO.setSubOrganizationOf(entitiy);

        organizationService.save(organizationDAO);

        return generateOkResponse(organizationDAO.getSubOrganizationOf(), organizationSpecRestMapper, generateOrganizationHateoas(organizationDAO));
    }

    @ApiOperation(value = "Deletes an existing linked organization to a organization")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ORG_UNIT_OF_REL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Deprecated
    public ResponseEntity deleteUnitOf(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        organizationDAO.setSubOrganizationOf(null);

        organizationService.save(organizationDAO);

        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets a list of hasUnit organizations")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ORG_UNIT_OF_REL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Deprecated
    public ResponseEntity<Resource<OrganizationSpecView>> getUnitOf(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        return generateOkResponse(organizationDAO.getSubOrganizationOf(), organizationSpecRestMapper, generateOrganizationHateoas(organizationDAO));

    }

    @ApiOperation(value = "Gets a list of ChildOrganisation organizations")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ORG_CHILD_ORGANIZATION_REL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> listChildOrganisation(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        return generateListResponse(organizationDAO.getChildOrganisation(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing related organizations to a organization")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ORG_SUB_ORGANIZATION_OF_REL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> setSubOrganizationOf(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        OrganizationSpecDAO entitiy = retrieveEntity(organizationService, false, oids.getSingleOid());

        organizationDAO.setSubOrganizationOf(entitiy);

        organizationService.save(organizationDAO);

        return generateOkResponse(organizationDAO.getSubOrganizationOf(), organizationSpecRestMapper, generateOrganizationHateoas(organizationDAO));
    }

    @ApiOperation(value = "Deletes an existing linked organization to a organization")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ORG_SUB_ORGANIZATION_OF_REL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteSubOrganizationOf(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        organizationDAO.setSubOrganizationOf(null);

        organizationService.save(organizationDAO);

        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets a list of SubOrganisation organizations")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ORG_SUB_ORGANIZATION_OF_REL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<OrganizationSpecView>> getSubOrganizationOf(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        return generateOkResponse(organizationDAO.getSubOrganizationOf(), organizationSpecRestMapper, generateOrganizationHateoas(organizationDAO));

    }

    @ApiOperation(value = "Gets a list of (Accreditation) Organization from Organization")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ORG_ACCREDITATION,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<AccreditationSpecLiteView>> listOrgsAccreditation(
            @ApiParam(required = true, value = "The Accreditation oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        return generateListResponse(organizationDAO.getAccreditation(), "/specs", accreditationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing related (Accreditation) to Organization")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ORG_ACCREDITATION,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<AccreditationSpecLiteView>> setAccreditation(
            @ApiParam(required = true, value = "The Accreditation oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        OrganizationSpecDAO organizationDAO = organizationService.find(oid);
        if (organizationDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        Set<AccreditationSpecDAO> entities = retrieveEntities(accreditationSpecService, false, oids.getOid());

        organizationDAO.setAccreditation(entities);

        organizationService.save(organizationDAO);

        return generateListResponse(organizationDAO.getAccreditation(), "/specs", accreditationSpecRestMapper, LocaleContextHolder.getLocale().toString());
    }

    public Link[] generateOrganizationHateoas(OrganizationSpecDAO organizationDAO) {

        if (organizationDAO != null) {
            Link hateoasSelf = ControllerLinkBuilder.linkTo(OrganizationResource.class).slash(IssuerEndpoint.V1.SPECS).slash(organizationDAO.getPk()).withSelfRel();

            Link hateoasSubOrganizationOf = ControllerLinkBuilder.linkTo(OrganizationResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(organizationDAO.getPk())
                    .slash(IssuerEndpoint.V1.ORG_SUB_ORGANIZATION_OF_REL).withRel("subOrganizationOf");

            Link hateoasChildOrganisation = ControllerLinkBuilder.linkTo(OrganizationResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(organizationDAO.getPk())
                    .slash(IssuerEndpoint.V1.ORG_CHILD_ORGANIZATION_REL).withRel("childOrganisation");

            Link hateoasChildAccreditation = ControllerLinkBuilder.linkTo(OrganizationResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(organizationDAO.getPk())
                    .slash(IssuerEndpoint.V1.ORG_ACCREDITATION).withRel("accreditation");

            return new Link[]{
                    hateoasSelf, hateoasSubOrganizationOf, hateoasChildOrganisation, hateoasChildAccreditation
            };
        } else {
            return null;
        }
    }

}


