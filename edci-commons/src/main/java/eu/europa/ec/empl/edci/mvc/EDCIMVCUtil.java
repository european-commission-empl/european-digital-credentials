package eu.europa.ec.empl.edci.mvc;

import com.google.gson.Gson;
import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCIMVCUtil {
    @Autowired
    private IConfigService iconfigService;

    public String getFrontEndContext() {
        //Get All frontend Properties from current eu.europa.ec.empl.edci.dss.service
        Map<String, Object> frontEndContext = this.getIconfigService().getFrontEndProperties();
        //Add Context variables in json format
        return new Gson().toJson(frontEndContext);
    }

    public String getBaseHref(HttpServletRequest req) {
        return this.getIconfigService().getString(EDCIConfig.Front.HTML_BASE_HREF, req.getContextPath());
    }

    public void setFrontEndContext(HttpServletRequest request) {
        request.setAttribute(EDCIConfig.FRONTEND_CONTEXT_VARIABLE, this.getFrontEndContext());
    }

    public void setBaseHref(HttpServletRequest request) {
        request.setAttribute(EDCIConfig.BASE_HREF_VARIABLE, this.getBaseHref(request));
    }

    public void setFrontEndContext(ModelAndView modelAndView) {
        modelAndView.addObject(EDCIConfig.FRONTEND_CONTEXT_VARIABLE, this.getFrontEndContext());
    }

    public void setBaseHref(ModelAndView modelAndView, HttpServletRequest req) {
        modelAndView.addObject(EDCIConfig.BASE_HREF_VARIABLE, this.getBaseHref(req));
    }

    public IConfigService getIconfigService() {
        return iconfigService;
    }

    public void setIconfigService(IConfigService iconfigService) {
        this.iconfigService = iconfigService;
    }
}