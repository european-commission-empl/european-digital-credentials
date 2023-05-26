package integration.eu.europa.ec.empl.base;

import eu.europa.ec.empl.edci.util.MockFactoryUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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


}
