package eu.europa.ec.empl.edci.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import eu.europa.ec.empl.edci.datamodel.adapter.DurationJsonAdapter;
import eu.europa.ec.empl.edci.datamodel.model.LearningSpecificationDTO;
import eu.europa.ec.empl.edci.datamodel.model.QualificationDTO;
import org.apache.log4j.Logger;
import org.joda.time.Period;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class JsonUtil {

    protected static final Logger logger = Logger.getLogger(JsonUtil.class);

    protected Gson getGsonContext() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        RuntimeTypeAdapterFactory<LearningSpecificationDTO> qualificationTypeAdapterFactory =
                RuntimeTypeAdapterFactory.of(LearningSpecificationDTO.class, "type")
                        .registerSubtype(LearningSpecificationDTO.class, LearningSpecificationDTO.class.getName())
                        .registerSubtype(QualificationDTO.class, QualificationDTO.class.getName());
        gsonBuilder.registerTypeAdapterFactory(qualificationTypeAdapterFactory);
        gsonBuilder.registerTypeAdapter(Period.class, new DurationJsonAdapter());
        return gsonBuilder.create();
    }

    protected ObjectMapper getJSONObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
        return objectMapper;
    }

    public <T> String toJSON(T object) throws JsonProcessingException {
        return getJSONObjectMapper().writeValueAsString(object);
    }

    public <T> T fromJSON(String json, Class<T> clazz) throws IOException {
        return getJSONObjectMapper().readValue(json, clazz);
    }

}
