package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.issuer.common.model.ConfigDTO;
import eu.europa.ec.empl.edci.issuer.entity.config.ConfigDAO;
import eu.europa.ec.empl.edci.issuer.mapper.ConfigMapper;
import eu.europa.ec.empl.edci.issuer.repository.ConfigRepository;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigDBService implements CrudService<ConfigDAO> {

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private EDCIUserService edciUserService;

    @Autowired
    private IssuerConfigService issuerConfigService;


    public List<ConfigDTO> getConfiguration() {
        return this.getConfigMapper().toDTO(this.getConfigRepository().findAll());
    }

    public List<ConfigDTO> setConfiguration(List<ConfigDTO> configDTOS) {
        return this.configMapper.toDTO(this.getConfigRepository().saveAll(this.getConfigMapper().toDAO(configDTOS)));
    }

    //Environment is added automatically to query at service layer
    public ConfigDTO findByKey(String key) {
        Defaults.Environment environment = issuerConfigService.getCurrentEnvironment();
        return this.configMapper.toDTO(this.getConfigRepository().findByKeyAndEnvironment(key, environment));
    }

    //Environment is added automatically to config at service layer
    public ConfigDTO save(ConfigDTO configDTO) {
        if (configDTO.getEnvironment() == null) configDTO.setEnvironment(issuerConfigService.getCurrentEnvironment());
        return this.getConfigMapper().toDTO(this.getConfigRepository().save(this.getConfigMapper().toDAO(configDTO)));
    }

    public ConfigRepository getConfigRepository() {
        return configRepository;
    }

    public void setConfigRepository(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public ConfigMapper getConfigMapper() {
        return configMapper;
    }

    public void setConfigMapper(ConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    public EDCIUserService getEDCIUserService() {
        return edciUserService;
    }

    public void setEdciUserService(EDCIUserService edciUserService) {
        this.edciUserService = edciUserService;
    }

    @Override
    public JpaRepository<ConfigDAO, Long> getRepository() {
        return this.getConfigRepository();
    }

}
