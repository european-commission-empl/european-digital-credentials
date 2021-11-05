package eu.europa.ec.empl.edci.wallet.repository.security;

import eu.europa.ec.empl.edci.wallet.entity.security.Right;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SecurityRightRepository extends CrudRepository<Right, Long>{
}
