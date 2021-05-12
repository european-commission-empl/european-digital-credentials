package eu.europa.ec.empl.edci.wallet.repository.security;

import eu.europa.ec.empl.edci.wallet.entity.security.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SecurityUserRepository extends CrudRepository<User, Long> {
    User findByUserId(String userId);
}
