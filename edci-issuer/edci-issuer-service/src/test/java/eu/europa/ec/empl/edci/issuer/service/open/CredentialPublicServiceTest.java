package eu.europa.ec.empl.edci.issuer.service.open;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.dss.service.certificate.ESealCertificateService;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialFileDTO;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialUploadResponseDTO;
import eu.europa.ec.empl.edci.issuer.common.model.EuropeanDigitalCredentialUploadResultDTO;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicBatchSealingDTO;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicSealingDTO;
import eu.europa.ec.empl.edci.issuer.service.CredentialService;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.JsonLdFactoryUtil;
import eu.europa.ec.empl.edci.util.WalletResourceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class CredentialPublicServiceTest extends AbstractUnitBaseTest {
    @InjectMocks
    CredentialPublicService credentialPublicService;

    @Mock
    IssuerConfigService issuerConfigService;

    @Mock
    ESealCertificateService eSealCertificateService;

    @Mock
    CredentialService credentialService;

    @Mock
    FileUtil fileUtil;

    @Mock
    EDCIMessageService edciMessageService;

    @Mock
    EDCIFileService edciFileService;

    @Mock
    WalletResourceUtil walletResourceUtil;

    @Test(expected = EDCIException.class)
    public void doBatchSealAndSendCredentials_noCert_shouldThrowException() {

        credentialPublicService.doBatchSealAndSendCredentials("batchId", new PublicBatchSealingDTO(null, "pass", false));
    }

    @Test
    public void doBatchSealAndSendCredentials_shouldReturnNull() throws URISyntaxException {
        EuropeanDigitalCredentialUploadResultDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadResultDTO();
        europeanDigitalCredentialUploadDTO.setCredential(JsonLdFactoryUtil.getSimpleCredential());
        ArrayList<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadResultDTOS = new ArrayList<>();
        europeanDigitalCredentialUploadResultDTOS.add(europeanDigitalCredentialUploadDTO);

        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        MultipartFile[] multipartFiles = {multipartFile};

        CredentialFileDTO f = new CredentialFileDTO();
        f.setValid(true);
        f.setEmail("email@email.com");
        f.setFile(multipartFile);
        f.setCredentials(new ArrayList<>());

        Mockito.doReturn(true).when(eSealCertificateService).checkCertificate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(europeanDigitalCredentialUploadResultDTOS).when(credentialService).obtainCredentials(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doReturn(f).when(credentialService).uploadParsedCredentials(ArgumentMatchers.anyList(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Assert.assertNull(credentialPublicService.doBatchSealAndSendCredentials("batchId", new PublicBatchSealingDTO(multipartFiles, "pass", false)));
    }

    @Test
    public void doBatchSealAndSendCredentials_shouldReturnNotNull() throws URISyntaxException {
        EuropeanDigitalCredentialUploadResultDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadResultDTO();
        europeanDigitalCredentialUploadDTO.setCredential(JsonLdFactoryUtil.getSimpleCredential());
        europeanDigitalCredentialUploadDTO.setErrorAddress(true);
        europeanDigitalCredentialUploadDTO.setSigned(true);
        europeanDigitalCredentialUploadDTO.setBadFormat(true);
        europeanDigitalCredentialUploadDTO.setFileName("fileNameTest");
        ArrayList<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadResultDTOS = new ArrayList<>();
        europeanDigitalCredentialUploadResultDTOS.add(europeanDigitalCredentialUploadDTO);


        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        MultipartFile[] multipartFiles = {multipartFile};

        CredentialFileDTO f = new CredentialFileDTO();
        f.setValid(true);
        f.setEmail("email@email.com");
        f.setFile(multipartFile);
        f.setCredentials(new ArrayList<>());

        Mockito.doReturn(true).when(eSealCertificateService).checkCertificate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(europeanDigitalCredentialUploadResultDTOS).when(credentialService).obtainCredentials(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doReturn(f).when(credentialService).uploadParsedCredentials(ArgumentMatchers.anyList(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any(), ArgumentMatchers.any());

        Assert.assertNotNull(credentialPublicService.doBatchSealAndSendCredentials("batchId", new PublicBatchSealingDTO(multipartFiles, "pass", false)));
    }

    @Test(expected = EDCIException.class)
    public void doSealAndSendCredentials_noCert_shouldThrowException() throws IOException {
        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        credentialPublicService.doSealAndSendCredentials( new PublicSealingDTO(null, "pass", false));
        file.delete();
    }

    @Test(expected = EDCIException.class)
    public void doSealAndSendCredentials_addressLimit_shouldThrowException() throws IOException, URISyntaxException {
        EuropeanDigitalCredentialUploadResultDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadResultDTO();
        europeanDigitalCredentialUploadDTO.setCredential(JsonLdFactoryUtil.getSimpleCredential());
        europeanDigitalCredentialUploadDTO.setErrorAddress(true);
        europeanDigitalCredentialUploadDTO.setSigned(true);
        europeanDigitalCredentialUploadDTO.setBadFormat(true);
        europeanDigitalCredentialUploadDTO.setFileName("fileNameTest");
        ArrayList<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadResultDTOS = new ArrayList<>();
        europeanDigitalCredentialUploadResultDTOS.add(europeanDigitalCredentialUploadDTO);


        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        MultipartFile[] multipartFiles = {multipartFile};

        CredentialFileDTO f = new CredentialFileDTO();
        f.setValid(true);
        f.setEmail("email@email.com");
        f.setFile(multipartFile);
        f.setCredentials(new ArrayList<>());

        Mockito.doReturn(true).when(eSealCertificateService).checkCertificate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(europeanDigitalCredentialUploadResultDTOS).when(credentialService).obtainCredentials(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doReturn(f).when(credentialService).uploadParsedCredentials(ArgumentMatchers.anyList(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any(), ArgumentMatchers.any());

        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        credentialPublicService.doSealAndSendCredentials( new PublicSealingDTO(null, "pass", false));
        file.delete();
    }

    @Test
    public void doSealAndSendCredentials_shouldReturnEmpty() throws IOException, URISyntaxException {
        EuropeanDigitalCredentialUploadResultDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadResultDTO();
        europeanDigitalCredentialUploadDTO.setCredential(JsonLdFactoryUtil.getSimpleCredential());
        europeanDigitalCredentialUploadDTO.setFileName("fileNameTest");
        ArrayList<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadResultDTOS = new ArrayList<>();
        europeanDigitalCredentialUploadResultDTOS.add(europeanDigitalCredentialUploadDTO);


        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        MultipartFile[] multipartFiles = {multipartFile};

        CredentialFileDTO f = new CredentialFileDTO();
        f.setValid(true);
        f.setEmail("email@email.com");
        f.setFile(multipartFile);
        f.setCredentials(new ArrayList<>());

        Mockito.doReturn(true).when(eSealCertificateService).checkCertificate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(europeanDigitalCredentialUploadResultDTOS).when(credentialService).obtainCredentials(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doReturn(f).when(credentialService).uploadParsedCredentials(ArgumentMatchers.anyList(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any(), ArgumentMatchers.any());

        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Assert.assertTrue(credentialPublicService.doSealAndSendCredentials( new PublicSealingDTO(null, "pass", false)).getViewerURL().isEmpty());
        file.delete();
    }

    @Test(expected = EDCIBadRequestException.class)
    public void doSealAndSendCredentials_notFound_shouldThrowException() throws IOException, URISyntaxException {
        EuropeanDigitalCredentialUploadResultDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadResultDTO();
        europeanDigitalCredentialUploadDTO.setCredential(JsonLdFactoryUtil.getSimpleCredential());
        europeanDigitalCredentialUploadDTO.setFileName("fileNameTest");
        ArrayList<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadResultDTOS = new ArrayList<>();
        europeanDigitalCredentialUploadResultDTOS.add(europeanDigitalCredentialUploadDTO);


        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        MultipartFile[] multipartFiles = {multipartFile};

        CredentialFileDTO f = new CredentialFileDTO();
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setUuid("urn:1");
        f.setValid(true);
        f.setEmail("email@email.com");
        f.setFile(multipartFile);
        f.setCredentials(Arrays.asList(credentialDTO));

        Mockito.doReturn(true).when(eSealCertificateService).checkCertificate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(europeanDigitalCredentialUploadResultDTOS).when(credentialService).obtainCredentials(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doReturn(f).when(credentialService).uploadParsedCredentials(ArgumentMatchers.anyList(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(fileUtil).getFileName(ArgumentMatchers.any());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any(), ArgumentMatchers.any());

        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        credentialPublicService.doSealAndSendCredentials( new PublicSealingDTO(null, "pass", false));
        file.delete();
    }

    @Test(expected = EDCIException.class)
    public void doLocalSignOrCreateAndDownloadCredential_noCert_shouldThrowException() throws IOException {
        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        credentialPublicService.doLocalSignOrCreateAndDownloadCredential( new PublicSealingDTO(null, "pass", false), true);
        file.delete();
    }

    @Test(expected = EDCIException.class)
    public void doLocalSignOrCreateAndDownloadCredential_addressLimit_shouldThrowException() throws IOException, URISyntaxException {
        EuropeanDigitalCredentialUploadResultDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadResultDTO();
        europeanDigitalCredentialUploadDTO.setCredential(JsonLdFactoryUtil.getSimpleCredential());
        europeanDigitalCredentialUploadDTO.setErrorAddress(true);
        europeanDigitalCredentialUploadDTO.setFileName("fileNameTest");
        ArrayList<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadResultDTOS = new ArrayList<>();
        europeanDigitalCredentialUploadResultDTOS.add(europeanDigitalCredentialUploadDTO);


        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        MultipartFile[] multipartFiles = {multipartFile};

        CredentialFileDTO f = new CredentialFileDTO();
        f.setValid(true);
        f.setEmail("email@email.com");
        f.setFile(multipartFile);
        f.setCredentials(new ArrayList<>());

        Mockito.doReturn(true).when(eSealCertificateService).checkCertificate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(europeanDigitalCredentialUploadResultDTOS).when(credentialService).obtainCredentials(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doReturn(f).when(credentialService).uploadParsedCredentials(ArgumentMatchers.anyList(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any(), ArgumentMatchers.any());

        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        credentialPublicService.doLocalSignOrCreateAndDownloadCredential( new PublicSealingDTO(null, "pass", false), false);
        file.delete();
    }

    @Test(expected = EDCIException.class)
    public void doLocalSignOrCreateAndDownloadCredential_noCreds_shouldThrowException() throws IOException, URISyntaxException {
        EuropeanDigitalCredentialUploadResultDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadResultDTO();
        europeanDigitalCredentialUploadDTO.setCredential(JsonLdFactoryUtil.getSimpleCredential());
        europeanDigitalCredentialUploadDTO.setFileName("fileNameTest");
        ArrayList<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadResultDTOS = new ArrayList<>();
        europeanDigitalCredentialUploadResultDTOS.add(europeanDigitalCredentialUploadDTO);


        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        MultipartFile[] multipartFiles = {multipartFile};

        CredentialFileDTO f = new CredentialFileDTO();
        f.setValid(true);
        f.setEmail("email@email.com");
        f.setFile(multipartFile);
        f.setCredentials(new ArrayList<>());

        Mockito.doReturn(true).when(eSealCertificateService).checkCertificate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(europeanDigitalCredentialUploadResultDTOS).when(credentialService).obtainCredentials(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doReturn(f).when(credentialService).uploadParsedCredentials(ArgumentMatchers.anyList(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any(), ArgumentMatchers.any());

        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Assert.assertNull(credentialPublicService.doLocalSignOrCreateAndDownloadCredential( new PublicSealingDTO(null, "pass", false),false));
        file.delete();
    }

    @Test(expected = EDCIBadRequestException.class)
    public void doLocalSignOrCreateAndDownloadCredential_notFound_shouldThrowException() throws IOException, URISyntaxException {
        EuropeanDigitalCredentialUploadResultDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadResultDTO();
        europeanDigitalCredentialUploadDTO.setCredential(JsonLdFactoryUtil.getSimpleCredential());
        europeanDigitalCredentialUploadDTO.setFileName("fileNameTest");
        ArrayList<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadResultDTOS = new ArrayList<>();
        europeanDigitalCredentialUploadResultDTOS.add(europeanDigitalCredentialUploadDTO);


        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        MultipartFile[] multipartFiles = {multipartFile};

        CredentialFileDTO f = new CredentialFileDTO();
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setUuid("urn:1");
        f.setValid(true);
        f.setEmail("email@email.com");
        f.setFile(multipartFile);
        f.setCredentials(Arrays.asList(credentialDTO));

        Mockito.doReturn(true).when(eSealCertificateService).checkCertificate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any());
        Mockito.doReturn(2).when(issuerConfigService).getInteger(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(europeanDigitalCredentialUploadResultDTOS).when(credentialService).obtainCredentials(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doReturn(f).when(credentialService).uploadParsedCredentials(ArgumentMatchers.anyList(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(fileUtil).getFileName(ArgumentMatchers.any());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any());
        Mockito.doReturn("test").when(edciMessageService).getMessage(ArgumentMatchers.any(), ArgumentMatchers.any());

        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        credentialPublicService.doLocalSignOrCreateAndDownloadCredential( new PublicSealingDTO(null, "pass", false), false);
        file.delete();
    }

    @Test(expected = EDCIBadRequestException.class)
    public void localSignAndSendCredential_noDeliveryAddress_shouldThrowException() throws IOException {
        PublicSealingDTO publicSealingDTO = new PublicSealingDTO(null, "pass", false);
        publicSealingDTO.setCredential(new CredentialDTO());
        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());
        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());

        credentialPublicService.localSignAndSendCredential(publicSealingDTO , "batchId");
        file.delete();
    }

    @Test(expected = EDCIException.class)
    public void localSignAndSendCredential_shouldThrowException() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        PublicSealingDTO publicSealingDTO = new PublicSealingDTO(multipartFile, "pass", false);
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setEmail(Arrays.asList("email@email.com"));
        credentialDTO.setWalletAddress(Arrays.asList("wallet"));
        publicSealingDTO.setCredential(credentialDTO);
        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());

        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doThrow(new EDCIBadRequestException()).when(walletResourceUtil).doWalletPostRequest(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyMap());

        credentialPublicService.localSignAndSendCredential(publicSealingDTO , "batchId");
        file.delete();
    }

    @Test
    public void localSignAndSendCredential_shouldReturnNotNull() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        PublicSealingDTO publicSealingDTO = new PublicSealingDTO(multipartFile, "pass", false);
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setEmail(Arrays.asList("email@emai.com"));
        credentialDTO.setWalletAddress(Arrays.asList("wallet"));
        publicSealingDTO.setCredential(credentialDTO);
        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());

        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(new CredentialUploadResponseDTO()).when(walletResourceUtil).doWalletPostRequest(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyMap());

        Assert.assertNotNull(credentialPublicService.localSignAndSendCredential(publicSealingDTO , "batchId"));
        file.delete();
    }

    @Test(expected = EDCIException.class)
    public void localSignAndSendCredential_badEmailFormat_shouldThrowException() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8));
        PublicSealingDTO publicSealingDTO = new PublicSealingDTO(multipartFile, "pass", false);
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setEmail(Arrays.asList("email"));
        credentialDTO.setWalletAddress(Arrays.asList("wallet"));
        publicSealingDTO.setCredential(credentialDTO);
        String path = "folder".concat("test");
        File file = new File(Paths.get(path).normalize().toString());

        Mockito.doReturn("test").when(fileUtil).getCredentialPublicFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(file).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn("test").when(issuerConfigService).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(new CredentialUploadResponseDTO()).when(walletResourceUtil).doWalletPostRequest(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyMap());

        credentialPublicService.localSignAndSendCredential(publicSealingDTO , "batchId");
        file.delete();
    }

}