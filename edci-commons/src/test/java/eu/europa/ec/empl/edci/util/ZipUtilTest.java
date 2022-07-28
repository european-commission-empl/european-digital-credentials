package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Content;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import org.eclipse.persistence.tools.file.FileUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class ZipUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    private ZipUtil zipUtil;

    @Spy
    private EDCIFileService edciFileService;

    @Mock
    private FileUtil fileUtil;

    @Test
    public void generateZIPFile_shouldPutAllListedFiles_ById() throws IOException {

        List<String> list = new ArrayList<>();
        list.add("credential-045cb3fe-1445-4766-9fdd-dfbbaa38550e.xml");
        list.add("credential-045cb3fe-1445-4766-9fdd-dfbbaa38550d.xml");

        zipUtil.addfilesToZIP("src/test/resources/zipFiles", "credential-20210202_010101.zip",
                list);

        Assert.assertTrue(this.edciFileService.getOrCreateFile("src/test/resources/zipFiles/credential-20210202_010101.zip").exists());

    }
}
