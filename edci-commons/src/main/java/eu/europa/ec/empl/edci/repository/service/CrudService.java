package eu.europa.ec.empl.edci.repository.service;

import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.repository.entity.IAuditedDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import eu.europa.ec.empl.edci.repository.entity.IOCBIdentifiedDAO;
import eu.europa.ec.empl.edci.repository.mapper.IRestMapper;
import eu.europa.ec.empl.edci.repository.mapper.InputsParser;
import eu.europa.ec.empl.edci.repository.specifications.DAOSpecSpecification;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import eu.europa.ec.empl.edci.repository.util.SearchOperation;
import eu.europa.ec.empl.edci.repository.util.SpecSearchCriteria;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public interface CrudService<T extends IGenericDAO> {

    static final Logger logger = LogManager.getLogger(CrudService.class);

    EDCIUserService getEDCIUserService();

    JpaRepository<T, Long> getRepository();

    default T filterByUser(T entity) {

        T returnEntity = null;

        if (getEDCIUserService() == null) {
            returnEntity = entity;
        }

        if (entity instanceof IAuditedDAO && getEDCIUserService().getUserId().equals(((IAuditedDAO) entity).getAuditDAO().getCreateUserId())) {
            returnEntity = entity;
        }

        return returnEntity;

    }

    default Page findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    default Set<T> findAll(Iterable<Long> iterable) {

        //Bug https://jira.spring.io/browse/DATAJPA-433 -> findAll<Iterator> error...
        Set<T> result = new HashSet<>();

        for (Long pk : iterable) {
            try {
                result.add(find(pk));
            } catch (Exception e) {
                logger.error("Element with PK: " + pk + " not found", e);
            }
        }


        return result;
    }

    default <V, L> Page<L> findAll(Pageable pageable, IRestMapper<T, V, L> mapper) {
        Pageable pageableaux = InputsParser.parseViewSortFields(pageable, mapper);
        return findAll(null, pageableaux).map(mapper::toVOLite);
    }

    default <V, L> Page<L> findAll(Specification<T> specification, Pageable pageable, IRestMapper<T, V, L> mapper) {
        Pageable pageableaux = InputsParser.parseViewSortFields(pageable, mapper);
        return findAll(specification, pageableaux)
                .map(mapper::toVOLite);
    }

    default Page<T> findAll(Specification<T> specification, Pageable pageable) {
        if (specification instanceof DAOSpecSpecification) {
            if (pageable.getSort() != null) {
                for (Sort.Order ord : pageable.getSort()) {
                    ((DAOSpecSpecification<T>) specification).addCriteria(new SpecSearchCriteria(ord.getProperty(), SearchOperation.ORDER_BY, ord.getDirection().name()));
                }
            }
        }
        return ((JpaSpecificationExecutor<T>) getRepository()).findAll(specification,
                new PageParam(pageable.getPageNumber(), pageable.getPageSize()).toPageRequest());
    }

    default T find(Long id) {

        return getRepository().existsById(id) ? filterByUser(getRepository().findById(id).orElse(null)) : null;

    }

    default boolean exists(Long id) {
        if (id == null) return false;
        return getRepository().existsById(id) && filterByUser(getRepository().findById(id).orElse(null)) != null;
    }

    default T save(T objectDAO, Runnable... postCreateActions) {

        if (objectDAO instanceof IAuditedDAO && objectDAO.getPk() != null) {
            IAuditedDAO d = (IAuditedDAO) getRepository().getOne(objectDAO.getPk());
            ((IAuditedDAO) objectDAO).setAuditDAO(d.getAuditDAO());
        }

        if (objectDAO instanceof IOCBIdentifiedDAO && objectDAO.getPk() != null) {
            IOCBIdentifiedDAO ocbid = (IOCBIdentifiedDAO) getRepository().getOne(objectDAO.getPk());
            ((IOCBIdentifiedDAO) objectDAO).setOCBID(ocbid.getOCBID());
        }

        if (postCreateActions != null && postCreateActions.length > 0) {
            getRepository().save(objectDAO);

            Arrays.stream(postCreateActions).forEach(Runnable::run);
        }

        return getRepository().save(objectDAO);
    }

    default boolean delete(T obj) {
        if (obj != null) {
            return delete(obj.getPk());
        } else {
            return true;
        }
    }

    default boolean delete(Long id) {
        getRepository().deleteById(id);
        return !getRepository().existsById(id);
    }

    default Set<T> retrieveEntities(boolean checkMissingEntities, Long... oids) {

        Set<T> entites = findAll(Arrays.asList(oids));

        if (checkMissingEntities) {

            entites.remove(null);

            Set<Long> allEntitiesPK = new HashSet<>(Arrays.asList(oids));
            Set<Long> allExistingPK = entites.stream().map(IGenericDAO::getPk).collect(Collectors.toSet());

            allEntitiesPK.removeAll(allExistingPK);

            if (!allEntitiesPK.isEmpty()) {
                throw new EDCINotFoundException().addDescription("Some of the resources can not be found. ("
                        + allEntitiesPK.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")");
            }
        }

        entites.remove(null);

        return entites;

    }

    default Set<T> retrieveEntities(boolean checkMissingEntities, Collection<Long> oids) {

        Set<T> entites = new HashSet<>();
        if (oids != null && !oids.isEmpty()) {
            entites = retrieveEntities(checkMissingEntities, oids.toArray(new Long[0]));
        }
        return entites;

    }

    default String generateTitleDuplicated(String title) {
        String duplicatedSubstring = " - DUPLICATED";
        if (title.length() >= (255 - duplicatedSubstring.length())) {
            return title.substring(0, 255 - duplicatedSubstring.length()) + duplicatedSubstring;
        }
        return title + duplicatedSubstring;
    }

}
