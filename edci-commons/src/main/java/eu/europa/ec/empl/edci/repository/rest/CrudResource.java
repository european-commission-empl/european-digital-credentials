package eu.europa.ec.empl.edci.repository.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Joiner;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import eu.europa.ec.empl.edci.repository.mapper.IRestMapper;
import eu.europa.ec.empl.edci.repository.mapper.InputsParser;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.repository.specifications.DAOSpecificationsBuilder;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import eu.europa.ec.empl.edci.repository.util.SearchOperation;
import eu.europa.ec.empl.edci.security.base.IEDCISecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.*;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface CrudResource {

    default <T extends IGenericDAO> Set<T> retrieveEntities(CrudService<T> linkObjectService, boolean checkMissingEntities, Collection<Long> oids) {

        return linkObjectService.retrieveEntities(checkMissingEntities, oids);

    }

    default <T extends IGenericDAO> T retrieveEntity(CrudService<T> linkObjectService, boolean checkMissingEntities, Long oid) {
        if (oid != null) {
            Set<T> result = retrieveEntities(linkObjectService, checkMissingEntities, oid);
            if (!result.isEmpty()) {
                return result.iterator().next();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    default <T extends IGenericDAO> Set<T> retrieveEntities(CrudService<T> linkObjectService, boolean checkMissingEntities, Long... oids) {

        return linkObjectService.retrieveEntities(checkMissingEntities, oids);

    }

    default <T> ResponseEntity<Resource<T>> generateResponse(T entity, HttpStatus status, HttpHeaders httpHeaders, Link... hateoas) {
        return new ResponseEntity<Resource<T>>(new Resource<T>(entity, hateoas), httpHeaders, status);
    }

    default <T> ResponseEntity<T> generateResponse(T entity, HttpStatus status, HttpHeaders httpHeaders) {
        return new ResponseEntity<T>(entity, httpHeaders, status);
    }

    default <T> ResponseEntity<T> generateResponse(T entity, HttpStatus status) {
        return new ResponseEntity<T>(entity, status);
    }

    default <T> ResponseEntity<Resource<T>> generateResponse(T entity, HttpStatus status, Link... hateoas) {
        return new ResponseEntity<Resource<T>>(new Resource<T>(entity, hateoas), status);
    }

    default <T> ResponseEntity<PagedResources<T>> generateListResponse(Page<T> page, String href) throws JsonProcessingException {
        PagedResources.PageMetadata meta = new PagedResources.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());

        PagedResources<T> pr = new PagedResources<>(page.getContent(), meta);

        mountSearchHateoas(this.getClass(), page, href, PageParam.NavAction.ALL).stream().forEach(h -> pr.add(h));

        return generateResponse(pr, HttpStatus.OK);

    }

    default <D, L> ResponseEntity<PagedResources<L>> generateListResponse(Collection<D> elements, String href, IRestMapper<D, ?, L> restMapper) throws JsonProcessingException {
        return generateListResponse(new PageImpl(elements != null ? new ArrayList(elements) : new ArrayList()), href, restMapper);
    }

    default <D, L> ResponseEntity<PagedResources<L>> generateListResponse(Page<D> page, String href, IRestMapper<D, ?, L> restMapper) throws JsonProcessingException {

        PagedResources.PageMetadata meta = new PagedResources.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());

        List<L> content = page.getContent().stream().map(restMapper::toVOLite).collect(Collectors.toList());

        PagedResources<L> pr = new PagedResources<>(content, meta);

        mountSearchHateoas(this.getClass(), page, href, PageParam.NavAction.ALL).stream().forEach(h -> pr.add(h));

        return generateResponse(pr, HttpStatus.OK);
    }

    default <D, V> ResponseEntity<Resource<V>> generateOkResponse(D entity, IRestMapper<D, V, ?> restMapper, Link... hateoas) {
        if (entity == null) {
            return generateNoContentResponse();//This should be a 404, but, given the correct situation, a No Content will be returned
        }
        return generateResponse(restMapper.toVO(entity), HttpStatus.OK, hateoas);
    }

    default <D, V> ResponseEntity generateCreatedResponse(D entity, IRestMapper<D, V, ?> restMapper, Link... hateoas) {
        return generateResponse(restMapper.toVO(entity), HttpStatus.CREATED, hateoas);
    }

    default ResponseEntity generateOkResponse(Object entity, Link... hateoas) {
        return generateResponse(entity, HttpStatus.OK, hateoas);
    }

    default ResponseEntity generateCreatedResponse(Object entity, Link... hateoas) {
        return generateResponse(entity, HttpStatus.CREATED, hateoas);
    }

    default ResponseEntity generateNoContentResponse() {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    default ResponseEntity generateNotFoundResponse(Object entity) {
        return new ResponseEntity(entity, HttpStatus.NOT_FOUND);
    }


    default Link generateHateoas(String rel, Class<? extends CrudResource> resource, String href, TemplateVariable.VariableType templateVariablesType, String... templateVariables) {
        TemplateVariables variables = new TemplateVariables(
                Arrays.stream(templateVariables).map(temp -> new TemplateVariable(temp, templateVariablesType))
                        .collect(Collectors.toList()));
        ;
        return new Link(new UriTemplate(ControllerLinkBuilder.linkTo(resource).slash(href).toString(), variables), rel);
    }

    default Link generateSelfHateoas(Class<? extends CrudResource> resource, String href, TemplateVariable.VariableType templateVariablesType, String... templateVariables) {
        return generateHateoas("self", resource, href, templateVariablesType, templateVariables);
    }

    default Link generateHateoas(String rel, Class<? extends CrudResource> resource, String href) {
        return new Link(new UriTemplate(ControllerLinkBuilder.linkTo(resource).slash(href).toString()), rel);
    }

    default Link generateSelfHateoas(Class<? extends CrudResource> resource, String href) {
        return generateHateoas("self", resource, href);
    }

    default Specification buildSearchSpecification(String search, IRestMapper specRestMapper, IEDCISecurityContextHolder edciUserHolder) {

        DAOSpecificationsBuilder builder = new DAOSpecificationsBuilder<>(edciUserHolder);

        if (search != null) {

            String operationSetExper = Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET);
            Pattern pattern = Pattern.compile("(\\p{Punct}?)(\\w[\\.\\w]+?)(" + operationSetExper + ")([^;]*)(\\b)\\s*;");
            Matcher matcher = pattern.matcher(search + ";");
            while (matcher.find()) {
                String field = InputsParser.parseViewSearchFields(matcher.group(2), specRestMapper);
                builder.with(matcher.group(1), field, matcher.group(3), matcher.group(4).trim(), null, null);
            }

            if (builder.build() == null) {
                throw new EDCIBadRequestException().addDescription("Incorrect search params");
            }

        }

        return builder.build();

    }

    default Specification buildSearchSpecification(String search) {

        return buildSearchSpecification(search, null, null);

    }

    default List<Link> mountSearchHateoas(Class<? extends CrudResource> resource, Page page, String href, PageParam.NavAction action) {

        List<Link> returnValue = new ArrayList<>();
        String result = "?";
        String sPage = "page=";
        String sSize = PageParam.SIZE_PAGE_DEFAULT == page.getSize() ? "" : "&size=".concat(String.valueOf(page.getSize()));
        String sSortAsc =
                page.getSort() != null ?
                        page.getSort().getOrderFor(Sort.Direction.ASC.name()) != null ?
                                "&sort=".concat(String.valueOf(page.getSort().getOrderFor(Sort.Direction.ASC.name()).getProperty())).concat("&direction=ASC") : null
                        : null;
        String sSortDesc =
                page.getSort() != null ?
                        page.getSort().getOrderFor(Sort.Direction.DESC.name()) != null ?
                                "&sort=".concat(String.valueOf(page.getSort().getOrderFor(Sort.Direction.DESC.name()).getProperty())).concat("&direction=DESC") : null
                        : null;
        ;


        switch (action) {

            case ALL:
                //self
                returnValue.add(generateHateoas("self", resource, href,
                        TemplateVariable.VariableType.REQUEST_PARAM, "page", "size", "sort", "search"));
                returnValue.addAll(mountSearchHateoas(resource, page, href, PageParam.NavAction.FIRST));
                returnValue.addAll(mountSearchHateoas(resource, page, href, PageParam.NavAction.LAST));
                returnValue.addAll(mountSearchHateoas(resource, page, href, PageParam.NavAction.NEXT));
                returnValue.addAll(mountSearchHateoas(resource, page, href, PageParam.NavAction.PREV));
                break;
            case FIRST:
                result += sPage.concat("0");
                result += sSize;
                result += sSortAsc != null ? sSortAsc : "";
                result += sSortDesc != null ? sSortDesc : "";
                returnValue.add(generateHateoas(PageParam.NavAction.FIRST.name().toLowerCase(),
                        resource, href.concat(result)));
                break;
            case NEXT:
                int nextPage = page.getNumber() + 1;
                if (nextPage < page.getTotalPages()) {
                    result += sPage.concat(String.valueOf(nextPage));
                    result += sSize;
                    result += sSortAsc != null ? sSortAsc : "";
                    result += sSortDesc != null ? sSortDesc : "";
                    returnValue.add(generateHateoas(PageParam.NavAction.NEXT.name().toLowerCase(),
                            resource, href.concat(result)));
                }
                break;
            case PREV:
                int prevPage = page.getNumber() - 1;
                if (prevPage >= 0) {
                    result += sPage.concat(String.valueOf(prevPage));
                    result += sSize;
                    result += sSortAsc != null ? sSortAsc : "";
                    result += sSortDesc != null ? sSortDesc : "";
                    returnValue.add(generateHateoas(PageParam.NavAction.PREV.name().toLowerCase(),
                            resource, href.concat(result)));
                }
                break;
            case LAST:
                int lastPage = page.getTotalPages() - 1;
                if (lastPage >= 0) {
                    result += sPage.concat(String.valueOf(lastPage));
                    result += sSize;
                    result += sSortAsc != null ? sSortAsc : "";
                    result += sSortDesc != null ? sSortDesc : "";
                    returnValue.add(generateHateoas(PageParam.NavAction.LAST.name().toLowerCase(),
                            resource, href.concat(result)));
                }
                break;
        }

        return returnValue.stream().filter(Objects::nonNull).collect(Collectors.toList());

    }

}
