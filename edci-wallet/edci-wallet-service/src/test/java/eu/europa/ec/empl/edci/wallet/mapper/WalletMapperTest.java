package eu.europa.ec.empl.edci.wallet.mapper;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialLocalizableInfoDTO;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialLocalizableInfoDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.mapper.CycleAvoidingMappingContext;
import eu.europa.ec.empl.edci.wallet.mapper.WalletMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.Date;

public class WalletMapperTest extends AbstractUnitBaseTest {

    @InjectMocks
    private WalletMapper walletMapper = Mappers.getMapper(WalletMapper.class);

    WalletDAO walletDAO = new WalletDAO();

    WalletDTO walletDTO = new WalletDTO();

    @Before
    public void injectDependencies() throws Exception {

        walletDAO.setPk(1L);
        walletDAO.setFolder("folder");
        walletDAO.setWalletAddress("someWalletADdress");
        walletDAO.setCredentialDAOList(Arrays.asList(new CredentialDAO[]{new CredentialDAO()}));
        walletDAO.setCreateDate(new Date());
        walletDAO.setTemporary(true);
        walletDAO.setUserEmail("userEmail");
        walletDAO.setUserId("userId");

        walletDTO.setId(1L);
        walletDTO.setFolder("folder");
        walletDTO.setWalletAddress("someWalletADdress");
        walletDTO.setCredentialDTOList(Arrays.asList(new CredentialDTO[]{new CredentialDTO()}));
        walletDTO.setTemporary(true);
        walletDTO.setUserEmail("userEmail");
        walletDTO.setUserId("userId");

    }

    @Test
    public void toDTO_shouldReturnDTO_givenOneDAO() {

        WalletDTO wallet = walletMapper.toDTO(walletDAO, new CycleAvoidingMappingContext());

        Assert.assertNotNull(wallet);

        Assert.assertNotNull(wallet.getId());
        Assert.assertNotNull(wallet.getFolder());
        Assert.assertNotNull(wallet.getWalletAddress());
        Assert.assertNotNull(wallet.getTemporary());
        Assert.assertNotNull(wallet.getUserEmail());
        Assert.assertNotNull(wallet.getUserId());
        Assert.assertTrue(wallet.getCredentialDTOList().isEmpty());

    }

    @Test
    public void toDAO_shouldReturnDAO_givenOneDTO() {

        WalletDAO wallet = walletMapper.toDAO(walletDTO, new CycleAvoidingMappingContext());

        Assert.assertNotNull(wallet.getPk());
        Assert.assertNotNull(wallet.getFolder());
        Assert.assertNotNull(wallet.getWalletAddress());
        Assert.assertNotNull(wallet.getCreateDate());
        Assert.assertNotNull(wallet.getTemporary());
        Assert.assertNotNull(wallet.getUserEmail());
        Assert.assertNotNull(wallet.getUserId());
        Assert.assertTrue(wallet.getCredentialDAOList().isEmpty());

    }

}