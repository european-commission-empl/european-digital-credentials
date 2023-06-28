package eu.europa.ec.empl.edci.issuer.web.mvc;

import eu.europa.ec.empl.edci.config.service.IMVCConfigService;
import eu.europa.ec.empl.edci.mvc.EDCIMVCMainController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller()
public class IssuerMVCMainController extends EDCIMVCMainController {

    @Autowired
    private IMVCConfigService configService;

    public IMVCConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(IMVCConfigService configService) {
        this.configService = configService;
    }
}