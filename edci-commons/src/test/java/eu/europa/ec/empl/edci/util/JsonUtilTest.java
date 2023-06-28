package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

public class JsonUtilTest extends AbstractUnitBaseTest {

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
