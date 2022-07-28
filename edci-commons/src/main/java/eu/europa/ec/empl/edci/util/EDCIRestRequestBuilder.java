package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class EDCIRestRequestBuilder {

    public static final Logger logger = LogManager.getLogger(EDCIRestRequestBuilder.class);

    RestTemplate restTemplate;
    UriComponentsBuilder builder;
    HttpHeaders headers;
    HttpMethod httpMethod;
    Object body;

    public EDCIRestRequestBuilder(HttpMethod httpMethod, String url) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        this.restTemplate = restTemplate;
        this.builder = UriComponentsBuilder.fromHttpUrl(url);
        this.headers = new HttpHeaders();
        this.httpMethod = httpMethod;
    }

    public EDCIRestRequestBuilder addQueryParam(String param, Object value) {
        builder.queryParam(param, value);
        return this;
    }

    public EDCIRestRequestBuilder addQueryParams(Map<String, String> params) {
        if (params != null) {
            params.keySet().stream().forEach(key -> builder.queryParam(key, params.get(key)));
        }
        return this;
    }

    public EDCIRestRequestBuilder addAuthenticationToken(String accessToken) {
        headers.set("Authorization", "Bearer " + accessToken);
        return this;
    }

    public EDCIRestRequestBuilder addHeaderRequestedWith() {
        headers.set("X-Requested-With", "XMLHttpRequest");
        return this;
    }

    public EDCIRestRequestBuilder addHeaders(HttpHeaders httpHeaders) {
        this.headers = httpHeaders;
        return this;
    }

    public EDCIRestRequestBuilder addHeaders(Map<String, String> params) {
        if (params != null) {
            params.keySet().stream().forEach(key -> headers.set(key, params.get(key)));
        }
        return this;
    }

    public EDCIRestRequestBuilder addHeaders(MediaType contentType, MediaType accept) {

        if (contentType != null) {
            headers.set("Content-Type", contentType.getType() + "/" + contentType.getSubtype());
        }
        if (accept != null) {
            headers.set("Accept", accept.getType() + "/" + accept.getSubtype());
        }
        return this;
    }

    public static LinkedMultiValueMap prepareMultiPartFileBody(String paramName, MultipartFile file, MediaType fileType) {
        return prepareMultiPartFileBody(paramName, file, fileType, null);
    }

    public static LinkedMultiValueMap prepareMultiPartFileBody(String paramName, MultipartFile file, MediaType fileType, Map<String, Object> otherParams) {

        LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
        pdfHeaderMap.add("Content-disposition", "form-data; name=" + paramName + "; filename=" + file.getOriginalFilename());
        pdfHeaderMap.add("Content-type", fileType.getType() + "/" + fileType.getSubtype());

        LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();

        try {

            org.springframework.http.HttpEntity<byte[]> doc = new org.springframework.http.HttpEntity<byte[]>(file.getBytes(), pdfHeaderMap);
            multipartReqMap.add(paramName, doc);

            //TODO: This is not tested yet:
            if (otherParams != null) {
                otherParams.keySet().stream().forEach(key -> multipartReqMap.add(key, otherParams.get(key)));
            }
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }

        return multipartReqMap;
    }


    public static LinkedMultiValueMap prepareMultiPartStringBody(String paramName, String text, Map<String, Object> otherParams) {

        LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();

        try {
            multipartReqMap.add(paramName, text);

            //TODO: This is not tested yet:
            if (otherParams != null) {
                otherParams.keySet().stream().forEach(key -> multipartReqMap.add(key, otherParams.get(key)));
            }
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }

        return multipartReqMap;
    }

    public EDCIRestRequestBuilder addBody(Object body) {
        this.body = body;
        return this;
    }

    public <T> EDCIExecute<T> buildRequest(Class<T> responseType) {

        org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(body, headers);

        EDCIExecute<T> returnValue = () -> restTemplate.exchange(
                builder.build().toString(),
                httpMethod,
                entity,
                responseType).getBody();

        return returnValue;
    }

    public <T> EDCIExecute<ResponseEntity<Resource<T>>> buildRequest(ParameterizedTypeReference<Resource<T>> responseType) {

        org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(body, headers);

        String url = builder.build().toString();

        EDCIExecute<ResponseEntity<Resource<T>>> returnValue = () -> restTemplate.exchange(
                url,
                httpMethod,
                entity,
                responseType);

        if (logger.isDebugEnabled()) {
            logger.debug("Building REST call -> {}: ", () -> url);
        }

        return returnValue;
    }


    public interface EDCIExecute<T> {
        T execute() throws EDCIRestException;
    }

}
