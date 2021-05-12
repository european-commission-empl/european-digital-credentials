package eu.europa.ec.empl.edci.issuer.service;


import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.issuer.common.model.ELementCLBasicDTO;
import eu.europa.ec.empl.edci.issuer.service.spec.ControlledListsOldService;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FileServiceTest extends AbstractUnitBaseTest {

    @InjectMocks
    FileService fileService;

    @Spy
    FileUtil fileUtil;

    @Spy
    ControlledListsOldService controlledListsService;

    @Mock
    private ControlledListCommonsService controlledListCommonsService;

    @Before
    public void injectMockObjects() throws Exception {
        List<RDFConcept> elementCLDAOS = Arrays.asList(testUtilities.createMockObject(RDFConcept.class), testUtilities.createMockObject(RDFConcept.class));
        Mockito.lenient().when(controlledListCommonsService.searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyList())).thenReturn(elementCLDAOS);
    }


    @Test
    public void test_getTargetNames_shouldReturnEmptyStringList() throws Exception {
        Set<ELementCLBasicDTO> templates = fileService.getAvailableTemplates();
        Assert.assertTrue(templates.size() == 0);
    }
}


