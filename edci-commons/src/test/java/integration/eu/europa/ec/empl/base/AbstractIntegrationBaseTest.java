package integration.eu.europa.ec.empl.base;

import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import eu.europa.ec.empl.edci.util.MockFactoryUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
//Annotation marked with inheritance, so all subclasses will inherit this annotation.
public abstract class AbstractIntegrationBaseTest {

    protected static final Logger logger = LogManager.getLogger(AbstractIntegrationBaseTest.class);

    @Spy
    private Validator validator;

    @Spy
    @InjectMocks
    public MockFactoryUtil mockFactoryUtil;

    @Spy
    public ReflectiveUtil reflectiveUtil;

    public void setUpProxy(ProxyConfigService proxyConfigService) {
        Mockito.lenient().doReturn("60").when(proxyConfigService).getString("http.https.timeout.seconds", "10");
        Mockito.lenient().doReturn("false").when(proxyConfigService).getString("proxy.http.enabled", "false");
        Mockito.lenient().doReturn("false").when(proxyConfigService).getString("proxy.https.enabled", "false");
        Mockito.lenient().doReturn("").when(proxyConfigService).getString("proxy.http.host", "");
        Mockito.lenient().doReturn(null).when(proxyConfigService).getInteger("proxy.http.port", null);
        Mockito.lenient().doReturn("").when(proxyConfigService).getString("proxy.https.host", "");
        Mockito.lenient().doReturn(null).when(proxyConfigService).getInteger("proxy.https.port", null);
        Mockito.lenient().doReturn("").when(proxyConfigService).getString("proxy.noproxy.regex.url", "");
    }

    public void setUpJena(BaseConfigService baseConfigService) {
        Mockito.lenient().doReturn("application/rdf+xml").when(baseConfigService).getString("jena.default.triples.content.type");
        Mockito.lenient().doReturn(JsonLdUtil.JENA_OFFICIAL_CONTENT_TYPE_RDF_XML).when(baseConfigService).getString("jena.default.triples.content.type", JsonLdUtil.JENA_OFFICIAL_CONTENT_TYPE_RDF_XML);
    }

}
