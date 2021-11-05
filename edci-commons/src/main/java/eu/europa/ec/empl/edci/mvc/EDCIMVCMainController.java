package eu.europa.ec.empl.edci.mvc;

import com.google.gson.Gson;
import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class EDCIMVCMainController {

    @Autowired
    private IConfigService configService;

    public void setFrontEndContext(ModelAndView modelAndView) {
        //Get All frontend Properties from current eu.europa.ec.empl.edci.dss.service
        Map<String, Object> frontEndContext = configService.getFrontEndProperties();
        //Add Context variables in json format
        String jsonFrontEndContext = new Gson().toJson(frontEndContext);
        modelAndView.addObject(EDCIConfig.FRONTEND_CONTEXT_VARIABLE, jsonFrontEndContext);
    }

    public void setBaseHref(ModelAndView modelAndView, HttpServletRequest req) {
        String baseHref = configService.getString(EDCIConfig.Front.HTML_BASE_HREF, req.getContextPath());
        modelAndView.addObject(EDCIConfig.BASE_HREF_VARIABLE, baseHref);
    }

    @RequestMapping("/home")
    public ModelAndView homePage(HttpServletRequest req, HttpServletResponse resp) {
        //Serve index.jsp, injecting FrontEnd Context
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        this.setFrontEndContext(modelAndView);
        this.setBaseHref(modelAndView, req);
        return modelAndView;
    }


    public IConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(IConfigService configService) {
        this.configService = configService;
    }
}