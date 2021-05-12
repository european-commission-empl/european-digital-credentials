package eu.europa.ec.empl.edci.datamodel.listener;


import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.datamodel.model.MailboxDTO;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.Unmarshaller;
import java.net.URI;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCIJAXBUnmarshalListener extends Unmarshaller.Listener {

    @Override
    public void afterUnmarshal(Object target, Object parent) {
        if (target instanceof MailboxDTO) {
            MailboxDTO mailboxDTO = (MailboxDTO) target;
            String mailString = mailboxDTO.getId().toString();

            if (mailString.contains(Defaults.DEFAULT_MAILTO)) {
                mailString = mailString.replace(Defaults.DEFAULT_MAILTO, "");
                mailboxDTO.setId(URI.create(mailString));
            }
        }

        super.afterUnmarshal(target, parent);
    }
}
