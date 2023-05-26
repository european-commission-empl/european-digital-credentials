package eu.europa.ec.empl.edci.issuer.repository;

import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAssessmentSpecDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * The interface User repository.
 */
@Repository
public interface AssessmentSpecRepository extends JpaRepository<LearningAssessmentSpecDAO, Long>, JpaSpecificationExecutor<LearningAssessmentSpecDAO>, OCBIDSpecRepository<LearningAssessmentSpecDAO> {

}