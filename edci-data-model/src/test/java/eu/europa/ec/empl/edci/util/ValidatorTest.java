package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ValidatorTest {

    @InjectMocks
    @Spy
    protected Validator validator;


    @Test
    public void notEmpty_ShouldCallMethod() {
        Mockito.doReturn(true).when(validator).isEmpty(ArgumentMatchers.any());
        validator.notEmpty(new EuropeanDigitalCredentialDTO());

        Mockito.verify(validator,Mockito.times(1)).isEmpty(ArgumentMatchers.any());

    }

    @Test
    public void isEmpty_ShouldReturnTrue() {
        Assert.assertTrue(validator.isEmpty(null));
    }

    @Test
    public void isEmpty_ShouldReturnTrueOptional() {
        Assert.assertTrue(validator.isEmpty(Optional.empty()));
    }

    @Test
    public void isEmpty_ShouldReturnTrueLong() {
        Assert.assertTrue(validator.isEmpty(0L));
    }

    @Test
    public void isEmpty_ShouldReturnTrueCharSequence() {
        CharSequence charSequence = null;
        Assert.assertTrue(validator.isEmpty(charSequence));
    }

    @Test
    public void isEmpty_ShouldReturnTrueString() {
        String charSequence = "";
        Assert.assertTrue(validator.isEmpty(charSequence));
    }

    @Test
    public void isEmpty_ShouldReturnFalseString() {
        String charSequence = "test";
        Assert.assertFalse(validator.isEmpty(charSequence));
    }

    @Test
    public void isEmpty_ShouldReturnTrueArray() {
        Assert.assertTrue(validator.isEmpty(new ArrayList<>()));
    }

    @Test
    public void isEmpty_ShouldReturnTrueMap() {
        Assert.assertTrue(validator.isEmpty(new HashMap<>()));
    }

    @Test
    public void isEmpty_ShouldReturnFalseArray() {
        Assert.assertFalse(validator.isEmpty(Arrays.asList(new EuropeanDigitalCredentialDTO())));
    }

    @Test
    public void isEmpty_ShouldReturnFalseMap() {
        Map<String, String> map = new HashMap<>();
        map.put("test", "test");
        Assert.assertFalse(validator.isEmpty(map));
    }

    @Test
    public void getValueNullSafe_ShouldCallMethod() {
        Mockito.doReturn(true).when(validator).getValueNullSafe(ArgumentMatchers.any(), ArgumentMatchers.any());
        validator.getValueNullSafe(() -> new EuropeanDigitalCredentialDTO());

        Mockito.verify(validator,Mockito.times(1)).getValueNullSafe(ArgumentMatchers.any(), ArgumentMatchers.any());;

    }

    @Test
    public void getValueNullSafe_AlternativeValue_ShouldCallMethod() {
        Assert.assertNotNull(validator.getValueNullSafe(() -> new EuropeanDigitalCredentialDTO(), null));
    }

    @Test
    public void getValueNullSafe_AlternativeValue_ShouldReturnAlternative() {
        Assert.assertEquals("altValue", validator.getValueNullSafe(() -> null, "altValue"));
    }

    @Test
    public void getValueNullSafe_AlternativeValue_ShouldReturnNull() {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = null;
        Assert.assertNull(validator.getValueNullSafe(() -> europeanDigitalCredentialDTO.getCredentialProfiles(), "altValue"));
    }

    @Test
    public void isNotNull_ShouldReturnFalse() {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = null;
        Assert.assertFalse(validator.isNotNull(() -> europeanDigitalCredentialDTO.getCredentialProfiles()));
    }

    @Test
    public void isNotNull_ShouldReturnTrue() {
        Assert.assertTrue(validator.isNotNull(() -> new EuropeanDigitalCredentialDTO()));
    }

}
