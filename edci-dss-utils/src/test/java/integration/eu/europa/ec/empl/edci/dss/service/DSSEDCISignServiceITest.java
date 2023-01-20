package integration.eu.europa.ec.empl.edci.dss.service;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.dss.service.DSSEDCICertificateService;
import eu.europa.ec.empl.edci.dss.service.DSSEDCISignService;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.tsl.cache.CacheCleaner;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doReturn;

public class DSSEDCISignServiceITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    @Spy
    private DSSEDCISignService dssedciSignService;

    @Mock
    private IConfigService iConfigService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    private String samplePath = "src/test/resources/creds/sampleXML.xml";
    private String credPath = "src/test/resources/creds/cred_and.xml";
    private String certPath = "src/test/resources/seal/QSEALC.pfx";
    private String certPassword = "12341234";

    //    @Before
    public void setUp() throws Exception {
        //TODO diabled timestamp because bambo cannot connect
//        String tspSource = "http://dss.nowina.lu/pki-factory/tsa/good-tsa";
//        OnlineTSPSource onlineTSPSource = new OnlineTSPSource(tspSource);
//        TimestampDataLoader timestampDataLoader = new TimestampDataLoader();
//        onlineTSPSource.setDataLoader(timestampDataLoader);

        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource();
        OCSPDataLoader ocspDataLoader = new OCSPDataLoader();
        onlineOCSPSource.setDataLoader(ocspDataLoader);

        OnlineCRLSource onlineCRLSource = new OnlineCRLSource();
        CommonsDataLoader commonsHttpDataLoader = new CommonsDataLoader();
        commonsHttpDataLoader.setTrustStrategy(TrustAllStrategy.INSTANCE);
        onlineCRLSource.setDataLoader(commonsHttpDataLoader);

        //KeyStoreCertificateSource keyStoreCertificateSource = new KeyStoreCertificateSource(new File("src/test/resources/keystore/keystore.p12"), "PKCS12", "dss-password");
        LOTLSource lotlSource = new LOTLSource();
        lotlSource.setUrl("https://ec.europa.eu/tools/lotl/eu-lotl.xml");
        //lotlSource.setCertificateSource(keyStoreCertificateSource);
        lotlSource.setPivotSupport(true);

        FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader(commonsHttpDataLoader);
        CacheCleaner cacheCleaner = new CacheCleaner();
        cacheCleaner.setCleanFileSystem(true);
        cacheCleaner.setDSSFileLoader(onlineFileLoader);
        TLValidationJob validationJob = new TLValidationJob();
        validationJob.setOnlineDataLoader(onlineFileLoader);
        validationJob.setCacheCleaner(cacheCleaner);
        validationJob.setListOfTrustedListSources(lotlSource);

        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        commonCertificateVerifier.setCheckRevocationForUntrustedChains(true);
        TrustedListsCertificateSource tslCertificateSource = new TrustedListsCertificateSource();
        commonCertificateVerifier.setTrustedCertSources(tslCertificateSource);
        commonCertificateVerifier.setOcspSource(onlineOCSPSource);
        commonCertificateVerifier.setCrlSource(onlineCRLSource);

        EDCIFileService edciFileService = new EDCIFileService();

        doReturn(edciFileService).when(dssedciSignService).getEdciFileService();
        doReturn(validationJob).when(dssedciSignService).getValidationJob();
//        doReturn(onlineTSPSource).when(dssedciSignService).getOnlineTSPSource();
        doReturn(onlineOCSPSource).when(dssedciSignService).getOcspSource();
        doReturn(onlineCRLSource).when(dssedciSignService).getCrlSource();
        doReturn(new DSSEDCICertificateService()).when(dssedciSignService).getDssedciCertificateService();
        doReturn(commonCertificateVerifier).when(dssedciSignService).getCertificateVerifier();
    }

    @Test
    public void xxxx_xxxx_xxxx() throws IOException {
        //Integration tests disabled
        assertTrue(true);
    }

    //    @Test
    public void signXMLDocumentBadSignature_shouldThrowEDCIException() {
        doReturn("badSignatureLevel").when(iConfigService).getString(EDCIConfig.DSS.SIGNATURE_LEVEL);
        thrown.expect(EDCIException.class);
        thrown.expectMessage(EDCIMessageKeys.Exception.DSS.INVALID_SIGNATURE_LEVEL);
        dssedciSignService.signXMLDocument(samplePath, certPath, certPassword, null, true);
    }

    //    @Test
    public void signXMLDocumentB_shouldSignSampleXMLDocument() throws Exception {
        doReturn("XAdES-BASELINE-B").when(iConfigService).getString(EDCIConfig.DSS.SIGNATURE_LEVEL);
        DSSDocument signedDocument = dssedciSignService.signXMLDocument(samplePath, certPath, certPassword, null, true);
        this.testFinalDocument(signedDocument);
    }

    //    @Test
    public void signXMLDocumentT_shouldSignSampleXMLDocument() throws Exception {
        doReturn("XAdES-BASELINE-T").when(iConfigService).getString(EDCIConfig.DSS.SIGNATURE_LEVEL);
        DSSDocument signedDocument = dssedciSignService.signXMLDocument(samplePath, certPath, certPassword, null, true);
        this.testFinalDocument(signedDocument);
    }

    //    @Test
    public void signXMLDocumentLT_shouldSignSampleXMLDocument() throws Exception {
        doReturn("XAdES-BASELINE-LT").when(iConfigService).getString(EDCIConfig.DSS.SIGNATURE_LEVEL);
        DSSDocument signedDocument = dssedciSignService.signXMLDocument(samplePath, certPath, certPassword, null, true);
        this.testFinalDocument(signedDocument);
    }


    //    @Test
    public void signXMLDocumentB_shouldSignXMLCred() throws Exception {
        doReturn("XAdES-BASELINE-B").when(iConfigService).getString(EDCIConfig.DSS.SIGNATURE_LEVEL);
        DSSDocument signedDocument = dssedciSignService.signXMLDocument(credPath, certPath, certPassword, EDCIConfig.XML.XML_SIGNATURE_XPATH, true);
        this.testFinalDocument(signedDocument);
    }

    //    @Test
    public void signXMLDocumentLT_shouldSignXMLCred() throws Exception {
        doReturn("XAdES-BASELINE-LT").when(iConfigService).getString(EDCIConfig.DSS.SIGNATURE_LEVEL);
        DSSDocument signedDocument = dssedciSignService.signXMLDocument(credPath, certPath, certPassword, EDCIConfig.XML.XML_SIGNATURE_XPATH, true);
        this.testFinalDocument(signedDocument);
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
        Assert.assertTrue(reports.getSimpleReport().getSignaturesCount() == 1);
    }

    public SignatureTokenConnection getQSEALCToken(String filePath, String password) {
        Pkcs12SignatureToken jksSignatureToken = null;
        try {
            InputStream is = new FileInputStream(filePath);
            jksSignatureToken = new Pkcs12SignatureToken(is, new KeyStore.PasswordProtection(password.toCharArray()));
        } catch (Exception e) {
            throw new EDCIException().addDescription("Could not load the signature keystore").setCause(e);
        }
        return jksSignatureToken;
    }

}