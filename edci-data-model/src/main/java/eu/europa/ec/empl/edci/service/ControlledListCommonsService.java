package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConceptScheme;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptSchemeDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import org.apache.commons.lang3.tuple.Pair;
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

import java.net.URI;
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

        return getRdfSparqlBridgeService().searchRDFConcepts(targetFramework, search, locale, page, size, retrieveLangs);

    }

    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByConcept(ControlledListConcept concept) {
        return searchConceptByUri(concept.getControlledList().getUrl(), concept.getUrl(), LocaleContextHolder.getLocale().toString());
    }

    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByConcept(ControlledListConcept concept, String locale) {
        return searchConceptByUri(concept.getControlledList().getUrl(), concept.getUrl(), locale);
    }

    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByConcept(ControlledListConcept concept, Collection<String> retrieveLangs) {
        return searchConceptByUri(concept.getControlledList().getUrl(), concept.getUrl(), retrieveLangs, LocaleContextHolder.getLocale().toString());
    }

    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByConcept(ControlledListConcept concept, Collection<String> retrieveLangs, String locale) {
        return searchConceptByUri(concept.getControlledList().getUrl(), concept.getUrl(), retrieveLangs, locale);
    }

    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByUri(String targetFramework, String uri, String locale) {
        RDFConceptScheme controlledListCS = getRdfSparqlBridgeService().searchConceptScheme(targetFramework);
        List<RDFConcept> rdfConcepts = searchRDFConceptsByUri(targetFramework, locale, ALLOWED_LANGS, uri);
        if (rdfConcepts != null && rdfConcepts.size() > 0) {
            return toConcept(controlledListCS, rdfConcepts, Arrays.asList(locale)).get(0);
        } else {
            return null;
        }
    }

    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByUri(String targetFramework, String uri, Collection<String> retrieveLangs, String locale) {
        RDFConceptScheme controlledListCS = getRdfSparqlBridgeService().searchConceptScheme(targetFramework);
        List<RDFConcept> rdfConcepts = searchRDFConceptsByUri(targetFramework, locale, retrieveLangs != null ? retrieveLangs : ALLOWED_LANGS, uri);
        if (rdfConcepts != null && rdfConcepts.size() > 0) {
            return toConcept(controlledListCS, rdfConcepts, Arrays.asList(locale)).get(0);
        } else {
            return null;
        }
    }

    public List<ConceptDTO> searchConceptsByUri(String targetFramework, String locale, Collection<String> retrieveLangs, String... uris) {
        RDFConceptScheme controlledListCS = getRdfSparqlBridgeService().searchConceptScheme(targetFramework);
        List<RDFConcept> rdfConcepts = searchRDFConceptsByUri(targetFramework, locale, retrieveLangs, uris);
        return toConcept(controlledListCS, rdfConcepts, retrieveLangs);
    }

    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByUri(String targetFramework, String uri) {
        return this.searchConceptByUri(targetFramework, uri, LocaleContextHolder.getLocale().toString(), ALLOWED_LANGS);
    }

    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByUri(String targetFramework, String uri, String locale, Collection<String> retrieveLangs) {
        List<ConceptDTO> concepts = this.searchConceptsByUri(targetFramework, locale, retrieveLangs, uri);
        return concepts != null && !concepts.isEmpty() ? concepts.get(0) : null;
    }

    public RDFConcept searchRDFConceptByUri(String targetFramework, String uri) {
        List<RDFConcept> rdfConcepts = getRdfSparqlBridgeService().searchRDFConcepts(targetFramework, LocaleContextHolder.getLocale().getLanguage(), ALLOWED_LANGS, uri);
        if (rdfConcepts != null && rdfConcepts.size() > 0) {
            return rdfConcepts.get(0);
        } else {
            return null;
        }
    }

    public List<RDFConcept> searchRDFConceptsByUri(String targetFramework, String locale, Collection<String> retrieveLangs, String... uris) {
        return getRdfSparqlBridgeService().searchRDFConcepts(targetFramework, locale, retrieveLangs, uris);
    }

    @Cacheable("CL_sparQL")
    public ConceptDTO searchCountryByEuvocField(String targetFramework, String euvocId, List<String> availableLangs) {
        RDFConceptScheme controlledListCS = getRdfSparqlBridgeService().searchConceptScheme(targetFramework);
        List<RDFConcept> rdfConcepts = getRdfSparqlBridgeService().searchCountryByEuvocField(targetFramework, euvocId, availableLangs);
        if (rdfConcepts != null && rdfConcepts.size() > 0) {
            return toConcept(controlledListCS, rdfConcepts, ALLOWED_LANGS).get(0);
        } else {
            return null;
        }
    }

    @Cacheable("CL_FileType")
    public ConceptDTO getFileType(String extension) {
        String fileType = extension;
        if (extension.equalsIgnoreCase("JPG")) {
            fileType = "JPEG";
        }
        //TODO code-concept
        Page<ConceptDTO> fileTypeCode = this.searchConcepts(ControlledList.FILE_TYPE.getUrl(), fileType, Defaults.LOCALE, 0, 1, null);
        return fileTypeCode != null ? fileTypeCode.getContent().get(0) : null;
    }

    @Cacheable("CL_sparQL")
    public ConceptDTO searchLanguageByLang(String lang) {
        RDFConcept rdfConcepts = getRdfSparqlBridgeService().searchLanguagesByLang(ControlledList.LANGUAGE.getUrl(), lang, ALLOWED_LANGS);
        if (rdfConcepts != null) {
            return toConcept(ControlledList.LANGUAGE.getUrl(), new ArrayList<RDFConcept>() {{
                add(rdfConcepts);
            }}, Arrays.asList(lang)).get(0);
        } else {
            return null;
        }
    }

    public List<ConceptDTO> searchConceptsTreeByUri(String targetFramework, String uri, String lang) {
        Set<RDFConcept> rdfConcepts = getRdfSparqlBridgeService().searchTreeConcepts(targetFramework, uri);
        if (rdfConcepts != null) {
            return toConcept(targetFramework, rdfConcepts, Arrays.asList(lang));
        } else {
            return null;
        }
    }

    @Cacheable("CL_ISOLanguages")
    public List<String> searchLanguageISO639ByConcept(List<ConceptDTO> conceptDTO) {
        return conceptDTO.stream().map(concept -> this.searchLanguageISO639ByConcept(concept)).collect(Collectors.toList());
    }

    @Cacheable("CL_ISOLanguage")
    public String searchLanguageISO639ByURI(String langURI) {
        return getRdfSparqlBridgeService().searchLanguageISO639ByURI(langURI);
    }

    @Cacheable("CL_ISOLanguage")
    public String searchLanguageISO639ByConcept(ConceptDTO langConcept) {
        return this.searchLanguageISO639ByURI(langConcept.getId().toString());
    }

    @Cacheable("CL_sparQLPage")
    public Page<ConceptDTO> searchConcepts(String targetFramework, String search, String locale, int page, int size, Collection<String> retrieveLangs) {
        RDFConceptScheme controlledListCS = getRdfSparqlBridgeService().searchConceptScheme(targetFramework);
        List<RDFConcept> rdfConcepts = searchRDFConcepts(targetFramework, search, locale, page, size, retrieveLangs);
        List<ConceptDTO> ConceptDTOList = toConcept(controlledListCS, rdfConcepts, retrieveLangs);
        Integer ConceptDTOCount = ConceptDTOList.isEmpty() ? 0 : ConceptDTOList.size() < size ? ConceptDTOList.size() : getRdfSparqlBridgeService().countConcepts(targetFramework, null, search, locale, retrieveLangs);
        PageParam pageParam = new PageParam(page, size);
        return new PageImpl<ConceptDTO>(
                ConceptDTOList,
                pageParam.toPageRequest(),
                ConceptDTOCount);
    }

    public List<ConceptDTO> toConcept(String targetFramework, Collection<RDFConcept> concepts, Collection<String> retrieveLanguages) {
        RDFConceptScheme conceptScheme = getRdfSparqlBridgeService().searchConceptScheme(targetFramework);
        return toConcept(conceptScheme, concepts, retrieveLanguages);
    }

    public List<ConceptDTO> toConcept(RDFConceptScheme conceptScheme, Collection<RDFConcept> concepts, Collection<String> retrieveLanguages) {
        List<ConceptDTO> conceptDTOArrayList = new ArrayList<>();
        ConceptDTO mainConcept = new ConceptDTO();
        mainConcept.setNotation(conceptScheme.getTargetNotation());
        mainConcept.setId(URI.create(conceptScheme.getTargetFrameworkUri()));
        mainConcept.setPrefLabel(new LiteralMap());
        //Concepts come already localized, but Schemes are cached and require localization on mapping, if null, get all languages
        if (retrieveLanguages != null) {
            for (Map.Entry<String, String> entry : conceptScheme.targetFramework.entrySet()) {
                if (retrieveLanguages.contains(entry.getKey())) {
                    mainConcept.getPrefLabel().put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            for (Map.Entry<String, String> entry : conceptScheme.targetFramework.entrySet()) {
                mainConcept.getPrefLabel().put(entry.getKey(), entry.getValue());
            }
        }

        for (RDFConcept concept : concepts) {
            ConceptDTO conceptDTO = new ConceptDTO();
            conceptDTO.setId(URI.create(concept.getUri()));
            conceptDTO.setNotation(conceptScheme.getTargetNotation());
            ConceptSchemeDTO conceptSchemeDTO = new ConceptSchemeDTO();
            conceptSchemeDTO.setId(mainConcept.getId());
            conceptDTO.setInScheme(conceptSchemeDTO);
            conceptDTO.setPrefLabel(new LiteralMap());
            for (Map.Entry<String, String> entry : concept.getTargetName().entrySet()) {
                conceptDTO.getPrefLabel().put(entry.getKey(), entry.getValue());
            }
            conceptDTOArrayList.add(conceptDTO);
        }
        return conceptDTOArrayList;
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

    public boolean isValidCredentialProfile(URI credentialProfileId) {
        return ControlledListConcept.fromUrl(credentialProfileId.toString()) != null;
    }

    @Cacheable("CL_sparQL")
    public ConceptDTO getDefaultCredentialProfile() {
        return this.searchConceptByUri(ControlledList.CREDENTIAL_TYPE.getUrl(), ControlledListConcept.CREDENTIAL_TYPE_GENERIC.getUrl(), Defaults.LOCALE);
    }


    private URI getShaclURIFromProfile(ConceptDTO profile) {
        Pair<String, URI> extraData = this.getRdfSparqlBridgeService().searchTypeAndSchemaForCredType(profile.getId().toString());
        return extraData.getValue();
    }

    @Cacheable("CL_ProfileUri")
    public List<URI> getShaclURIsFromProfiles(List<ConceptDTO> profiles) {
        return profiles.stream().map(type -> this.getShaclURIFromProfile(type)).filter(uri -> uri != null).collect(Collectors.toList());
    }


    public void resetAllControlledListsConceptSchemes() {
        controlledListsConceptSchemes.clear();
    }

    public RDFsparqlBridgeService getRdfSparqlBridgeService() {
        return rdfSparqlBridgeService;
    }

    public void setRdfSparqlBridgeService(RDFsparqlBridgeService rdfSparqlBridgeService) {
        this.rdfSparqlBridgeService = rdfSparqlBridgeService;
    }
}

