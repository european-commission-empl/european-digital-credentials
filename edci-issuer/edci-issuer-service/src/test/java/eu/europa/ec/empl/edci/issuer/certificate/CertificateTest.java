package eu.europa.ec.empl.edci.issuer.certificate;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.issuer.util.CertificateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

public class CertificateTest extends AbstractUnitBaseTest {

    @InjectMocks
    CertificateUtils certificateService;

    @Test
    public void dummmyTest() {
        Assert.assertTrue(true);
    }
    /*@Test
    public void certificate1_test() {

        String certificate = "MIIGYTCCBUmgAwIBAgIDEPSPMA0GCSqGSIb3DQEBCwUAMFwxCzAJBgNVBAYTAlNJMRQwEgYDVQQKEwtIYWxjb20gZC5kLjEXMBUGA1UEYRMOVkFUU0ktNDMzNTMxMjYxHjAcBgNVBAMTFUhhbGNvbSBDQSBQTyBlLXNlYWwgMTAeFw0xOTEyMjMxMDI4MzNaFw0yMjEyMjMxMDI4MzNaMIH+MQswCQYDVQQGEwJTSTEmMCQGA1UEChMdQU5USE9OWSBGSVNIRVIgQ0FNSUxMRVJJIFMuUC4xFzAVBgkrBgEEAa4zAgMTCDYxMDM4NzUwMRcwFQYDVQRhEw5WQVRTSS02MTAzODc1MDEtMCsGA1UEAxMkQW50aG9ueSBGaXNoZXIgQ2FtaWxsZXJpIFMucC4gRSBTZWFsMQ8wDQYDVQQEEwZFIFNlYWwxJjAkBgNVBCoTHUFudGhvbnkgRmlzaGVyIENhbWlsbGVyaSBTLnAuMS0wKwYJKoZIhvcNAQkBFh5hbnRob255QGtub3dsZWRnZWlubm92YXRpb24uZXUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCq6v/DdquFWMOCDqs3PXRyooEPJ7YXZHvhauSL8DncZzCpziPmEDQ9h2NR6sjOM43Om5B9nFWXHdXcUl8a5wwAuHH9TkKGJIhgSMDDG8cM6+l2Ns6BrnveVJ2L7Wcbi1sTDfoaqZHxe472X3YwhnP7YEZXzt9KGvFO3PyhFEb8y/a3vhL4X30OTicQJmO7GLlLHWVBy28o1z9he3rHFfINOvH9wHnCzAqbTLcNiOYnc0Jp0RtlFtZj8FwpdY6RGCl8qAVLaIuG/ASpd6tTd8zs8fyazBOMHMKQ2IM92G+TdnesyER6eMLB7Oj7VKW9I+JEiZaPaCWsBKRAqgnvvF/DAgMBAAGjggKHMIICgzATBgNVHSMEDDAKgAhJSHZQdwqxDDCBggYIKwYBBQUHAQMEdjB0MBUGCCsGAQUFBwsCMAkGBwQAi+xJAQIwCAYGBACORgEBMAgGBgQAjkYBBDAyBgYEAI5GAQUwKDAmFiBodHRwczovL3d3dy5oYWxjb20uc2kvcmVwb3NpdG9yeRMCRU4wEwYGBACORgEGMAkGBwQAjkYBBgIwgYAGCCsGAQUFBwEBBHQwcjBNBggrBgEFBQcwAoZBaHR0cDovL3d3dy5oYWxjb20uc2kvdXBsb2Fkcy9yZXBvc2l0b3J5L0hhbGNvbV9DQV9QT19lLXNlYWxfMS5jZXIwIQYIKwYBBQUHMAGGFWh0dHA6Ly9vY3NwLmhhbGNvbS5zaTBmBgNVHSAEXzBdMFAGCisGAQQBrjMFAwEwQjBABggrBgEFBQcCARY0aHR0cDovL3d3dy5oYWxjb20uc2kvdXBsb2Fkcy9maWxlcy9DUFNfaGFsY29tX2NhLnBkZjAJBgcEAIvsQAEDMIGzBgNVHR8EgaswgagwgaWggaKggZ+GZWxkYXA6Ly9sZGFwLmhhbGNvbS5zaS9jbj1IYWxjb20lMjBDQSUyMFBPJTIwZS1zZWFsJTIwMSxvPUhhbGNvbSxjPVNJP2NlcnRpZmljYXRlcmV2b2NhdGlvbmxpc3Q7YmluYXJ5hjZodHRwOi8vZG9taW5hLmhhbGNvbS5zaS9jcmxzL2hhbGNvbV9jYV9wb19lLXNlYWxfMS5jcmwwEQYDVR0OBAoECEsy60sNP7RzMA4GA1UdDwEB/wQEAwIFoDAYBgYqhXAiAgEEDhMMODg4ODAzMDAwNjc2MAkGA1UdEwQCMAAwDQYJKoZIhvcNAQELBQADggEBAFE+e6vcubeQ4I6Eptx1lE2dBxB+DEaw4m6quPbSZk7yanByp0QRG/rSXFAJC2PDQRVc9k/J096VftrE9tIPyOpEYXXugdLJ5t9ufpkTbGNOp1O/ioxqWcMqvY/vyuXrvsu5wAd0sAmKaruOqNKLSIxoy1xRxZjhfFUYIjATK8T6SCVRfojZw81Cbx0TNZHRG79dlEEg5zViy8ZPt419O4iCRuzVCUfIlZ8lVtAWiEDALWR4VUXXAJN5GFLgj6Br26kLxiiABTLZcYgr8fEPUCU5mNvHWU+gD9yHYv68ploPbEPONK6OlcTfhvEjPitVOOB+/QBiSIr95U3+vkGRSf0=";

        Map<String, String> decodedCertificate = certificateService.getCertificateInfo(certificate);

        for (Map.Entry<String, String> entry : decodedCertificate.entrySet()) {
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME)) {
                Assert.assertEquals("SI", entry.getValue());
            }
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION)) {
                Assert.assertEquals("Halcom d.d.", entry.getValue());
            }
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER)) {
                Assert.assertEquals("VATSI-43353126", entry.getValue());
            }
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COMMON_NAME)) {
                Assert.assertEquals("Halcom CA PO e-seal 1", entry.getValue());
            }
        }
    }

    @Test
    public void certificate2_test() {

        String certificate = "MIIEsDCCA5igAwIBAgIDDg7QMA0GCSqGSIb3DQEBCwUAMGgxCzAJBgNVBAYTAlNJMRQwEgYDVQQKEwtIYWxjb20gZC5kLjEXMBUGA1UEYRMOVkFUU0ktNDMzNTMxMjYxKjAoBgNVBAMTIUhhbGNvbSBSb290IENlcnRpZmljYXRlIEF1dGhvcml0eTAeFw0xNzA0MjIwODAwMDBaFw0yNzA0MjIwODAwMDBaMFwxCzAJBgNVBAYTAlNJMRQwEgYDVQQKEwtIYWxjb20gZC5kLjEXMBUGA1UEYRMOVkFUU0ktNDMzNTMxMjYxHjAcBgNVBAMTFUhhbGNvbSBDQSBQTyBlLXNlYWwgMTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAIaubUdxwYlJE8Nh1T5btUr30yS0Zer+2/5q0iiudkjPiVCFfmdOLGwyBhzcXn2+TRmiPUgDkRxiaUzSFmbEDQ+cDTprfRl0nPtrWrknfTZmcZL0utqtP9T6pnDBJL1+eG9b3UqtXIGtu11/QtNLCXgWc8hLb+d1kNIMtaSuNgI7ndZRCYlNCXaJxUgMyYWPK9JJA5krKGr581DPJaZS1dVPIgqtVxMeH6qzJiY1zBSnMAczi4DK8iVU+mzXbUk2zDs96ENBIMrnQO9OXj8jUOjpV39UF7bHyVXLjK9PfqGXlYmUOA/9LKp8E4IjHWfUdJLns66LOZzpxZw0cnYl8CkCAwEAAaOCAW0wggFpMA8GA1UdEwEB/wQFMAMBAf8wEQYDVR0OBAoECElIdlB3CrEMMBMGA1UdIwQMMAqACEKupkPHmCiwMAsGA1UdDwQEAwIBBjCByQYDVR0fBIHBMIG+MIG7oIG4oIG1hm9sZGFwOi8vbGRhcC5oYWxjb20uc2kvY249SGFsY29tJTIwUm9vdCUyMENlcnRpZmljYXRlJTIwQXV0aG9yaXR5LG89SGFsY29tLGM9U0k/Y2VydGlmaWNhdGVyZXZvY2F0aW9ubGlzdDtiaW5hcnmGQmh0dHA6Ly9kb21pbmEuaGFsY29tLnNpL2NybHMvaGFsY29tX3Jvb3RfY2VydGlmaWNhdGVfYXV0aG9yaXR5LmNybDBVBgNVHSAETjBMMEoGBFUdIAAwQjBABggrBgEFBQcCARY0aHR0cDovL3d3dy5oYWxjb20uc2kvdXBsb2Fkcy9maWxlcy9DUFNfaGFsY29tX2NhLnBkZjANBgkqhkiG9w0BAQsFAAOCAQEAlakYcOLF73O+O88WXxhoyEl3V0Kg7wp6+KAv8coi1SyE5is0Uv89Z+ko1HUCUMxjTwnb6W1o9V/18eLjUE00QtLu6+Th4he0XtpneD+NA3dpk4aIkX0dansFAWuk6c4KHuQq8+UmYb1mScEnuZXQsiomFI1qjx6H7AeTHyLpb58UJOr5wHu/QiQgfZo3FjqXu+y279r97cBZkAszMSaz5lkHJwqc7OgaFMoZOsfrf4LFIMh+8GDw6ZBIQegXzvu+VRec1HBX7GyF8X/xdcPqljw/Z6uKXWATp6e3qYTk0hveG54JkxhvqlI0xhEvtqEPFmUFydtItsYtDUVav+wrew==";

        Map<String, String> decodedCertificate = certificateService.getCertificateInfo(certificate);

        for (Map.Entry<String, String> entry : decodedCertificate.entrySet()) {
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME)) {
                Assert.assertEquals("SI", entry.getValue());
            }
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION)) {
                Assert.assertEquals("Halcom d.d.", entry.getValue());
            }
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER)) {
                Assert.assertEquals("VATSI-43353126", entry.getValue());
            }
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COMMON_NAME)) {
                Assert.assertEquals("Halcom Root Certificate Authority", entry.getValue());
            }
        }
    }

    @Test
    public void certificate3_test() {

        String certificate = "MIIDgDCCAmigAwIBAgIDDN+bMA0GCSqGSIb3DQEBCwUAMGgxCzAJBgNVBAYTAlNJMRQwEgYDVQQKEwtIYWxjb20gZC5kLjEXMBUGA1UEYRMOVkFUU0ktNDMzNTMxMjYxKjAoBgNVBAMTIUhhbGNvbSBSb290IENlcnRpZmljYXRlIEF1dGhvcml0eTAeFw0xNjA2MTAwNzA3NTBaFw0zNjA2MTAwNzA3NTBaMGgxCzAJBgNVBAYTAlNJMRQwEgYDVQQKEwtIYWxjb20gZC5kLjEXMBUGA1UEYRMOVkFUU0ktNDMzNTMxMjYxKjAoBgNVBAMTIUhhbGNvbSBSb290IENlcnRpZmljYXRlIEF1dGhvcml0eTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAOlSpsYa72O7rYH0kLJajw3VFjO0HBj7y4kqMLtlgcTh+wKplAd25dcV5HpkEIDqPNCzoq2uHB/qu4FhmNT5jWmVxEUuAwnKhvpcWhEXQDA+8MZjCcnxjUGlVg0FZGlLWKwqKZa7QDMWNEtnbNfxtEal6lmoQ2gPjDgqqjz2RAOG+IrbRSErKR4St/qlZUHeBghYcJU+9EzZ6w8pqZGKnq3KEvXlleY42Rqmi5xPpkgTEKV5RL1qOyn1FndAy36bXN++i+vnoBlvnxU/J54psfUN/F9HojzdLgsC+/SN6uwMsfm0Baz5j6k9biwdOZ/QTp9OyGqegANh3M/4bZTLD88CAwEAAaMzMDEwDwYDVR0TAQH/BAUwAwEB/zARBgNVHQ4ECgQIQq6mQ8eYKLAwCwYDVR0PBAQDAgEGMA0GCSqGSIb3DQEBCwUAA4IBAQBSuXnQ22P+GYH7DPnB5VBZyp2y+1wz0Dioq7UaTlMldSLTSb/Kgc/T4XujkUZ1yhrr2fVdvHuGNf2Bl5yE1yaYIvyxNdCplbZ8/+SXtEB+SV1oyOLUOXUnTwORsjFXv4bXbcpxACI30DtYJFCgnIyaiY71KEZs5xbtsIGr9EYmr6boGkV3cBaSsntxcdz330lnwDMIDi5TwXerx0qRTBLv5w4J5XUxIK5u/FqKgJwQsNuoSszzK9w2NKb3qQtnnZDLPSafdc1MyR0GCnWLUsCB8NEmrMySphScXDwWQvuTzAKoE/PargrDuBX0sNDU4BYgT6xQmHgmlB5o65Ry/veL";

        Map<String, String> decodedCertificate = certificateService.getCertificateInfo(certificate);

        for (Map.Entry<String, String> entry : decodedCertificate.entrySet()) {
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME)) {
                Assert.assertEquals("SI", entry.getValue());
            }
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION)) {
                Assert.assertEquals("Halcom d.d.", entry.getValue());
            }
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER)) {
                Assert.assertEquals("VATSI-43353126", entry.getValue());
            }
            if (entry.getKey().equals(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COMMON_NAME)) {
                Assert.assertEquals("Halcom Root Certificate Authority", entry.getValue());
            }
        }
    }*/

}
