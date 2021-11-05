package eu.europa.ec.empl.edci.dss.service;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import sun.misc.BASE64Encoder;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

public class DSSEDCICertificateServiceTest extends AbstractUnitBaseTest {


    @InjectMocks
    @Spy
    private DSSEDCICertificateService dssedciCertificateService;

    private String certPath = "src/test/resources/seal/QSEALC.pfx";
    private String certPassword = "12341234";
    private String linedCert = "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";
    private String encodedCert = "MIIH4TCCBcmgAwIBAgILU8EOM305Ktq2tAcwDQYJKoZIhvcNAQELBQAwggEhMQswCQYDVQQGEwJF" + System.lineSeparator() +
            "UzESMBAGA1UECAwJQmFyY2Vsb25hMVgwVgYDVQQHDE9CYXJjZWxvbmEgKHNlZSBjdXJyZW50IGFk" + System.lineSeparator() +
            "ZHJlc3MgYXQgaHR0cDovL3d3dy5hbmYuZXMvZXMvYWRkcmVzcy1kaXJlY2Npb24uaHRtbCApMScw" + System.lineSeparator() +
            "JQYDVQQKDB5BTkYgQXV0b3JpZGFkIGRlIENlcnRpZmljYWNpb24xLjAsBgNVBAsMJUFORiBBdXRv" + System.lineSeparator() +
            "cmlkYWQgSW50ZXJtZWRpYSBkZSBJZGVudGlkYWQxGjAYBgkqhkiG9w0BCQEWC2luZm9AYW5mLmVz" + System.lineSeparator() +
            "MRIwEAYDVQQFEwlHNjMyODc1MTAxGzAZBgNVBAMMEkFORiBBc3N1cmVkIElEIENBMTAeFw0yMTA1" + System.lineSeparator() +
            "MDUxNjMxMDBaFw0yMzA1MDUxNjMxMDBaMIHaMQswCQYDVQQGEwJFUzESMBAGA1UECBMJQmFyY2Vs" + System.lineSeparator() +
            "b25hMRIwEAYDVQQHEwlCYXJjZWxvbmExKzApBgNVBAoTIkNlcnRpZmljYWRvIFRFU1QgU2VsbG8g" + System.lineSeparator() +
            "RWxlY3Ryb25pY28xNTAzBgNVBAsTLENlcnRpZmljYWRvIEN1YWxpZmljYWRvIGRlIFNlbGxvIEVs" + System.lineSeparator() +
            "ZWN0cm9uaWNvMSUwIwYDVQQDExxOb21icmUgU2VydmljaW8gU2VsbGFkbyBURVNUMRgwFgYDVQRh" + System.lineSeparator() +
            "Ew9WQVRFUy1CMDAwMDAwMDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDChgWBhmDu" + System.lineSeparator() +
            "5uCxkfeH+TEGyN3ccD/UdX/UyFoWc7S8dbxF0fbuw3AiebfkYFBM4xYg0bOhcymBkj+UG5nqjTj1" + System.lineSeparator() +
            "t33WfP7SdwYY9h6i3TeDsbNyjCa+YsRff9sK8NVAxBcZ1ZP62+y2LNZKTDTQL27EZl3Fo91wiSNP" + System.lineSeparator() +
            "YLvIqIiKmYOAURM7spTPYWBKKKTu3jInSHI9/AuqmXLnVWu0k0GDF5xJZyk3rzTXTOEM24+QAa6Z" + System.lineSeparator() +
            "yUdpUR2N9vgCUyFVznB+cXF8aweAicaeAXWPw5xBSFRLIZ3z7B8Yty2Fr3YqGz0O05noipQuBBdD" + System.lineSeparator() +
            "LAubLV39R1CMJVwC+8/gz/I7sunTAgMBAAGjggJcMIICWDAfBgNVHSMEGDAWgBQ1Li3mKRy/i9lK" + System.lineSeparator() +
            "fJ4r4JVH0cZPPTAdBgNVHQ4EFgQUioOqulRDfwOKT5FK9QOv89zc8gIwGQYDVR0RBBIwEIEOc29w" + System.lineSeparator() +
            "b3J0ZUBhbmYuZXMwDgYDVR0PAQH/BAQDAgXgMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcD" + System.lineSeparator() +
            "BDBFBgNVHR8EPjA8MDqgOKA2hjRodHRwczovL3d3dy5hbmYuZXMvY3JsL0FORl9Bc3N1cmVkX0lE" + System.lineSeparator() +
            "X0NBMV9TSEEyNTYuY3JsMFEGA1UdIARKMEgwOwYMKwYBBAGBjxwZAQEBMCswKQYIKwYBBQUHAgEW" + System.lineSeparator() +
            "HWh0dHBzOi8vd3d3LmFuZi5lcy9kb2N1bWVudG9zMAkGBwQAi+xAAQEwgY4GCCsGAQUFBwEBBIGB" + System.lineSeparator() +
            "MH8wJwYIKwYBBQUHMAGGG2h0dHA6Ly9vY3NwLmFuZi5lcy9zcGFpbi9BVjBUBggrBgEFBQcwAoZI" + System.lineSeparator() +
            "aHR0cDovL3d3dy5hbmYuZXMvZXMvY2VydGlmaWNhdGVzX2Rvd25sb2FkL0FORl9Bc3N1cmVkX0lE" + System.lineSeparator() +
            "X0NBMV9TSEEyNTYuY2VyMAwGA1UdEwEB/wQCMAAwbAYIKwYBBQUHAQMEYDBeMAoGCCsGAQUFBwsC" + System.lineSeparator() +
            "MAgGBgQAjkYBATALBgYEAI5GAQMCAQ8wJAYGBACORgEFMBowGBYSaHR0cHM6Ly9hbmYuZXMvZW4v" + System.lineSeparator() +
            "EwJlbjATBgYEAI5GAQYwCQYHBACORgEGAjAlBgkrBgEEAYGPHBMEGAwWNTE1Njk2NTkzLTQ4MDk0" + System.lineSeparator() +
            "NDM2NzIzODANBgkqhkiG9w0BAQsFAAOCAgEAmtQGEywvMbLHUTWct4TW9rIrnSXyDvVbnLLwFOhy" + System.lineSeparator() +
            "yNNM3jHoDz0X5XtHhHqAhQcCiXR1wGIaeh0rkmf5AY5st5PxLfgJ1cUShMizQe3FxL5+TJ2hQtqU" + System.lineSeparator() +
            "yrCLTDJnEP/LnPQzQZWdOo3Rr6OM6fQX8W0jsroTumUK50Tl/XW6NbfdPmwmhrEVh/NC5TYiDaRC" + System.lineSeparator() +
            "AO0jfdoKxfBvjfd64CwOliQZNbZZrfeAzNmBJ5xPF8NwsS8VpbS5XjaTSYPknzF/SZ5y3lY/MZuo" + System.lineSeparator() +
            "wPg6rw44to0eRZPQh41Tng0PydvIaEOA0a0LwVB1/LF2GCVaI55YiSmrMvKZWGM620itNQ9YtB8d" + System.lineSeparator() +
            "0v00aNLTBA6VVik0UqU/wNWB2mLrOvIUIL+5UjqiOkFXMEy+CGjtI1RjlVtHWxztwkgWg77/+MCC" + System.lineSeparator() +
            "KJbOvodB+9KqnhSKhP2si7SmudQWUeIb7DrIGxacGHeNmmf82QfP9me8dfG4TGvz3P02VyZZH3f5" + System.lineSeparator() +
            "6DSmktzHxZcA5f5nW6DyGpTtmwu/WhVotcw0jiEvaaIrzy9FbT9TOM9lDQ13K5U9OkExebQnSEjl" + System.lineSeparator() +
            "dkbE1X6zyiZRQFaJg4ZlySTlX82cIhrV9u6OFKsOLM0Lf0Yezl/EpVyEKE+tsNlCoIYraSwgE+qz" + System.lineSeparator() +
            "TNBNNPFrGw248Fhv1+6knP67KtWXktBMREM=";

