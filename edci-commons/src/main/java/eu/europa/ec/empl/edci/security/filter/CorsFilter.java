package eu.europa.ec.empl.edci.security.filter;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CorsFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(CorsFilter.class);

    @Autowired
    IConfigService configService;

    @Autowired
    Validator validator;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // Authorize (allow) all domains to consume the content
        List<String> allowedDomains = null;
        try {
            allowedDomains = Arrays.asList(configService.getStringArray(EDCIConfig.ALLOWED_DOMAINS));
        } catch (Exception e) {
            //If no allowed domains, leaving as null
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[D] - # Request from Origin {} / METHOD: {} / AUTHORIZATION: {}/{}", () -> request.getHeader(HttpHeaders.ORIGIN), () -> request.getMethod(), () -> request.getAuthType(), () -> request.getHeader(HttpHeaders.AUTHORIZATION));
        }

        if (allowedDomains == null || allowedDomains.contains(request.getHeader(HttpHeaders.ORIGIN))) {
            if (logger.isDebugEnabled()) {
                logger.debug("[D] - Adding Allow Origin: {}", () -> request.getHeader(HttpHeaders.ORIGIN));
            }
            ((HttpServletResponse) servletResponse).addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(HttpHeaders.ORIGIN));
            ((HttpServletResponse) servletResponse).addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        } else {
            ((HttpServletResponse) servletResponse).addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, allowedDomains.get(0));
        }

        ((HttpServletResponse) servletResponse).addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.GET + ", " + HttpMethod.OPTIONS + ", " + HttpMethod.HEAD + ", " + HttpMethod.PUT + ", " + HttpMethod.POST + ", " + HttpMethod.DELETE);
        ((HttpServletResponse) servletResponse).addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.CONTENT_TYPE + ", x-requested-with, " + HttpHeaders.AUTHORIZATION + ", x-xsrf-token");
        ((HttpServletResponse) servletResponse).addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        logger.debug("[D] - Inside Cors Filter ");

        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // pass the request along the filter chain
        if (request.getMethod().equals("OPTIONS")) {
            if (logger.isDebugEnabled()) {
                logger.debug("[D] - OPTIONS Request Found - Returning 200");
            }
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        } else {
            filterChain.doFilter(request, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
