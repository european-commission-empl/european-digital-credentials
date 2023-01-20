package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

public class ControlledListsUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    protected ControlledListsUtil controlledListsUtil;


    @Test
    public void getMimeType_shouldReturnMimeType_givenAValidExtension() {

        Assert.assertNotNull(controlledListsUtil.getMimeType("http://pngimage.png"));
        Assert.assertNotNull(controlledListsUtil.getMimeType("http://jpegimage.jpeg"));
        Assert.assertNotNull(controlledListsUtil.getMimeType("http://jpgimage.jpg"));
        Assert.assertNotNull(controlledListsUtil.getMimeType("http://gifimage.gif"));

    }

    @Test
    public void getMimeType_shouldReturnNull_givenAnInvalidExtension() {

        Assert.assertNull(controlledListsUtil.getMimeType("http://notAnImage.txt"));
        Assert.assertNull(controlledListsUtil.getMimeType("http://NotPNGimage.png.txt"));

    }

    @Test
    public void getMimeType_shouldReturnNull_ifURLIsNull() {

        Assert.assertNull(controlledListsUtil.getMimeType(null));

    }

}
