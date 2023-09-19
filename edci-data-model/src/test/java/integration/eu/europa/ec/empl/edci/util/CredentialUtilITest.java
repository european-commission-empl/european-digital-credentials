package integration.eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CredentialUtilITest {

    @Spy
    private CredentialUtil credentialUtil;

    @Spy
    private JsonLdUtil jsonLdUtil;

    private String cred1_jsonld = "src/test/resources/jsonld/cred1.jsonld";
    private String signedCredential = "src/test/resources/jsonld/signed_credential.jsonld";
    private String jsonCredential;

    @Before
    public void setUp() throws IOException {
        Mockito.lenient().when(credentialUtil.getJsonLdUtil()).thenReturn(jsonLdUtil);
        this.jsonCredential = Files.readString(Paths.get(cred1_jsonld));
    }

    @Test
    public void givenEmptyCredentialThenResultIsFalse() {
        ValidationResult validationResult = credentialUtil.validateCredential("");
        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void givenCredentialThenResultIsFalse() {
        ValidationResult validationResult = credentialUtil.validateCredential(this.jsonCredential);
        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void givenCredentialWithSHACLThenResultIsFalse() {
        Set<String> shacls = new HashSet<>();
        shacls.add("http://data.europa.eu/snb/model/ap/edc-generic-full");

        ValidationResult validationResult = credentialUtil.validateCredential(this.jsonCredential, shacls);
        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void getPayload_shouldGetCredentialString_whenUsingSignedCred() throws IOException, ParseException {
        byte[] credentialFile = Files.readAllBytes(Paths.get(signedCredential));
        String credentialString = credentialUtil.getCredentialOrPayload(credentialFile);
        Assert.assertTrue(credentialString.startsWith(DataModelConstants.StringPool.STRING_OPEN_BRACKET));
    }

    @Test
    public void validateSignedRDF_withMalFormedSubjects_shouldThrowException() throws IOException, ParseException {
        byte[] credentialFile = Files.readAllBytes(Paths.get(signedCredential));
        String credentialString = credentialUtil.getCredentialOrPayload(credentialFile);
        ValidationResult validationResult = credentialUtil.validateCredential(credentialString);
        Assert.assertFalse(validationResult.isValid());
    }


}
