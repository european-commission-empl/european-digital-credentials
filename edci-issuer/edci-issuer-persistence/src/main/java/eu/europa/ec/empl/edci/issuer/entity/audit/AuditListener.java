package eu.europa.ec.empl.edci.issuer.entity.audit;

import eu.europa.ec.empl.edci.repository.entity.IAuditDAO;
import eu.europa.ec.empl.edci.repository.entity.IAuditedDAO;
import eu.europa.ec.empl.edci.security.EDCISecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import java.util.Date;

@Component
public class AuditListener {

    enum Operation {
        INSERT, UPDATE, DELETE
    }

    @PrePersist
    protected void onPrePersist(IAuditedDAO dao) {
        audit(dao, Operation.INSERT);
    }

    @PostUpdate
    protected void onPostUpdate(IAuditedDAO dao) {
        audit(dao, Operation.UPDATE);
    }

    @PreRemove
    protected void onPreRemove(IAuditedDAO dao) {
        audit(dao, Operation.DELETE);
    }

    private static EDCISecurityContextHolder edciUserHolder;

    @Autowired
    public void init(EDCISecurityContextHolder edciUserHolder) {
        AuditListener.edciUserHolder = edciUserHolder;
    }

    private void audit(IAuditedDAO dao, Operation operation) {
        String sub = edciUserHolder.getSub();

        IAuditDAO audit = null;
        switch (operation) {
            case INSERT:
                audit = new AuditDAO();
                audit.setCreateDate(new Date());
                audit.setCreateUserId(sub);
                audit.setUpdateDate(audit.getCreateDate());
                audit.setUpdateUserId(audit.getCreateUserId());
                break;
            case UPDATE:
                audit = dao.getAuditDAO();
                if (audit == null) {
                    audit = new AuditDAO();
                    audit.setCreateDate(new Date());
                    audit.setCreateUserId(sub);
                }
                audit.setUpdateDate(new Date());
                audit.setUpdateUserId(sub);
                break;
            case DELETE:
                //Nothing
                break;
        }

        dao.setAuditDAO(audit);
    }

}
