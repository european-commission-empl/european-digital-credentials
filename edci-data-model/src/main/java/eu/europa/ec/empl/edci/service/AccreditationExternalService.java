package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;
import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.model.external.qdr.QDRAccreditationDTO;
import eu.europa.ec.empl.edci.model.external.qdr.QDRConceptDTO;
import eu.europa.ec.empl.edci.model.external.qdr.QDRConceptSchemeDTO;
import eu.europa.ec.empl.edci.model.external.qdr.QDRJsonLdCommonDTO;
import eu.europa.ec.empl.edci.util.EDCIRestRequestBuilder;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AccreditationExternalService {
    private static final Logger logger = LogManager.getLogger(AccreditationExternalService.class);

    @Autowired
    private BaseConfigService baseConfigService;

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    public <T> T doGetRequest(String url, Map<String, String> parameters, Class<T> responseType, MediaType accept) {
        return new EDCIRestRequestBuilder(HttpMethod.GET, url)
                .addHeaderRequestedWith()
                .addHeaders(null, MediaType.APPLICATION_JSON)
                .addQueryParams(parameters)
                .buildRequest(responseType)
                .execute();
    }

    public QDRAccreditationDTO retrieveAccreditationByUri(String uri, String lang) {
        String url = this.getBaseConfigService().getString(DataModelConstants.Properties.ACCREDITATION_ENDPOINT);

        Map<String, String> parameters = new HashMap<>();

        parameters.put("language", lang);
        parameters.put("uri", uri);

        QDRAccreditationDTO qdrAccreditationDTO = null;

        try {
            qdrAccreditationDTO = this.doGetRequest(url, parameters, QDRAccreditationDTO.class, MediaType.APPLICATION_JSON);
        } catch (EDCIRestException e) {
            if (e.getHttpStatus().equals(HttpStatus.BAD_REQUEST)) {
                throw new EDCIException(HttpStatus.NOT_FOUND, "Accreditation not found");
            }

            throw e;
        }
        this.doAddConceptSchemes(qdrAccreditationDTO);

        return qdrAccreditationDTO;
    }

    public void doAddConceptSchemes(QDRAccreditationDTO qdrAccreditationDTO) {
        BiConsumer<QDRConceptDTO, String> consumer = (obj, value) -> {
            try {
                if (obj != null && (obj.getInScheme() == null || obj.getInScheme().isEmpty())) {
                    obj.setInScheme(Arrays.asList(new QDRConceptSchemeDTO(new URI(value))));
                }
            } catch (Exception e) {
                logger.error("Error generating URI for: " + value);
                throw new EDCIException(e).addDescription(e.getMessage());
            }
        };
        getReflectiveUtil().doActionToAllFieldsAnnotatedWith(qdrAccreditationDTO, QDRJsonLdCommonDTO.class, MandatoryConceptScheme.class, consumer);
    }

    public BaseConfigService getBaseConfigService() {
        return baseConfigService;
    }

    public void setBaseConfigService(BaseConfigService baseConfigService) {
        this.baseConfigService = baseConfigService;
    }

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }
}
