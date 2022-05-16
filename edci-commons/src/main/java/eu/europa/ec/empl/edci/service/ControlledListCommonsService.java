package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConceptScheme;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Content;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ControlledListCommonsService {

    private static final Logger logger = LogManager.getLogger(RDFsparqlBridgeService.class);

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private RDFsparqlBridgeService rdfSparqlBridgeService;

    private static final String SPARQL_SERVICE = "http://publications.europa.eu/webapi/rdf/sparql";

    private static Map<String, RDFConceptScheme> controlledListsConceptSchemes = new HashMap<>();

    public static final List<String> ALLOWED_LANGS = Arrays.asList("bg", "cs", "da", "de", "et", "el", "en", "es", "fr",
            "ga", "it", "lv", "lt", "hu", "mt", "nl", "pl", "pt", "ro", "sk", "sl", "fi", "sv", "hr", "is", "mk", "no", "tr", "sr");

    public List<RDFConcept> searchRDFConcepts(String targetFramework, String search, String locale, int page, int size, Collection<String> retrieveLangs) {

        return rdfSparqlBridgeService.searchRDFConcepts(targetFramework, search, locale, page, size, retrieveLangs);

    }

    public Code searchConceptByConcept(ControlledListConcept concept) {
        return searchConceptByUri(concept.getControlledList().getUrl(), concept.getUrl(), LocaleContextHolder.getLocale().toString());
    }

    public Code searchConceptByConcept(ControlledListConcept concept, String locale) {
        return searchConceptByUri(concept.getControlledList().getUrl(), concept.getUrl(), locale);
    }


    @Cacheable("CL_sparQL")
    public Code searchConceptByUri(String targetFramework, String uri, String locale) {

        //ToDo -> Remove when translations are up in publications office.
        if (targetFramework.equals(ControlledList.VERIFICATION_CHECKS.getUrl())) {
            locale = EDCIConfig.Defaults.DEFAULT_LOCALE;
        }

        RDFConceptScheme controlledListCS = rdfSparqlBridgeService.searchConceptScheme(targetFramework);

        List<RDFConcept> rdfConcepts = searchRDFConceptsByUri(targetFramework, locale, ALLOWED_LANGS, uri);

        if (rdfConcepts != null && rdfConcepts.size() > 0) {
            return toCode(controlledListCS, rdfConcepts, Arrays.asList(locale)).get(0);
        } else {
            return null;
        }

    }

    public List<Code> searchConceptsByUri(String targetFramework, String locale, Collection<String> retrieveLangs, String... uris) {

        RDFConceptScheme controlledListCS = rdfSparqlBridgeService.searchConceptScheme(targetFramework);

        List<RDFConcept> rdfConcepts = searchRDFConceptsByUri(targetFramework, locale, retrieveLangs, uris);

        return toCode(controlledListCS, rdfConcepts, retrieveLangs);

    }

    @Cacheable("CL_sparQL")
    public Code searchConceptByUri(String targetFramework, String uri, String locale, Collection<String> retrieveLangs) {
        List<Code> concepts = this.searchConceptsByUri(targetFramework, locale, retrieveLangs, uri);
        return concepts != null && !concepts.isEmpty() ? concepts.get(0) : null;
    }

    public RDFConcept searchRDFConceptByUri(String targetFramework, String uri) {

        List<RDFConcept> rdfConcepts = rdfSparqlBridgeService.searchRDFConcepts(targetFramework, LocaleContextHolder.getLocale().getLanguage(), ALLOWED_LANGS, uri);

        if (rdfConcepts != null && rdfConcepts.size() > 0) {
            return rdfConcepts.get(0);
        } else {
            return null;
        }

    }

    public List<RDFConcept> searchRDFConceptsByUri(String targetFramework, String locale, Collection<String> retrieveLangs, String... uris) {

        return rdfSparqlBridgeService.searchRDFConcepts(targetFramework, locale, retrieveLangs, uris);

    }


    public Code searchCountryByEuvocField(String targetFramework, String euvocId, List<String> availableLangs) {

        RDFConceptScheme controlledListCS = rdfSparqlBridgeService.searchConceptScheme(targetFramework);

        List<RDFConcept> rdfConcepts = rdfSparqlBridgeService.searchCountryByEuvocField(targetFramework, euvocId, availableLangs);

        if (rdfConcepts != null && rdfConcepts.size() > 0) {
            return toCode(controlledListCS, rdfConcepts, ALLOWED_LANGS).get(0);
        } else {
            return null;
        }

    }

    public Code searchLanguageByLang(String lang) {
        RDFConcept rdfConcepts = rdfSparqlBridgeService.searchLanguagesByLang(ControlledList.LANGUAGE.getUrl(), LocaleContextHolder.getLocale().getLanguage(), ALLOWED_LANGS);
        if (rdfConcepts != null) {
            return toCode(ControlledList.LANGUAGE.getUrl(), new ArrayList<RDFConcept>() {{
                add(rdfConcepts);
            }}, Arrays.asList(lang)).get(0);
        } else {
            return null;
        }
    }

    public Page<Code> searchConcepts(String targetFramework, String search, String locale, int page, int size, Collection<String> retrieveLangs) {

        RDFConceptScheme controlledListCS = rdfSparqlBridgeService.searchConceptScheme(targetFramework);

        List<RDFConcept> rdfConcepts = searchRDFConcepts(targetFramework, search, locale, page, size, retrieveLangs);

        List<Code> codeList = toCode(controlledListCS, rdfConcepts, retrieveLangs);

        Integer codeCount = codeList.isEmpty() ? 0 : codeList.size() < size ? codeList.size() : rdfSparqlBridgeService.countConcepts(targetFramework, null, search, locale, retrieveLangs);

        PageParam pageParam = new PageParam(page, size);

        return new PageImpl<Code>(
                codeList,
                pageParam.toPageRequest(),
                codeCount);

    }


    public List<Code> toCode(String targetFramework, Collection<RDFConcept> concepts, Collection<String> retrieveLanguages) {

        RDFConceptScheme conceptScheme = rdfSparqlBridgeService.searchConceptScheme(targetFramework);

        return toCode(conceptScheme, concepts, retrieveLanguages);

    }

    public List<Code> toCode(RDFConceptScheme conceptScheme, Collection<RDFConcept> concepts, Collection<String> retrieveLanguages) {

        List<Code> codeList = new ArrayList<>();

        for (RDFConcept concept : concepts) {

            Code code = new Code();
            //Concepts come already localized, but Schemes are cached and require localization on mapping, if null, get all languages
            Text localizedTargetFramework = new Text();
            if (retrieveLanguages != null) {
                List<Content> localizedContents = toText(conceptScheme.getTargetFramework()).getContents().stream().filter(item -> retrieveLanguages.contains(item.getLanguage())).collect(Collectors.toList());
                localizedTargetFramework.setContents(localizedContents);
            } else {
                localizedTargetFramework = toText(conceptScheme.getTargetFramework());
            }
            code.setTargetFrameworkURI(conceptScheme.getTargetFrameworkUri());
            code.setTargetFramework(localizedTargetFramework);
            code.setTargetNotation(conceptScheme.getTargetNotation());

            code.setTargetName(toText(concept.getTargetName()));
            code.setUri(concept.getUri());

            codeList.add(code);
        }

        return codeList;

    }

    public Text toText(Map<String, String> labels) {

        Text text = new Text();

        try {
            text.setContents(labels.entrySet().stream().filter(locale -> ALLOWED_LANGS.contains(locale.getKey()))
                    .map(entry -> new Content(entry.getValue(), entry.getKey())).collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error(e);
        }

        if (text.getContents() == null) {
            return null;
        } else {
            return text;
        }

    }

    public Set<RDFConcept> toRDFConcept(String targetFrameworkURI, ResultSet rs) {

        Set<RDFConcept> conceptList = new LinkedHashSet<>();
        RDFConcept rdfConcept = new RDFConcept();

        while (rs.hasNext()) {

            QuerySolution rb = rs.nextSolution();

            RDFNode uri = rb.get("concept");

            if (uri != null) {

                if (rdfConcept.getUri() == null || !uri.toString().equals(rdfConcept.getUri())) {
                    rdfConcept = new RDFConcept();
                    rdfConcept.setUri(uri.toString());
                    rdfConcept.setTargetFrameworkUri(targetFrameworkURI);
                    conceptList.add(rdfConcept);
                }

                try {
                    Literal targetName = rb.getLiteral("targetName");
                    if (targetName != null) {
                        String prefix = "";
                        RDFNode targetNamePrefix = rb.get("targetNamePrefix");
                        if (targetNamePrefix != null) {
                            prefix = targetNamePrefix.toString() + " - ";
                        }
                        rdfConcept.addTargetName(targetName.getLanguage(), prefix + targetName.getString());
                    }
                } catch (Exception e) {
                    logger.error("Error loading Concept name literal for " + targetFrameworkURI + " controlled list", e);
                }

                Literal externalResource = rb.getLiteral("externalResource");
                if (rdfConcept.getExternalResource() == null && externalResource != null) {
                    rdfConcept.setExternalResource(externalResource.getString());
                }

            }

        }

        if (rdfConcept.getTargetName() != null && rdfConcept.getTargetName().isEmpty()) {
            rdfConcept.setTargetName(null);
        }

        return conceptList;

    }


    public RDFConceptScheme toRDFConceptScheme(String targetFrameworkURI, ResultSet rs) {

        RDFConceptScheme rdfConceptScheme = new RDFConceptScheme();

        rdfConceptScheme.setTargetFrameworkUri(targetFrameworkURI);

        while (rs.hasNext()) {
            QuerySolution rb = rs.nextSolution();

            Literal targetNotation = rb.getLiteral("targetNotation");
            if (targetNotation != null && rdfConceptScheme.getTargetNotation() == null) {
                rdfConceptScheme.setTargetNotation(targetNotation.getString());
            }

            try {
                Literal targetName = rb.getLiteral("targetName");
                if (targetName != null) {
                    rdfConceptScheme.addTargetFramework(targetName.getLanguage(), targetName.getString());
                }
            } catch (Exception e) {
                logger.error("Error loading ConceptSchema name literal for " + targetFrameworkURI + " controlled list", e);
            }

        }

        if (rdfConceptScheme.getTargetFramework() != null && rdfConceptScheme.getTargetFramework().isEmpty()) {
            rdfConceptScheme.setTargetFramework(null);
        }

        return rdfConceptScheme;

    }

    public void resetAllControlledListsConceptSchemes() {
        controlledListsConceptSchemes.clear();
    }
}

