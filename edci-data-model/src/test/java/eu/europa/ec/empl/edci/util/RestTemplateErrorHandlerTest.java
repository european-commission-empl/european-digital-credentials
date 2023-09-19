package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RestTemplateErrorHandlerTest {

    @InjectMocks
    protected RestTemplateErrorHandler restTemplateErrorHandler;


    @Test
    public void hasError_shouldReturnFalse() throws IOException {
        MockClientHttpResponse mockClientHttpResponse = new MockClientHttpResponse(InputStream.nullInputStream(), HttpStatus.OK);
        Assert.assertFalse(restTemplateErrorHandler.hasError(mockClientHttpResponse));

    }

    @Test
    public void hasError_shouldReturnTrue() throws IOException {
        MockClientHttpResponse mockClientHttpResponse = new MockClientHttpResponse(InputStream.nullInputStream(), HttpStatus.BAD_REQUEST);
        Assert.assertTrue(restTemplateErrorHandler.hasError(mockClientHttpResponse));

    }

    @Test(expected = EDCIRestException.class)
    public void handleError_shouldThrowException() throws IOException {
        DefaultResponseCreator responseCreator = MockRestResponseCreators.withBadRequest();
        restTemplateErrorHandler.handleError(responseCreator.body("{\"message\" : \"hola\"}".getBytes(StandardCharsets.UTF_8)).createResponse(null));

    }

    @Test(expected = EDCIException.class)
    public void handleError_shouldThrowException_catch() throws IOException {
        DefaultResponseCreator responseCreator = MockRestResponseCreators.withBadRequest();
        restTemplateErrorHandler.handleError(responseCreator.body("this is not a proper JSON".getBytes(StandardCharsets.UTF_8)).createResponse(null));

    }


}
