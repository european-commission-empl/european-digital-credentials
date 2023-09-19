package eu.europa.ec.empl.edci.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Properties;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EDCIMessageServiceTest {

    @InjectMocks
    @Spy
    protected EDCIMessageService edciMessageService;

    @Mock
    MessageSource messageSource;

    @Mock
    ExposedResourceMessageBundleSource exposedResourceMessageBundleSource;

    @Before
    public void setUp() {
        Mockito.doReturn(messageSource).when(edciMessageService).getMessageSource();
    }


    @Test
    public void getCurrentLocale_ShouldReturnLocale() {
        Assert.assertNotNull(edciMessageService.getCurrentLocale());

    }

    @Test
    public void getMessage_StringObject_ShouldReturnString() {
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Assert.assertNotNull(edciMessageService.getMessage("", null));
        Mockito.verify(edciMessageService, Mockito.times(1)).getMessage(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void getMessage_LocaleStringObject_ShouldMessageKey() {
        //Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Assert.assertEquals("message", edciMessageService.getMessage(Locale.ENGLISH, "message", "paatata"));
        Mockito.verify(edciMessageService, Mockito.times(1)).getMessage(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void getMessage_LocaleStringObject_ShouldMessage() {
        Mockito.doReturn("test").when(messageSource).getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertEquals("test", edciMessageService.getMessage(Locale.ENGLISH, "message", "paatata"));
        Mockito.verify(edciMessageService, Mockito.times(1)).getMessage(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void getVariableMessage_LocaleStringObject_ShouldMessageKey() {
        Assert.assertEquals("message", edciMessageService.getMessage(Locale.ENGLISH, "message", "paatata"));
        Mockito.verify(edciMessageService, Mockito.times(1)).getMessage(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void getMessages_Locale_ShouldMessage() {
        Properties properties = new Properties();
        properties.put("test", "test");
        Mockito.doReturn(properties).when(exposedResourceMessageBundleSource).getMessages(ArgumentMatchers.any());
        Assert.assertEquals(properties.get("test"), edciMessageService.getMessages(Locale.ENGLISH).get("test"));
        Mockito.verify(exposedResourceMessageBundleSource, Mockito.times(1)).getMessages(ArgumentMatchers.any());
    }
    

}
