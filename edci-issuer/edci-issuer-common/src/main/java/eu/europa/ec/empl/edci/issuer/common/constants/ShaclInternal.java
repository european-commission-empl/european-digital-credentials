package eu.europa.ec.empl.edci.issuer.common.constants;

import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ShaclInternal {

    @Autowired
    private BaseConfigService configService;

    public static HashMap<String, List<String>> shaclMap = new HashMap<>();

    @PostConstruct
    public void shaclInternalPost() {
        shaclMap.put(this.getConfigService().getString(DataModelConstants.Properties.GENERIC), Arrays.asList("shacl/EDC-generic-full", "shacl/EDC-generic-no-cv"));
        shaclMap.put(this.getConfigService().getString(DataModelConstants.Properties.CONVERTED), Arrays.asList("shacl/EDC-converted", "shacl/EDC-generic-full", "shacl/EDC-generic-no-cv"));
        shaclMap.put(this.getConfigService().getString(DataModelConstants.Properties.ACCREDITED), Arrays.asList("shacl/EDC-accredited", "shacl/EDC-generic-full", "shacl/EDC-generic-no-cv"));
        shaclMap.put(this.getConfigService().getString(DataModelConstants.Properties.DIPLOMA_SUPPLEMENT), Arrays.asList("shacl/EDC-diplomaSupplement", "shacl/EDC-accredited", "shacl/EDC-generic-full", "shacl/EDC-generic-no-cv"));
        shaclMap.put(this.getConfigService().getString(DataModelConstants.Properties.ISSUED_MANDATE), Arrays.asList("shacl/EDC-issuedByMandate", "shacl/EDC-generic-full", "shacl/EDC-generic-no-cv"));
    }

    /**
     * To internal list.
     *
     * @param external the external
     * @return the list
     */
    public List<String> toInternal(String external) {
        return shaclMap.get(external);
    }

    public BaseConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(BaseConfigService configService) {
        this.configService = configService;
    }
}
