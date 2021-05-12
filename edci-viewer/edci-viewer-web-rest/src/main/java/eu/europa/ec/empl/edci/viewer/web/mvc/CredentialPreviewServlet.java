package eu.europa.ec.empl.edci.viewer.web.mvc;

import eu.europa.ec.empl.edci.viewer.web.mapper.EuropassCredentialDetailRestMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Api(tags = {
        "mvc"
})
public class CredentialPreviewServlet {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CredentialPreviewServlet.class);

//    private final static Logger _log = LoggerFactory.getLogger(CredentialPreviewServlet.class);

    @Autowired
    private EuropassCredentialDetailRestMapper europassCredentialDetailRestMapper;


    @ApiOperation(value = " get HTML for a credential XML", produces = MediaType.TEXT_HTML_VALUE)
    @PostMapping(value = "/preview")
    public ModelAndView preview(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index_with_credential");

        String xml = req.getParameter("xml");
        if (xml == null || (xml != null && StringUtils.isBlank(xml))) {
            xml = "";
        }

        modelAndView.addObject("xml", "<script id='cred' type='text/xmldata'>" + xml + "</script>");

        return modelAndView;
    }
}