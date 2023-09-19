package eu.europa.ec.empl.edci.wallet.converter;

//import eu.europa.ec.empl.edci.wallet.common.ecas.UserHolder;

import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConceptScheme;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.RDFsparqlBridgeService;
import org.apache.jena.query.QuerySolution;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;
import java.util.*;


@RunWith(MockitoJUnitRunner.Silent.class)
public class StringCollectionConverterTest {

    @InjectMocks
    @Spy
    protected StringCollectionConverter stringCollectionConverter;

    @Test
    public void convertToDatabaseColumn_shouldConcatenateValue_ifListIsProvided() {

        List<String> stringList = new ArrayList<>();
        stringList.add("first");
        stringList.add("second");
        stringList.add("third");

        String returnValue = stringCollectionConverter.convertToDatabaseColumn(stringList);

        Assert.assertEquals("first|second|third", returnValue);
    }

    @Test
    public void convertToDatabaseColumn_shouldReturnEmpty_ifEmptyList() {

        List<String> stringList = new ArrayList<>();

        String returnValue = stringCollectionConverter.convertToDatabaseColumn(stringList);

        Assert.assertEquals("", returnValue);
    }

    @Test
    public void convertToDatabaseColumn_shouldReturnNull_ifNullList() {

        String returnValue = stringCollectionConverter.convertToDatabaseColumn(null);

        Assert.assertEquals(null, returnValue);
    }

    @Test
    public void convertToEntityAttribute_shouldSplitList_whenStringWithSeparatorIsProvided() {

        String stringValue = "first|second|third";

        List<String> returnValue = stringCollectionConverter.convertToEntityAttribute(stringValue);

        List<String> stringList = new ArrayList<>();
        stringList.add("first");
        stringList.add("second");
        stringList.add("third");
        Assert.assertEquals(stringList, returnValue);
    }

    @Test
    public void convertToEntityAttribute_shouldReturnListWithOneBlankString_ifEmptyStringIsProvided() {

        String stringValue = "";

        List<String> returnValue = stringCollectionConverter.convertToEntityAttribute(stringValue);

        Assert.assertEquals(Arrays.asList(""), returnValue);
    }

    @Test
    public void convertToEntityAttribute_shouldReturnNull_ifNullIsProvided() {

        List<String> returnValue = stringCollectionConverter.convertToEntityAttribute(null);

        Assert.assertEquals(null, returnValue);
    }

}