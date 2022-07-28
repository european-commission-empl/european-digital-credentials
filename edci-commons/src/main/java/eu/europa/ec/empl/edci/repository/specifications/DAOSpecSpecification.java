package eu.europa.ec.empl.edci.repository.specifications;

import eu.europa.ec.empl.edci.repository.util.SpecSearchCriteria;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DAOSpecSpecification<T> implements Specification<T> {

    private List<SpecSearchCriteria> criterias = new ArrayList<>();

    public DAOSpecSpecification(final SpecSearchCriteria criteria) {
        super();
        this.criterias.add(criteria);
    }

    public DAOSpecSpecification(List<SpecSearchCriteria> criterias) {
        super();
        this.criterias.addAll(criterias);
    }

    public DAOSpecSpecification addCriteria(SpecSearchCriteria criteria) {
        this.criterias.add(criteria);
        return this;
    }

    public List<SpecSearchCriteria> getCriterias() {
        return criterias;
    }

    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();
        List<Order> orderByList = new ArrayList<>();

        Map<String, Path> pathMap = new HashMap<>();

        for (SpecSearchCriteria criteria : criterias) {

            Path path = root;

            String key = criteria.getKey();

            if (key.contains(".")) {
                for (String subKey : key.split("\\.")) {

                    if (pathMap.containsKey(subKey)) {
                        path = pathMap.get(subKey);
                    } else {
                        path = path.get(subKey);
                        pathMap.put(subKey, path);
                    }

                }
            } else {
                if (pathMap.containsKey(criteria.getKey())) {
                    path = pathMap.get(criteria.getKey());
                } else {
                    path = path.get(criteria.getKey());
                    pathMap.put(criteria.getKey(), path);
                }
            }

            switch (criteria.getOperation()) {
                case ORDER_BY:
                    if (Sort.Direction.ASC.toString().equals(criteria.getValue())) {
                        orderByList.add(builder.asc(path));
                    } else if (Sort.Direction.DESC.toString().equals(criteria.getValue())) {
                        orderByList.add(builder.desc(path));
                    }
                    break;
                case EQUALITY:
                    predicates.add(builder.equal(path, criteria.getValue()));
                    break;
                case NEGATION:
                    predicates.add(builder.notEqual(path, criteria.getValue()));
                    break;
                case GREATER_THAN:
                    predicates.add(builder.greaterThan(path, criteria.getValue().toString()));
                    break;
                case LESS_THAN:
                    predicates.add(builder.lessThan(path, criteria.getValue().toString()));
                    break;
                case LIKE:
                    predicates.add(builder.like(
                            builder.lower(path), "%" + criteria.getValue().toString().toLowerCase() + "%"));
                    break;
                default:
                    break;
            }

        }

        if (!orderByList.isEmpty()) {
            query.orderBy(orderByList);
        }

        return builder.and(predicates.toArray(new Predicate[]{}));

    }

}
