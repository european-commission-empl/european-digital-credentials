package eu.europa.ec.empl.edci.wallet.service.security;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.mapper.WalletMapper;
import eu.europa.ec.empl.edci.wallet.repository.WalletRepository;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class WalletServiceTest extends AbstractUnitBaseTest {

    @Spy
    @InjectMocks
    WalletService walletService;

    @Mock
    private WalletConfigService walletConfigService;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    EDCIMessageService edciMessageService;

    @Before
    public void injectDependencies() throws Exception {

        Mockito.when(walletConfigService.getString("wallet.address.prefix")).thenReturn("walletAddressPrefix");

    }

    @Test
    public void addWalletEntity_shouldCreatedAWallet_whenUserIdAndEmailNotExists() throws Exception {

        WalletDTO wallet = new WalletDTO();
        wallet.setUserEmail("test@test.com");
        wallet.setUserId("userId");

        WalletDAO walletDao = new WalletDAO();

        Mockito.when(walletRepository.fetchByUserEmail(ArgumentMatchers.anyString())).thenReturn(null);
        Mockito.when(walletRepository.fetchByUserId(ArgumentMatchers.anyString())).thenReturn(null);
        Mockito.when(walletMapper.toDTO(ArgumentMatchers.any())).thenReturn(wallet);
        Mockito.when(walletRepository.save(walletDao)).thenReturn(walletDao);
        Mockito.when(walletMapper.toDAO(ArgumentMatchers.any(WalletDTO.class))).thenReturn(walletDao);

        WalletDTO aux = walletService.addWalletEntity(wallet);

        Mockito.verify(walletRepository).save(ArgumentMatchers.any(WalletDAO.class));
        Assert.assertNotNull(aux);

    }

    @Test(expected = EDCIException.class)
    public void addWalletEntity_shouldThrowException_whenUserEmailAlreadyExists() throws Exception {

        WalletDAO walletDao = new WalletDAO();
        walletDao.setUserEmail("test@test.com");
        walletDao.setUserId("userId");

        WalletDTO wallet = new WalletDTO();
        wallet.setUserEmail("test@test.com");
        wallet.setUserId("userId");

        Mockito.when(walletRepository.fetchByUserEmail(ArgumentMatchers.anyString())).thenReturn(walletDao);
        Mockito.when(walletRepository.fetchByUserId(ArgumentMatchers.anyString())).thenReturn(null);
        Mockito.when(walletMapper.toDTO(ArgumentMatchers.any())).thenReturn(wallet);

        WalletDTO aux = walletService.addWalletEntity(wallet);

    }

    @Test(expected = EDCIException.class)
    public void addWalletEntity_shouldThrowException_whenUserIdAlreadyExists() throws Exception {

        WalletDAO walletDao = new WalletDAO();
        walletDao.setUserEmail("test@test.com");
        walletDao.setUserId("userId");

        WalletDTO wallet = new WalletDTO();
        wallet.setUserEmail("test@test.com");
        wallet.setUserId("userId");

        Mockito.when(walletRepository.fetchByUserEmail(ArgumentMatchers.anyString())).thenReturn(null);
        Mockito.when(walletRepository.fetchByUserId(ArgumentMatchers.anyString())).thenReturn(walletDao);
        Mockito.when(walletMapper.toDTO(ArgumentMatchers.any())).thenReturn(wallet);

        WalletDTO aux = walletService.addWalletEntity(wallet);

    }

    @Test(expected = EDCIException.class)
    public void addWalletEntity_shouldThrowException_whenUserIdAndEmailExistsButDontMatch() throws Exception {

        WalletDAO walletDao1 = new WalletDAO();
        walletDao1.setPk(1L);
        walletDao1.setUserEmail("test@test.com");
        walletDao1.setUserId("userId");

        WalletDAO walletDao2 = new WalletDAO();
        walletDao2.setPk(2L);
        walletDao2.setUserEmail("test@test.com");
        walletDao2.setUserId("userId");

        WalletDTO wallet = new WalletDTO();
        wallet.setUserEmail("test@test.com");
        wallet.setUserId("userId");

        Mockito.when(walletRepository.fetchByUserEmail(ArgumentMatchers.anyString())).thenReturn(walletDao1);
        Mockito.when(walletRepository.fetchByUserId(ArgumentMatchers.anyString())).thenReturn(walletDao2);
        Mockito.when(walletMapper.toDTO(ArgumentMatchers.any())).thenReturn(wallet);

        WalletDTO aux = walletService.addWalletEntity(wallet);

    }

    @Test
    public void addWalletEntity_shouldReturnWallet_whenUserIdAndEmailExists() throws Exception {

        WalletDAO walletDao = new WalletDAO();
        walletDao.setUserEmail("test@test.com");
        walletDao.setUserId("userId");

        WalletDTO wallet = new WalletDTO();
        wallet.setUserEmail("test@test.com");
        wallet.setUserId("userId");

        Mockito.when(walletRepository.fetchByUserEmail(ArgumentMatchers.anyString())).thenReturn(walletDao);
        Mockito.when(walletRepository.fetchByUserId(ArgumentMatchers.anyString())).thenReturn(walletDao);
        Mockito.when(walletMapper.toDTO(ArgumentMatchers.any())).thenReturn(wallet);

        WalletDTO aux = walletService.addWalletEntity(wallet);

        Mockito.verify(walletRepository, Mockito.times(0)).save(ArgumentMatchers.any(WalletDAO.class));
        Assert.assertNotNull(aux);

    }

    @Test
    public void addWalletEntity_shouldUpdateWalletUserId_whenEmailWalletFoundIsTemporary() throws Exception {

        WalletDAO walletDao = new WalletDAO();
        walletDao.setUserEmail("test@test.com");
        walletDao.setUserId("userId");
        walletDao.setTemporary(true);

        WalletDTO wallet = new WalletDTO();
        wallet.setUserEmail("test@test.com");
        wallet.setUserId("userId");

        Mockito.when(walletRepository.fetchByUserEmail(ArgumentMatchers.anyString())).thenReturn(walletDao);
        Mockito.when(walletRepository.fetchByUserId(ArgumentMatchers.anyString())).thenReturn(null);
        Mockito.when(walletMapper.toDTO(ArgumentMatchers.any())).thenReturn(wallet);

        WalletDTO aux = walletService.addWalletEntity(wallet);

        Mockito.verify(walletRepository, Mockito.times(1)).save(ArgumentMatchers.any(WalletDAO.class));
        Assert.assertNotNull(aux);

    }

    @Test(expected = EDCIException.class)
    public void addWalletEntity_shouldThrowException_whenEmailWalletFoundIsTemporaryButUserIdWalletIsAlsoFound() throws Exception {

        WalletDAO walletDao = new WalletDAO();
        walletDao.setPk(1L);
        walletDao.setUserEmail("test@test.com");
        walletDao.setUserId("userId");
        walletDao.setTemporary(true);

        WalletDAO walletDao2 = new WalletDAO();
        walletDao2.setPk(2L);
        walletDao2.setUserEmail("test@test.com");
        walletDao2.setUserId("userId");

        WalletDTO wallet = new WalletDTO();
        wallet.setUserEmail("test@test.com");
        wallet.setUserId("userId");

        Mockito.when(walletRepository.fetchByUserEmail(ArgumentMatchers.anyString())).thenReturn(walletDao);
        Mockito.when(walletRepository.fetchByUserId(ArgumentMatchers.anyString())).thenReturn(walletDao2);
        Mockito.when(walletMapper.toDTO(ArgumentMatchers.any())).thenReturn(wallet);

        WalletDTO aux = walletService.addWalletEntity(wallet);

        Mockito.verify(walletRepository, Mockito.times(1)).save(ArgumentMatchers.any(WalletDAO.class));
        Assert.assertNotNull(aux);

    }

    @Test
    public void addBulkWalletEntity_shouldCreateTemporalWallet_whenNoWalletExistsWithSameEmail() throws Exception {

        WalletDAO walletDao = new WalletDAO();
        walletDao.setPk(1L);
        walletDao.setUserEmail("test@test.com");
        walletDao.setUserId("userId");
        walletDao.setTemporary(true);

        WalletDTO wallet = new WalletDTO();
        wallet.setUserEmail("test@test.com");
        wallet.setUserId("userId");

        Mockito.when(walletRepository.countByUserEmail(ArgumentMatchers.anyString())).thenReturn(0);
        Mockito.when(walletRepository.save(walletDao)).thenReturn(walletDao);
        Mockito.when(walletMapper.toDAO(ArgumentMatchers.any(WalletDTO.class))).thenReturn(walletDao);

        WalletDTO aux = walletService.addBulkWalletEntity(wallet);

        Mockito.verify(walletRepository, Mockito.times(1)).save(ArgumentMatchers.any(WalletDAO.class));

        Assert.assertNotNull(aux);
        Assert.assertNull(aux.getErrorCode());

    }

    @Test
    public void addBulkWalletEntity_shouldReturnWalletError_whenWalletExistsWithSameEmail() throws Exception {

        WalletDAO walletDao = new WalletDAO();
        walletDao.setPk(1L);
        walletDao.setUserEmail("test@test.com");
        walletDao.setUserId("userId");
        walletDao.setTemporary(true);

        WalletDTO wallet = new WalletDTO();
        wallet.setUserEmail("test@test.com");
        wallet.setUserId("userId");

        Mockito.when(walletRepository.countByUserEmail(ArgumentMatchers.anyString())).thenReturn(1);
        Mockito.when(walletRepository.save(walletDao)).thenReturn(walletDao);
        Mockito.when(walletMapper.toDAO(ArgumentMatchers.any(WalletDTO.class))).thenReturn(walletDao);

        WalletDTO aux = walletService.addBulkWalletEntity(wallet);

        Assert.assertNotNull(aux);
        Assert.assertNotNull(aux.getErrorCode());

    }

}