package integration.eu.europa.ec.empl.edci.model;

import eu.europa.ec.empl.edci.model.qmsaccreditation.QMSAccreditationDTO;
import eu.europa.ec.empl.edci.util.JsonUtil;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class AccreditationDTOJSONUtilITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    @Spy
    protected JsonUtil jsonUtil;
    protected String simpleMockedAcc = "src/test/resources/accreditation/MockedSampleAccreditation.json";
    protected String serviceAcc = "src/test/resources/accreditation/ServiceSampleAccreditation.json";

    @Test
    public void parseSimleAccreditationJSON_shouldResultInFullDTO_whenMockedJSONisSimpleMockedAcc() throws Exception {
        QMSAccreditationDTO qmsAccreditationDTO = this.jsonUtil.fromJSONFile(new File(simpleMockedAcc), QMSAccreditationDTO.class);
        String jsonFile = StringUtils.normalizeSpace(new String(Files.readAllBytes(new File(simpleMockedAcc).toPath()), StandardCharsets.UTF_8));
        String parsedJson = StringUtils.normalizeSpace(this.jsonUtil.toJSON(qmsAccreditationDTO));
        Assert.assertEquals(StringUtils.deleteWhitespace(jsonFile), StringUtils.deleteWhitespace(parsedJson));
    }

    @Test
    public void parseServiceAccreditationJSON() throws Exception {
        QMSAccreditationDTO qmsAccreditationDTO = this.jsonUtil.fromJSONFile(new File(serviceAcc), QMSAccreditationDTO.class);
        String jsonFile = StringUtils.normalizeSpace(new String(Files.readAllBytes(new File(simpleMockedAcc).toPath()), StandardCharsets.UTF_8));
        String parsedJson = StringUtils.normalizeSpace(this.jsonUtil.toJSON(qmsAccreditationDTO));
        Assert.assertEquals(StringUtils.deleteWhitespace(jsonFile), StringUtils.deleteWhitespace(parsedJson));

    }

}
