package eu.europa.ec.empl.edci.wallet.service.security;

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
import liquibase.pro.packaged.D;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;

public class SharelinkMapperTest extends AbstractUnitBaseTest {


    @InjectMocks
    private ShareLinkMapper shareLinkMapper = Mappers.getMapper(ShareLinkMapper.class);;

    @Mock
    private CredentialMapper credentialMapper = Mappers.getMapper(CredentialMapper.class);;

    @Mock
    private WalletMapper walletMapper = Mappers.getMapper(WalletMapper.class);;

    @Mock
    private CredentialLocalizableInfoMapper credentialLocalizableInfoMapper = Mappers.getMapper(CredentialLocalizableInfoMapper.class);;


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
        sharelinkdao1.setShareHash("sharehash");
        sharelinkdao1.setCreationDate(new Date());
        sharelinkdao1.setExpirationDate(new Date());
        sharelinkdao2.setPk(30L);

        sharelinkdao1.setCredential(credentialDAO);
        sharelinkdao2.setCredential(credentialDAO);

        credentialDAO.setPk(1L);
        credentialDAO.setUuid("someUUID");
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

        Mockito.when(credentialMapper.toDTO(ArgumentMatchers.any(CredentialDAO.class),
                ArgumentMatchers.any(CycleAvoidingMappingContext.class))).thenReturn(new CredentialDTO());


        walletdto.setId(10L);
        walletdto.setCredentialDTOList(new ArrayList<>());
        walletdto.getCredentialDTOList().add(credentialDTO);

        sharelinkdto1.setId(20L);
        sharelinkdto1.setShareHash("sharehash");
        sharelinkdto1.setCreationDate(new Date());
        sharelinkdto1.setExpirationDate(new Date());
        sharelinkdto2.setId(30L);

        sharelinkdto1.setCredential(credentialDTO);
        sharelinkdto2.setCredential(credentialDTO);

        credentialDTO.setPk(1L);
        credentialDTO.setUuid("someUUID");
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

        Mockito.when(credentialMapper.toDAO(ArgumentMatchers.any(CredentialDTO.class),
                ArgumentMatchers.any(CycleAvoidingMappingContext.class))).thenReturn(new CredentialDAO());

    }

    @Test
    public void toDTO_shouldReturnDTO_givenOneDAO() {

        ShareLinkDTO shareLinkDTO = shareLinkMapper.toDTO(sharelinkdao1, new CycleAvoidingMappingContext());

        Assert.assertNotNull(shareLinkDTO);
        Assert.assertNotNull(shareLinkDTO.getShareHash());
        Assert.assertNotNull(shareLinkDTO.getCreationDate());
        Assert.assertNotNull(shareLinkDTO.getCredential());
        Assert.assertNotNull(shareLinkDTO.getExpirationDate());
        Assert.assertNotNull(shareLinkDTO.getId());

    }

    @Test
    public void toDAO_shouldReturnDAO_givenOneDTO() {

        ShareLinkDAO shareLink = shareLinkMapper.toDAO(sharelinkdto1, new CycleAvoidingMappingContext());

        Assert.assertNotNull(shareLink);
        Assert.assertNotNull(shareLink.getShareHash());
        Assert.assertNotNull(shareLink.getCreationDate());
        Assert.assertNotNull(shareLink.getCredential());
        Assert.assertNotNull(shareLink.getExpirationDate());
        Assert.assertNotNull(shareLink.getPk());

    }

}