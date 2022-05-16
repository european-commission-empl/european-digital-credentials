package integration.eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.wallet.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.wallet.mapper.WalletMapper;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.service.CredentialService;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import eu.europa.ec.empl.edci.wallet.service.utils.EuropassCredentialVerifyUtil;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.List;


public class CredentialServiceITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    private CredentialService credentialService;

    @Mock
    private WalletConfigService walletConfigService;

    @Mock
    private CredentialMapper credentialMapper;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    EuropassCredentialVerifyUtil europassCredentialVerifyUtil;

    @Mock
    ServletContext servletContext;

    @Mock
    private EuropassCredentialDTO europassCredentialDTO;

    @Before
    public void setUp() {
        Mockito.doReturn("SHA256").when(walletConfigService).getString("signature.xml.digestAlgorithm");
        Mockito.doReturn("XAdES-BASELINE-B").when(walletConfigService).getString("signature.xml.level");
        Mockito.doReturn("ENVELOPED").when(walletConfigService).getString("signature.xml.packaging");
        Mockito.doReturn("SHA256").when(walletConfigService).getString("signature.pdf.digestAlgorithm");
        Mockito.doReturn("PAdES-BASELINE-B").when(walletConfigService).getString("signature.pdf.level");
    }

    /**
     * Get a PKCS12 signature token stored at resources/signCredential/keystore.p12.
     *
     * @return
     * @throws Exception
     */
    protected SignatureTokenConnection getPkcs12Token() {

        Pkcs12SignatureToken jksSignatureToken = null;

        try {
            InputStream is = new FileInputStream("src/test/resources/signCredential/dummyKeystore.p12");
            jksSignatureToken = new Pkcs12SignatureToken(is, new KeyStore.PasswordProtection("test".toCharArray()));
//            InputStream is = new FileInputStream("src/test/resources/signCredential/np_qcorp_tarj_521_730_hw_kusu_valido.p12");
//            jksSignatureToken = new Pkcs12SignatureToken(is, new KeyStore.PasswordProtection("nhJn8xCZYseEKUFt".toCharArray()));
        } catch (Exception e) {
            throw new EDCIException().addDescription("Could not load the signature keystore").setCause(e);
        }

        return jksSignatureToken;
    }

    @Test
    public void signPDFDocument_shouldSignPDFDocument() throws Exception {

        // GET document to be signed -
        // Return DSSDocument toSignDocument
        DSSDocument toSignDocument = new FileDocument(new File("src/test/resources/signCredential/hello-world.pdf"));

        try (SignatureTokenConnection signingToken = getPkcs12Token()) {

            //DSSDocument signedDocument = DSSSignatureUtils.tokenSignPDFDocument(toSignDocument, signingToken);

            //testFinalDocument(signedDocument);
        }
    }

    @Test
    public void signXMLDocument_shouldSignXMLVPDocument() throws Exception {

        Assert.assertEquals(1 - 1, 0);
        // GET document to be signed -
        // Return DSSDocument toSignDocument
        DSSDocument toSignDocument = new FileDocument(new File("src/test/resources/signCredential/verif_present_xml_example.xml"));

        /*try (SignatureTokenConnection signingToken = getPkcs12Token()) {

            DSSDocument signedDocument = DSSEDCISignService.tokenSignXMLDocument(toSignDocument, signingToken, DSSConstants.VERIFIABLE_PRESENTATION_XPATH_LOCATION);

            testFinalDocument(signedDocument);
        }*/

    }

    //@Test
    public void signXMLDocument_shouldSignXMLCredDocument() throws Exception {

        Assert.assertEquals(1 - 1, 0);
        // GET document to be signed -
        // Return DSSDocument toSignDocument
        DSSDocument toSignDocument = new FileDocument(new File("src/test/resources/signCredential/europass_credential_xml_example.xml"));

       /* try (SignatureTokenConnection signingToken = getPkcs12Token()) {

            DSSDocument signedDocument = DSSEDCISignService.tokenSignXMLDocument(toSignDocument, signingToken, DSSConstants.EUROPASS_CREDENTIAL_XPATH_LOCATION);

            testFinalDocument(signedDocument);
        }*/

    }

    /**
     * Validates the signed document
     *
     * @param signedDocument
     */
    protected void testFinalDocument(DSSDocument signedDocument) {
        Assert.assertNotNull(signedDocument);
        Assert.assertNotNull(DSSUtils.toByteArray(signedDocument));

        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(signedDocument);
        validator.setCertificateVerifier(new CommonCertificateVerifier());
        Reports reports = validator.validateDocument();
        Assert.assertNotNull(reports);

        DiagnosticData diagnosticData = reports.getDiagnosticData();

        List<SignatureWrapper> signatures = diagnosticData.getSignatures();
        for (SignatureWrapper signatureWrapper : signatures) {
            Assert.assertTrue(signatureWrapper.isBLevelTechnicallyValid());

            List<TimestampWrapper> timestampList = signatureWrapper.getTimestampList();
            for (TimestampWrapper timestampWrapper : timestampList) {
                Assert.assertTrue(timestampWrapper.isMessageImprintDataFound());
                Assert.assertTrue(timestampWrapper.isMessageImprintDataIntact());
                Assert.assertTrue(timestampWrapper.isSignatureValid());
            }
        }
    }

}