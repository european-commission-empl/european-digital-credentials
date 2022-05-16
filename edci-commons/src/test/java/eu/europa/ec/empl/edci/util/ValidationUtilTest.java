package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.LearningAchievementDTO;
import eu.europa.ec.empl.edci.datamodel.model.LearningActivityDTO;
import eu.europa.ec.empl.edci.datamodel.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.EDCIValidationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.context.MessageSource;

import java.net.URI;
import java.util.Arrays;

import static org.mockito.Mockito.lenient;

//TODO: Review
public class ValidationUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    protected EDCIValidationService europassValidationUtil;

    @Spy
    private EDCIMessageService edciMessageService;

    @Spy
    private MessageSource messageSource;

    @Spy
    private EDCIValidationUtil edciValidationUtil;

    @Spy
    private Validator validator;

    @Spy
    private ReflectiveUtil reflectiveUtil;


    @Before
    public void injectDependencies() {
        lenient().when(edciMessageService.getMessageSource()).thenReturn(messageSource);
        lenient().when(edciValidationUtil.getValidator()).thenReturn(validator);
        lenient().when(edciValidationUtil.getReflectiveUtil()).thenReturn(reflectiveUtil);
        lenient().when(reflectiveUtil.getValidator()).thenReturn(validator);
        lenient().when(edciValidationUtil.getEdciMessageService()).thenReturn(edciMessageService);
    }

    @Test
    public void validateEuropassCredentialDTO_ShouldBeInvalid() {
        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();
        europassCredentialDTO.setId(URI.create("urn:epass:1"));

        PersonDTO credentialSubject = new PersonDTO();
        credentialSubject.setAchieved(Arrays.asList(new LearningAchievementDTO()));
        credentialSubject.setPerformed(Arrays.asList(new LearningActivityDTO()));
        europassCredentialDTO.setCredentialSubject(credentialSubject);

        ValidationResult validationResult = europassValidationUtil.validate(europassCredentialDTO);

        System.out.println(validationResult.getErrorKeys());

        Assert.assertFalse(validationResult.isValid());


    }

}
