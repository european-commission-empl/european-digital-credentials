package eu.europa.ec.empl.edci.issuer.repository;

import eu.europa.ec.empl.edci.issuer.entity.specs.DiplomaSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * The interface User repository.
 */
@Repository
public interface DiplomaSpecRepository extends JpaRepository<DiplomaSpecDAO, Long>, JpaSpecificationExecutor<DiplomaSpecDAO> {

}