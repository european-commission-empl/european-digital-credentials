package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Association;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialHashDTO;
import eu.europa.ec.empl.edci.issuer.common.model.StatusDTO;
import eu.europa.ec.empl.edci.issuer.service.dss.SigningService;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;

public class CredentialServiceTest extends AbstractUnitBaseTest {
    @InjectMocks
    CredentialService credentialService;

    @InjectMocks
    AssociationService associationService;

    @InjectMocks
    SigningService signingService;

    @Mock
    private CredentialHashDTO credentialHashDTO;

    @Mock
    private FileUtil fileUtil;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();


    @Test
    public void deleteCredentials_shouldDeleteFile_obtainedByUUID() throws IOException {

        //Test preparation
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        File file = tmpFolder.newFile("testFile");
        Assert.assertTrue("Problem with TemporaryFolder rule preparing test input files", file.exists());

        doReturn(file.getAbsolutePath()).when(fileUtil).getCredentialFileAbsolutePath(anyString(), anyString());

        //Test execution
        StatusDTO saved = credentialService.deleteCredentials("uuid-1");

        //Test verifications
        Assert.assertFalse("The credentials file has not been deleted", file.exists());
    }

    @Test
    public void checkRecursiveness_test1() {
        List<Association> list = new ArrayList<Association>();
        Association association1 = new Association("AchievementDTO", 1, "hasPart", "AchievementDTO", 2);
        Association association2 = new Association("AchievementDTO", 2, "hasPart", "AchievementDTO", 3);
        list.add(association1);
        list.add(association2);

        Assert.assertFalse(associationService.validateAssociations(list));
    }

    // @Test
    public void checkRecursiveness_test2() {
        List<Association> list = new ArrayList<Association>();
        Association association1 = new Association("LearningActivity", 1, "specifiedBy", "LearningActivitySpecification", 1);
        Association association2 = new Association("LearningActivitySpecification", 1, "hasPart", "LearningActivitySpecification", 2);
        Association association3 = new Association("LearningActivitySpecification", 2, "hasPart", "LearningActivitySpecification", 3);
        Association association4 = new Association("LearningActivitySpecification", 3, "specializationOf", "LearningActivitySpecification", 1);
        list.add(association1);
        list.add(association2);
        list.add(association3);
        list.add(association4);

        Assert.assertTrue(associationService.validateAssociations(list));
    }

    // @Test
    public void checkRecursiveness_test3() {
        List<Association> list = new ArrayList<Association>();
        Association association1 = new Association("AchievementDTO", 1, "hasPart", "AchievementDTO", 2);
        Association association2 = new Association("AchievementDTO", 2, "hasPart", "AchievementDTO", 1);
        list.add(association1);
        list.add(association2);

        Assert.assertTrue(associationService.validateAssociations(list));
    }

    //  @Test
    public void checkRecursiveness_test4() {
        List<Association> list = new ArrayList<Association>();
        Association association1 = new Association("AchievementDTO", 1, "hasPart", "AchievementDTO", 2);
        Association association2 = new Association("AchievementDTO", 2, "hasPart", "AchievementDTO", 3);
        Association association3 = new Association("AchievementDTO", 3, "hasPart", "AchievementDTO", 1);
        list.add(association1);
        list.add(association2);
        list.add(association3);

        Assert.assertTrue(associationService.validateAssociations(list));
    }

