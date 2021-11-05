package eu.europa.ec.empl.edci.parsers.rdf;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.parsers.ControlledListParser;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDF;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDFDescription;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDFResource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RDFParser implements ControlledListParser<RDFDescription> {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RDFParser.class);

    protected final static int NUM_THREADS = 10;

    public Queue<RDFDescription> unmarshallControlledList(ControlledList source) {

        RDF list = getEntityFromXML(source.getUrl(), null);

        Queue<RDFDescription> rdfReturnList = new ConcurrentLinkedQueue<>();

        RDFDescription root = getRootDescription(source.getUrl(), list);

        if (root != null) {

            //remove root element from list
            list.getList().removeIf(desc -> desc.getUri().equals(root.getUri()));
            List<String> uriList = list.getList().stream().map(elem -> elem.getUri()).collect(Collectors.toList());

            rdfReturnList = umarshalElements(source, uriList);

            rdfReturnList.stream().forEach(ent -> {
                ent.setTargetFramework(root.getTargetName());
                ent.setTargetNotation(source.getName());
            });

        } else {
            logger.error("Controlled list - Structure error: " + source.getUrl() + ". not found root description");
        }
        return rdfReturnList;
    }

    protected Queue<RDFDescription> umarshalElements(ControlledList source, List<String> list) {

        Queue<RDFDescription> rdfList = new ConcurrentLinkedQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        for (String elem : list) {

            executor.submit(() -> {

                RDFDescription entityDesc = cleanUndesiredDescriptions(elem, source, getEntityFromXML(elem, source));
                rdfList.add(entityDesc);

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

    protected RDFDescription getRootDescription(String url, RDF list) {
        String[] urlSplit = url.split("/");
        String urlEnding = urlSplit[urlSplit.length - 2] + "/" + urlSplit[urlSplit.length - 1];

        RDFDescription root = list.getList().stream().filter(desc -> desc.getUri().endsWith(urlEnding)).findFirst().get();

        return root;
    }

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
            //we will only save leaf elements
            if (descrList.get(0).getNarrower() == null) {
                returnDesc = descrList.get(0);
            }
        }

        return returnDesc;
    }

    protected HttpURLConnection openConnection(String url, int retry) throws IOException {
        HttpURLConnection connection;
        boolean redirected;
        do {
            connection = (HttpURLConnection) new URL(url).openConnection();
            int code = connection.getResponseCode();
            redirected = code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_SEE_OTHER || code == 307 || (code == HttpURLConnection.HTTP_UNAVAILABLE && retry-- >= 0);
            if (redirected) {
                if (code != HttpURLConnection.HTTP_UNAVAILABLE) {
                    url = connection.getHeaderField("Location");
                }
                connection.disconnect();
            }
        } while (redirected);
        return connection;
    }

    protected RDF getEntityFromXML(String url, ControlledList source) {

        RDF entity = null;
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(RDF.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            InputStream is = openConnection(url, 2).getInputStream();

            entity = (RDF) jaxbUnmarshaller.unmarshal(is);

        } catch (IOException e) {
            entity = buildErrorEntry(url, source);
            logger.error("Can't load " + url + " controlled list", e);
        } catch (JAXBException e) {
            entity = buildErrorEntry(url, source);
            logger.error("Can't parse " + url + " controlled list file", e);
        }

        return entity;
    }

    protected RDF buildErrorEntry(String uri, ControlledList source) {

        RDFDescription elem = new RDFDescription();

        elem.setTargetFrameworkURI(new RDFResource() {{
            setValue(source != null ? source.getUrl() + "_ERROR" : uri + "_ERROR");
        }});

        elem.setUri(uri);

        return new RDF() {{
            setError(true);
            setList(new ArrayList<RDFDescription>() {{
                add(elem);
            }});
        }};

    }
}
