package eu.europa.ec.empl.edci.wallet.web;


//import eu.cec.digit.ecas.client.Client;
//import eu.cec.digit.ecas.client.EcasUtil;
//import eu.cec.digit.ecas.client.configuration.EcasConfigurationIntf;
//import eu.cec.digit.ecas.client.resolver.eu.europa.ec.empl.edci.dss.service.StatefulServiceResolver;
//import eu.cec.digit.ecas.util.RFC3986PercentCodec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
public class SingleSignoutController implements ServletContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SingleSignoutController.class);

    private ServletContext servletContext;

    public SingleSignoutController() {
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @RequestMapping(value = {"/logout"}, method = RequestMethod.GET)
    public String doSSOSignOut(HttpServletRequest request, HttpSession session, HttpServletResponse response) throws Exception {
//        logger.debug("-------> /logout");
//
//        if (session == null) {
//            logger.info("Session is expired. Redirecting to homePage");
//            return redirect("/");
//        }
//
//        // 1. retrieve the client configuration and the validation configuration
//        logger.debug("1. retrieve the client configuration and the validation configuration");
//        EcasConfigurationIntf ecasClientConfig = Client.getConfigFromContext(servletContext);
//
//        if (ecasClientConfig == null) {
//            logger.info("ecasClientConfig is null. Redirecting to homePage");
//            doLogout(session);
//            return redirect("/");
//        }
//
//        StatefulServiceResolver serviceResolver = (StatefulServiceResolver) ecasClientConfig.getServiceResolver();
//
//        // 2. Find the eu.europa.ec.empl.edci.dss.service:
//        logger.debug("2. Find the eu.europa.ec.empl.edci.dss.service:");
//        String eu.europa.ec.empl.edci.dss.service = serviceResolver.getService(request);
//        if (logger.isInfoEnabled()) {
//            logger.info("serviceResolver.getService(request)={" + eu.europa.ec.empl.edci.dss.service + "}");
//        }
//
//        //3. Construct the root URL of the application
//        logger.debug("3. Construct the root URL of the application");
//        String contextRoot = servletContext.getContextPath();
//        int position = eu.europa.ec.empl.edci.dss.service.indexOf(contextRoot);
//
//        final String rootUrl;
//        if (position >= 0) {
//            rootUrl = eu.europa.ec.empl.edci.dss.service.substring(0, position) + contextRoot;
//        } else {
//            rootUrl = eu.europa.ec.empl.edci.dss.service;
//        }
//
//        //4. Invalidate session to perform local Logout
//        logger.debug("4. Invalidate session to perform local Logout");
//        doLogout(session);
//
//        //5. Create the Ecas url where we are redirecting to provide access to single sign out.
//        logger.debug("5. Create the Ecas url where we are redirecting to provide access to single sign out.");
//        String escapedRootUrl = RFC3986PercentCodec.UTF8_PERCENT_CODEC.encode(rootUrl);
//        String ecasLogoutUrl = EcasUtil.replace(ecasClientConfig.getLoginUrl(), "login", "logout", -1) + "?url=" + escapedRootUrl;
//        if (logger.isInfoEnabled()) {
//            logger.info("Redirecting to ECAS logout with param url={" +
//                    rootUrl + "} escaped as {" + escapedRootUrl + "}");
//        }
//
//        logger.debug("6 final : redirect(ecasLogoutUrl) : " + ecasLogoutUrl);
//
//        request.setAttribute("ecasLogoutUrl", ecasLogoutUrl);
//
//        return redirect(ecasLogoutUrl);
//        //response.sendRedirect(ecasLogoutUrl);
//        //return "page.logout";
        return null;
    }

    private void doLogout(HttpSession session) {
        session.invalidate();
    }

    private String redirect(String url) {
        return "redirect:" + url;
    }

}