    // @Test
    public void checkRecursiveness_test5() {
        List<Association> list = new ArrayList<Association>();
        Association association1 = new Association("AchievementDTO", 1, "hasNext", "AchievementDTO", 2);
        Association association2 = new Association("AchievementDTO", 2, "hasNext", "AchievementDTO", 3);
        Association association3 = new Association("AchievementDTO", 4, "hasNext", "AchievementDTO", 1);
        Association association4 = new Association("AchievementDTO", 1, "hasNext", "AchievementDTO", 5);
        Association association5 = new Association("AchievementDTO", 5, "hasNext", "AchievementDTO", 6);
        Association association6 = new Association("AchievementDTO", 6, "hasNext", "AchievementDTO", 7);
        Association association7 = new Association("AchievementDTO", 3, "hasNext", "AchievementDTO", 7);
        Association association8 = new Association("AchievementDTO", 3, "hasNext", "AchievementDTO", 1);
        Association association9 = new Association("AchievementDTO", 7, "hasNext", "AchievementDTO", 2);
        Association association10 = new Association("AchievementDTO", 9, "hasNext", "AchievementDTO", 10);
        list.add(association1);
        list.add(association2);
        list.add(association3);
        list.add(association4);
        list.add(association5);
        list.add(association6);
        list.add(association7);
        list.add(association8);
        list.add(association9);
        list.add(association10);

        Assert.assertTrue(associationService.validateAssociations(list));
    }


//    @Test
//    public void isCredentialSigned_shouldReturnTrue_whenXMLIsSigned() throws Exception {
//
//        // GET document to be signed -
//        // Return DSSDocument toSignDocument
//        InputStream xmlDoc = new FileInputStream(new File("src/test/resources/signCredential/Credential_EPAS_476.xml"));
//
//        Assert.assertTrue(credentialService.isCredentialSigned(IOUtils.toByteArray(xmlDoc)));
//
//    }
//
//    @Test
//    public void isCredentialSigned_shouldReturnFalse_whenXMLIsNotSigned() throws Exception {
//
//        // GET document to be signed -
//        // Return DSSDocument toSignDocument
//        InputStream xmlDoc = new FileInputStream(new File("src/test/resources/signCredential/Credential_EPAS_476_unsigned.xml"));
//
//        Assert.assertFalse(credentialService.isCredentialSigned(IOUtils.toByteArray(xmlDoc)));
//
//    }
//
//
//    @Test
//    public void validateCredentialXML_shouldEndOK_whenXMLSignedIsSigned() throws Exception {
//
//        // GET document to be signed -
//        // Return DSSDocument toSignDocument
//        InputStream xmlDoc = new FileInputStream(new File("src/test/resources/signCredential/Credential_EPAS_476.xml"));
//
//        credentialService.validateCredentialXML(IOUtils.toByteArray(xmlDoc), true);
//
//    }
//
//    @Test
//    public void validateCredentialXML_shouldEndOK_whenXMLUnsignedIsUnsigned() throws Exception {
//
//        // GET document to be signed -
//        // Return DSSDocument toSignDocument
//        InputStream xmlDoc = new FileInputStream(new File("src/test/resources/signCredential/Credential_EPAS_476_unsigned.xml"));
//
//        credentialService.validateCredentialXML(IOUtils.toByteArray(xmlDoc), false);
//
//    }
//
//
//    @Test(expected = RuntimeException.class)
//    public void validateCredentialXML_shouldThrowBadRequestExcpetion_whenXMLIsSigned() throws Exception {
//
//        // GET document to be signed -
//        // Return DSSDocument toSignDocument
//        InputStream xmlDoc = new FileInputStream(new File("src/test/resources/signCredential/Credential_EPAS_476.xml"));
//
//        credentialService.validateCredentialXML(IOUtils.toByteArray(xmlDoc), false);
//
//    }
//
//    @Test(expected = RuntimeException.class)
//    public void validateCredentialXML_shouldThrowBadRequestExcpetion_whenXMLIsNotValid() throws Exception {
//
//        // GET document to be signed -
//        // Return DSSDocument toSignDocument
//        InputStream xmlDoc = new FileInputStream(new File("src/test/resources/signCredential/Credential_EPAS_476_error.xml"));
//
//        credentialService.validateCredentialXML(IOUtils.toByteArray(xmlDoc), false);
//
//    }

//    private File file = new File("src/test/resources/signCredential/credential-1.xml");
//    private File fileTwo = new File("src/test/resources/signCredential/credential-2.xml");
//
//    @Test
//    public void getDataToSign_shouldGenerateTwoDifferentData() throws IOException {
//
//
//        SignatureParametersDTO signatureParametersDTO = new SignatureParametersDTO();
//        SignatureParametersResponseDTO responseDTO = new SignatureParametersResponseDTO();
//        responseDTO.setCertificate("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\u003d\u003d");
//        responseDTO.setEncryptionAlgorithm("RSA");
//        List<String> certChain = new ArrayList<>();
//        certChain.add("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\u003d\u003d");
//        certChain.add("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\u003d");
//        certChain.add("MIIFCTCCAvGgAwIBAgIQEVFQTdnrIJxFtTuoAPxmsjANBgkqhkiG9w0BAQsFADAXMRUwEwYDVQQDEwxldmVyaXNSb290Q0EwHhcNMTcwNTMwMTMxOTEyWhcNMjIwNTMwMTMyOTEwWjAXMRUwEwYDVQQDEwxldmVyaXNSb290Q0EwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQDBeLqB+GeJKGu0gFJOkaQsTSQioDyYvSKgkpRAXoF/eoByZ8rwz7BfryPE+lr3ZfWS+PSkjdyEFKdI9j1kSEdadT43cbGNQxIhFR0FRF0dfTAaXhNSuf7mg41fbXKp7ivno3vVCJz9g6RwPzXrBn9BPnf+bMFAAIc2csqcMxJnige/a7reQvsJMHIRVILYCBw6tzKgph2/JzeSDjM0FQz4+K+iWYZFLPWAqle5Sj3ISydTdqAb9kgFpL2bTQUkYh5FQHVMsyViM59l5HnFZ0Xk7FpT89sA2D9xA6vPn+SFDlM6xE3qIYOixsDLljd7gBZ8kYZUFX8QVNFyh2ko5iXCHVisypAEEC8SbRRsPnv1rDQITBTCTxC8ub/SWFA41FM4rp9nygqS6hnWXmd2Ny+zMvfJh7VuGd2cSdJsApuBufd0GFqqV68zOmUATRDVn39VMxB1YAhZQmamp7VI4SbDIanjQaPs2X377JhLf2o0NoNzukmYN0zAmoT1+UKOMmDYVk9d7x2769pamIe+JMc64EEg7Zh++mBwdrSNAClCBAbzNmcAdJ3xDKC8YRRsU8Gu3zuYn6I3/dci1A+hdli9Z5AfmvIv8blpL6Uzg162ZH/rujwpqwxb3EHiuqE82ue75VeF2GrJhTwWc4Hdc125DW3A2yYQMCQfAcmWI8yTZQIDAQABo1EwTzALBgNVHQ8EBAMCAYYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQU5zpc9zeO1DwTwTr/M3dKoZSPo+MwEAYJKwYBBAGCNxUBBAMCAQAwDQYJKoZIhvcNAQELBQADggIBADMQmYzRGFE66LAlJkDNaq1aXq0edTy1t12TJbODzoqODu/zgxTYqi7/eqP16ZxA88GtkYvd/9HfogrqsYn6Uc2AjUycM77pO6fvPawUg/wtSrZGv0v9ATOUxeI4tpyZNX8CpVsdOB52YvjBZoljz5vIaJMyBy09etzs0uLYv6yZfFc5yijgQqmO5w0/ciwkRoOsqpDIZf8ig8aIKjhUCLrE9zZVSuVRNZSWgaqIJNaExZm3Ku4QcloceRsf+5Mx5kOEFgAjpskF/Iyj2q2PuhvVxxuQwtThVbFY40R29Nr3c51y4aCJDg5wGH00if3SHtm1O6XJAgP98FRi7Va95tQv7K6sPqSlnleKUJDqDq8fA/OhfNMdK9LqXfWCd9naY5oL6rHKcBCVO+k7Sk6KAoGSihLHnLCbhT/3eG1ClGKhWewksV4zMldhJM6It785KivE1Tf6U1XfwYcgGZFk6mM4CiafZWZVN4yCzhzw4JgUMwLP0DmXTPTnx7LPjlTfr0y9ONQxyb/C0bbJxo+QchQoGteZ8pRbFUFzZUSLzIlVGmeGlnl3EY9uro+GbPWrbRhLczhrU1Qeu12CAMKw7cm/8/Xtjt7V1v6cDLrz48DhPV9wX962HjN4Fo5QHWYjATp3I7hlJaP8GmpEWA92cmidA9x4UAQwkCRqUJUFSvjE");
//        responseDTO.setCertificateChain(certChain);
//        signatureParametersDTO.setResponse(responseDTO);
//        List<String> uuids = new ArrayList<>();
//        uuids.add("1");
//        uuids.add("2");
//        signatureParametersDTO.setUuids(uuids);
//        //SignatureBytesDTO signatureBytesDTO = credentialService.getSignatureBytes(signatureParametersDTO);
//
//
//        // HARDCODE PARAMS
//        DSSSignatureDocumentForm form = new DSSSignatureDocumentForm();
//        form.setContainerType(null);
//        form.setSigningDate(new Date());
//        form.setSignatureForm(SignatureForm.XAdES);
//        form.setSignaturePackaging(SignaturePackaging.ENVELOPED);
//        form.setSignatureLevel(SignatureLevel.XAdES_BASELINE_LT);
//        form.setDigestAlgorithm(DigestAlgorithm.SHA256);
//        form.setBase64Certificate(signatureParametersDTO.getResponse().getCertificate());
//        form.setBase64CertificateChain(signatureParametersDTO.getResponse().getCertificateChain());
//
//
//        Map<String, String> uuidBytes = new HashMap<>();
//
//        // FILE ONE
//        form.setDocumentToSign(file);
//
//        // NEED TO IMPLEMENT A XAdES SERVICE WITH A VALID TSPSOURCE FIRST
//        if (false) {
//            form.setContentTimestamp(DSSSignatureUtils.fromTimestampToken(signingService.getContentTimestamp(form)));
//        }
//        ToBeSigned dataToSign = signingService.getDataToSign(form, false);
//        uuidBytes.put("1", DatatypeConverter.printBase64Binary(dataToSign.getBytes()));
//        System.out.println(uuidBytes.get("1"));
//        //FILE TWO
//        form.setDocumentToSign(fileTwo);
//
//        // NEED TO IMPLEMENT A XAdES SERVICE WITH A VALID TSPSOURCE FIRST
//        if (false) {
//            form.setContentTimestamp(DSSSignatureUtils.fromTimestampToken(signingService.getContentTimestamp(form)));
//        }
//        ToBeSigned dataToSignTwo = signingService.getDataToSign(form, false);
//        uuidBytes.put("2", DatatypeConverter.printBase64Binary(dataToSignTwo.getBytes()));
//        System.out.println(uuidBytes.get("2"));
//        System.out.println("data is " + (uuidBytes.get("2").equals(uuidBytes.get("1")) ? "equal" : "not equal"));
//        //Assert.assertEquals(uuidBytes.get("1"), uuidBytes.get("2"));
//        Assert.assertFalse(uuidBytes.get("1").equals(uuidBytes.get("2")));
//    }

}