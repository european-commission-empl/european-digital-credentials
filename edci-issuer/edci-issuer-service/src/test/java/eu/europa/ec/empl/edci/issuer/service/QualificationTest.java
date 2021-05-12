package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.dss.validation.DSSValidationUtils;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.service.TSLRepository;
import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.reports.CertificateReports;
import org.apache.log4j.Logger;
import org.junit.Assert;

public class QualificationTest {

    private static final Logger logger = Logger.getLogger(QualificationTest.class);
    private static String keystoreResourcePath = "src/test/resources-unfiltered/dss/keystore.p12";

    //@Test
    public void validateCertificate_shouldBeQualified() {

        String certifBase64Qseal = "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";
        DSSValidationUtils dssValidationUtils = new DSSValidationUtils();
        CertificateVerifier cv = new CommonCertificateVerifier();
        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();
        TSLRepository tslRepository = new TSLRepository();
        tslRepository.setTrustedListsCertificateSource(trustedListsCertificateSource);
        tslRepository.setCacheDirectoryPath("src/test/resources-unfiltered/dss/");
        TSLValidationJob job = new TSLValidationJob();
        job.setDataLoader(new CommonsDataLoader());
        job.setOjContentKeyStore(new KeyStoreCertificateSource(DSSValidationUtils.class.getResourceAsStream(keystoreResourcePath), "PKCS12",
                "dss-password"));
        job.setLotlUrl("https://ec.europa.eu/tools/lotl/eu-lotl.xml");
        job.setOjUrl("https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG");
        job.setLotlCode("EU");
        job.setRepository(tslRepository);
        job.initRepository();
        job.refresh();
        cv.setTrustedCertSource(trustedListsCertificateSource);

        CertificateReports reports = dssValidationUtils.validateCertificate(certifBase64Qseal, cv);
        System.out.println(reports.getSimpleReport().getQualificationAtCertificateIssuance());
        Assert.assertTrue(reports.getSimpleReport().getQualificationAtCertificateIssuance().toString().startsWith("QCERT_"));

    }

