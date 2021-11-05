package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Transactional
public interface WalletCrudService<T extends IGenericDAO> extends CrudService<T> {

    static final Logger logger = Logger.getLogger(WalletCrudService.class);

    default EDCIUserService getEDCIUserService() {
        return null;
    }

    default T save(T objectDAO, Runnable... postCreateActions) {

//        if (objectDAO instanceof AuditedDAO && objectDAO.getPk() != null) {
//            AuditedDAO d = (AuditedDAO) getRepository().getOne(objectDAO.getPk());
//            ((AuditedDAO) objectDAO).setAuditDAO(d.getAuditDAO());
//        }

        if (postCreateActions != null && postCreateActions.length > 0) {
            getRepository().save(objectDAO);

            Arrays.stream(postCreateActions).forEach(Runnable::run);
        }

        return getRepository().save(objectDAO);
    }

}
