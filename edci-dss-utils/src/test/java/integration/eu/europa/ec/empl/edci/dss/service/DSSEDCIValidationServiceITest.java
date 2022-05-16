package integration.eu.europa.ec.empl.edci.dss.service;

import eu.europa.ec.empl.edci.dss.service.DSSEDCIValidationService;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.tsl.cache.CacheCleaner;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doReturn;

public class DSSEDCIValidationServiceITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    @Spy
    private DSSEDCIValidationService dssedciValidationService;

    private String sampleXMLLB = "src/test/resources/creds/signedSampleXML-B.badSign.xml";
    private String sampleXMLLT = "src/test/resources/creds/signedSampleXML-LT.xml";
    private String sampleCRED = "src/test/resources/creds/sewing_course.xml";
    private String sampleQSESAL = "src/test/resources/creds/sampleQSEAL.xml";

//    @Before
    public void setUp() throws Exception {

        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource();
        OCSPDataLoader ocspDataLoader = new OCSPDataLoader();
        onlineOCSPSource.setDataLoader(ocspDataLoader);

        OnlineCRLSource onlineCRLSource = new OnlineCRLSource();
        CommonsDataLoader commonsHttpDataLoader = new CommonsDataLoader();
        commonsHttpDataLoader.setTrustStrategy(TrustAllStrategy.INSTANCE);
        onlineCRLSource.setDataLoader(commonsHttpDataLoader);

        // KeyStoreCertificateSource keyStoreCertificateSource = new KeyStoreCertificateSource(new File("src/test/resources/keystore/keystore.p12"), "PKCS12", "dss-password");
        LOTLSource lotlSource = new LOTLSource();
        lotlSource.setUrl("https://ec.europa.eu/tools/lotl/eu-lotl.xml");
        //lotlSource.setCertificateSource(keyStoreCertificateSource);
        lotlSource.setPivotSupport(true);

        FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader(commonsHttpDataLoader);
        CacheCleaner cacheCleaner = new CacheCleaner();
        cacheCleaner.setCleanFileSystem(true);
        cacheCleaner.setDSSFileLoader(onlineFileLoader);
        CommonTrustedCertificateSource commonTrustedCertificateSource = new CommonTrustedCertificateSource();
        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();
        TLValidationJob validationJob = new TLValidationJob();
        validationJob.setTrustedListCertificateSource(trustedListsCertificateSource);
        validationJob.setOnlineDataLoader(onlineFileLoader);
        validationJob.setListOfTrustedListSources(lotlSource);
        validationJob.setCacheCleaner(cacheCleaner);

        CommonCertificateVerifier cv = new CommonCertificateVerifier();
        cv.setDataLoader(commonsHttpDataLoader);
        cv.setOcspSource(onlineOCSPSource);
        cv.setCrlSource(onlineCRLSource);
        cv.setTrustedCertSources(commonTrustedCertificateSource);
        cv.addTrustedCertSources(trustedListsCertificateSource);
        cv.setCheckRevocationForUntrustedChains(false);

        doReturn(cv).when(dssedciValidationService).getCertificateVerifier();
        doReturn(validationJob).when(dssedciValidationService).getValidationJob();

    }

    @Test
    public void xxxx_xxxx_xxxx() throws IOException {
        //Integration tests disabled
        assertTrue(true);
    }

//    @Test
    public void checkXML_BSignature_shouldHave0ValidSignatures_whenSampleIsInvalid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(sampleXMLLB));
        Reports reports = dssedciValidationService.validateXML(bytes, true);
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() == 0);

    }


//    @Test
    public void checkXML_LTSignature_shouldHaveAtLeast1ValidSignature_whenSampleIsValid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(sampleXMLLT));
        Reports reports = dssedciValidationService.validateXML(bytes, true);
        if (reports.getSimpleReport().getValidSignaturesCount() == 0) {
            System.out.println(reports.getSimpleReport().getSubIndication(reports.getSimpleReport().getFirstSignatureId()));
            System.out.println(reports.getSimpleReport().getErrors(reports.getSimpleReport().getFirstSignatureId()));
        }
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() > 0);

    }


//    @Test
    public void checkXML_LTZetesSignature_shouldHaveAtLeast1ValidSignature_whenSampleIsValid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(sampleCRED));
        Reports reports = dssedciValidationService.validateXML(bytes, true);
        if (reports.getSimpleReport().getValidSignaturesCount() == 0) {
            System.out.println(reports.getSimpleReport().getSubIndication(reports.getSimpleReport().getFirstSignatureId()));
            System.out.println(reports.getSimpleReport().getErrors(reports.getSimpleReport().getFirstSignatureId()));
        }
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() > 0);

    }

//    @Test
    public void checkXML_QSEALSignature_shouldHaveAtLeast1ValidSignature_whenSampleIsValid() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(sampleQSESAL));
        Reports reports = dssedciValidationService.validateXML(bytes, true);
        if (reports.getSimpleReport().getValidSignaturesCount() == 0) {
            System.out.println(reports.getSimpleReport().getSubIndication(reports.getSimpleReport().getFirstSignatureId()));
            System.out.println(reports.getSimpleReport().getErrors(reports.getSimpleReport().getFirstSignatureId()));
        }
        assertTrue(reports.getSimpleReport().getValidSignaturesCount() > 0);

    }

}
