package eu.europa.ec.empl.edci.issuer.service.security;

import eu.europa.ec.empl.edci.issuer.service.FileService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class SessionDestructionListener implements ApplicationListener<SessionDestroyedEvent> {

    private Logger logger = Logger.getLogger(SessionDestructionListener.class);

    @Autowired
    private FileService dynamicFileService;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent sessionDestroyedEvent) {
        HttpSession session = (HttpSession) sessionDestroyedEvent.getSource();
        logger.debug("[SESSION] DESTROYING XML FILES FOR SESSION " + session.getId());
        dynamicFileService.deleteSessionFolder(session.getId());
    }
}
