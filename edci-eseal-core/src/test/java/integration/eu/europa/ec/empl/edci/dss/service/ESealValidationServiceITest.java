package integration.eu.europa.ec.empl.edci.dss.service;

import eu.europa.ec.empl.edci.dss.config.ESealCoreConfigService;
import eu.europa.ec.empl.edci.dss.constants.ESealConfig;
import eu.europa.ec.empl.edci.dss.service.messages.FileService;
import eu.europa.ec.empl.edci.dss.service.validation.ESealValidationService;
import eu.europa.ec.empl.edci.dss.service.validation.JadesValidationService;
import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.cache.CacheCleaner;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class ESealValidationServiceITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    @Spy
    private ESealValidationService eSealValidationService;

    @Spy
    private JadesValidationService jsonValidationService;

    @Mock
    private ESealCoreConfigService configService;

    @Spy
    private TLValidationJob validationJob;

    @Spy
    private FileService edciFileService;

    private String jsonAdvancedFilePath = "src/test/resources/creds/json_sealed_advanced.json";
    private String jsonLTAfilePath = "src/test/resources/creds/json_to_seal-signed-jades-baseline-lta.json";
    private String jsonNoSignatures = "src/test/resources/creds/json_to_seal.json";
    private CommonsDataLoader commonsDataLoader;

    @Before
    public void setUp() throws Exception {


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

        TrustedListsCertificateSource tslCertificateSource = new TrustedListsCertificateSource();

        TLValidationJob validationJob = new TLValidationJob();
        validationJob.setOnlineDataLoader(onlineFileLoader);
        validationJob.setCacheCleaner(cacheCleaner);
        validationJob.setListOfTrustedListSources(lotlSource);
        validationJob.setTrustedListCertificateSource(tslCertificateSource);

        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        commonCertificateVerifier.setCheckRevocationForUntrustedChains(true);
        commonCertificateVerifier.setTrustedCertSources(tslCertificateSource);
        commonCertificateVerifier.setOcspSource(onlineOCSPSource);
        commonCertificateVerifier.setCrlSource(onlineCRLSource);
        commonCertificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());

        FileService edciFileService = new FileService();
        doReturn(edciFileService).when(jsonValidationService).getEdciFileService();
        doReturn(validationJob).when(jsonValidationService).getValidationJob();
        doReturn(commonCertificateVerifier).when(eSealValidationService).getCertificateVerifier();
        doReturn(commonCertificateVerifier).when(jsonValidationService).getCertificateVerifier();
        doReturn(jsonValidationService).when(eSealValidationService).getJsonValidationService();
        doReturn(configService).when(eSealValidationService).getConfigService();
        doReturn(commonsDataLoader).when(eSealValidationService).getCommonsDataLoader();

        this.commonsDataLoader = commonsHttpDataLoader;

        when(configService.getBoolean(ESealConfig.Properties.JOB_ONLINE_REFRESH,
                ESealConfig.Defaults.JOB_ONLINE_REFRESH)).thenReturn(true);

    }

    @Test
    public void xxxx_xxxx_xxxx() throws IOException {
        //Integration tests disabled
        assertTrue(true);
    }
    
    /*
    @Test
    public void checkJsonFromPath_shouldHave0ValidSignatures_whenSampleIsInvalid() throws IOException {
        Reports reports = eSealValidationService.validateJson(jsonNoSignatures, commonsDataLoader);
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() == 0);
    }

    @Test
    public void checkJsonFromPathNoDataLoader_shouldHave0ValidSignatures_whenSampleIsInvalid() throws IOException {
        Reports reports = eSealValidationService.validateJson(jsonNoSignatures);
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() == 0);
    }

    @Test
    public void checkJsonFromBytes_shouldHave0ValidSignatures_whenSampleIsInvalid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(jsonNoSignatures));
        Reports reports = eSealValidationService.validateJson(bytes, commonsDataLoader);
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() == 0);
    }

    @Test
    public void checkJsonFromBytesNoDataLoader_shouldHave0ValidSignatures_whenSampleIsInvalid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(jsonNoSignatures));
        Reports reports = eSealValidationService.validateJson(bytes);
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() == 0);
    }

    @Test
    public void checkJsonFromBytesNoDataLoader_LTASignature_shouldHaveAtLeast1ValidSignature_whenSampleIsValid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(jsonLTAfilePath));
        Reports reports = eSealValidationService.validateJson(bytes);
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() > 0);
    }

    @Test
    public void checkJsonFromBytesL_LTSignature_shouldHaveAtLeast1ValidSignature_whenSampleIsValid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(jsonAdvancedFilePath));
        Reports reports = eSealValidationService.validateJson(bytes, commonsDataLoader);
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() > 0);
    }

    @Test
    public void checkJsonFromBytesLNoDataLoader_LTSignature_shouldHaveAtLeast1ValidSignature_whenSampleIsValid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(jsonAdvancedFilePath));
        Reports reports = eSealValidationService.validateJson(bytes);
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() > 0);
    }

    @Test
    public void checkJsonFromPath_LTZetesSignature_shouldHaveAtLeast1ValidSignature_whenSampleIsValid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(jsonAdvancedFilePath));
        Reports reports = eSealValidationService.validateJson(jsonAdvancedFilePath, commonsDataLoader);
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() > 0);
    }

    @Test
    public void checkJsonFromPathNoDataLoader_LTZetesSignature_shouldHaveAtLeast1ValidSignature_whenSampleIsValid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(jsonAdvancedFilePath));
        Reports reports = eSealValidationService.validateJson(jsonAdvancedFilePath);
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() > 0);
    }


    @Test
    public void checkJSON_QSEALSignature_shouldHaveAtLeast1ValidSignature_whenSampleIsValid() throws IOException {
//        byte[] bytes = Files.readAllBytes(Paths.get(jsonAdvancedFilePath));
//        Reports reports = dssValidationService.validateJson(bytes);
//        if (reports.getSimpleReport().getValidSignaturesCount() == 0) {
//            System.out.println(reports.getSimpleReport().getSubIndication(reports.getSimpleReport().getFirstSignatureId()));
//            System.out.println(reports.getSimpleReport().getAdESValidationErrors(reports.getSimpleReport().getFirstSignatureId()));
//        }
//        assertTrue(reports.getSimpleReport().getValidSignaturesCount() > 0);

    }*/

}
