package eu.europa.ec.empl.edci.wallet.repository;

import eu.europa.ec.empl.edci.wallet.entity.CredentialLocalizableInfoDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialLocalizableInfoRepository extends JpaRepository<CredentialLocalizableInfoDAO, Long> {
}
