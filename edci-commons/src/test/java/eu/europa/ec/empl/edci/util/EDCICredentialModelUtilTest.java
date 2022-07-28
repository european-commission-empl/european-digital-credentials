package eu.europa.ec.empl.edci.util;

import com.google.gson.GsonBuilder;
import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.DownloadableObject;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.RDFsparqlBridgeService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EDCICredentialModelUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Mock
    private JsonUtil jsonUtil;

    @Mock
    private XmlUtil xmlUtil;

    @Spy
    @InjectMocks
    private ControlledListCommonsService controlledListCommonsService;

    @Mock
    private RDFsparqlBridgeService rdfSparqlBridgeService;

    @Test
    public void fromXML_shouldSetOriginalXML_always() throws Exception {

        Mockito.when(xmlUtil.fromString(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(new EuropassCredentialDTO());

        EuropassCredentialDTO ec = edciCredentialModelUtil.fromXML("originalXML").getCredential();

        Assert.assertEquals("originalXML", ec.getOriginalXML());
    }

    @Test
    public void toXML_shouldReturnAnXML_whenCalled() throws Exception {

        Mockito.when(xmlUtil.toXML(Mockito.any(), Mockito.any(Class.class))).thenReturn("someCredential");

        String ec = edciCredentialModelUtil.toXML(new EuropassCredentialDTO());

        Assert.assertEquals("someCredential", ec);
    }

    @Test
    public void generateXMLSubCreds_shouldReturnSubCred_ifTheresAny() throws Exception {

        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();
        europassCredentialDTO.getContains().add(new DownloadableObject() {{
            setContent("subCred1".getBytes());
        }});
        europassCredentialDTO.getContains().add(new DownloadableObject() {{
            setContent("subCred2".getBytes());
        }});

        List<String> subList = edciCredentialModelUtil.generateXMLSubCreds(europassCredentialDTO);

        Assert.assertEquals(2, subList.size());

    }

    @Test
    public void generateXMLSubCreds_shouldReturnEmptyList_ifThereAreNoSubCredentials() throws Exception {

        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();

        List<String> subList = edciCredentialModelUtil.generateXMLSubCreds(europassCredentialDTO);

        Assert.assertEquals(0, subList.size());

    }

    @Test
    public void parseSubCredentials_shouldReturnAListOfEuroCred_fromAListOfStringsXML() throws Exception {

        Mockito.when(xmlUtil.fromString(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(new EuropassCredentialDTO());

        List<String> subList = new ArrayList<>();
        subList.add("one");
        subList.add("two");
        subList.add("three");

        List<EuropassCredentialDTO> euroPassCredList = edciCredentialModelUtil.parseSubCredentials(subList);

        Assert.assertEquals(3, euroPassCredList.size());

    }

    @Test
    public void parseSubCredentials_shouldReturnEmptyList_ifThereAreNoSubCredentials() throws Exception {

        Mockito.when(xmlUtil.fromString(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(new EuropassCredentialDTO());

        List<String> subList = new ArrayList<>();

        List<EuropassCredentialDTO> euroPassCredList = edciCredentialModelUtil.parseSubCredentials(subList);

        Assert.assertEquals(0, euroPassCredList.size());

    }

    @Test
    public void fromInputStream_shouldReturnACredential_givenAnInputStream() throws Exception {

        Mockito.when(xmlUtil.fromInputStream(Mockito.any(), Mockito.any(Class.class))).thenReturn(new EuropassCredentialDTO());
        InputStream is = new ByteArrayInputStream("".getBytes());

        EuropassCredentialDTO ec = edciCredentialModelUtil.fromInputStream(is).getCredential();

        Assert.assertNotNull(ec);

    }

    @Test
    public void fromByteArray_shouldReturnACredential_givenAByteArray() throws Exception {

        Mockito.when(xmlUtil.fromBytes(Mockito.any(), Mockito.any(Class.class))).thenReturn(new EuropassCredentialDTO());

        EuropassCredentialDTO ec = edciCredentialModelUtil.fromByteArray("".getBytes()).getCredential();

        Assert.assertNotNull(ec);

    }

    @Test
    public void toEuroPassPresentation_shouldReturnEuropassPresentationDTO_givenSomeInfo() throws Exception {

        EuropassPresentationDTO epDTO = edciCredentialModelUtil.toVerifiablePresentation(new EuropassCredentialDTO(), new ArrayList<VerificationCheckDTO>());

        Assert.assertNotNull(epDTO);

    }

    @Test
    public void toVerifiablePresentationCredentialDTO_shouldReturnVerifiablePresentation_givenSomeInfo() throws Exception {


        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();

        EuropassPresentationDTO vpDTO = edciCredentialModelUtil.toVerifiablePresentation(europassCredentialDTO, null);

        Assert.assertNotNull(vpDTO);

    }

    @Test
    public void cloneModel_shouldReturnAClonedObject_givenSomeObject() throws Exception {

        Mockito.when(jsonUtil.getGsonContext()).thenReturn(new GsonBuilder().create());

        List inputList = new ArrayList<String>();
        inputList.add("elem1");
        inputList.add("elem2");

        List outputList = edciCredentialModelUtil.cloneModel(inputList);

        Assert.assertEquals(inputList, outputList);

    }

    @Test
    public void cloneArrayModel_shouldReturnSomeClonedObjects_givenSomeObject() throws Exception {

        Mockito.when(jsonUtil.getGsonContext()).thenReturn(new GsonBuilder().create());

        List inputList = new ArrayList<String>();
        inputList.add("elem1");
        inputList.add("elem2");

        Boolean inputBoolean = true;

        List outputList = edciCredentialModelUtil.cloneArrayModel(inputList, inputBoolean);

        Assert.assertEquals(inputList, outputList.get(0));
        Assert.assertEquals(inputBoolean, outputList.get(1));

    }

}
