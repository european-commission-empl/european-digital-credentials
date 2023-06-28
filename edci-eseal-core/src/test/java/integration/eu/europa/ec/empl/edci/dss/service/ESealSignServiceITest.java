package integration.eu.europa.ec.empl.edci.dss.service;

import eu.europa.ec.empl.edci.dss.config.ESealCoreConfigService;
import eu.europa.ec.empl.edci.dss.constants.ESealConfig;
import eu.europa.ec.empl.edci.dss.service.certificate.ESealCertificateService;
import eu.europa.ec.empl.edci.dss.service.messages.FileService;
import eu.europa.ec.empl.edci.dss.service.signature.ESealSignService;
import eu.europa.ec.empl.edci.dss.service.signature.JadesSignService;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.cache.CacheCleaner;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class ESealSignServiceITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    @Spy
    private ESealSignService dssedciSignService;

    @Spy
    private JadesSignService jsonSignService;

    @Spy
    private CertificateVerifier certificateVerifier;

    @Mock
    private ESealCoreConfigService iConfigService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String jsonFilePath = "src/test/resources/creds/json_to_seal.json";
    private String jsonFilePathSealedOutput = "src/test/resources/creds/json_sealed_test_output.json";
    private String certPath = "src/test/resources/seal/QSEALC.pfx";
    private String certPassword = "12341234";

    //    @Before
    public void setUp() throws Exception {
        //disabled setUp because EC bamboo plans cannot connect
        String tspSource = "http://dss.nowina.lu/pki-factory/tsa/good-tsa";
        OnlineTSPSource onlineTSPSource = new OnlineTSPSource(tspSource);
        TimestampDataLoader timestampDataLoader = new TimestampDataLoader();
        onlineTSPSource.setDataLoader(timestampDataLoader);

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

        FileService edciFileService = new FileService();

        doReturn(edciFileService).when(jsonSignService).getEdciFileService();
        doReturn(edciFileService).when(dssedciSignService).getEdciFileService();
        doReturn(validationJob).when(dssedciSignService).getValidationJob();
        doReturn(onlineOCSPSource).when(dssedciSignService).getOcspSource();
        doReturn(onlineTSPSource).when(dssedciSignService).getOnlineTSPSource();
        doReturn(onlineCRLSource).when(dssedciSignService).getCrlSource();
        doReturn(validationJob).when(jsonSignService).getValidationJob();
        doReturn(certificateVerifier).when(jsonSignService).getCertificateVerifier();
        doReturn(certificateVerifier).when(dssedciSignService).getCertificateVerifier();
        doReturn(onlineTSPSource).when(jsonSignService).getOnlineTSPSource();
        doReturn(new ESealCertificateService()).when(jsonSignService).getDssedciCertificateService();
        doReturn(new ESealCertificateService()).when(dssedciSignService).getDssedciCertificateService();
        doReturn(commonCertificateVerifier).when(jsonSignService).getCertificateVerifier();
        doReturn(commonCertificateVerifier).when(dssedciSignService).getCertificateVerifier();
        doReturn(jsonSignService).when(dssedciSignService).getJsonSignService();
        doReturn(iConfigService).when(dssedciSignService).getConfigService();
        doReturn(iConfigService).when(jsonSignService).getConfigService();

        when(iConfigService.getString(anyString(), anyString()))
                .thenAnswer(invocation ->
                        invocation.getArgument(1));

        when(iConfigService.getBoolean(anyString(), anyBoolean()))
                .thenAnswer(invocation ->
                        invocation.getArgument(1));

    }

    @Test
    public void default_empty_test() throws IOException {
        Assert.assertTrue(true);
    }

    //disabled test because bambo cannot connect to timestamp provider

    //    @Test
    public void signJsonDocument_shouldSignWithJAdES_WhenJsonProvided() throws IOException {
        doReturn(SignatureLevel.JAdES_BASELINE_LTA.toString()).when(iConfigService).getString(ESealConfig.Properties.SIGNATURE_LEVEL);
        DSSDocument signedDocument = dssedciSignService.signDocument(jsonFilePath, certPath, certPassword);
        this.testFinalDocument(signedDocument);
        signedDocument.save(jsonFilePathSealedOutput);
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

}