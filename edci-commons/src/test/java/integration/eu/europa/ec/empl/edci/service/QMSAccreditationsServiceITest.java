package integration.eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.model.qmsaccreditation.QMSAccreditationDTO;
import eu.europa.ec.empl.edci.service.QMSAccreditationsService;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.JsonUtil;
import eu.europa.ec.empl.edci.util.XmlUtil;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class QMSAccreditationsServiceITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    @Spy
    private QMSAccreditationsService qmsAccreditationsService;
    @Spy
    @InjectMocks
    private EDCICredentialModelUtil edciCredentialModelUtil;
    @Spy
    protected XmlUtil xmlUtil;
    @Spy
    protected JsonUtil jsonUtil;
    @Mock
    protected IConfigService iConfigService;

    private String mockedAccreditation = "src/test/resources/accreditation/MockedSampleAccreditation.json";
    private String mockedAccreditation2 = "src/test/resources/accreditation/MockedSampleAccreditation_2.json";
    private String accredited_credential_false = "src/test/resources/accreditation/test_name_accredited_credential_false.xml";
    private String accredited_credential_step1_true = "src/test/resources/accreditation/test_name_accredited_credential_step1_true.xml";
    private String accredited_credential_step2_issuer_true = "src/test/resources/accreditation/test_name_accredited_credential_step2_issuer_true.xml";
    private String accredited_credential_step2_issuer_false = "src/test/resources/accreditation/test_name_accredited_credential_step2_issuer_false.xml";
    private String accredited_credential_step2_awardingBody_true = "src/test/resources/accreditation/test_name_accredited_credential_step2_awardingBody_true.xml";
    private String accredited_credential_step2_awardingBody_false = "src/test/resources/accreditation/test_name_accredited_credential_step2_awardingBody_false.xml";
    private String accredited_credential_step2_awardingBody_issuerTrue = "src/test/resources/accreditation/test_name_accredited_credential_step2_awardingBody_issuerTrue.xml";

    @Before
    public void prepareMocks() throws IOException {
        QMSAccreditationDTO qmsAccreditationDTO = this.jsonUtil.fromJSONFile(new File(mockedAccreditation), QMSAccreditationDTO.class);
        Mockito.doReturn(qmsAccreditationDTO).when(qmsAccreditationsService).getMockedAccreditation();
    }

    @Test
    public void checkAccreditedCredential_shouldGiveOK_whenParsingMock2() throws IOException, JAXBException {
        QMSAccreditationDTO qmsAccreditationDTO = this.jsonUtil.fromJSONFile(new File(mockedAccreditation2), QMSAccreditationDTO.class);
        Assert.assertTrue(qmsAccreditationDTO != null);
    }

    @Test
    public void checkAccreditedCredential_shouldGiveFalse_whenUsingFalseAccreditedCredential() throws IOException, JAXBException {
        byte[] xmlCred = Files.readAllBytes(Paths.get(accredited_credential_false));
        ValidationResult validationResult = qmsAccreditationsService.isCoveredCredential(xmlCred);
        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void checkAccreditedCredential_shouldGiveOK_whenUsingStep1TrueCredential() throws IOException, JAXBException {
        byte[] xmlCred = Files.readAllBytes(Paths.get(accredited_credential_step1_true));
        ValidationResult validationResult = qmsAccreditationsService.isCoveredCredential(xmlCred);
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void checkAccreditedCredential_shouldGiveOK_whenUsingStep2issuerTrueCredential() throws IOException, JAXBException {
        byte[] xmlCred = Files.readAllBytes(Paths.get(accredited_credential_step2_issuer_true));
        ValidationResult validationResult = qmsAccreditationsService.isCoveredCredential(xmlCred);
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void checkAccreditedCredential_shouldGiveKO_whenUsingStep2issuerFalseCredential() throws IOException, JAXBException {
        byte[] xmlCred = Files.readAllBytes(Paths.get(accredited_credential_step2_issuer_false));
        ValidationResult validationResult = qmsAccreditationsService.isCoveredCredential(xmlCred);
        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void checkAccreditedCredential_shouldGiveOK_whenUsingStep2AwardingBodyTrueCredential() throws IOException, JAXBException {
        byte[] xmlCred = Files.readAllBytes(Paths.get(accredited_credential_step2_awardingBody_true));
        ValidationResult validationResult = qmsAccreditationsService.isCoveredCredential(xmlCred);
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void checkAccreditedCredential_shouldGiveOK_whenUsingStep2AwardingBodyFalseCredential() throws IOException, JAXBException {
        byte[] xmlCred = Files.readAllBytes(Paths.get(accredited_credential_step2_awardingBody_false));
        ValidationResult validationResult = qmsAccreditationsService.isCoveredCredential(xmlCred);
        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void checkAccreditedCredential_shouldGiveKO_whenUsingStep2AwardingBodyTrueIssuerCoveredCredential() throws IOException, JAXBException {
        byte[] xmlCred = Files.readAllBytes(Paths.get(accredited_credential_step2_awardingBody_issuerTrue));
        ValidationResult validationResult = qmsAccreditationsService.isCoveredCredential(xmlCred);
        Assert.assertTrue(validationResult.isValid());
    }
}
