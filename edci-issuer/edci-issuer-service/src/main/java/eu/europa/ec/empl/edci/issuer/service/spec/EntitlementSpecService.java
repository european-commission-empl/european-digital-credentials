package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.entity.specs.EntitlementSpecDAO;
import eu.europa.ec.empl.edci.issuer.repository.EntitlementSpecRepository;
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
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class EntitlementSpecService implements CrudService<EntitlementSpecDAO> {
    private static final Logger logger = LogManager.getLogger(EntitlementSpecService.class);

    @Autowired
    ResourcesUtil resourcesUtil;

    @Autowired
    private EDCIUserService edciUserService;

    @Override
    public EDCIUserService getEDCIUserService() {
        return this.edciUserService;
    }

    @Autowired
    private EntitlementSpecRepository entitlementSpecRepository;

    public EntitlementSpecRepository getRepository() {
        return entitlementSpecRepository;
    }

    public <T> EntitlementSpecDAO clone(Long oid, IRestMapper<EntitlementSpecDAO, T, ?> entitlementSpecRestMapper) {

        EntitlementSpecDAO entitlementSpecDAO = find(oid);
        if (entitlementSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Entitlement with oid [" + oid + "] not found");
        }

        //Removing the oids of the data type dependencies
        EntitlementSpecDAO entitlementSpecCloneDAO = entitlementSpecRestMapper.toDAO(entitlementSpecRestMapper.toVO(entitlementSpecDAO));
        entitlementSpecCloneDAO.setPk(null);
        entitlementSpecCloneDAO.setAuditDAO(null);
        entitlementSpecCloneDAO.setLabel(this.generateDuplicatedLabel(entitlementSpecCloneDAO.getLabel()));

        //Relations
        if (entitlementSpecDAO.getAwardedBy() != null) {
            entitlementSpecCloneDAO.getAwardedBy().setAwardingBody(new HashSet<>(
                    entitlementSpecDAO.getAwardedBy().getAwardingBody()));
        }

        entitlementSpecCloneDAO.setWasDerivedFrom(entitlementSpecDAO.getWasDerivedFrom());
        entitlementSpecCloneDAO.setHasPart(entitlementSpecDAO.getHasPart());
        if (entitlementSpecDAO.getSpecifiedBy() != null && entitlementSpecCloneDAO.getSpecifiedBy() != null) {
            entitlementSpecCloneDAO.getSpecifiedBy().setLimitOrganisation(entitlementSpecDAO.getSpecifiedBy().getLimitOrganisation());
        }

        return save(entitlementSpecCloneDAO);

    }

    public EntitlementSpecDAO save(EntitlementSpecDAO objectDAO, Runnable... postCreateActions) {

        List<Runnable> runnableList = new ArrayList<>(Arrays.asList(postCreateActions));
        runnableList.add(() -> resourcesUtil.checkLoopTree(objectDAO, (a) -> a.getHasPart(), "Sub entitlements"));

        return CrudService.super.save(objectDAO, runnableList.toArray(new Runnable[]{}));

    }

}