    @Test
    public void getCertificateInfo_shouldGiveA4SizeMap_whenCertPathAndPasswordAreCorrect() throws CertificateEncodingException {
        Map<String, String> certInfo = dssedciCertificateService.getCertificateInfo(certPath, certPassword);
        Assert.assertEquals(4, certInfo.size());
    }

    @Test
    public void getCertificateInfo_shouldGiveA7SizeMap_whenCertPathAndPasswordAreCorrect() throws Exception {
        Map<String, String> certInfo = dssedciCertificateService.getCertificateInfo(this.addCertificateWrappers(encodedCert));
        System.out.println(certInfo.size());
        Assert.assertEquals(7, certInfo.size());
    }

    @Test
    public void checkCertificate_ShouldThrowBadPasswordException_WhenPasswordIsBad() {
        try {
            dssedciCertificateService.checkCertificate(certPath, "badPassword");
        } catch (EDCIBadRequestException e) {
            Assert.assertEquals(EDCIMessageKeys.Exception.DSS.LOCAL_CERTIFICATE_BAD_PASSWORD, e.getMessage());
        }
    }

    @Test
    public void checkCertificate_ShouldThrowBadPathException_WhenCertPathIsBad() {
        try {
            dssedciCertificateService.checkCertificate("bad/path/nonexistant.pfx", certPassword);
        } catch (EDCIException e) {
            Assert.assertEquals(EDCIMessageKeys.Exception.DSS.LOCAL_CERTIFICATE_NOT_FOUND, e.getMessage());
        }
    }

    @Test
    public void getCertificate_ShouldBeSameString_WhenCertificateIsSame() throws Exception {
        X509Certificate certificate = dssedciCertificateService.getCertificate(certPath, certPassword);
        Base64.Encoder base64Encoder = Base64.getEncoder();
        String certString = base64Encoder.encodeToString(certificate.getEncoded());
        Assert.assertEquals(addCertificateWrappers(certString).replaceAll("(\r)?\n", ""),
                addCertificateWrappers(encodedCert).replaceAll("(\r)?\n", ""));
    }

    @Test
    public void getCertificateSignatureToken_shouldNotBeNull_WhenCertificateIsValid() {
        SignatureTokenConnection signatureTokenConnection = dssedciCertificateService.getCertificateSignatureToken(certPath, certPassword);
        Assert.assertNotNull(signatureTokenConnection);
    }

    private String addCertificateWrappers(String certificate) {
        return EDCIConstants.Certificate.CERTIFICATE_BEGIN_MARKER + certificate + EDCIConstants.Certificate.CERTIFICATE_END_MARKER;
    }
}
