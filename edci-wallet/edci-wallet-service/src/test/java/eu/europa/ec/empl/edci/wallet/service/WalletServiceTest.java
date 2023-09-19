package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletConfig;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.mapper.WalletMapper;
import eu.europa.ec.empl.edci.wallet.repository.WalletRepository;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialStorageUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalletServiceTest extends AbstractUnitBaseTest {

    @Spy
    @InjectMocks
    WalletService walletService;

    @Mock
    private WalletConfigService walletConfigService;

    @Spy
    private WalletMapper walletMapper;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private EDCIFileService edciFileService;

    @Mock
    EDCIMessageService edciMessageService;

    @Mock
    private CredentialStorageUtil credentialStorageUtil;

    @Mock
    private File fileOrFolder;

    @Before
    public void injectDependencies() throws Exception {

        Mockito.when(walletConfigService.getString("wallet.address.prefix")).thenReturn("walletAddressPrefix");
        Mockito.when(walletConfigService.getString("data.wallets.location")).thenReturn("${catalina.home}/temp/");
        Mockito.when(walletConfigService.getString("data.wallets.root.folder")).thenReturn("wallets/");
        Mockito.when(edciFileService.getOrCreateFile(Matchers.any())).thenReturn(new File("test"));
    }

    @Test
    public void updateWalletByWalletAddress_shouldUpdateAWallet_whenEmailIsNotUsedAndAddressExists() throws Exception {

        WalletDAO walletDao = new WalletDAO();
        WalletDTO walletDto = new WalletDTO();
        walletDto.setUserEmail("someEmail");

        Mockito.doReturn(walletDao).when(walletService).fetchWalletDAOByWalletAddress(ArgumentMatchers.anyString());
        Mockito.doReturn(walletDto).when(walletMapper).toDTO(ArgumentMatchers.any(), ArgumentMatchers.any());

        WalletDTO aux = walletService.updateWalletByWalletAddress("someWalletAddress", "someEmail");

        Mockito.verify(walletRepository).save(ArgumentMatchers.any(WalletDAO.class));
        Assert.assertNotNull(aux);
        Assert.assertEquals("someEmail", aux.getUserEmail());

    }

    @Test(expected = EDCIException.class)
    public void updateWalletByWalletAddress_shouldThowAnException_whenWalletAddressDoesntExist() throws Exception {

        WalletDAO walletDao = new WalletDAO();
        WalletDTO walletDto = new WalletDTO();
        walletDto.setUserEmail("someEmail");

        Mockito.doThrow(new EDCIException()).when(walletService).fetchWalletDAOByWalletAddress(ArgumentMatchers.anyString());

        WalletDTO aux = walletService.updateWalletByWalletAddress("someWalletAddress", "someEmail");

    }

    @Test
    public void createBulkWallet_shouldReturnEmtyListIfNoErrors_whenInformationIsProvided() throws Exception {

        List<String> walletEmailList = new ArrayList<>();
        walletEmailList.add("someEmail@email.com");

        WalletDAO walletDao = new WalletDAO();
        WalletDTO walletDto = new WalletDTO();
        walletDto.setUserEmail("someEmail@email.com");

        Mockito.doReturn(walletDao).when(walletService).addBulkWalletEntity(ArgumentMatchers.anyString());
        Mockito.doReturn(walletDto).when(walletMapper).toDTO(ArgumentMatchers.any(), ArgumentMatchers.any());

        List<WalletDTO> walletDTOList = walletService.createBulkWallet(walletEmailList);

        Assert.assertTrue(walletDTOList.isEmpty());

    }

    @Test
    public void createBulkWallet_shouldReturnErrorList_whenErrorsAreThrown() throws Exception {

        List<String> walletEmailList = new ArrayList<>();
        walletEmailList.add("someEmail@email.com");

        WalletDTO walletDto = new WalletDTO();
        walletDto.setUserEmail("someEmail@email.com");

        Mockito.doThrow(new EDCIException()).when(walletService).addBulkWalletEntity(ArgumentMatchers.anyString());

        List<WalletDTO> walletDTOList = walletService.createBulkWallet(walletEmailList);

        Assert.assertFalse(walletDTOList.isEmpty());

    }

    @Test
    public void deleteWalletByWalletAddress_shouldDeleteWallet_whenExistingOneIsProvided() throws Exception {

        List<String> walletEmailList = new ArrayList<>();
        walletEmailList.add("someEmail@email.com");

        WalletDAO walletDao = new WalletDAO();

        Mockito.doReturn(true).when(walletService).isValidWalletAddress(ArgumentMatchers.anyString());
        Mockito.doReturn(walletDao).when(walletService).fetchWalletDAOByWalletAddress(ArgumentMatchers.anyString());
        Mockito.doNothing().when(walletService).deleteWalletEntity(ArgumentMatchers.any());

        walletService.deleteWalletByWalletAddress("some/wallet/address");

        Mockito.verify(walletService).deleteWalletEntity(ArgumentMatchers.any());

    }

    @Test
    public void deleteWalletByWalletAddress_shouldVerifyIfWalletExists_whenOneIsProvided() throws Exception {

        List<String> walletEmailList = new ArrayList<>();
        walletEmailList.add("someEmail@email.com");

        WalletDAO walletDao = new WalletDAO();

        Mockito.doReturn(true).when(walletService).isValidWalletAddress(ArgumentMatchers.anyString());
        Mockito.doReturn(walletDao).when(walletService).fetchWalletDAOByWalletAddress(ArgumentMatchers.anyString());
        Mockito.doNothing().when(walletService).deleteWalletEntity(ArgumentMatchers.any());

        walletService.deleteWalletByWalletAddress("some/wallet/address");

        Mockito.verify(walletService).isValidWalletAddress(ArgumentMatchers.any());

    }

    @Test(expected = EDCIException.class)
    public void deleteWalletByWalletAddress_shouldThrowException_whenWalletDoesntExists() throws Exception {

        List<String> walletEmailList = new ArrayList<>();
        walletEmailList.add("someEmail@email.com");

        WalletDAO walletDao = new WalletDAO();

        Mockito.doReturn(false).when(walletService).isValidWalletAddress(ArgumentMatchers.anyString());

        walletService.deleteWalletByWalletAddress("some/wallet/address");

    }

    @Test
    public void deleteOldTempWallets_shouldThrowException_whenWalletDoesntExists() throws Exception {

        //Start from here next day
        List<WalletDAO> walletDaoList = new ArrayList<>();
        walletDaoList.add(new WalletDAO());

        Mockito.doReturn(1).when(walletConfigService).getInteger(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());
        Mockito.doReturn(false).when(walletConfigService).getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(walletDaoList).when(walletRepository).fetchOldTemporaryWallets(ArgumentMatchers.anyInt());
        Mockito.doNothing().when(credentialStorageUtil).deleteWalletStorage(ArgumentMatchers.anyList());
        Mockito.doNothing().when(walletRepository).deleteAll(ArgumentMatchers.anyList());

        walletService.deleteOldTempWallets();

        Mockito.verify(walletRepository).deleteAll(ArgumentMatchers.any());

    }

    @Test
    public void fetchWalletByUserId_shouldFetchUserById_whenUserExists() throws Exception {

        WalletDAO walletDao = new WalletDAO();

        Mockito.doNothing().when(walletService).validateWalletUserIdExists(ArgumentMatchers.anyString());
        Mockito.doReturn(walletDao).when(walletRepository).fetchByUserId(ArgumentMatchers.anyString());

        walletService.fetchWalletByUserId("someUserId");

        Mockito.verify(walletRepository).fetchByUserId(ArgumentMatchers.anyString());

    }

    @Test(expected = EDCIException.class)
    public void fetchWalletByUserId_shouldThrowException_whenUserDoesntExists() throws Exception {

        Mockito.doThrow(new EDCIException()).when(walletService).validateWalletUserIdExists(ArgumentMatchers.anyString());

        walletService.fetchWalletByUserId("someUserId");

    }

    @Test
    public void fetchWalletByWalletAddress_shouldFetchUserById_whenUserExists() throws Exception {

        WalletDAO walletDao = new WalletDAO();

        Mockito.doNothing().when(walletService).validateWalletWalletAddressExists(ArgumentMatchers.anyString());
        Mockito.doReturn(walletDao).when(walletRepository).fetchByWalletAddress(ArgumentMatchers.anyString());

        walletService.fetchWalletByWalletAddress("some/wallet/address");

        Mockito.verify(walletRepository).fetchByWalletAddress(ArgumentMatchers.anyString());

    }

    @Test(expected = EDCIException.class)
    public void fetchWalletByWalletAddress_shouldThrowException_whenUserDoesntExists() throws Exception {

        Mockito.doThrow(new EDCIException()).when(walletService).validateWalletWalletAddressExists(ArgumentMatchers.anyString());

        walletService.fetchWalletByWalletAddress("some/wallet/address");

    }

    @Test
    public void fetchWalletDAOByWalletAddress_shouldFetchUserById_whenUserExists() throws Exception {

        WalletDAO walletDao = new WalletDAO();

        Mockito.doNothing().when(walletService).validateWalletWalletAddressExists(ArgumentMatchers.anyString());
        Mockito.doReturn(walletDao).when(walletRepository).fetchByWalletAddress(ArgumentMatchers.anyString());

        walletService.fetchWalletDAOByWalletAddress("some/wallet/address");

        Mockito.verify(walletRepository).fetchByWalletAddress(ArgumentMatchers.anyString());

    }

    @Test(expected = EDCIException.class)
    public void fetchWalletDAOByWalletAddress_shouldThrowException_whenUserDoesntExists() throws Exception {

        Mockito.doThrow(new EDCIException()).when(walletService).validateWalletWalletAddressExists(ArgumentMatchers.anyString());

        walletService.fetchWalletDAOByWalletAddress("some/wallet/address");

    }

    @Test
    public void getOrCreateFolder_shouldGetFolder_whenItExists() throws Exception {

        Mockito.doReturn(true).when(fileOrFolder).exists();
        Mockito.doReturn(true).when(fileOrFolder).isDirectory();
        Mockito.doReturn(fileOrFolder).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());

        walletService.getOrCreateFolder("folderName");

        Mockito.verify(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Mockito.verify(fileOrFolder, Mockito.times(0)).mkdir();

    }

    @Test
    public void getOrCreateFolder_shouldCreateFolder_whenItDoesntExists() throws Exception {

        Mockito.doReturn(false).when(fileOrFolder).exists();
        Mockito.doReturn(true).when(fileOrFolder).isDirectory();
        Mockito.doReturn(fileOrFolder).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());

        walletService.getOrCreateFolder("folderName");

        Mockito.verify(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Mockito.verify(fileOrFolder).mkdir();

    }

    @Test
    public void getOrCreateFolder_shouldCreateFolder_whenTheFileIsNotAFolder() throws Exception {

        Mockito.doReturn(true).when(fileOrFolder).exists();
        Mockito.doReturn(false).when(fileOrFolder).isDirectory();
        Mockito.doReturn(fileOrFolder).when(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());

        walletService.getOrCreateFolder("folderName");

        Mockito.verify(edciFileService).getOrCreateFile(ArgumentMatchers.anyString());
        Mockito.verify(fileOrFolder).mkdir();

    }

    @Test
    public void getWalletDataLocation_shouldGetDataFromProperties_whenDataLocationWhantsToBeRetrieved() throws Exception {

        walletService.getWalletDataLocation();

        Mockito.verify(walletConfigService).getString(WalletConfig.Wallet.WALLETS_DATA_LOCATION);

    }

    @Test
    public void createWalletFolderInFileSystem_shouldCallGetOrCreateFolder_whenAFolderNeedsToBeCreated() throws Exception {

        Mockito.doReturn("").when(walletService).getWalletPrivateFolderName(ArgumentMatchers.anyString());
        Mockito.doReturn(new File("")).when(walletService).getOrCreateFolder(ArgumentMatchers.anyString());

        walletService.createWalletFolderInFileSystem(ArgumentMatchers.anyString());

        Mockito.verify(walletService).getOrCreateFolder(ArgumentMatchers.anyString());

    }

    @Test
    public void fetchWalletDAOByUserEmail_shouldReturnWalletDAO_whenEmailIsValid() throws Exception {

        Mockito.doNothing().when(walletService).validateWalletExistsByEmail(ArgumentMatchers.anyString());

        Mockito.doReturn(Arrays.asList(new WalletDAO())).when(walletRepository).listByUserEmail(ArgumentMatchers.anyString());

        WalletDAO wallet = walletService.fetchWalletDAOByUserEmail("string", true);

        Assert.assertNotNull(wallet);

    }

    @Test
    public void fetchWalletDAOByUserEmail_shouldValidateEmail_whenValidateExistsIsTrue() throws Exception {

        Mockito.doNothing().when(walletService).validateWalletExistsByEmail(ArgumentMatchers.anyString());

        Mockito.doReturn(Arrays.asList(new WalletDAO())).when(walletRepository).listByUserEmail(ArgumentMatchers.anyString());

        walletService.fetchWalletDAOByUserEmail("string", true);

        Mockito.verify(walletService).validateWalletExistsByEmail(ArgumentMatchers.anyString());

    }

    @Test
    public void createWalletFolderIfNotCreated_shouldCreateFolder_whenWalletDaoHasNoFolder() throws Exception {

        WalletDAO wallet = new WalletDAO();

        Mockito.doNothing().when(walletService).createWalletFolderInFileSystem(ArgumentMatchers.anyString());

        walletService.createWalletFolderIfNotCreated(wallet);

        Mockito.verify(walletService).createWalletFolderInFileSystem(ArgumentMatchers.anyString());

    }

    @Test
    public void createWalletFolderIfNotCreated_shouldRetrieveFolder_whenWalletDaoHasFolder() throws Exception {

        WalletDAO wallet = new WalletDAO();
        wallet.setFolder("somefolder");

        Mockito.doNothing().when(walletService).createWalletFolderInFileSystem(ArgumentMatchers.anyString());

        walletService.createWalletFolderIfNotCreated(wallet);

        Mockito.verify(walletService, Mockito.times(0)).createWalletFolderInFileSystem(ArgumentMatchers.anyString());

    }

    @Test
    public void deleteWalletEntity_shouldRetrieveFolder_whenWalletDaoHasFolder() throws Exception {

        WalletDAO wallet = new WalletDAO();
        wallet.setFolder("somefolder");

        Mockito.doNothing().when(credentialStorageUtil).deleteWalletStorage(ArgumentMatchers.anyString(), ArgumentMatchers.anyList());
        Mockito.doNothing().when(walletRepository).deleteById(ArgumentMatchers.any());

        walletService.deleteWalletEntity(wallet);

        Mockito.verify(credentialStorageUtil).deleteWalletStorage(ArgumentMatchers.anyString(), ArgumentMatchers.anyList());

    }

    @Test
    public void createWallet_shouldCreatePermanentWallet_whenNoTempWalletSharesSameEmail() throws Exception {

        WalletDTO wallet = new WalletDTO();
        wallet.setUserId("userId");
        wallet.setUserEmail("userEmail");

        WalletDAO walletDAO = new WalletDAO();
        walletDAO.setUserId("userId");
        walletDAO.setUserEmail("userEmail");
        walletDAO.setTemporary(false);

        Mockito.doReturn("some/prefix").when(walletConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(walletRepository).fetchByUserId(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(walletRepository).listByUserEmail(ArgumentMatchers.anyString());
        Mockito.doReturn(walletDAO).when(walletMapper).toDAO(ArgumentMatchers.any(), ArgumentMatchers.any());

        walletService.createWallet(wallet);

        Mockito.verify(walletRepository).save(ArgumentMatchers.argThat(x -> {
            Assert.assertFalse(x.getTemporary()); return true;
        }));

    }

    @Test
    public void createWallet_shouldUpdateTemporaryWallet_whenTempWalletSharesSameEmail() throws Exception {

        WalletDTO wallet = new WalletDTO();
        wallet.setUserId("userId");
        wallet.setUserEmail("userEmail");

        WalletDAO walletDAO = new WalletDAO();
        walletDAO.setUserId("userId");
        walletDAO.setUserEmail("userEmail");
        walletDAO.setTemporary(true);

        Mockito.doReturn("some/prefix").when(walletConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(walletRepository).fetchByUserId(ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(walletDAO)).when(walletRepository).listByUserEmail(ArgumentMatchers.anyString());

        walletService.createWallet(wallet);

        Mockito.verify(walletMapper, Mockito.times(0)).toDAO(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(walletRepository).save(ArgumentMatchers.argThat(x -> {
            Assert.assertFalse(x.getTemporary()); return true;
        }));

    }

    @Test
    public void createWallet_shouldReturnCredential_whenWalletWithSameEmailAndUserIdExists() throws Exception {

        WalletDTO wallet = new WalletDTO();
        wallet.setUserId("userId");
        wallet.setUserEmail("userEmail");

        WalletDAO walletDAO = new WalletDAO();
        walletDAO.setPk(10L);
        walletDAO.setUserId("userId");
        walletDAO.setUserEmail("userEmail");

        WalletDAO wallet2DAO = new WalletDAO();
        wallet2DAO.setPk(10L);
        wallet2DAO.setUserId("userId");
        wallet2DAO.setUserEmail("userEmail");

        Mockito.doReturn("some/prefix").when(walletConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn(wallet2DAO).when(walletRepository).fetchByUserId(ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(walletDAO)).when(walletRepository).listByUserEmail(ArgumentMatchers.anyString());
        Mockito.doReturn(wallet).when(walletMapper).toDTO(ArgumentMatchers.any(), ArgumentMatchers.any());

        WalletDTO walletReturn = walletService.createWallet(wallet);

        Assert.assertNotNull(walletReturn);

    }

    @Test(expected = EDCIException.class)
    public void createWallet_shouldThrowError_whenWalletWithSameEmailAndUserIdExists() throws Exception {

        WalletDTO wallet = new WalletDTO();
        wallet.setUserId("userId");
        wallet.setUserEmail("userEmail");

        WalletDAO walletDAO = new WalletDAO();
        walletDAO.setPk(8L);
        walletDAO.setUserId("userId");
        walletDAO.setUserEmail("userEmail");
        walletDAO.setTemporary(true);

        WalletDAO wallet2DAO = new WalletDAO();
        wallet2DAO.setPk(10L);
        wallet2DAO.setUserId("userId");
        wallet2DAO.setUserEmail("userEmail");
        wallet2DAO.setTemporary(true);

        Mockito.doReturn("some/prefix").when(walletConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn(wallet2DAO).when(walletRepository).fetchByUserId(ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(walletDAO)).when(walletRepository).listByUserEmail(ArgumentMatchers.anyString());

        walletService.createWallet(wallet);

    }

    @Test(expected = EDCIException.class)
    public void createWallet_shouldThrowError_whenWalletWithSameUserIdExists() throws Exception {

        WalletDTO wallet = new WalletDTO();
        wallet.setUserId("userId");
        wallet.setUserEmail("userEmail");

        WalletDAO walletDAO = new WalletDAO();
        walletDAO.setPk(8L);
        walletDAO.setUserId("userId");
        walletDAO.setUserEmail("userEmail");
        walletDAO.setTemporary(true);

        WalletDAO wallet2DAO = new WalletDAO();
        wallet2DAO.setPk(10L);
        wallet2DAO.setUserId("userId");
        wallet2DAO.setUserEmail("userEmail");
        wallet2DAO.setTemporary(true);

        Mockito.doReturn("some/prefix").when(walletConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn(wallet2DAO).when(walletRepository).fetchByUserId(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(walletRepository).listByUserEmail(ArgumentMatchers.anyString());

        walletService.createWallet(wallet);

    }


    @Test
    public void createOrRetrieveWalletByEmail_shouldRetrieveWalletRightAway_whenCreateTempIsDisable() throws Exception {

        Mockito.doReturn(false).when(walletConfigService).getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(new WalletDAO()).when(walletService).fetchWalletDAOByUserEmail("email@email.com", false);

        walletService.createOrRetrieveWalletByEmail("email@email.com");

        Mockito.verify(walletService, Mockito.times(0)).addBulkWalletEntity(ArgumentMatchers.anyString());
        Mockito.verify(walletService).fetchWalletDAOByUserEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());

    }

    @Test
    public void createOrRetrieveWalletByEmail_shouldCreateWallet_whenCreateTempIsEnableAndNoWalletIsFound() throws Exception {

        Mockito.doReturn(true).when(walletConfigService).getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(null).when(walletService).fetchWalletDAOByUserEmail("email@email.com", false);

        walletService.createOrRetrieveWalletByEmail("email@email.com");

        Mockito.verify(walletService).addBulkWalletEntity(ArgumentMatchers.anyString());
        Mockito.verify(walletService).fetchWalletDAOByUserEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());

    }

    @Test
    public void createOrRetrieveWalletByEmail_shouldNotCreateWallet_whenCreateTempIsNotEnableAndNoWalletIsFound() throws Exception {

        Mockito.doReturn(false).when(walletConfigService).getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(null).when(walletService).fetchWalletDAOByUserEmail("email@email.com", false);

        walletService.createOrRetrieveWalletByEmail("email@email.com");

        Mockito.verify(walletService, Mockito.times(0)).addBulkWalletEntity(ArgumentMatchers.anyString());
        Mockito.verify(walletService).fetchWalletDAOByUserEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());

    }

    @Test
    public void createOrRetrieveWalletByEmail_shouldReturnNull_whenCreateTempIsNotEnabledAndNoWalletIsFound() throws Exception {

        Mockito.doReturn(false).when(walletConfigService).getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(null).when(walletService).fetchWalletDAOByUserEmail("email@email.com", false);

        WalletDAO wallet = walletService.createOrRetrieveWalletByEmail("email@email.com");

        Assert.assertNull(wallet);

    }

    @Test
    public void createOrRetrieveWalletByEmail_shouldReturnWallet_whenCreateTempIsNotEnabledAndWalletIsFound() throws Exception {

        Mockito.doReturn(false).when(walletConfigService).getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(new WalletDAO()).when(walletService).fetchWalletDAOByUserEmail("email@email.com", false);

        WalletDAO wallet = walletService.createOrRetrieveWalletByEmail("email@email.com");

        Assert.assertNotNull(wallet);

    }

    @Test(expected = EDCIBadRequestException.class)
    public void createOrRetrieveWalletByEmail_shouldThrowError_whenEmailIsNotValid() throws Exception {

        walletService.createOrRetrieveWalletByEmail("noValidEmail");

    }

    @Test
    public void addBulkWalletEntity_shouldCreateTemporaryWallet_whenEmailIsNotUsed() throws Exception {

        Mockito.doReturn("string").when(walletConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn(0).when(walletRepository).countByUserEmail(ArgumentMatchers.anyString());

        walletService.addBulkWalletEntity("some@email.com");

        Mockito.verify(walletRepository).save(ArgumentMatchers.any());

    }

    @Test(expected = EDCIException.class)
    public void addBulkWalletEntity_shouldNotCreateTemporaryWallet_whenEmailIsUsed() throws Exception {

        Mockito.doReturn("string").when(walletConfigService).getString(ArgumentMatchers.anyString());
        Mockito.doReturn(1).when(walletRepository).countByUserEmail(ArgumentMatchers.anyString());

        walletService.addBulkWalletEntity("some@email.com");

    }
}