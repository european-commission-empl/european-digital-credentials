package eu.europa.ec.empl.edci.security.session;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionCreationEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class SessionCreationListener implements ApplicationListener<SessionCreationEvent> {

    private Logger logger = LogManager.getLogger(SessionCreationListener.class);

    @Autowired
    private IConfigService issuerConfigService;

    @Override
    public void onApplicationEvent(SessionCreationEvent sessionCreationEvent) {
        HttpSession session = (HttpSession) sessionCreationEvent.getSource();
        session.setMaxInactiveInterval(issuerConfigService.get(EDCIConstants.Security.CONFIG_PROPERTY_SESSION_TIMEOUT, Integer.class) * 60);
        logger.debug("SESSION - CREATING SESSION {}, WITH TIMEOUT {}", () -> session.getId(), () -> session.getMaxInactiveInterval());
    }
}
