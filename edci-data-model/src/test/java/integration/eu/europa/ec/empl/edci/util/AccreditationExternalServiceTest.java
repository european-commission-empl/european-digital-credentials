package integration.eu.europa.ec.empl.edci.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.model.external.mapper.QDRAccreditationMapper;
import eu.europa.ec.empl.edci.model.external.qdr.QDRAccreditationDTO;
import eu.europa.ec.empl.edci.service.AccreditationExternalService;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AccreditationExternalServiceTest {

    private String testUri = "http://data.europa.eu/snb/data-dev/accreditation/628c4d45-e715-4ec2-9efd-bcd159e803e8";
    private String testUri2 = "http://data.europa.eu/qdr-test/accreditation/c9761c29-0fc1-4830-becf-cc06d5e71fad";
    private String host1 = "https://esco-qdr-dev-searchapi.cogni.zone/europass/qdr-search/accreditation";
    private String host2 = "https://webgate.acceptance.ec.europa.eu/europass/qdr-search-1/accreditation";

    @Spy
    private JsonLdUtil jsonLdUtil;

    @Spy
    private AccreditationExternalService accreditationExternalService;

    @Spy
    private BaseConfigService iConfigService;

    @Spy
    private QDRAccreditationMapper qdrAccreditationMapper = Mappers.getMapper(QDRAccreditationMapper.class);

    @Spy
    private ReflectiveUtil reflectiveUtil;

    @Before
    public void setUp() throws IOException {
        Mockito.lenient().when(accreditationExternalService.getBaseConfigService()).thenReturn(iConfigService);
        Mockito.lenient().when(accreditationExternalService.getReflectiveUtil()).thenReturn(reflectiveUtil);
        Mockito.lenient().doReturn(host2).when(iConfigService).getString(DataModelConstants.Properties.ACCREDITATION_ENDPOINT);
    }

    @Test
    public void givenUriWhenRetrieveAccreditationByUriThenResultIsQDR() throws JsonProcessingException {
        QDRAccreditationDTO qdrAccreditationDTO = accreditationExternalService.retrieveAccreditationByUri(testUri2, "en");
        AccreditationDTO accreditationDTO = qdrAccreditationMapper.toAccreditationDTO(qdrAccreditationDTO, qdrAccreditationDTO.getMetadata().getLanguage().getValue());

        Assert.assertNotNull(qdrAccreditationDTO);
        Assert.assertNotNull(accreditationDTO);

        jsonLdUtil.marshallAsString(accreditationDTO);

        List<String> list1 = Arrays.stream(qdrAccreditationDTO.getClass().getDeclaredFields()).map(o -> o.getName()).collect(Collectors.toList());
        List<String> list2 = Arrays.stream(accreditationDTO.getClass().getDeclaredFields()).map(o -> o.getName()).collect(Collectors.toList());

        //metadata is not present in AccreditationDTO
        list1.remove("metadata");

        Assert.assertEquals(list1.size(), list2.size());
        for(int i = 0; i < list1.size(); ++i) {
            if(list1.get(i).equalsIgnoreCase("modified") ||
                    list1.get(i).equalsIgnoreCase("rdfType") ||
                    list1.get(i).equalsIgnoreCase("type")) {
                Assert.assertNotEquals(list1.get(i), list2.get(i));
            } else {
                Assert.assertEquals(list1.get(i), list2.get(i));
            }
        }
    }
}
