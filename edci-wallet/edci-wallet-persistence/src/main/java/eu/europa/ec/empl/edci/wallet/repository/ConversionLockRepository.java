package eu.europa.ec.empl.edci.wallet.repository;

import eu.europa.ec.empl.edci.wallet.entity.ConversionLockDAO;
import eu.europa.ec.empl.edci.wallet.entity.ConversionLogDAO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Repository
public interface ConversionLockRepository extends JpaRepository<ConversionLockDAO, Long> {

}
