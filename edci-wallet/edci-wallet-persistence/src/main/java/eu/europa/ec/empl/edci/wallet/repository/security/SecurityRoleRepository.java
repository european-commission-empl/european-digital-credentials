package eu.europa.ec.empl.edci.wallet.repository.security;

import eu.europa.ec.empl.edci.wallet.entity.security.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SecurityRoleRepository extends CrudRepository<Role, Long> {
    Iterable<Role> findByRights_rightId(Long rightId);
}
