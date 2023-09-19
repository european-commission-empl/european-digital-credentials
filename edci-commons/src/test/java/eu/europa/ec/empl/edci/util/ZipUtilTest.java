package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.upload.DeliveryDetailsDTO;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import org.junit.Test;
import org.mockito.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ZipUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    @Spy
    private ZipUtil zipUtil;

    @Mock
    private EDCIFileService edciFileService;

    @Mock
    private CredentialUtil credentialUtil;

    @Mock
    private JsonUtil jsonUtil;

    @Mock
    private JsonLdUtil jsonLdUtil;


    @Test
    public void addFilesToZIP_shouldCallMethod() throws Exception {
        String zipFile = "credentials_" + new SimpleDateFormat("yyyyMMdd_hhmm").format(new Date()) + ".zip";
        Map<String, String> map1 = new HashMap<>();
        map1.put("uuid1", "test");

        Map<String, DeliveryDetailsDTO> map2 = new HashMap<>();
        map2.put("uuid1", new DeliveryDetailsDTO());

        Mockito.doCallRealMethod().when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Mockito.doReturn(jsonUtil).when(jsonLdUtil).getJsonUtil();
        Mockito.doReturn("test".getBytes(StandardCharsets.UTF_8)).when(zipUtil).readFileBytes(ArgumentMatchers.any());
        Mockito.doReturn(JsonLdFactoryUtil.getSimpleCredential()).when(credentialUtil).unMarshallCredential(ArgumentMatchers.any(byte[].class));
        Mockito.doReturn(ByteArrayInputStream.nullInputStream()).when(jsonUtil).marshallAsStream(ArgumentMatchers.any());

        try {
            zipUtil.addFilesToZIP("folder", zipFile, map1, map2);
        } finally {
            String path = "folder".concat(zipFile);
            File file = new File(Paths.get(path).normalize().toString());
            file.delete();
        }

        Mockito.verify(credentialUtil, Mockito.times(1)).unMarshallCredential(ArgumentMatchers.any(byte[].class));
        Mockito.verify(edciFileService, Mockito.times(2)).getOrCreateFile(ArgumentMatchers.anyString());
        Mockito.verify(jsonUtil, Mockito.times(1)).marshallAsStream(ArgumentMatchers.any());


    }


}