    //@Test
    public void validateCertificate_shouldNotBeQualified() {

        String certifBase64CA = "MIII0jCCBrqgAwIBAgITYgAC1Xpr+70LzLxbBwAAAALVejANBgkqhkiG9w0BAQsFADBiMRMwEQYKCZImiZPyLGQBGRYDaW50MRYwFAYKCZImiZPyLGQBGRYGZXZlcmlzMRcwFQYKCZImiZPyLGQBGRYHdXNlcnNhZDEaMBgGA1UEAxMRZXZlcmlzIElzc3VpbmcgQ0EwHhcNMTkxMjE3MTYyMjI2WhcNMjIwNTMwMTMyOTEwWjCB6TETMBEGCgmSJomT8ixkARkWA2ludDEWMBQGCgmSJomT8ixkARkWBmV2ZXJpczEXMBUGCgmSJomT8ixkARkWB3VzZXJzYWQxDzANBgNVBAsTBkV2ZXJpczEPMA0GA1UECxMGRXVyb3BlMQ4wDAYDVQQLEwVTcGFpbjESMBAGA1UECxMJQmFyY2Vsb25hMQ4wDAYDVQQLEwVVc2VyczEcMBoGA1UEAxMTSm9yZGkgQ2FzdGlsbG8gUXVlcjEtMCsGCSqGSIb3DQEJARYeam9yZGkuY2FzdGlsbG8ucXVlckBldmVyaXMuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0RnZ0rLbiRI+GBboGKQsl5w6lZSGHsnnUBRtEMOdmpJACx11GDH3gmQQDBOAA+mhLhVXW4QdSUQ16P649PyjBbNu9A4s8FDRecE1QNP6Mke/t+1rcuntJy+L9hQqKqbD9QoXhoajUgi+T0AiaJIEX/4Sw1bMM0UhS+XE4TgvyqI+1IwJa+TlQYHpiwcvP1982QOaAXbBoKm1wkDadKxTc/TJMLUZ6OU1zAYXctZPVXpygYVNz7uTQtRtKhoWZojZ5Wlf+9PqgEGJeiu9MM1Y28D0x0UcHXz+0OhToWGQN++YGaoMFYmoibXpbJWeAeZmivZGyYpYYdMM3gbPuNh0QQIDAQABo4ID9zCCA/MwPQYJKwYBBAGCNxUHBDAwLgYmKwYBBAGCNxUIgayQaIfmmAWFsZEahcCFZIGyywEQgrHcE4efxGsCAWQCAQ8wKQYDVR0lBCIwIAYKKwYBBAGCNwoDBAYIKwYBBQUHAwQGCCsGAQUFBwMCMA4GA1UdDwEB/wQEAwIFoDA1BgkrBgEEAYI3FQoEKDAmMAwGCisGAQQBgjcKAwQwCgYIKwYBBQUHAwQwCgYIKwYBBQUHAwIwRAYJKoZIhvcNAQkPBDcwNTAOBggqhkiG9w0DAgICAIAwDgYIKoZIhvcNAwQCAgCAMAcGBSsOAwIHMAoGCCqGSIb3DQMHMB0GA1UdDgQWBBSG7JSa0CWWnhlfI2iWgJ2//JDlTDAfBgNVHSMEGDAWgBRCi0ZiimNw+pfn6loa8oBhLSf7ezCCASYGA1UdHwSCAR0wggEZMIIBFaCCARGgggENhoHBbGRhcDovLy9DTj1ldmVyaXMlMjBJc3N1aW5nJTIwQ0EsQ049U0NMRDAxUENBU0IwMSxDTj1DRFAsQ049UHVibGljJTIwS2V5JTIwU2VydmljZXMsQ049U2VydmljZXMsQ049Q29uZmlndXJhdGlvbixEQz1ldmVyaXMsREM9aW50P2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RDbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludIZHaHR0cDovL2V2ZXJpc0NBLnVzZXJzYWQuZXZlcmlzLmludC9DZXJ0RW5yb2xsL2V2ZXJpcyUyMElzc3VpbmclMjBDQS5jcmwwggE+BggrBgEFBQcBAQSCATAwggEsMIGzBggrBgEFBQcwAoaBpmxkYXA6Ly8vQ049ZXZlcmlzJTIwSXNzdWluZyUyMENBLENOPUFJQSxDTj1QdWJsaWMlMjBLZXklMjBTZXJ2aWNlcyxDTj1TZXJ2aWNlcyxDTj1Db25maWd1cmF0aW9uLERDPWV2ZXJpcyxEQz1pbnQ/Y0FDZXJ0aWZpY2F0ZT9iYXNlP29iamVjdENsYXNzPWNlcnRpZmljYXRpb25BdXRob3JpdHkwdAYIKwYBBQUHMAKGaGh0dHA6Ly9ldmVyaXNDQS51c2Vyc2FkLmV2ZXJpcy5pbnQvQ2VydEVucm9sbC9TQ0xEMDFQQ0FTQjAxLnVzZXJzYWQuZXZlcmlzLmludF9ldmVyaXMlMjBJc3N1aW5nJTIwQ0EuY3J0ME4GA1UdEQRHMEWgIwYKKwYBBAGCNxQCA6AVDBNqY2FzdGlscUBldmVyaXMuY29tgR5qb3JkaS5jYXN0aWxsby5xdWVyQGV2ZXJpcy5jb20wDQYJKoZIhvcNAQELBQADggIBAB97JYiCLEjRnOr0ZHlN5wF2mkiERCGqjwM5QbhyeFGdo21asggnnPNx5FTfFYZ1IVzWCMBROX805mGUC1vfZ4zxqpN0rln3nVH2k8IXIIzH2thOz3COg9SYGfvUZrux7eTgWE3K0csVtbct5OvgZNpdi3Y2Lq2P9/4KnRR55WZHP8wJ3w3EeU4nlIC1gaCR5/EWgwAxeTFI3xSnwOqrOR/NVXPjN7j6uv/REhGIVqfGepgSN1NLVpG8CQXCQhXNQe/XmZ/25pHq3MHfX5bo90q3z7l/9JUavGxN+mnZWyCg/FApz/WffPnKkvGM4UwP0Wfatk4iVHxzK0ri8Tu/nncrcGu4lJj0AqOIaUDO/7IwnzRf19cPUsdwJvfHg/5HVlnzyTZPXAlro/M7ITm/zBzFZMuahbdidc6H0R8B/MCJ/+4s/BJ69eq6MogurjHZ0/0L7u+jn0eoQ7svQxWbOuqj01qrSUqzVaZAx3pA0NUvf7XuZ/Dtwf3hV/Nnm9R2dlPi86OiC6ta0nb8bi7DyDoHPKPdb4WoQUoHdZXiN7mw8//Y+HIzCUxDFz4j6JWdcOVO7bsPrHN9cWwH3wOz9/+qAhH8qZfltr+i7BP87LEfAAsvKChI9mEu2P0ZneBG7gwOzwKuGUfQu8nhx50yAsY5/2K9M6wHWC6a+YBdiFLR";

        DSSValidationUtils dssValidationUtils = new DSSValidationUtils();
        CertificateVerifier cv = new CommonCertificateVerifier();
        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();
        TSLRepository tslRepository = new TSLRepository();
        tslRepository.setTrustedListsCertificateSource(trustedListsCertificateSource);
        tslRepository.setCacheDirectoryPath("src/test/resources-unfiltered/dss/");
        TSLValidationJob job = new TSLValidationJob();
        job.setDataLoader(new CommonsDataLoader());
        job.setOjContentKeyStore(new KeyStoreCertificateSource(DSSValidationUtils.class.getResourceAsStream(keystoreResourcePath), "PKCS12",
                "dss-password"));
        job.setLotlUrl("https://ec.europa.eu/tools/lotl/eu-lotl.xml");
        job.setOjUrl("https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG");
        job.setLotlCode("EU");
        job.setRepository(tslRepository);
        job.initRepository();
        job.refresh();
        cv.setTrustedCertSource(trustedListsCertificateSource);

        CertificateReports reports = dssValidationUtils.validateCertificate(certifBase64CA, cv);
        System.out.println(reports.getSimpleReport().getQualificationAtCertificateIssuance());
        Assert.assertFalse(reports.getSimpleReport().getQualificationAtCertificateIssuance().toString().startsWith("QCERT_"));

    }

}
