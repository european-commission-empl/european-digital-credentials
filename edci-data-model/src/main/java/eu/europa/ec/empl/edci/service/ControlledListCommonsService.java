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

/**
 * Service to handle controlled lists and calls to SPARQL Service.
 */
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

    /**
     * Search rdf concepts that belong to the controlled list indicated with the 'target framework'.
     * Filtered by the uri that identifies the concept and a locale language.
     * The actual page and the number of elements of this page can be provided for pagination.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @param search          the text fragment to be searched
     * @param page            the actual page of the pagination
     * @param size            the number of elements inside the page
     * @param retrieveLangs   the retrieve languages
     * @return the concept list
     */
    public List<RDFConcept> searchRDFConcepts(String targetFramework, String search, String locale, int page, int size, Collection<String> retrieveLangs) {
        return getRdfSparqlBridgeService().searchRDFConcepts(targetFramework, search, locale, page, size, retrieveLangs);
    }

    /**
     * Search concepts that belong to the controlled list indicated inside the controlledListConcept.
     *
     * @param concept the controlled list concept that has the information of the framework
     * @return the concept dto
     */
    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByConcept(ControlledListConcept concept) {
        if(concept == null ) {
            return null;
        }

        return searchConceptByUri(concept.getControlledList().getUrl(), concept.getUrl(), LocaleContextHolder.getLocale().toString());
    }

    /**
     * Search concepts that belong to the controlled list indicated inside the controlledListConcept.
     * Can be filtered providing a locale language
     *
     * @param concept the controlled list concept that has the information of the framework
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @return the concept dto
     */
    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByConcept(ControlledListConcept concept, String locale) {
        if(concept == null ) {
            return null;
        }
        return searchConceptByUri(concept.getControlledList().getUrl(), concept.getUrl(), locale);
    }

    /**
     * Search concepts that belong to the controlled list indicated inside the controlledListConcept.
     * If the 'retrieve languages' are provided, the results will come in those languages.
     *
     * @param concept the controlled list concept that has the information of the framework
     * @param retrieveLangs the retrieve languages
     * @return the concept dto
     */
    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByConcept(ControlledListConcept concept, Collection<String> retrieveLangs) {
        if(concept == null ) {
            return null;
        }

        return searchConceptByUri(concept.getControlledList().getUrl(), concept.getUrl(), retrieveLangs, LocaleContextHolder.getLocale().toString());
    }

    /**
     * Search concepts that belong to the controlled list indicated inside the controlledListConcept.
     * Can be filtered providing a locale language.
     * If the 'retrieve languages' are provided, the results will come in those languages.
     *
     * @param concept the controlled list concept that has the information of the framework
     * @param retrieveLangs the retrieve languages
     * @param locale        the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @return the concept dto
     */
    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByConcept(ControlledListConcept concept, Collection<String> retrieveLangs, String locale) {
        if(concept == null ) {
            return null;
        }

        return searchConceptByUri(concept.getControlledList().getUrl(), concept.getUrl(), retrieveLangs, locale);
    }

    /**
     * This method search concepts that belong to the controlled list indicated with the 'target framework'.
     * Filtered by the uri that identifies the concept and a locale language.
     *
     *
     * @param targetFramework the URL identifying a controlled list
     * @param uri             the URI identifying a concept
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @return the concept dto
     */
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

    /**
     * This method search concepts that belong to the controlled list indicated with the 'target framework'.
     * Filtered by the uri that identifies the concept and a locale language.
     * If the 'retrieve languages' are provided, the results will come in those languages.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param uri             the URI identifying a concept
     * @param retrieveLangs   the retrieve languages
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @return the concept dto
     */
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

    /**
     * This method search concepts that belong to the controlled list indicated with the 'target framework'.
     * Filtered by a list of uris that identifies the concepts and a locale language.
     * If the 'retrieve languages' are provided, the results will come in those languages.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param uris             the URIs identifying the concepts
     * @param retrieveLangs   the retrieve languages
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @return the list of concepts
     */
    public List<ConceptDTO> searchConceptsByUri(String targetFramework, String locale, Collection<String> retrieveLangs, String... uris) {
        RDFConceptScheme controlledListCS = getRdfSparqlBridgeService().searchConceptScheme(targetFramework);
        List<RDFConcept> rdfConcepts = searchRDFConceptsByUri(targetFramework, locale, retrieveLangs, uris);
        if (rdfConcepts != null && rdfConcepts.size() > 0) {
            return toConcept(controlledListCS, rdfConcepts, retrieveLangs);
        } else {
            return null;
        }
    }

    /**
     * This method search concepts that belong to the controlled list indicated with the 'target framework'.
     * Filtered by the uri that identifies the concept.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param uri             the URI identifying a concept
     * @return the concept dto
     */
    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByUri(String targetFramework, String uri) {
        return this.searchConceptByUri(targetFramework, uri, LocaleContextHolder.getLocale().toString(), ALLOWED_LANGS);
    }

    /**
     * This method search concepts that belong to the controlled list indicated with the 'target framework'.
     * Filtered by the uri that identifies the concept and a locale language.
     * If the 'retrieve languages' are provided, the results will come in those languages.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param uri             the URI identifying a concept
     * @param retrieveLangs   the retrieve languages
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     *
     * @return the concept dto, if the result is empty, null
     */
    @Cacheable("CL_sparQL")
    public ConceptDTO searchConceptByUri(String targetFramework, String uri, String locale, Collection<String> retrieveLangs) {
        List<ConceptDTO> concepts = this.searchConceptsByUri(targetFramework, locale, retrieveLangs, uri);
        return concepts != null && !concepts.isEmpty() ? concepts.get(0) : null;
    }

    /**
     * This method search rdf concepts that belong to the controlled list indicated with the 'target framework'.
     * Filtered by the uri that identifies the concept.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param uri             the URI identifying a concept
     * @return the rdf concept
     */
    public RDFConcept searchRDFConceptByUri(String targetFramework, String uri) {
        List<RDFConcept> rdfConcepts = getRdfSparqlBridgeService().searchRDFConcepts(targetFramework, LocaleContextHolder.getLocale().getLanguage(), ALLOWED_LANGS, uri);
        if (rdfConcepts != null && rdfConcepts.size() > 0) {
            return rdfConcepts.get(0);
        } else {
            return null;
        }
    }

    /**
     * This method search rdf concepts that belong to the controlled list indicated with the 'target framework'.
     * Filtered by a list of uris that identifies the concepts and a locale language.
     * If the 'retrieve languages' are provided, the results will come in those languages.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param uris            the URIs identifying the concepts
     * @param retrieveLangs   the retrieve languages
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @return the list of rdf concepts
     */
    public List<RDFConcept> searchRDFConceptsByUri(String targetFramework, String locale, Collection<String> retrieveLangs, String... uris) {
        return getRdfSparqlBridgeService().searchRDFConcepts(targetFramework, locale, retrieveLangs, uris);
    }

    /**
     * Search rdf concepts that belong to the controlled list indicated with the 'target framework'.
     * Filtered by the evouc id that identifies the country.
     * If the 'available languages' are provided, the results will come in those languages.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param euvocId         the euvoc id identifying the country
     * @param availableLangs   the retrieve languages
     * @return the concept dto
     */
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

    /**
     * Gets file type concept inside a controlled list from a provided extension.
     *
     * @param extension the extension
     * @return the file type concept
     */
    @Cacheable("CL_FileType")
    public ConceptDTO getFileType(String extension) {
        if(extension == null) {
            return null;
        }

        String fileType = extension;
        if (extension.equalsIgnoreCase("JPG")) {
            fileType = "JPEG";
        }

        Page<ConceptDTO> fileTypeCode = this.searchConcepts(ControlledList.FILE_TYPE.getUrl(), fileType, Defaults.LOCALE, 0, 1, null);
        return fileTypeCode != null ? fileTypeCode.getContent().get(0) : null;
    }

    /**
     * Search languages that belong to the controlled list authority/language.
     * Can be filtered providing a lang code.
     *
     * @param lang   the lang code in ISO 639-1 format (Ex: 'en' - English)
     * @return the concept dto
     */
    @Cacheable("CL_sparQL")
    public ConceptDTO searchLanguageByLang(String lang) {
        RDFConcept rdfConcepts = getRdfSparqlBridgeService().searchLanguagesByLang(ControlledList.LANGUAGE.getUrl(), lang, ALLOWED_LANGS);
        if (rdfConcepts != null) {
            return toConcept(ControlledList.LANGUAGE.getUrl(), new ArrayList<>() {{
                add(rdfConcepts);
            }}, Arrays.asList(lang)).get(0);
        } else {
            return null;
        }
    }

    /**
     * This method search concepts that belong to the controlled list indicated with the 'target framework' that have a tree structure.
     * Can be filtered using the uri provided that identifies the concept and a language code.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param uri             the URI identifying a concept
     * @param lang   the lang code in ISO 639-1 format (Ex: 'en' - English)
     * @return the list of concepts
     */
    public List<ConceptDTO> searchConceptsTreeByUri(String targetFramework, String uri, String lang) {
        Set<RDFConcept> rdfConcepts = getRdfSparqlBridgeService().searchTreeConcepts(targetFramework, uri);
        if (rdfConcepts != null) {
            return toConcept(targetFramework, rdfConcepts, Arrays.asList(lang));
        } else {
            return null;
        }
    }

    /**
     * Search language in iso 639 format using the uri that identifies the language concept.
     *
     * @param conceptDTO the language concept that contains the uri
     * @return the list of languages
     */
    @Cacheable("CL_ISOLanguages")
    public List<String> searchLanguageISO639ByConcept(List<ConceptDTO> conceptDTO) {
        return conceptDTO.stream().map(concept -> this.searchLanguageISO639ByConcept(concept)).collect(Collectors.toList());
    }

    /**
     * Search language in iso 639 format using the uri that identifies the language concept.
     *
     * @param langURI the language uri identifying the concept
     * @return the language
     */
    @Cacheable("CL_ISOLanguage")
    public String searchLanguageISO639ByURI(String langURI) {
        return getRdfSparqlBridgeService().searchLanguageISO639ByURI(langURI);
    }

    /**
     * Search language in iso 639 format using the uri that identifies the language concept.
     *
     * @param langConcept the language concept that contains the uri
     * @return the language
     */
    @Cacheable("CL_ISOLanguage")
    public String searchLanguageISO639ByConcept(ConceptDTO langConcept) {
        if(langConcept == null  || langConcept.getId() == null) {
            return null;
        }

        return this.searchLanguageISO639ByURI(langConcept.getId().toString());
    }

    /**
     * This method search concepts that belong to the controlled list indicated with the 'target framework'.
     * The search and locale fields can be used to filter the concepts providing a fragment of text.
     * The actual page and the number of elements of this page can be provided for pagination.
     * The results will be returned in a Page with all the concepts and the total number of concepts.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param search          the text fragment to be searched
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @param page            the actual page of the pagination
     * @param size            the number of elements inside the page
     * @param retrieveLangs   the retrieve languages
     * @return the page containing the concepts
     */
    @Cacheable("CL_sparQLPage")
    public Page<ConceptDTO> searchConcepts(String targetFramework, String search, String locale, int page, int size, Collection<String> retrieveLangs) {
        RDFConceptScheme controlledListCS = getRdfSparqlBridgeService().searchConceptScheme(targetFramework);
        List<RDFConcept> rdfConcepts = this.searchRDFConcepts(targetFramework, search, locale, page, size, retrieveLangs);
        List<ConceptDTO> ConceptDTOList = this.toConcept(controlledListCS, rdfConcepts, retrieveLangs);
        Integer ConceptDTOCount = ConceptDTOList == null || ConceptDTOList.isEmpty() ? 0 : ConceptDTOList.size() < size ? ConceptDTOList.size() : getRdfSparqlBridgeService().countConcepts(targetFramework, null, search, locale);
        PageParam pageParam = new PageParam(page, size);

        if(ConceptDTOList == null) {
            return null;
        }

        return new PageImpl<>(
                ConceptDTOList,
                pageParam.toPageRequest(),
                ConceptDTOCount);
    }

    /**
     * Convert RDF Concepts to ConceptDTO using a provided target framework that identifies the controlled list to retrieve the concept scheme.
     *
     * @param targetFramework the URL identifying a controlled list
     * @param concepts          the RDFConcepts to be converted
     * @param retrieveLanguages the retrieve languages
     *
     * @return the list of converted concepts
     */
    public List<ConceptDTO> toConcept(String targetFramework, Collection<RDFConcept> concepts, Collection<String> retrieveLanguages) {
        RDFConceptScheme conceptScheme = getRdfSparqlBridgeService().searchConceptScheme(targetFramework);
        return toConcept(conceptScheme, concepts, retrieveLanguages);
    }

    /**
     * Convert RDF Concepts to ConceptDTO.
     * The provided concept scheme will be stored in the ConceptDTO.
     * If the 'retrieve languages' are provided, the concepts will have those languages, if not, will have all the languages available.
     *
     * @param conceptScheme     the concept scheme
     * @param concepts          the concepts
     * @param retrieveLanguages the retrieve languages
     * @return the converted list of concepts
     */
    public List<ConceptDTO> toConcept(RDFConceptScheme conceptScheme, Collection<RDFConcept> concepts, Collection<String> retrieveLanguages) {
        if(concepts == null || conceptScheme == null) {
            return new ArrayList<>();
        }

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

    /**
     * Converts from result set to RDFConcept, the target framework used to obtain the result set is provided in order to have the complete information in
     * the RDFConcept.
     *
     * @param targetFrameworkURI the URL identifying a controlled list
     * @param rs                 the result set
     * @return the set of RDFConcepts
     */
    public Set<RDFConcept> toRDFConcept(String targetFrameworkURI, ResultSet rs) {
        if(rs == null) {
            return new LinkedHashSet<>();
        }

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


    /**
     * Converts from result set to RDFConceptScheme, to target framework used to obtain the result set is provided in order to have the complete information in
     * the RDFConceptScheme.
     *
     * @param targetFrameworkURI the URL identifying a controlled list
     * @param rs                 the result set
     * @return the rdf concept scheme
     */
    public RDFConceptScheme toRDFConceptScheme(String targetFrameworkURI, ResultSet rs) {
        if(rs == null) {
            return null;
        }

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

    /**
     * This method will check if the provided credential profile ID exists as a controlled list.
     *
     * @param credentialProfileId the credential profile id
     * @return the validity
     */
    public boolean isValidCredentialProfile(URI credentialProfileId) {
        return ControlledListConcept.fromUrl(credentialProfileId.toString()) != null;
    }

    /**
     * Gets default credential profile.
     *
     * @return the default credential profile
     */
    @Cacheable("CL_sparQL")
    public ConceptDTO getDefaultCredentialProfile() {
        return this.searchConceptByUri(ControlledList.CREDENTIAL_TYPE.getUrl(), ControlledListConcept.CREDENTIAL_TYPE_GENERIC.getUrl(), Defaults.LOCALE);
    }


    private URI getShaclURIFromProfile(ConceptDTO profile) {
        Pair<String, URI> extraData = this.getRdfSparqlBridgeService().searchTypeAndSchemaForCredType(profile.getId().toString());
        return extraData.getValue();
    }

    /**
     * Gets shacl uri using the uri provided from credential profiles.
     *
     * @param profiles the credential profiles
     * @return the shacl uri
     */
    @Cacheable("CL_ProfileUri")
    public List<URI> getShaclURIsFromProfiles(List<ConceptDTO> profiles) {
        return profiles.stream().map(type -> this.getShaclURIFromProfile(type)).filter(uri -> uri != null).collect(Collectors.toList());
    }


    /**
     * Reset all controlled lists concept schemes.
     */
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

