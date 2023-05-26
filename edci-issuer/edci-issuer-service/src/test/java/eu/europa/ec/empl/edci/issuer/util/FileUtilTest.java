package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class FileUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    FileUtil fileUtil;

    @Mock
    private IssuerConfigService issuerConfigService0;

    @Mock
    private static IssuerConfigService issuerConfigService;

    @Test
    public void xxx_xxx_xxx() throws Exception {
        Assert.assertTrue(true);
    }

}
