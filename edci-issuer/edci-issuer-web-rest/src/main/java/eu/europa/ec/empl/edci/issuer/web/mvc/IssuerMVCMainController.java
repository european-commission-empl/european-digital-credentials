package eu.europa.ec.empl.edci.issuer.web.mvc;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.mvc.EDCIMVCMainController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller()
public class IssuerMVCMainController extends EDCIMVCMainController {

    @Autowired
    private IConfigService configService;

    public IConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(IConfigService configService) {
        this.configService = configService;
    }
}