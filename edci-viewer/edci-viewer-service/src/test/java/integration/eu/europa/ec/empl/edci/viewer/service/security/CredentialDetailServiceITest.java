package integration.eu.europa.ec.empl.edci.viewer.service.security;

import eu.europa.ec.empl.edci.viewer.service.CredentialDetailService;
import eu.europa.ec.empl.edci.viewer.service.ViewerConfigService;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class CredentialDetailServiceITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    CredentialDetailService credentialDetailService;

    @Mock
    private ViewerConfigService viewerConfigService;

    @Test
    public void xxx_xxx_xxx() {
        Assert.assertTrue(true);
    }

}