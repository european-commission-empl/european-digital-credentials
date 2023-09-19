package eu.europa.ec.empl.edci.service;

import com.apicatalog.jsonld.JsonLdError;
import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.model.external.qdr.QDRSearchResponseDTO;
import eu.europa.ec.empl.edci.util.EDCIRestRequestBuilder;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import eu.europa.ec.empl.edci.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for externally retrieving QDR Accreditations, uses edci.accreditation.endpoint property.
 * Uses internal frame accreditation/accreditations_frame.jsonld before marshalling rdf response from QDR.
 * Accreditations are cached and can be configured through ehcache.xml.
 *
 * @see DataModelConstants.Properties#ACCREDITATION_ENDPOINT
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class QDRAccreditationExternalService {
    private static final Logger logger = LogManager.getLogger(QDRAccreditationExternalService.class);

    @Autowired
    private BaseConfigService baseConfigService;
    @Autowired
    private ControlledListCommonsService controlledListCommonsService;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private JsonLdUtil jsonLdUtil;
    private String frame;

    /**
     * PreLoads Frame content from accreditation/accreditations_frame.jsonld IllegalStateException trhown if not found
     */
    @PostConstruct
    private void postConstruct() {
        String frameJson;
        try {
            InputStream inputStream = new ClassPathResource("accreditation/accreditations_frame.jsonld").getInputStream();
            frameJson = IOUtils.toString(inputStream);
        } catch (Exception e) {
            throw new IllegalStateException("error loading accreditation frame", e);
        }
        this.setFrame(frameJson);
    }

    /**
     * Gets AccreditationDTO from QDR by URI and language, cached at AC_Accreditation
     *
     * @param uri  the uri/id of the accreditation
     * @param lang the ConceptDTO of the language
     * @return the parsed AccreditationDTO
     */
    @Cacheable("AC_Accreditation")
    public AccreditationDTO retrieveAccreditationByUri(String uri, ConceptDTO lang) {
        return this.retrieveAccreditationByUri(uri, this.getControlledListCommonsService().searchLanguageISO639ByConcept(lang));
    }

    /**
     * Gets AccreditationDTO from QDR by URI and language, cached at AC_Accreditation
     *
     * @param uri  the uri/id of the accreditation
     * @param lang the language string
     * @return the parsed AccreditationDTO
     */
    @Cacheable("AC_Accreditation")
    public AccreditationDTO retrieveAccreditationByUri(String uri, String lang) {
        String accreditationString = this.doQDRAccreditationServiceRequest(uri, lang);
        AccreditationDTO accreditationDTO;
        try {
            accreditationDTO = this.doFrameAccreditation(accreditationString);
        } catch (JsonLdError | IOException e) {
            throw new EDCIException(HttpStatus.BAD_REQUEST, String.format("error framing accreditation %s", uri)).setCause(e);
        }
        return accreditationDTO;
    }

    /**
     * Gets AccreditationDTO from QDR by notation and language, cached at AC_Accreditation.
     * This method first calls que QDR search to get the uri for the accreditation, then requests the details.
     *
     * @param notation
     * @param lang
     * @return
     */
    @Cacheable("AC_Accreditation")
    public AccreditationDTO retrieveAccreditationByNotation(String notation, String lang) {
        QDRSearchResponseDTO qdrSearchResponseDTO = this.doQDRAccreditationSearchServiceRequest(notation, lang);
        if (qdrSearchResponseDTO != null && qdrSearchResponseDTO.getTotalMatchingCount() == 1) {
            return this.retrieveAccreditationByUri(qdrSearchResponseDTO.getAccreditations().get(0).getUri().toString(), lang);
        } else {
            throw new EDCIException(HttpStatus.NOT_FOUND, String.format("Accreditation for notation %s not found", notation));
        }
    }

    /**
     * Checks if an accreditation exists in a language
     *
     * @param notation the notation of the accreditation
     * @param lang     the language string
     * @return true if the accreditation existis
     */
    public Boolean doesAccreditationExist(String notation, String lang) {
        QDRSearchResponseDTO qdrSearchResponseDTO = this.doQDRAccreditationSearchServiceRequest(notation, lang);
        return qdrSearchResponseDTO != null && qdrSearchResponseDTO.getTotalMatchingCount() == 1;
    }

    public Boolean doesAccreditationExist(URI id, String lang){
        try{
            String json = this.doQDRAccreditationServiceRequest(id.toString(),lang);
            return json != null && !json.isBlank();
        }catch (EDCIException e){
            return false;
        }
    }

    /**
     * Performs request to QDR search service using edci.accreditation.search.endpoint, returns the result of the search query
     *
     * @param notation the notation to search
     * @param lang     the language
     * @return the search query response
     */
    public QDRSearchResponseDTO doQDRAccreditationSearchServiceRequest(String notation, String lang) {
        String url = this.getBaseConfigService().getString(DataModelConstants.Properties.ACCREDITATION_SEARCH_ENDPOINT);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("language", lang);
        parameters.put("notation", notation);

        QDRSearchResponseDTO qdrSearchResponseDTO = null;

        try {
            qdrSearchResponseDTO = this.executeRequestBuilder(url, parameters, QDRSearchResponseDTO.class);
        } catch (Exception e) {
            throw new EDCIException(HttpStatus.NOT_FOUND, String.format("Accreditation for notation %s not found", notation)).setCause(e);
        }

        return qdrSearchResponseDTO;
    }

    /**
     * Performs request to QDR service using edci.accreditation.endpoint property, returns response in plain string.
     *
     * @param uri  the uri/id of the accreditation
     * @param lang the language string
     * @return the response's body in plain string
     */
    private String doQDRAccreditationServiceRequest(String uri, String lang) {
        String accreditationString;

        String url = this.getBaseConfigService().getString(DataModelConstants.Properties.ACCREDITATION_ENDPOINT);

        Map<String, String> parameters = new HashMap<>();


        parameters.put("language", lang);
        parameters.put("uri", uri);

        try {
            accreditationString = executeRequestBuilder(url, parameters, String.class);
        } catch (EDCIRestException e) {
            if (e.getHttpStatus().equals(HttpStatus.BAD_REQUEST)) {
                throw new EDCIException(HttpStatus.NOT_FOUND, String.format("Accreditation for uri %s not found", uri)).setCause(e);
            }
            throw e;
        }
        return accreditationString;
    }

    /**
     * Frames accreditation response into an AccreditationDTO
     *
     * @param json the Accreditation's json
     * @return the parsed AccreditationDTO
     * @throws JsonLdError on framing error
     * @throws IOException on unMarshalling framed string error
     */
    private AccreditationDTO doFrameAccreditation(String json) throws JsonLdError, IOException {
        String framedAcc = this.getJsonLdUtil().doFrame(json, this.getFrame());
        return this.getJsonUtil().unMarshall(framedAcc, AccreditationDTO.class);
    }

    public BaseConfigService getBaseConfigService() {
        return baseConfigService;
    }

    public void setBaseConfigService(BaseConfigService baseConfigService) {
        this.baseConfigService = baseConfigService;
    }

    /**
     * Make a rest call to a provided endpoint.
     * Parameters can be provided and will be added to the query.
     * The result is received in the provided class {clazz parameter}
     *
     * @param <T>        the type parameter
     * @param url        the endpoint
     * @param parameters the parameters
     * @param clazz      the expected result class
     * @return the result of the call
     */
    public <T> T executeRequestBuilder(String url, Map<String, String> parameters, Class<T> clazz) {
        return new EDCIRestRequestBuilder(HttpMethod.GET, url)
                .addHeaderRequestedWith()
                .addHeaders(null, MediaType.APPLICATION_JSON)
                .addQueryParams(parameters)
                .buildRequest(clazz)
                .execute();
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public JsonUtil getJsonUtil() {
        return jsonUtil;
    }

    public void setJsonUtil(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public JsonLdUtil getJsonLdUtil() {
        return jsonLdUtil;
    }

    public void setJsonLdUtil(JsonLdUtil jsonLdUtil) {
        this.jsonLdUtil = jsonLdUtil;
    }

    public ControlledListCommonsService getControlledListCommonsService() {
        return controlledListCommonsService;
    }

    public void setControlledListCommonsService(ControlledListCommonsService controlledListCommonsService) {
        this.controlledListCommonsService = controlledListCommonsService;
    }
}
