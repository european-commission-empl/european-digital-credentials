package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LearningActSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import org.junit.Assert;

import java.util.HashSet;

public class EmptiableTest {

    public void isEmpty_shouldReturnTrue_WhenAllIsNull() throws Exception {

        LearningActSpecificationDCDAO las = new LearningActSpecificationDCDAO();

        Assert.assertTrue(las.isEmpty());

    }

    public void isEmpty_shouldReturnTrue_WhenIgnorableFieldsAreNotEmpty() throws Exception {

        LearningActSpecificationDCDAO las = new LearningActSpecificationDCDAO();
        las.setPk(1L);
        Assert.assertTrue(las.isEmpty());

    }

    public void isEmpty_shouldReturnTrue_WhenAllIsNullAndCollectionsAreEmpty() throws Exception {

        LearningActSpecificationDCDAO las = new LearningActSpecificationDCDAO();

        las.setWorkload(null);
        las.setIdentifier(new HashSet<>());

        Assert.assertTrue(las.isEmpty());

    }

    public void isEmpty_shouldReturnFalse_WhenAFieldIsNotNull() throws Exception {

        LearningActSpecificationDCDAO las = new LearningActSpecificationDCDAO();

        las.setDescription(new NoteDTDAO());

        Assert.assertFalse(las.isEmpty());

    }

    public void isEmpty_shouldReturnFalse_WhenACollectionIsNotEmpty() throws Exception {

        LearningActSpecificationDCDAO las = new LearningActSpecificationDCDAO();

        las.setDescription(null);
        las.setIdentifier(new HashSet<IdentifierDTDAO>() {{
            add(new IdentifierDTDAO());
        }});

        Assert.assertFalse(las.isEmpty());

    }

}
