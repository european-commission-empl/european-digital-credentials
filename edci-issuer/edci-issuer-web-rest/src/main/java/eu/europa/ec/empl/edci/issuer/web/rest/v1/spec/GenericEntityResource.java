package eu.europa.ec.empl.edci.issuer.web.rest.v1.spec;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerEndpoint;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.issuer.service.spec.EscoBridgeService;
import eu.europa.ec.empl.edci.issuer.utils.ecso.EscoElementPayload;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.GenericEntitiyRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.repository.rest.CrudResource;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.RDFsparqlBridgeService;
import eu.europa.ec.empl.edci.util.EDCIRestRequestBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@Api(tags = {
        "V1"
})
@Controller(value = "v1.GenericEntityResource")
@RequestMapping(value = EDCIConstants.Version.V1 + IssuerEndpoint.V1.GENERIC_ENTITIES_BASE)
@CrossOrigin(origins = "*", methods = {RequestMethod.GET})
public class GenericEntityResource implements CrudResource {

    @Autowired
    private EscoBridgeService escoBridgeService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private RDFsparqlBridgeService rdfSparqlBridgeService;

    @Autowired
    private GenericEntitiyRestMapper genericEntitiyRestMapper;

    @Autowired
    private IssuerConfigService issuerConfigService;

    @ApiOperation(value = "Get some resources by uri")
    @GetMapping(value = IssuerEndpoint.V1.GENERIC_ENTITY + Parameter.Path.TYPE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public List<CodeDTView> listEntitiesByUri(
            @ApiParam(required = true, value = "The entity type") @PathVariable(Parameter.TYPE) String type,
            @ApiParam() @RequestParam(value = Parameter.URIS, required = true) String uris,
            @ApiParam() @RequestParam(value = Parameter.REQ_LANGS, required = false) String requestedLangs,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        List<CodeDTView> returnValue = new ArrayList();

        List<String> rdfTypes = Arrays.stream(ControlledList.values()).map(rdf -> rdf.getName()).collect(Collectors.toList());

        String[] uriList = null;
        if (!StringUtils.isEmpty(uris)) {
            try {
                uriList = uris.split(",");
            } catch (Exception e) {
                uriList = new String[]{};
            }
        }

        List<String> reqLangs = null;
        if (!StringUtils.isEmpty(requestedLangs)) {
            try {
                reqLangs = Arrays.asList(requestedLangs.split(","));
            } catch (Exception e) {
                reqLangs = new ArrayList<>();
            }
        }

        //ESCO
        if (EscoBridgeService.EscoList.SKILL.getType().equalsIgnoreCase(type) || EscoBridgeService.EscoList.OCCUPATION.getType().equalsIgnoreCase(type)) {

            try {
                //TODO: Clean langs
                returnValue = escoBridgeService.searchEscoElements(type.toLowerCase(), LocaleContextHolder.getLocale().getLanguage(), reqLangs, uriList).stream()
                        .map(dao -> genericEntitiyRestMapper.toCodeView(dao)).collect(Collectors.toList());
            } catch (Exception e) {
                throw new EDCIException("controlled.list.service.error", type).setCause(e);
            }
        //RDF
        } else if (rdfTypes.contains(type)) {

            ControlledList clSel = ControlledList.getByName(type);
            try {
                returnValue = controlledListCommonsService.searchConceptsByUri(clSel.getUrl(), LocaleContextHolder.getLocale().getLanguage(), reqLangs, uriList).stream()
                    .map(dao -> genericEntitiyRestMapper.toCodeView(dao)).collect(Collectors.toList());
            } catch (Exception e) {
                throw new EDCIException("controlled.list.service.error", type).setCause(e);
            }

        } else {

            throw new EDCIBadRequestException("Types enabled so far: 'skill', 'occupation', " + rdfTypes.stream().collect(Collectors.joining(", ")));

        }

        return returnValue;
    }

    @ApiOperation(value = "Gets a list of resources")
    @GetMapping(value = Parameter.Path.TYPE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page> listEntities(
            @ApiParam(required = true, value = "The entity type") @PathVariable(Parameter.TYPE) String type,
            @ApiParam() @RequestParam(value = Parameter.PAGE, required = false, defaultValue = "0") Integer page,
            @ApiParam() @RequestParam(value = Parameter.SIZE, required = false, defaultValue = PageParam.SIZE_PAGE_DEFAULT + "") Integer size,
            @ApiParam() @RequestParam(value = Parameter.SORT, required = false) String sort,
            @ApiParam() @RequestParam(value = Parameter.DIRECTION, required = false) String dir,
            @ApiParam() @RequestParam(value = Parameter.SEARCH, required = false) String search,
            @ApiParam() @RequestParam(value = Parameter.PARENT, required = false) String parent,
            @ApiParam() @RequestParam(value = Parameter.REQ_LANGS, required = false) String requestedLangs,
            @ApiParam(value = "locale") @RequestParam(value = Parameter.LOCALE, required = false) String locale
    ) throws Exception {

        Page returnValue = new PageImpl(new ArrayList());

        List<String> rdfTypes = Arrays.stream(ControlledList.values()).map(rdf -> rdf.getName()).collect(Collectors.toList());

        if (sort != null || dir != null) {
            throw new EDCIBadRequestException().addDescription("Sorting not enabled for this resource type (" + type + ")");
        }

        List<String> reqLangs = null;
        if (!StringUtils.isEmpty(requestedLangs)) {
            try {
                reqLangs = Arrays.asList(requestedLangs.split(","));
            } catch (Exception e) {
                reqLangs = new ArrayList<>();
            }
        } else {
            reqLangs = new ArrayList<>();
        }

        //ESCO
        if ("skill".equalsIgnoreCase(type) || "occupation".equalsIgnoreCase(type)) {

            if (reqLangs.isEmpty()) {
                reqLangs = ControlledListCommonsService.ALLOWED_LANGS;
            }
            returnValue = escoBridgeService.searchEscoElements(type.toLowerCase(), search, LocaleContextHolder.getLocale().getLanguage(), reqLangs, page, size)
                    .map(dao -> genericEntitiyRestMapper.toCodeView(dao));

            //RDF
        } else if (rdfTypes.contains(type)) {

            ControlledList clSel = ControlledList.getByName(type);

            PageParam pageParam = new PageParam(page, size);
            if (ControlledList.ISCED_F.getName().equalsIgnoreCase(type)) {
                List elems = controlledListCommonsService.toCode(clSel.getUrl(), rdfSparqlBridgeService.searchISCEDFTreeConcepts(clSel.getUrl(), search, LocaleContextHolder.getLocale().getLanguage(), reqLangs), reqLangs);
                returnValue = new PageImpl<Code>(
                        elems,
                        pageParam.toPageRequest(),
                        elems.size());
            } else if (ControlledList.NQF.getName().equalsIgnoreCase(type) || !StringUtils.isEmpty(parent)) {
                List elems = controlledListCommonsService.toCode(clSel.getUrl(), rdfSparqlBridgeService.searchBroaderConcepts(clSel.getUrl(), parent, search, LocaleContextHolder.getLocale().getLanguage(), page, size, reqLangs), reqLangs);
                returnValue = new PageImpl<Code>(
                        elems,
                        pageParam.toPageRequest(),
                        elems.size());
            } else {
                returnValue = controlledListCommonsService.searchConcepts(clSel.getUrl(), search, LocaleContextHolder.getLocale().getLanguage(), page, size, reqLangs)
                        .map(dao -> genericEntitiyRestMapper.toCodeView(dao));
            }

        } else {

            throw new EDCIBadRequestException("Types enabled so far: 'skill', 'occupation', " + rdfTypes.stream().collect(Collectors.joining(", ")));

        }

        return generateListResponse(returnValue, "/specs");
    }

    @ApiOperation(value = "Checks the external services availability", hidden = true)
    @GetMapping(value = "/check",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> checkExternalServices(
    ) throws Exception {

        boolean error = false;
        Map<String, String> response = new HashMap<>();

        try {
            Object controlledListReturn = rdfSparqlBridgeService.searchConceptScheme(ControlledList.FILE_TYPE.getUrl());
            if (controlledListReturn == null) {
                throw new Exception("Search of concept scheme file-type returned no results");
            }
            response.put("Controlled List", "OK");
        } catch (Exception e) {
            error = true;
            response.put("Controlled List", "Controlled lists service is down: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }

        try {
            byte[] helloWorldBytes = new EDCIRestRequestBuilder(HttpMethod.POST, "https://webgate.acceptance.ec.europa.eu/europass/eportfolio/api/office/generate/pdf")
                    .addHeaderRequestedWith()
                    .addHeaders(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_PDF)
                    .addBody(EDCIRestRequestBuilder.prepareMultiPartStringBody("html", "<html>\n" +
                            "    <head>\n" +
                            "        <title>Hello</title>\n" +
                            "    </head>\n" +
                            "    <body>\n" +
                            "        <p>Hello world</p>\n" +
                            "    </body>\n" +
                            "</html>", new HashMap<>()))
                    .buildRequest(byte[].class)
                    .execute();
            if (helloWorldBytes == null) {
                throw new Exception("Trying to convert hello world HTML to PDF retuned an empty array of bytes");
            }
            response.put("WeasyPrint Acceptance", "OK");
        } catch (Exception e) {
            error = true;
            response.put("WeasyPrint Acceptance", "HTML to PDF weasyprint service is down: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }

        try {
            byte[] helloWorldBytes = new EDCIRestRequestBuilder(HttpMethod.POST, "https://europa.eu/europass/eportfolio/api/office/generate/pdf")
                    .addHeaderRequestedWith()
                    .addHeaders(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_PDF)
                    .addBody(EDCIRestRequestBuilder.prepareMultiPartStringBody("html", "<html>\n" +
                            "    <head>\n" +
                            "        <title>Hello</title>\n" +
                            "    </head>\n" +
                            "    <body>\n" +
                            "        <p>Hello world</p>\n" +
                            "    </body>\n" +
                            "</html>", new HashMap<>()))
                    .buildRequest(byte[].class)
                    .execute();
            if (helloWorldBytes == null) {
                throw new Exception("Trying to convert hello world HTML to PDF retuned an empty array of bytes");
            }
            response.put("WeasyPrint Production", "OK");
        } catch (Exception e) {
            error = true;
            response.put("WeasyPrint Production", "HTML to PDF weasyprint service is down: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }

        try {
            Object escoReturn = escoBridgeService.searchEsco(EscoElementPayload.class, "skill", "en", "http://data.europa.eu/esco/skill/a59708e3-e654-4e37-8b8a-741c3b756eee");
            if (escoReturn == null) {
                throw new Exception("Search of a random ESCO (http://data.europa.eu/esco/skill/a59708e3-e654-4e37-8b8a-741c3b756eee) skill returned no results");
            }
            response.put("ESCO", "OK");
        } catch (Exception e) {
            error = true;
            response.put("ESCO", "ESCO service is down: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }
        if (error) {
            return generateResponse(response, HttpStatus.SERVICE_UNAVAILABLE);
        } else {
            return generateOkResponse(response);
        }
    }

}


