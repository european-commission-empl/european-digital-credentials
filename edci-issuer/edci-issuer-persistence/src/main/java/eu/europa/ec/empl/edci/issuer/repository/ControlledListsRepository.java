package eu.europa.ec.empl.edci.issuer.repository;

import eu.europa.ec.empl.edci.issuer.entity.controlledLists.ElementCLDAO;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Path;
import java.util.List;
import java.util.Locale;


/**
 * The interface User repository.
 */
@Repository
public interface ControlledListsRepository extends JpaRepository<ElementCLDAO, Long>, JpaSpecificationExecutor<ElementCLDAO> {

    default ElementCLDAO findByUri(String targetFrameworkURI, String uri) {
        return findOne((root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(criteriaBuilder.equal(root.get("uri"), uri), criteriaBuilder.equal(root.get("targetFrameworkURI"), targetFrameworkURI));
        }).orElse(null);
    }

    default List<ElementCLDAO> findByTargetFrameworkUri(String targetFrameworkURI) {
        if (targetFrameworkURI == null) {
            return null;
        }

        return findAll((root, query, criteriabuilder) -> {
            return criteriabuilder.equal(root.get("targetFrameworkURI"), targetFrameworkURI);
        });

    }

    default ElementCLDAO findByTargetNameCaseInsensitive(String targetFrameworkURI, String targetName) {

        if (targetFrameworkURI == null || targetName == null) {
            return null;
        }

        return findOne((root, query, criteriaBuilder) -> {
            Path path = root.get("targetName");
            return criteriaBuilder.and(
                    criteriaBuilder.equal(criteriaBuilder.lower(path.get("name")), targetName.trim().toLowerCase()),
                    criteriaBuilder.equal(criteriaBuilder.lower(path.get("lang")), LocaleContextHolder.getLocale().getLanguage()),
                    criteriaBuilder.equal(root.get("targetFrameworkURI"), targetFrameworkURI));
        }).orElse(findOne((root, query, criteriaBuilder) -> {
            Path path = root.get("targetName");
            return criteriaBuilder.and(
                    criteriaBuilder.equal(criteriaBuilder.lower(path.get("name")), targetName.trim().toLowerCase()),
                    criteriaBuilder.equal(criteriaBuilder.lower(path.get("lang")), Locale.ENGLISH.getLanguage()),
                    criteriaBuilder.equal(root.get("targetFrameworkURI"), targetFrameworkURI));
        }).get());
    }

    default void deleteControlledList(String targetFrameworkURI) {
        List<ElementCLDAO> elemsToRemove = findAll((root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("targetFrameworkURI"), targetFrameworkURI);
        });
        deleteInBatch(elemsToRemove);
    }


}