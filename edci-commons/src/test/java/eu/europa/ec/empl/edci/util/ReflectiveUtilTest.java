package eu.europa.ec.empl.edci.util;


import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

public class ReflectiveUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    ReflectiveUtil reflectiveUtil;

    @Spy
    public Validator validator;

    public final List<String> emptyFieldList = new ArrayList<>();
    public final List<String> filledFieldList = new ArrayList<String>() {{
        add("one");
        add("two");
    }};
    public final String aField = "aField";
    public final String nullField = null;

    @Before
    public void injectDependencies() {
        reflectiveUtil.setValidator(validator);
    }

    @Test
    public void isListInstance_shouldReturnTrue_whenFieldIsList() throws Exception {

        Boolean returnBoolean = reflectiveUtil.isListInstance(ReflectiveUtilTest.class.getField("emptyFieldList"));

        Assert.assertTrue(returnBoolean);
    }

    @Test
    public void isListInstance_shouldReturnFalse_whenFieldIsList() throws Exception {

        Boolean returnBoolean = reflectiveUtil.isListInstance(ReflectiveUtilTest.class.getField("aField"));

        Assert.assertFalse(returnBoolean);
    }

    @Test
    public void getOrInstanciateListItem_shouldReturnListObject_whenListHasIndexElement() throws Exception {

        Object returnValue = reflectiveUtil.getOrInstanciateListItem(filledFieldList, 1,
                ReflectiveUtilTest.class.getField("aField"));

        Assert.assertEquals(filledFieldList.get(1), returnValue);
    }

    @Test
    public void setField_shouldSetFieldValue_whenObjectHasField() throws Exception {

        Note note = new Note();

        boolean ok = reflectiveUtil.setField("topic", note, "Note");

        Assert.assertEquals("Note", note.getTopic());
    }

    @Test
    public void tryCreationMethod_shouldCreateAnObject_givenTheCreationMethodAndValue() throws Exception {

        Note note = new Note();

        Long value = (Long) reflectiveUtil.tryCreationMethod(Long.class, "valueOf", "10");

        Assert.assertEquals(Long.valueOf("10"), value);
    }

}
