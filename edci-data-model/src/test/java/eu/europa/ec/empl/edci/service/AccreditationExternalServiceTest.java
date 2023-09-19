package eu.europa.ec.empl.edci.service;

import com.apicatalog.jsonld.JsonLdError;
import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import eu.europa.ec.empl.edci.util.JsonUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AccreditationExternalServiceTest {

    private String testUri = "http://data.europa.eu/snb/data-dev/accreditation/628c4d45-e715-4ec2-9efd-bcd159e803e8";
    private String testUri2 = "http://data.europa.eu/qdr-test/accreditation/c9761c29-0fc1-4830-becf-cc06d5e71fad";
    private String host1 = "https://esco-qdr-dev-searchapi.cogni.zone/europass/qdr-search/accreditation";
    private String host2 = "https://webgate.acceptance.ec.europa.eu/europass/qdr-search-1/accreditation";

    @InjectMocks
    @Spy
    private QDRAccreditationExternalService accreditationExternalService;

    @Mock
    private JsonLdUtil jsonLdUtil;

    @Mock
    private JsonUtil jsonUtil;

    @Mock
    private BaseConfigService iConfigService;

    @Mock
    private ReflectiveUtil reflectiveUtil;

    @Mock
    private ControlledListCommonsService controlledListCommonsService;

    @Before
    public void setUp() throws IOException {
        Mockito.lenient().when(accreditationExternalService.getBaseConfigService()).thenReturn(iConfigService);
        Mockito.lenient().doReturn(host2).when(iConfigService).getString(DataModelConstants.Properties.ACCREDITATION_ENDPOINT);
    }

    @Test(expected = EDCIException.class)
    public void retrieveAccreditationByUri_ShouldThrowException() throws JsonLdError, IOException {
        Mockito.doReturn("test").when(jsonLdUtil).doFrame(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doThrow(new EDCIException()).when(jsonUtil).unMarshall(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.doReturn("test").when(accreditationExternalService).executeRequestBuilder(ArgumentMatchers.any(), ArgumentMatchers.anyMap(), ArgumentMatchers.any());
        accreditationExternalService.retrieveAccreditationByUri(testUri2, "en");
    }

    @Test
    public void retrieveAccreditationByUri_ShouldReturnNotNull() throws JsonLdError, IOException {
        Mockito.doReturn("test").when(jsonLdUtil).doFrame(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(new AccreditationDTO()).when(jsonUtil).unMarshall(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.doReturn("test").when(accreditationExternalService).executeRequestBuilder(ArgumentMatchers.any(), ArgumentMatchers.anyMap(), ArgumentMatchers.any());
        Assert.assertNotNull(accreditationExternalService.retrieveAccreditationByUri(testUri2, "en"));
    }

    @Test
    public void retrieveAccreditationByUri_Overload_ShouldCallMethod() {
        Mockito.doReturn("lang").when(controlledListCommonsService).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));
        Mockito.doReturn(new AccreditationDTO()).when(accreditationExternalService).retrieveAccreditationByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString());
        Assert.assertNotNull(accreditationExternalService.retrieveAccreditationByUri(testUri2, new ConceptDTO()));

        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));
        Mockito.verify(accreditationExternalService, Mockito.times(1)).retrieveAccreditationByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString());
    }

    @Test
    public void doesAccreditationExist() {
        Mockito.doReturn("test").when(accreditationExternalService).executeRequestBuilder(ArgumentMatchers.any(), ArgumentMatchers.anyMap(), ArgumentMatchers.any());

        accreditationExternalService.doesAccreditationExist("uri", "en");
        Mockito.verify(accreditationExternalService, Mockito.times(1)).executeRequestBuilder(ArgumentMatchers.any(), ArgumentMatchers.anyMap(), ArgumentMatchers.any());

    }
}
