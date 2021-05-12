package eu.europa.ec.empl.edci.issuer.repository;

import eu.europa.ec.empl.edci.issuer.entity.specs.AssessmentSpecDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * The interface User repository.
 */
@Repository
public interface AssessmentSpecRepository extends JpaRepository<AssessmentSpecDAO, Long>, JpaSpecificationExecutor<AssessmentSpecDAO> {

}