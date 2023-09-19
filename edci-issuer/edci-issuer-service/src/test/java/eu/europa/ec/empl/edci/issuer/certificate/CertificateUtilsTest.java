package eu.europa.ec.empl.edci.issuer.certificate;

import com.apicatalog.jsonld.JsonLdError;
import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.view.AttachmentView;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.issuer.service.IssuerFileService;
import eu.europa.ec.empl.edci.issuer.util.CertificateUtils;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.JsonLdFactoryUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;

public class CertificateUtilsTest extends AbstractUnitBaseTest {

    @InjectMocks
    CertificateUtils certificateUtils;

    @Mock
    EDCIFileService edciFileService;

    @Mock
    CredentialUtil credentialUtil;

    @Mock
    IssuerConfigService issuerConfigService;

    @Mock
    IssuerFileService issuerFileService;

    @Mock
    ControlledListCommonsService controlledListCommonsService;

    @Test
    public void overwriteCertificateFields_shouldCallMethod() throws ParseException, JsonLdError, IOException, URISyntaxException {
        AttachmentView attachmentView = new AttachmentView();
        attachmentView.setDescription("description");
        attachmentView.setContent("content");
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        europeanDigitalCredentialDTO.setIssuer(null);

        Map<String, String> map = Mockito.mock(Map.class);
        Mockito.doReturn("test").when(map).get(ArgumentMatchers.anyString());
        Mockito.doReturn(File.createTempFile("test", "test")).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Mockito.doReturn(europeanDigitalCredentialDTO).when(credentialUtil).unMarshallCredential(ArgumentMatchers.any(byte[].class));
        Mockito.doReturn(true).when(issuerConfigService).getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn("en").when(controlledListCommonsService).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));
        Mockito.doReturn(Arrays.asList("en")).when(controlledListCommonsService).searchLanguageISO639ByConcept(ArgumentMatchers.anyList());
        Mockito.doReturn(JsonLdFactoryUtil.getConcept(1)).when(controlledListCommonsService).searchCountryByEuvocField(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyList());
        Mockito.doReturn(JsonLdFactoryUtil.getConcept(2)).when(controlledListCommonsService).searchConceptByConcept(ArgumentMatchers.any(), ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(URI.create(new ConceptDTO().getIdPrefix(new ConceptDTO()).concat("1423423")))).when(controlledListCommonsService).getShaclURIsFromProfiles(ArgumentMatchers.any());

        certificateUtils.overwriteCertificateFields(attachmentView, "file/path", map);
        Assert.assertNotNull(europeanDigitalCredentialDTO.getEvidence().get(0).getEmbeddedEvidence());
        Mockito.verify(issuerFileService, Mockito.times(1)).createJSONLDFile(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void overwriteCertificateFields_shouldNotHaveEvidence() throws ParseException, JsonLdError, IOException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        europeanDigitalCredentialDTO.setIssuer(null);

        Map<String, String> map = Mockito.mock(Map.class);
        Mockito.doReturn("test").when(map).get(ArgumentMatchers.anyString());
        Mockito.doReturn(File.createTempFile("test", "test")).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Mockito.doReturn(europeanDigitalCredentialDTO).when(credentialUtil).unMarshallCredential(ArgumentMatchers.any(byte[].class));
        Mockito.doReturn(true).when(issuerConfigService).getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn("en").when(controlledListCommonsService).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));
        Mockito.doReturn(Arrays.asList("en")).when(controlledListCommonsService).searchLanguageISO639ByConcept(ArgumentMatchers.anyList());
        Mockito.doReturn(JsonLdFactoryUtil.getConcept(1)).when(controlledListCommonsService).searchCountryByEuvocField(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyList());
        Mockito.doReturn(JsonLdFactoryUtil.getConcept(2)).when(controlledListCommonsService).searchConceptByConcept(ArgumentMatchers.any(), ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(URI.create(new ConceptDTO().getIdPrefix(new ConceptDTO()).concat("1423423")))).when(controlledListCommonsService).getShaclURIsFromProfiles(ArgumentMatchers.any());

        certificateUtils.overwriteCertificateFields(null, "file/path", map);
        Assert.assertTrue(europeanDigitalCredentialDTO.getEvidence().isEmpty());
        Mockito.verify(issuerFileService, Mockito.times(1)).createJSONLDFile(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test(expected = EDCIException.class)
    public void overwriteCertificateFields_shouldThrowException() throws ParseException, JsonLdError, IOException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        europeanDigitalCredentialDTO.setIssuer(null);

        Map<String, String> map = Mockito.mock(Map.class);

        certificateUtils.overwriteCertificateFields(null, null, map);
    }

}
