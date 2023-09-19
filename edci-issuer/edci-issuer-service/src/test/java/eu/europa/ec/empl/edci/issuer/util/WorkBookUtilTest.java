package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertEquals;

public class WorkBookUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    EDCIWorkBookReader edciWorkBookReader;

    @Test
    public void getColumnStringValue_shouldReturnA_when0IsPassedByParameter() {
        String result = edciWorkBookReader.getColumnStringValue(0);
        assertEquals("A", result);
    }

    @Test
    public void getColumnStringValue_shouldReturnC_when2IsPassedByParameter() {
        String result = edciWorkBookReader.getColumnStringValue(2);
        assertEquals("C", result);
    }

    @Test
    public void getColumnStringValue_shouldReturnZ_when25IsPassedByParameter() {
        String result = edciWorkBookReader.getColumnStringValue(25);
        assertEquals("Z", result);
    }

    @Test
    public void getColumnStringValue_shouldReturn30_when30IsPassedByParameter() {
        String result = edciWorkBookReader.getColumnStringValue(30);
        assertEquals("AE", result);
    }

}
