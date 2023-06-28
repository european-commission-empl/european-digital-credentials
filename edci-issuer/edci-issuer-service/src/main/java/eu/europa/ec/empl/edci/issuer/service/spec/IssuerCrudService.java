package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.issuer.repository.OCBIDSpecRepository;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import eu.europa.ec.empl.edci.repository.service.CrudService;

public interface IssuerCrudService<T extends IGenericDAO> extends CrudService<T> {

    default T findByOCBID(String ocbID) {
        OCBIDSpecRepository ocbidSpecRepository = (OCBIDSpecRepository) this.getRepository();
        return ocbidSpecRepository.existsByOCBID(ocbID) ? filterByUser((T) ocbidSpecRepository.findByOCBID(ocbID)) : null;
        // ocbidSpecRepository.findByOCBID(ocbID) == null ? null : (T) ocbidSpecRepository.findByOCBID(ocbID);
    }

    default boolean existsByOCBID(String ocbID) {
        OCBIDSpecRepository ocbidSpecRepository = (OCBIDSpecRepository) this.getRepository();
        return ocbidSpecRepository.existsByOCBID(ocbID) && filterByUser(this.findByOCBID(ocbID)) != null;
    }

}
