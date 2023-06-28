package eu.europa.ec.empl.edci.mvc;

import eu.europa.ec.empl.edci.config.service.IMVCConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EDCIMVCMainController {

    @Autowired
    private IMVCConfigService configService;

    @Autowired
    private EDCIMVCUtil edcimvcUtil;

    @RequestMapping("/home")
    public ModelAndView homePage(HttpServletRequest req, HttpServletResponse resp) {
        //Serve index.jsp, injecting FrontEnd Context
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        this.getEdcimvcUtil().setFrontEndContext(modelAndView);
        this.getEdcimvcUtil().setBaseHref(modelAndView, req);
        return modelAndView;
    }


    public IMVCConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(IMVCConfigService configService) {
        this.configService = configService;
    }

    public EDCIMVCUtil getEdcimvcUtil() {
        return edcimvcUtil;
    }

    public void setEdcimvcUtil(EDCIMVCUtil edcimvcUtil) {
        this.edcimvcUtil = edcimvcUtil;
    }
}