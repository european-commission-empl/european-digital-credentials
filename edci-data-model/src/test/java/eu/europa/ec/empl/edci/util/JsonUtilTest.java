package eu.europa.ec.empl.edci.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JsonUtilTest {

    @InjectMocks
    private JsonUtil jsonUtil;

    @Test
    public void toJSON_shouldReturnAJson_whenCalled() throws Exception {

        String ec = jsonUtil.toJSON(new Boolean(true));

        Assert.assertEquals("true", ec);
    }

    @Test
    public void fromJSON_shouldReturnAnObject_whenCalled() throws Exception {

        Boolean returnBoolean = jsonUtil.fromJSON("true", Boolean.class);

        Assert.assertEquals(Boolean.TRUE, returnBoolean);
    }

}
