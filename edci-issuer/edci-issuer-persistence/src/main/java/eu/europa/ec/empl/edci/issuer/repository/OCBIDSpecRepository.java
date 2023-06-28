package eu.europa.ec.empl.edci.issuer.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OCBIDSpecRepository<T> extends JpaSpecificationExecutor<T> {

    default boolean existsByOCBID(String ocbId) {
        return count((root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("OCBID"), ocbId);
        }) > 0;

    }

    default T findByOCBID(String ocbId) {
        return findOne((root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("OCBID"), ocbId);
        }).orElse(null);
    }


}
