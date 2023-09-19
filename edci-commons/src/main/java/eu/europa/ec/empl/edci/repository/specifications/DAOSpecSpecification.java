package eu.europa.ec.empl.edci.repository.specifications;

import eu.europa.ec.empl.edci.repository.util.SpecSearchCriteria;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

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

        List<Predicate> predicatesAnd = new ArrayList<>();
        List<Predicate> predicatesOr = new ArrayList<>();
        List<Order> orderByList = new ArrayList<>();

        Map<String, Path> pathMap = new HashMap<>();

        for (SpecSearchCriteria criteria : criterias) {

            Path path = root;

            String key = criteria.getKey();

            boolean isMultilang = key.contains("contents.content");

            if (key.contains(".")) {
                String currentPath = null;
                boolean first = true;
                Join joinTable = null;
                for (Iterator<String> i = Arrays.stream(key.split("\\.")).collect(Collectors.toList()).iterator(); i.hasNext(); ) {

                    String subKey = i.next();
                    currentPath = currentPath == null ? subKey : currentPath + "." + subKey;

                    if (first) {
                        joinTable = root.join(subKey, criteria.isOrPredicate() ? JoinType.LEFT : JoinType.INNER);
                        first = false;
                    } else if (i.hasNext()) {
                        joinTable = joinTable.join(subKey, criteria.isOrPredicate() ? JoinType.LEFT : JoinType.INNER);
                    } else {

                        if (pathMap.containsKey(currentPath)) {
                            path = pathMap.get(currentPath);
                        } else {
                            path = joinTable.get(subKey);
                            pathMap.put(currentPath, path);
                        }

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
                    if (isMultilang) {
                        predicatesAnd.add(builder.equal(path.getParentPath().get("language"), root.get("defaultLanguage")));
                    }
                    break;
                case EQUALITY:
                    if (criteria.isOrPredicate()) {
                        predicatesOr.add(builder.equal(path, criteria.getValue()));
                    } else {
                        predicatesAnd.add(builder.equal(path, criteria.getValue()));
                    }
                    if (isMultilang) {
                        predicatesAnd.add(builder.equal(path.getParentPath().get("language"), root.get("defaultLanguage")));
                    }
                    break;
                case NEGATION:
                    if (criteria.isOrPredicate()) {
                        predicatesOr.add(builder.notEqual(path, criteria.getValue()));
                    } else {
                        predicatesAnd.add(builder.notEqual(path, criteria.getValue()));
                    }
                    if (isMultilang) {
                        predicatesAnd.add(builder.equal(path.getParentPath().get("language"), root.get("defaultLanguage")));
                    }
                    break;
                case GREATER_THAN:
                    if (criteria.isOrPredicate()) {
                        predicatesOr.add(builder.greaterThan(path, criteria.getValue().toString()));
                    } else {
                        predicatesAnd.add(builder.greaterThan(path, criteria.getValue().toString()));
                    }
                    if (isMultilang) {
                        predicatesAnd.add(builder.equal(path.getParentPath().get("language"), root.get("defaultLanguage")));
                    }
                    break;
                case LESS_THAN:
                    if (criteria.isOrPredicate()) {
                        predicatesOr.add(builder.lessThan(path, criteria.getValue().toString()));
                    } else {
                        predicatesAnd.add(builder.lessThan(path, criteria.getValue().toString()));
                    }
                    if (isMultilang) {
                        predicatesAnd.add(builder.equal(path.getParentPath().get("language"), root.get("defaultLanguage")));
                    }
                    break;
                case LIKE:
                    if (criteria.isOrPredicate()) {
                        predicatesOr.add(builder.like(
                                builder.lower(path), "%" + criteria.getValue().toString().toLowerCase() + "%"));
                    } else {
                        predicatesAnd.add(builder.like(
                                builder.lower(path), "%" + criteria.getValue().toString().toLowerCase() + "%"));
                    }
                    if (isMultilang) {
                        predicatesAnd.add(builder.equal(path.getParentPath().get("language"), root.get("defaultLanguage")));
                    }
                    break;
                default:
                    break;
            }

        }

        if (!orderByList.isEmpty()) {
            query.orderBy(orderByList);
        }

        Predicate predicateAnd = builder.and(predicatesAnd.toArray(new Predicate[]{}));

        Predicate returnValue = null;

        if (predicatesOr.isEmpty()) {
            returnValue = builder.and(predicateAnd); //We'll always have a predicate And for the audit filter
        } else {
            Predicate predicateOr = builder.or(predicatesOr.toArray(new Predicate[]{}));
            returnValue = builder.and(predicateOr, predicateAnd);
        }

        return returnValue;

    }

}
