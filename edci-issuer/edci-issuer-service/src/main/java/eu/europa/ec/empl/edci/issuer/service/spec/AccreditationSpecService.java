package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.entity.specs.AccreditationSpecDAO;
import eu.europa.ec.empl.edci.issuer.repository.AccreditationSpecRepository;
import eu.europa.ec.empl.edci.repository.mapper.IRestMapper;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import eu.europa.ec.empl.edci.util.ResourcesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class AccreditationSpecService implements CrudService<AccreditationSpecDAO> {
    private static final Logger logger = LogManager.getLogger(AccreditationSpecService.class);

    @Autowired
    ResourcesUtil resourcesUtil;

    @Autowired
    private EDCIUserService edciUserService;

    @Override
    public EDCIUserService getEDCIUserService() {
        return this.edciUserService;
    }

    @Autowired
    private AccreditationSpecRepository accreditationSpecRepository;

    public AccreditationSpecRepository getRepository() {
        return accreditationSpecRepository;
    }

    public <T> AccreditationSpecDAO clone(Long oid, IRestMapper<AccreditationSpecDAO, T, ?> accreditationSpecRestMapper) {

        AccreditationSpecDAO accreditationSpecDAO = find(oid);
        if (accreditationSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Assessment with oid [" + oid + "] not found");
        }

        //Removing the oids of the data type dependencies
        AccreditationSpecDAO accreditationSpecCloneDAO = accreditationSpecRestMapper.toDAO(accreditationSpecRestMapper.toVO(accreditationSpecDAO));
        accreditationSpecCloneDAO.setPk(null);
        accreditationSpecCloneDAO.setAuditDAO(null);

        accreditationSpecCloneDAO.setLabel(this.generateDuplicatedLabel(accreditationSpecCloneDAO.getLabel()));

        //Relations
        accreditationSpecCloneDAO.setAccreditingAgent(accreditationSpecDAO.getAccreditingAgent());

        return save(accreditationSpecCloneDAO);

    }

    public AccreditationSpecDAO save(AccreditationSpecDAO objectDAO, Runnable... postCreateActions) {

        List<Runnable> runnableList = new ArrayList<>(Arrays.asList(postCreateActions));
        runnableList.add(() -> resourcesUtil.checkLoopTree(objectDAO,
                (a) -> a.getAccreditingAgent() != null ? a.getAccreditingAgent().getAccreditation() : null, "Accrediting agent"));

        return CrudService.super.save(objectDAO, runnableList.toArray(new Runnable[]{}));

    }

}
