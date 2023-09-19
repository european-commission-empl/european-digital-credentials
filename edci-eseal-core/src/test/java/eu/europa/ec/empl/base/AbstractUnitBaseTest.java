package eu.europa.ec.empl.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.validation.Validator;

@RunWith(MockitoJUnitRunner.Silent.class)
//Annotation marked with inheritance, so all subclasses will inherit this annotation.
public abstract class AbstractUnitBaseTest {

    private static final Logger _logger = LogManager.getLogger(AbstractUnitBaseTest.class);

    @Spy
    private Validator validator;

    @InjectMocks
    public TestUtilities testUtilities;

}
