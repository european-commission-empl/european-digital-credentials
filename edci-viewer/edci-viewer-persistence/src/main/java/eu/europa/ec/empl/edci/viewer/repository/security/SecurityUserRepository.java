package eu.europa.ec.empl.edci.viewer.repository.security;

import eu.europa.ec.empl.edci.viewer.entity.security.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SecurityUserRepository extends CrudRepository<User, Long> {
    User findByUserId(String userId);
}
