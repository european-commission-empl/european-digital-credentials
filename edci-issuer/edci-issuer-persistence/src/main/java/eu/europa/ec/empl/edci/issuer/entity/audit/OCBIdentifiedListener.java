package eu.europa.ec.empl.edci.issuer.entity.audit;

import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConstants;
import eu.europa.ec.empl.edci.repository.entity.IOCBIdentifiedDAO;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.UUID;

@Component
public class OCBIdentifiedListener {

    @PrePersist
    protected void onPrePersist(IOCBIdentifiedDAO dao) {
        identify(dao);
    }

    @PreUpdate
    protected void onPreUpdate(IOCBIdentifiedDAO dao) {
        identify(dao);
    }


    private void identify(IOCBIdentifiedDAO dao) {
        if (dao.getOCBID() == null || dao.getOCBID().equals(IssuerConstants.DEFAULT_OCBID)) {
            dao.setOCBID(UUID.randomUUID().toString());
        }
    }
}
