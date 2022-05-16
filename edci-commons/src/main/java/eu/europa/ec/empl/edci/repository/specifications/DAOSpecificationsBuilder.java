package eu.europa.ec.empl.edci.repository.specifications;

import eu.europa.ec.empl.edci.repository.util.SearchOperation;
import eu.europa.ec.empl.edci.repository.util.SpecSearchCriteria;
import eu.europa.ec.empl.edci.security.base.IEDCISecurityContextHolder;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class DAOSpecificationsBuilder<T> {

    //more info: https://www.baeldung.com/spring-rest-api-query-search-language-tutorial
    private final List<SpecSearchCriteria> params;

    private IEDCISecurityContextHolder edciUserHolder;

    public DAOSpecificationsBuilder(IEDCISecurityContextHolder userHolder) {
        this.params = new ArrayList<>();
        if (userHolder != null) {
            this.edciUserHolder = userHolder;
            String sub = edciUserHolder.getSub();
            with("auditDAO.createUserId", ":", sub, null, null);
        }
    }

    // API

    public final DAOSpecificationsBuilder with(final String key, final String operation, final Object value, final String prefix, final String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public final DAOSpecificationsBuilder with(final String orPredicate, final String key, final String operation, final Object value, final String prefix, final String suffix) {
        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (op != null) {
            params.add(new SpecSearchCriteria(orPredicate, key, op, value));
        }
        return this;
    }

    public Specification<T> build() {
        if (params.size() == 0)
            return null;

        Specification<T> result = new DAOSpecSpecification<T>(params);

        return result;
    }

    public final DAOSpecificationsBuilder with(DAOSpecSpecification<T> spec) {
        params.addAll(spec.getCriterias());
        return this;
    }

    public final DAOSpecificationsBuilder with(SpecSearchCriteria criteria) {
        params.add(criteria);
        return this;
    }
}
