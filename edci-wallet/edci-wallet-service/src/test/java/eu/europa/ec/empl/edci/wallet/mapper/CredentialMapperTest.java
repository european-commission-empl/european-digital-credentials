package eu.europa.ec.empl.edci.wallet.mapper;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialLocalizableInfoDTO;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialLocalizableInfoDAO;
import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.mapper.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class CredentialMapperTest extends AbstractUnitBaseTest {

    @InjectMocks
    private CredentialMapper credentialMapper = Mappers.getMapper(CredentialMapper.class);

    @Mock
    private WalletMapper walletMapper = Mappers.getMapper(WalletMapper.class);

    @Mock
    private CredentialLocalizableInfoMapper credentialLocalizableInfoMapper = Mappers.getMapper(CredentialLocalizableInfoMapper.class);

    @Mock
    private ShareLinkMapper shareLinkMapper = Mappers.getMapper(ShareLinkMapper.class);

    WalletDAO walletdao = new WalletDAO();
    ShareLinkDAO sharelinkdao1 = new ShareLinkDAO();
    ShareLinkDAO sharelinkdao2 = new ShareLinkDAO();
    CredentialDAO credentialDAO = new CredentialDAO();

    WalletDTO walletdto = new WalletDTO();
    ShareLinkDTO sharelinkdto1 = new ShareLinkDTO();
    ShareLinkDTO sharelinkdto2 = new ShareLinkDTO();
    CredentialDTO credentialDTO = new CredentialDTO();

    @Before
    public void injectDependencies() throws Exception {

        walletdao.setPk(10L);
        walletdao.setCredentialDAOList(new ArrayList<>());
        walletdao.getCredentialDAOList().add(credentialDAO);

        sharelinkdao1.setPk(20L);
        sharelinkdao2.setPk(30L);

        sharelinkdao1.setCredential(credentialDAO);
        sharelinkdao2.setCredential(credentialDAO);

        credentialDAO.setPk(1L);
        credentialDAO.setCreateDate(new Date());
        credentialDAO.setFile("someFile.jsonld");
        credentialDAO.setCredential("someBytes".getBytes());
        credentialDAO.setUuid("someUUID");
        credentialDAO.setType("type");
        credentialDAO.setWallet(walletdao);
        credentialDAO.setCredentialLocalizableInfo(new ArrayList<>());
        credentialDAO.getCredentialLocalizableInfo().add(new CredentialLocalizableInfoDAO());
        credentialDAO.setShareLinkList(new ArrayList<>());
        credentialDAO.getShareLinkList().add(sharelinkdao1);
        credentialDAO.getShareLinkList().add(sharelinkdao2);

        Mockito.when(walletMapper.toDTO(ArgumentMatchers.any(WalletDAO.class),
                ArgumentMatchers.any(CycleAvoidingMappingContext.class))).thenReturn(new WalletDTO());

        Mockito.when(credentialLocalizableInfoMapper.toDTO(ArgumentMatchers.any(CredentialLocalizableInfoDAO.class)
                )).thenReturn(new CredentialLocalizableInfoDTO());

        Mockito.when(shareLinkMapper.toDTOList(ArgumentMatchers.anyList(),
                ArgumentMatchers.any(CycleAvoidingMappingContext.class))).thenReturn(Arrays.asList(new ShareLinkDTO(), new ShareLinkDTO()));


        walletdto.setId(10L);
        walletdto.setCredentialDTOList(new ArrayList<>());
        walletdto.getCredentialDTOList().add(credentialDTO);

        sharelinkdto1.setId(20L);
        sharelinkdto2.setId(30L);

        sharelinkdto1.setCredential(credentialDTO);
        sharelinkdto2.setCredential(credentialDTO);

        credentialDTO.setPk(1L);
        credentialDTO.setFile("someFile.jsonld");
        credentialDTO.setCredential("someBytes".getBytes());
        credentialDTO.setUuid("someUUID");
        credentialDTO.setType("type");
        credentialDTO.setWallet(walletdto);
        credentialDTO.setCredentialLocalizableInfo(new ArrayList<>());
        credentialDTO.getCredentialLocalizableInfo().add(new CredentialLocalizableInfoDTO());
        credentialDTO.setShareLinkList(new ArrayList<>());
        credentialDTO.getShareLinkList().add(sharelinkdto1);
        credentialDTO.getShareLinkList().add(sharelinkdto2);

        Mockito.when(walletMapper.toDAO(ArgumentMatchers.any(WalletDTO.class),
                ArgumentMatchers.any(CycleAvoidingMappingContext.class))).thenReturn(new WalletDAO());

        Mockito.when(credentialLocalizableInfoMapper.toDAO(ArgumentMatchers.any(CredentialLocalizableInfoDTO.class)
                )).thenReturn(new CredentialLocalizableInfoDAO());

        Mockito.when(shareLinkMapper.toDAO(ArgumentMatchers.any(ShareLinkDTO.class),
                ArgumentMatchers.any(CycleAvoidingMappingContext.class))).thenReturn(new ShareLinkDAO(), new ShareLinkDAO());

    }

    @Test
    public void toDTO_shouldReturnDTO_givenOneDAO() {

        CredentialDTO credential = credentialMapper.toDTO(credentialDAO, new CycleAvoidingMappingContext());

        Assert.assertNotNull(credential);
        Assert.assertNotNull(credential.getWallet());
        Assert.assertNotNull(credential.getFile());
        Assert.assertNotNull(credential.getCredentialLocalizableInfo());
        Assert.assertNotNull(credential.getShareLinkList());
        Assert.assertNotNull(credential.getPk());
        Assert.assertNotNull(credential.getType());
        Assert.assertNotNull(credential.getUuid());

        Assert.assertEquals(2, credential.getShareLinkList().size());

    }

    @Test
    public void toDAO_shouldReturnDAO_givenOneDTO() {

        CredentialDAO credential = credentialMapper.toDAO(credentialDTO, new CycleAvoidingMappingContext());

        Assert.assertNotNull(credential);
        Assert.assertNotNull(credential.getWallet());
        Assert.assertNotNull(credential.getFile());
        Assert.assertNotNull(credential.getCredentialLocalizableInfo());
        Assert.assertNotNull(credential.getShareLinkList());
        Assert.assertNotNull(credential.getPk());
        Assert.assertNotNull(credential.getType());
        Assert.assertNotNull(credential.getUuid());

        Assert.assertEquals(2, credential.getShareLinkList().size());

    }

}