package eu.europa.ec.empl.edci.wallet.repository;

import eu.europa.ec.empl.edci.wallet.entity.ConversionLockDAO;
import eu.europa.ec.empl.edci.wallet.entity.EmailLockDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailLockRepository extends JpaRepository<EmailLockDAO, Long> {

}
