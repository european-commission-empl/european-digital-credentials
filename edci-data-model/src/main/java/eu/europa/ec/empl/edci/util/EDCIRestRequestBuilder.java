package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.exception.OIDCException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Builder class to do rest requests.
 */
public class EDCIRestRequestBuilder {

    public static final Logger logger = LogManager.getLogger(EDCIRestRequestBuilder.class);


    RestTemplate restTemplate;

    UriComponentsBuilder builder;

    HttpHeaders headers;

    HttpMethod httpMethod;

    Object body;

    /**
     * Instantiates a new Edci rest request builder.
     *
     * @param httpMethod the http method
     * @param url        the url
     */
    public EDCIRestRequestBuilder(HttpMethod httpMethod, String url) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        this.restTemplate = restTemplate;
        this.builder = UriComponentsBuilder.fromHttpUrl(url);
        this.headers = new HttpHeaders();
        this.httpMethod = httpMethod;
    }

    /**
     * Add query parameter to the request builder (Ex: _locale, "en").
     *
     * @param param the parameter
     * @param value the value
     * @return the edci rest request builder
     */
    public EDCIRestRequestBuilder addQueryParam(String param, Object value) {
        builder.queryParam(param, value);
        return this;
    }

    /**
     * Add multiple query parameters providing a map<Parameter, Value>.
     *
     * @param params the parameters
     * @return the edci rest request builder
     */
    public EDCIRestRequestBuilder addQueryParams(Map<String, String> params) {
        if (params != null) {
            params.keySet().stream().forEach(key -> builder.queryParam(key, params.get(key)));
        }
        return this;
    }

    /**
     * Add authentication token to the request builder.
     *
     * @param accessToken the access token
     * @return the edci rest request builder
     */
    public EDCIRestRequestBuilder addAuthenticationToken(String accessToken) {
        headers.set("Authorization", "Bearer " + accessToken);
        return this;
    }

    /**
     * Add header "X-Requested-With", "XMLHttpRequest" to the rest request builder.
     *
     * @return the edci rest request builder
     */
    public EDCIRestRequestBuilder addHeaderRequestedWith() {
        headers.set("X-Requested-With", "XMLHttpRequest");
        return this;
    }

    /**
     * Add custom http headers to the rest request builder.
     *
     * @param httpHeaders the http headers
     * @return the edci rest request builder
     */
    public EDCIRestRequestBuilder addHeaders(HttpHeaders httpHeaders) {
        this.headers = httpHeaders;
        return this;
    }

    /**
     * Add custom http headers to the rest request builder.
     *
     * @param params a Map containing the headers
     * @return the edci rest request builder
     */
    public EDCIRestRequestBuilder addHeaders(Map<String, String> params) {
        if (params != null) {
            params.keySet().stream().forEach(key -> headers.set(key, params.get(key)));
        }
        return this;
    }

    /**
     * Add headers "Content-Type" and "Accept" to the rest request builder.
     *
     * @param contentType the content type header
     * @param accept      the accept header
     * @return the edci rest request builder
     */
    public EDCIRestRequestBuilder addHeaders(MediaType contentType, MediaType accept) {

        if (contentType != null) {
            headers.set("Content-Type", contentType.getType() + "/" + contentType.getSubtype());
        }
        if (accept != null) {
            headers.set("Accept", accept.getType() + "/" + accept.getSubtype());
        }
        return this;
    }

    /**
     * Prepare multi part file body in order to be sent in a rest request.
     *
     * @param paramName the parameter name
     * @param file      the file
     * @param fileType  the file type
     * @return the linked multi value map
     */
    public static LinkedMultiValueMap prepareMultiPartFileBody(String paramName, MultipartFile file, MediaType fileType) {
        return prepareMultiPartFileBody(paramName, file, fileType, null);
    }

    /**
     * Prepare multi part file body in order to be sent in a rest request.
     *
     * @param paramName   the parameter name
     * @param file        the file
     * @param fileType    the file type
     * @param otherParams the other parameters
     * @return the linked multi value map
     */
    public static LinkedMultiValueMap prepareMultiPartFileBody(String paramName, MultipartFile file, MediaType fileType, Map<String, Object> otherParams) {

        LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
        pdfHeaderMap.add("Content-disposition", "form-data; name=" + paramName + "; filename=" + file.getOriginalFilename());
        pdfHeaderMap.add("Content-type", fileType.getType() + "/" + fileType.getSubtype());

        LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();

        try {

            org.springframework.http.HttpEntity<byte[]> doc = new org.springframework.http.HttpEntity<>(file.getBytes(), pdfHeaderMap);
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


    /**
     * Prepare multi part string body in order to be sent in a rest request.
     *
     * @param paramName   the parameter name
     * @param text        the value of the parameter
     * @param otherParams the other parameters
     * @return the linked multi value map
     */
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

    /**
     * Add body to the rest request builder.
     *
     * @param body the body
     * @return the edci rest request builder
     */
    public EDCIRestRequestBuilder addBody(Object body) {
        this.body = body;
        return this;
    }

    /**
     * Builds rest request providing the expected response class.
     *
     * @param <T>          the type parameter
     * @param responseType the response type
     * @return the result of the request
     */
    public <T> EDCIExecute<T> buildRequest(Class<T> responseType) {

        org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(body, headers);

        EDCIExecute<T> returnValue = () -> restTemplate.exchange(
                builder.build().toString(),
                httpMethod,
                entity,
                responseType).getBody();

        return returnValue;
    }

    /**
     * Builds rest request providing the expected response class as ParameterizedTypeReference.
     *
     * @param <T>          the type parameter
     * @param responseType the response type
     * @return the result of the request
     */
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
        T execute() throws EDCIRestException, OIDCException;
    }

}
