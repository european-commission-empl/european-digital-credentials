package eu.europa.ec.empl.edci.parsers.rdf;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDFDescription;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDFResource;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RDFParserTreeLeaf extends RDFParserTree {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RDFParserTreeLeaf.class);

    protected final static int NUM_THREADS = 10;

    @Override
    protected Queue<RDFDescription> umarshalElements(ControlledList source, List<String> list) {

        Queue<RDFDescription> rdfList = new ConcurrentLinkedQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        for (String elem : list) {
            executor.submit(() -> {

                RDFDescription entityDesc = cleanUndesiredDescriptions(elem, source, getEntityFromXML(elem, source));

                if (entityDesc.getNarrower() != null && !entityDesc.getNarrower().isEmpty()) {
                    for (RDFResource son : entityDesc.getNarrower()) {
                        rdfList.add(cleanUndesiredDescriptions(elem, source, getEntityFromXML(son.getValue(), source)));
                    }
                } else {
                    rdfList.add(entityDesc);
                }

            });
        }
        try {
            executor.shutdown();
            executor.awaitTermination(60L, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("Controlled list - Timeout for " + source.getUrl(), e);
        }

        return rdfList;
    }

}
