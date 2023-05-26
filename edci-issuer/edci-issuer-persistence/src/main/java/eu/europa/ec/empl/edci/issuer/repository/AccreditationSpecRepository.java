package eu.europa.ec.empl.edci.issuer.repository;

import eu.europa.ec.empl.edci.issuer.entity.specs.AccreditationSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAssessmentSpecDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * The interface User repository.
 */
@Repository
public interface AccreditationSpecRepository extends JpaRepository<AccreditationSpecDAO, Long>, JpaSpecificationExecutor<AccreditationSpecDAO>, OCBIDSpecRepository<AccreditationSpecDAO> {

}