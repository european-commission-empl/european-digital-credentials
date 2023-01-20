package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.EntitlemSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EntitlementSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.issuer.service.spec.EntitlementSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.LearningOutcomeSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.OrganizationSpecService;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.EntitlementSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.LearningOutcomeSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.OrganizationSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.specs.EntitlementSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.EntitlementSpecLiteView;
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
@Controller(value = "v1.EntitlementSpecResource")
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.ENTITLEMENTS_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class EntitlementResource implements CrudResource {

    @Autowired
    private EntitlementSpecService entitlementService;

    @Autowired
    private EntitlementSpecRestMapper entitlementSpecRestMapper;

    @Autowired
    private EDCISecurityContextHolder edciUserHolder;

    @Autowired
    private OrganizationSpecService organizationService;

    @Autowired
    private OrganizationSpecRestMapper organizationSpecRestMapper;

    @Autowired
    private EntitlementSpecService entitlementSpecService;

    @Autowired
    private OrganizationSpecService organizationSpecService;

    @Autowired
    private LearningOutcomeSpecService learningOutcomeSpecService;

    @Autowired
    private LearningOutcomeSpecRestMapper learningOutcomeSpecRestMapper;

    @ApiOperation(value = "Create an entitlement spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<EntitlementSpecView>> createEntitlement(@RequestBody @Valid EntitlementSpecView entitlementView,
                                                                           @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        final EntitlementSpecDAO entitlementDAO = entitlementSpecRestMapper.toDAO(entitlementView);

        EntitlementSpecDAO entitlementCreatedDAO = entitlementService.save(entitlementDAO,
                () -> {
                    entitlementDAO.setSpecifiedBy(entitlementDAO.getSpecifiedBy() == null ? new EntitlemSpecificationDCDAO() : entitlementDAO.getSpecifiedBy());
                    entitlementDAO.setHasPart(entitlementService.retrieveEntities(false, entitlementView.getRelHasPart().getOid()));
                    entitlementDAO.getSpecifiedBy().setLimitOrganization(organizationSpecService.retrieveEntities(false, entitlementView.getRelValidWith().getOid()));
                }
        );

        return generateOkResponse(entitlementCreatedDAO, entitlementSpecRestMapper, generateEntitlementHateoas(entitlementCreatedDAO));
    }

    @ApiOperation(value = "Duplicate an activity spec")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<EntitlementSpecView>> duplicateEntitlement(
            @ApiParam(required = true, value = "The Credential oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EntitlementSpecDAO entitlementDAO = entitlementService.clone(oid, entitlementSpecRestMapper);

        return generateOkResponse(entitlementDAO, entitlementSpecRestMapper, generateEntitlementHateoas(entitlementDAO));
    }

    @ApiOperation(value = "Update an entitlement spec")
    @PutMapping(value = IssuerEndpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<EntitlementSpecView>> updateEntitlement(
            @RequestBody @Valid EntitlementSpecView entitlementView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        if (!entitlementService.exists(entitlementView.getOid())) {
            throw new EDCINotFoundException().addDescription("Entitlement with.OID [" + entitlementView.getOid() + "] not found");
        } else {
            entitlementView.setOid(entitlementView.getOid());
        }

        final EntitlementSpecDAO entitlementDAO = entitlementSpecRestMapper.toDAO(entitlementView);

        EntitlementSpecDAO entitlementCreatedDAO = entitlementService.save(entitlementDAO,
                () -> {
                    entitlementDAO.setSpecifiedBy(entitlementDAO.getSpecifiedBy() == null ? new EntitlemSpecificationDCDAO() : entitlementDAO.getSpecifiedBy());
                    entitlementDAO.setHasPart(entitlementService.retrieveEntities(false, entitlementView.getRelHasPart().getOid()));
                    entitlementDAO.getSpecifiedBy().setLimitOrganization(organizationSpecService.retrieveEntities(false, entitlementView.getRelValidWith().getOid()));
                }
        );

        return generateOkResponse(entitlementCreatedDAO, entitlementSpecRestMapper, generateEntitlementHateoas(entitlementCreatedDAO));
    }

    @ApiOperation(value = "Delete an entitlement spec")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteEntitlement(
            @ApiParam(required = true, value = "The Entitlement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        boolean removed = entitlementService.delete(oid);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets an entitlement")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<EntitlementSpecView>> getEntitlement(
            @ApiParam(required = true, value = "The Entitlement.OID") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        EntitlementSpecDAO entitlementDAO = entitlementService.find(oid);
        if (entitlementDAO == null) {
            throw new EDCINotFoundException().addDescription("Entitlement with oid [" + oid + "] not found");
        }
        return generateOkResponse(entitlementDAO, entitlementSpecRestMapper, generateEntitlementHateoas(entitlementDAO));
    }

    @ApiOperation(value = "Gets a list of entitlements")
    @GetMapping(value = IssuerEndpoint.V1.SPECS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<EntitlementSpecLiteView>> listEntitlement(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SEARCH, required = false) String search,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        PageParam pageParam = new PageParam(page, size, sort, direction);
        Specification specif = buildSearchSpecification(search, entitlementSpecRestMapper, edciUserHolder);

        Page<EntitlementSpecLiteView> products = entitlementService.findAll(specif, pageParam.toPageRequest(), entitlementSpecRestMapper);
        return generateListResponse(products, "/specs");
    }

    @ApiOperation(value = "Gets a list of (hasPart) Entitlements from entitlements")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ENT_HAS_PART,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<EntitlementSpecLiteView>> listHasEntPart(
            @ApiParam(required = true, value = "The Entitlement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        PageParam pageParam = new PageParam(0, 1);

        EntitlementSpecDAO entitlementDAO = entitlementService.find(oid);
        if (entitlementDAO == null) {
            throw new EDCINotFoundException().addDescription("Entitlement with oid [" + oid + "] not found");
        }

        return generateListResponse(entitlementDAO.getHasPart(), "/specs", entitlementSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Link an existing related (hasPart) Entitlements to a entitlement")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ENT_HAS_PART,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<EntitlementSpecLiteView>> setHasEntPart(
            @ApiParam(required = true, value = "The Entitlement oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EntitlementSpecDAO entitlementDAO = entitlementService.find(oid);
        if (entitlementDAO == null) {
            throw new EDCINotFoundException().addDescription("Entitlement with oid [" + oid + "] not found");
        }

        Set<EntitlementSpecDAO> entities = retrieveEntities(entitlementService, false, oids.getOid());

        entitlementDAO.setHasPart(entities);

        entitlementService.save(entitlementDAO);

        return generateListResponse(entitlementDAO.getHasPart(), "/specs", entitlementSpecRestMapper, LocaleContextHolder.getLocale().toString());
    }

    @ApiOperation(value = "Gets a subresource (Organization)")
    @GetMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ENT_VALID_WITH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> getValidWith(
            @ApiParam(required = true, value = "The Organization oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        EntitlementSpecDAO entitlementDAO = entitlementService.find(oid);
        if (entitlementDAO == null) {
            throw new EDCINotFoundException().addDescription("Entitlement with oid [" + oid + "] not found");
        }

        Set<OrganizationSpecDAO> orgSpecs = null;

        if (entitlementDAO.getSpecifiedBy() != null && entitlementDAO.getSpecifiedBy().getLimitOrganization() != null) {
            orgSpecs = entitlementDAO.getSpecifiedBy().getLimitOrganization();
        }

        return generateListResponse(orgSpecs, "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());
    }


    @ApiOperation(value = "Link an existing subresource (Organization)")
    @PostMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ENT_VALID_WITH,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<OrganizationSpecLiteView>> setValidWith(
            @ApiParam(required = true, value = "The Entitlement oid") @PathVariable(Parameter.OID) Long oid,
            @RequestBody SubresourcesOids oids,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EntitlementSpecDAO entitlementDAO = entitlementService.find(oid);
        if (entitlementDAO == null) {
            throw new EDCINotFoundException().addDescription("Entitlement with oid [" + oid + "] not found");
        }

        if (entitlementDAO.getSpecifiedBy() == null) {
            entitlementDAO.setSpecifiedBy(new EntitlemSpecificationDCDAO());
        }

        Set<OrganizationSpecDAO> entities = retrieveEntities(organizationService, true, oids.getOid());

        entitlementDAO.getSpecifiedBy().setLimitOrganization(entities);

        entitlementService.save(entitlementDAO);

        return generateListResponse(entitlementDAO.getSpecifiedBy().getLimitOrganization(), "/specs", organizationSpecRestMapper, LocaleContextHolder.getLocale().toString());

    }

    @ApiOperation(value = "Deletes an existing subresource (Organization)")
    @DeleteMapping(value = IssuerEndpoint.V1.SPECS + Parameter.Path.OID + IssuerEndpoint.V1.ENT_VALID_WITH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteProvenBy(
            @ApiParam(required = true, value = "The LearningAchievement oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        EntitlementSpecDAO entitlementDAO = entitlementService.find(oid);
        if (entitlementDAO == null) {
            throw new EDCINotFoundException().addDescription("Entitlement with oid [" + oid + "] not found");
        }

        if (entitlementDAO.getSpecifiedBy() != null && entitlementDAO.getSpecifiedBy().getLimitOrganization() != null
                && entitlementDAO.getSpecifiedBy().getLimitOrganization().size() > 0) {
            entitlementDAO.getSpecifiedBy().getLimitOrganization().clear();
            entitlementService.save(entitlementDAO);
        }

        return generateNoContentResponse();
    }


    public Link[] generateEntitlementHateoas(EntitlementSpecDAO entitlementDAO) {

        if (entitlementDAO != null) {

            Link hateoasSelf = ControllerLinkBuilder.linkTo(EntitlementResource.class).slash(IssuerEndpoint.V1.SPECS).slash(entitlementDAO.getPk()).withSelfRel();

            Link hateoasHasPart = ControllerLinkBuilder.linkTo(EntitlementResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(entitlementDAO.getPk())
                    .slash(IssuerEndpoint.V1.ENT_HAS_PART).withRel("hasPart");

            Link hateoasvalidWith = ControllerLinkBuilder.linkTo(EntitlementResource.class)
                    .slash(IssuerEndpoint.V1.SPECS).slash(entitlementDAO.getPk())
                    .slash(IssuerEndpoint.V1.ENT_VALID_WITH).withRel("validWith");

            return new Link[]{
                    hateoasSelf, hateoasHasPart, hateoasvalidWith
            };

        } else {
            return null;
        }
    }

}