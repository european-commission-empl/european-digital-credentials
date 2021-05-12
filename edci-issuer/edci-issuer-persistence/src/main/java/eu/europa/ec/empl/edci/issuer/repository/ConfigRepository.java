package eu.europa.ec.empl.edci.issuer.repository;

import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.issuer.entity.config.ConfigDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigDAO, Long>, JpaSpecificationExecutor<ConfigDAO> {

    default ConfigDAO findByKeyAndEnvironment(String key, Defaults.Environment environment) {
        return this.findOne((root, query, cb) -> {
            return cb.and(cb.equal(root.get("key"), key), cb.equal(root.get("environment"), environment));
        }).orElse(null);
    }
}
