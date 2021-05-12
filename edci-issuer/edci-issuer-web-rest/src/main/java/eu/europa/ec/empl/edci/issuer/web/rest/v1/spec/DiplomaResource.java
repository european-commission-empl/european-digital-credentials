package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.Version;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.Endpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.entity.specs.DiplomaSpecDAO;
import eu.europa.ec.empl.edci.issuer.service.spec.EntitlementSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.LearningAchievementSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.LearningActivitySpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.DiplomaSpecService;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.DiplomaSpecRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.specs.DiplomaSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.DiplomaSpecLiteView;
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
@Controller(value = "v1.DiplomaSpecResource")
@RequestMapping(value = Version.V1 + Endpoint.V1.DIPLOMA_BASE)
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class DiplomaResource implements CrudResource {

    @Autowired
    private DiplomaSpecService diplomaService;

    @Autowired
    private DiplomaSpecRestMapper diplomaSpecRestMapper;

    @Autowired
    private EDCISecurityContextHolder edciUserHolder;

    @ApiOperation(value = "Create an diploma spec")
    @PostMapping(value = Endpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<DiplomaSpecView>> createDiploma(@RequestBody @Valid DiplomaSpecView diplomaView,
                                                                             @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        final DiplomaSpecDAO diplomaDAO = diplomaSpecRestMapper.toDAO(diplomaView);

        DiplomaSpecDAO diplomaCreatedDAO = diplomaService.save(diplomaDAO);

        return generateOkResponse(diplomaCreatedDAO, diplomaSpecRestMapper, generateDiplomaHateoas(diplomaCreatedDAO));
    }

    @ApiOperation(value = "Duplicate a activity spec")
    @PostMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<DiplomaSpecView>> duplicateDiploma(
            @ApiParam(required = true, value = "The Diploma oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        DiplomaSpecDAO diplomaDAO = diplomaService.clone(oid, diplomaSpecRestMapper);

        return generateOkResponse(diplomaDAO, diplomaSpecRestMapper, generateDiplomaHateoas(diplomaDAO));
    }

    @ApiOperation(value = "Update an diploma spec")
    @PutMapping(value = Endpoint.V1.SPECS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<DiplomaSpecView>> updateDiploma(
            @RequestBody @Valid DiplomaSpecView diplomaView,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {

        if (!diplomaService.exists(diplomaView.getOid())) {
            throw new EDCINotFoundException().addDescription("Diploma with.OID [" + diplomaView.getOid() + "] not found");
        } else {
            diplomaView.setOid(diplomaView.getOid());
        }

        final DiplomaSpecDAO diplomaDAO = diplomaSpecRestMapper.toDAO(diplomaView);

        DiplomaSpecDAO diplomaCreatedDAO = diplomaService.save(diplomaDAO);

        return generateOkResponse(diplomaCreatedDAO, diplomaSpecRestMapper, generateDiplomaHateoas(diplomaCreatedDAO));
    }

    @ApiOperation(value = "Upload a diploma background")
    @PostMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID + Endpoint.V1.BACKGROUND,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity addBackground(
            @ApiParam(required = true, value = "The Diploma oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(required = true, value = "The Logo file") @RequestPart(Parameter.FILE) MultipartFile file) {

        diplomaService.addBackground(oid, file);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Delete an diploma spec")
    @DeleteMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteDiploma(
            @ApiParam(required = true, value = "The Diploma oid") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale) throws Exception {
        boolean removed = diplomaService.delete(oid);
        return generateNoContentResponse();
    }

    @ApiOperation(value = "Gets an diploma")
    @GetMapping(value = Endpoint.V1.SPECS + Parameter.Path.OID,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Resource<DiplomaSpecView>> getDiploma(
            @ApiParam(required = true, value = "The Diploma.OID") @PathVariable(Parameter.OID) Long oid,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        DiplomaSpecDAO diplomaDAO = diplomaService.find(oid);
        if (diplomaDAO == null) {
            throw new EDCINotFoundException().addDescription("Diploma with oid [" + oid + "] not found");
        }
        return generateOkResponse(diplomaDAO, diplomaSpecRestMapper, generateDiplomaHateoas(diplomaDAO));
    }

    @ApiOperation(value = "Gets a list of diplomas")
    @GetMapping(value = Endpoint.V1.SPECS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<DiplomaSpecLiteView>> listDiploma(
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false, defaultValue = "ASC") String direction,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SEARCH, required = false) String search,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {
        PageParam pageParam = new PageParam(page, size, sort, direction);
        Specification specif = buildSearchSpecification(search, diplomaSpecRestMapper, edciUserHolder);

        Page<DiplomaSpecLiteView> products = diplomaService.findAll(specif, pageParam.toPageRequest(), diplomaSpecRestMapper);
        return generateListResponse(products, "/specs");
    }

    public Link[] generateDiplomaHateoas(DiplomaSpecDAO diplomaDAO) {

        if (diplomaDAO != null) {
            Link hateoasSelf = ControllerLinkBuilder.linkTo(DiplomaResource.class).slash(Endpoint.V1.SPECS).slash(diplomaDAO.getPk()).withSelfRel();

            return new Link[]{
                    hateoasSelf
            };
        } else {
            return null;
        }
    }

}


