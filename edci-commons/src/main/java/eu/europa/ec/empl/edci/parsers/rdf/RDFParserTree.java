package eu.europa.ec.empl.edci.parsers.rdf;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDF;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDFDescription;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDFResource;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RDFParserTree extends RDFParser {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RDFParserTree.class);

    protected final static int NUM_THREADS = 10;

    @Override
    protected Queue<RDFDescription> umarshalElements(ControlledList source, List<String> list) {

        Queue<RDFDescription> rdfList = new ConcurrentLinkedQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        for (String elem : list) {
            executor.submit(() -> {

                RDFDescription entityDesc = cleanUndesiredDescriptions(elem, source, getEntityFromXML(elem, source));

                rdfList.add(entityDesc);

                if (entityDesc.getNarrower() != null && !entityDesc.getNarrower().isEmpty()) {
                    for (RDFResource son : entityDesc.getNarrower()) {
                        rdfList.add(cleanUndesiredDescriptions(elem, source, getEntityFromXML(son.getValue(), source)));
                    }
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

    @Override
    protected RDFDescription cleanUndesiredDescriptions(String uri, ControlledList source, RDF rdf) {

        RDFDescription returnDesc = null;

        if (rdf == null || rdf.getList() == null) {
            return null;
        }

        if (rdf.isError()) {
            return rdf.getList().get(0);
        }

        List<RDFDescription> descrList = rdf.getList().stream().filter(d -> d.getTargetName() != null
                && !d.getTargetName().isEmpty()).collect(Collectors.toList());

        if (descrList.size() < 1) {
            returnDesc = buildErrorEntry(uri + "_NO_DESC_FOUND", source).getList().get(0);
            logger.error("ERROR while obtaining elements from controlled lists: No valid element found loading CL " + source.getUrl());
        } else if (descrList.size() > 1) {
            returnDesc = buildErrorEntry(uri + "_MULTIPLE_DESC_FOUND", source).getList().get(0);
            logger.error("ERROR while obtaining elements from controlled lists: More than one element found in " + descrList.get(0).getUri());
        } else {
            returnDesc = descrList.get(0);
        }


        return returnDesc;
    }

}
