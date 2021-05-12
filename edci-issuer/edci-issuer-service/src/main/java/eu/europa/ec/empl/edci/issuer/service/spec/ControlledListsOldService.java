package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.issuer.entity.controlledLists.ElementCLDAO;
import eu.europa.ec.empl.edci.issuer.mapper.ControlledListsMapper;
import eu.europa.ec.empl.edci.issuer.repository.ControlledListsRepository;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.parsers.ControlledListParser;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDFDescription;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Deprecated
@Service
public class ControlledListsOldService implements CrudService<ElementCLDAO> {

    private static final Logger logger = Logger.getLogger(ControlledListsOldService.class);

    @Autowired
    private ControlledListsRepository controlledListsRepository;

    @Autowired
    private ControlledListsMapper controlledListsMapper;

    @Autowired
    private IssuerConfigService issuerConfigService;

    protected final static int NUM_THREADS = 3;

    @Autowired
    private EDCIUserService edciUserService;

    @Override
    public EDCIUserService getEDCIUserService() {
        return this.edciUserService;
    }

    public ControlledListsRepository getRepository() {
        return controlledListsRepository;
    }

    public void saveElement(ControlledList source, ElementCLDAO newElem) {

        try {
            ElementCLDAO storedElem = getRepository().findByUri(newElem.getTargetFrameworkURI(), newElem.getUri());

            if (newElem.getDeprecatedSince() != null) {

                if (storedElem != null && storedElem.getDeprecatedSince() == null) {
                    storedElem.setDeprecatedSince(newElem.getDeprecatedSince());
                    getRepository().save(storedElem);
                } else {
                    //We don't save deprecated elements
                }

            } else {

                if (storedElem == null) {
                    getRepository().save(newElem);
                }

            }
        } catch (Exception e) {
            logger.error(e);
        }

    }

    public void saveElements(ControlledList source, List<ElementCLDAO> elemCLList) {

        for (ElementCLDAO newElem : elemCLList) {
            saveElement(source, newElem);
        }

    }

    public void loadControlledLists(Boolean clean) {

        ControlledList[] sources = ControlledList.values();

        if (sources == null) {
            logger.info("Loading controlled lists - No sources found");
            return;
        }

        logger.info("Loading controlled lists - " + new SimpleDateFormat("hh:mm:ss").format(new Date()));

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (ControlledList s : sources) {
            executor.submit(() -> {
                loadControlledList(s, clean);
            });
        }


        executor.shutdown();

    }

    public void loadControlledList(ControlledList source, Boolean clean) {

        try {
            ControlledListParser p = source.getParser().newInstance();

            logger.info("Controlled list - " + source);

            List<RDFDescription> allElems = new ArrayList();

            allElems.addAll(p.unmarshallControlledList(source));

            List<ElementCLDAO> elem = controlledListsMapper.toCLList(allElems);

            elem = elem.stream().filter(e -> e != null).collect(Collectors.toList());

            if (clean != null && clean) {
                getRepository().deleteControlledList(source.getUrl());
            }
            saveElements(source, elem);

        } catch (Throwable t) {
            logger.error(source, t);
        }

    }

    public Code findCodeByTargetName(String listId, String targetName) {

        Code code = null;
        try {
            ElementCLDAO elem = controlledListsRepository.findByTargetNameCaseInsensitive(listId, targetName);

            if (elem != null) {
                code = controlledListsMapper.toCodeDTO(elem, ControlledListCommonsService.ALLOWED_LANGS);
            }
        } catch (Exception e) {
            code = null;
        }

        return code;

    }

    public ElementCLDAO findElementByUri(String targetFrameworkURI, String uri) {

        ElementCLDAO elem = null;
        try {
            elem = controlledListsRepository.findByUri(targetFrameworkURI, uri);
        } catch (Exception e) {
            elem = null;
        }

        return elem;

    }
//
//    public List<ElementCLDAO> findElementsByFrameworkUri(String targetFrameworkURI) {
//        List<ElementCLDAO> elements = null;
//
//        try {
//            elements = controlledListsRepository.findByTargetFrameworkUri(targetFrameworkURI);
//        } catch (Exception e) {
//            elements = null;
//        }
//
//        return elements;
//    }
}