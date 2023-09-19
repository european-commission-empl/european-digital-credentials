package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JsonUtilTest {

    @InjectMocks
    private JsonUtil jsonUtil;

    @Test
    public void toJSON_shouldReturnAJson_whenCalled() throws Exception {

        String ec = jsonUtil.marshallAsString(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential());
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonUtil.unMarshall(ec, EuropeanDigitalCredentialDTO.class);
        Assert.assertNotNull(europeanDigitalCredentialDTO);
    }

    @Test
    public void marshall_stream_shouldReturnAJson_whenCalled() throws Exception {

        InputStream ec = jsonUtil.marshallAsStream(new Boolean(true));

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (ec, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        Assert.assertEquals("true", textBuilder.toString());
    }

    @Test
    public void marshall_bytes_shouldReturnAJson_whenCalled() throws Exception {

        byte[] ec = jsonUtil.marshallAsBytes(new Boolean(true));

        Assert.assertEquals("true", new String(ec, StandardCharsets.UTF_8));
    }

    @Test
    public void fromJSON_shouldReturnAnObject_whenCalled() throws Exception {

        Boolean returnBoolean = jsonUtil.unMarshall("true", Boolean.class);

        Assert.assertEquals(Boolean.TRUE, returnBoolean);
    }

    @Test
    public void unmarshall_shouldReturnAnObject_whenCalled() throws Exception {

        InputStream targetStream = new ByteArrayInputStream("true".getBytes());

        Boolean returnBoolean = jsonUtil.unMarshall(targetStream, Boolean.class);

        targetStream.close();

        Assert.assertEquals(Boolean.TRUE, returnBoolean);
    }

    @Test
    public void unmarshall_bytes_shouldReturnAnObject_whenCalled() throws Exception {
        Boolean returnBoolean = jsonUtil.unMarshall("true".getBytes(), Boolean.class);

        Assert.assertEquals(Boolean.TRUE, returnBoolean);
    }

}
