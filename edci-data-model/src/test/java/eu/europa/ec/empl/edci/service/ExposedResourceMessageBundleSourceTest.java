package eu.europa.ec.empl.edci.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ExposedResourceMessageBundleSourceTest {

    @InjectMocks
    @Spy
    ExposedResourceMessageBundleSource exposedResourceMessageBundleSource;

    @Test
    public void getMessages_ShouldReturnNull() {
        Assert.assertNull(exposedResourceMessageBundleSource.getMessages(null));
    }

    @Test
    public void getMessages_ShouldReturnNotNull() {
        Assert.assertNotNull(exposedResourceMessageBundleSource.getMessages(Locale.ENGLISH));
    }
    

}
