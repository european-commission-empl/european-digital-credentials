package eu.europa.ec.empl.edci.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import eu.europa.ec.empl.edci.datamodel.adapter.DurationJsonAdapter;
import eu.europa.ec.empl.edci.datamodel.adapter.LocalDateJsonSerializer;
import eu.europa.ec.empl.edci.datamodel.adapter.ZonedDateJsonSerializer;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.SpecificationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.QualificationDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Deprecated
public class JsonUtil {

    protected static final Logger logger = LogManager.getLogger(JsonUtil.class);

    protected Gson getGsonContext() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        RuntimeTypeAdapterFactory<SpecificationDTO> qualificationTypeAdapterFactory =
                RuntimeTypeAdapterFactory.of(SpecificationDTO.class, "type")
                        .registerSubtype(SpecificationDTO.class, SpecificationDTO.class.getName())
                        .registerSubtype(QualificationDTO.class, QualificationDTO.class.getName());
        gsonBuilder.registerTypeAdapterFactory(qualificationTypeAdapterFactory);
        gsonBuilder.registerTypeAdapter(Period.class, new DurationJsonAdapter());
        return gsonBuilder.create();
    }

    protected ObjectMapper getJacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        objectMapper.registerModule(new JodaModule());
        objectMapper.registerModule(module);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
        return objectMapper;
    }

    public <T> String toJSON(T object) throws JsonProcessingException {
        return getJacksonObjectMapper().writeValueAsString(object);
    }

    public <T> T fromJSON(String json, Class<T> clazz) throws IOException {
        return getJacksonObjectMapper().readValue(json, clazz);
    }

    public <T> T fromJSONFile(File jsonFile, Class<T> clazz) throws IOException {
        return this.getJacksonObjectMapper().readValue(jsonFile, clazz);

    }

}
