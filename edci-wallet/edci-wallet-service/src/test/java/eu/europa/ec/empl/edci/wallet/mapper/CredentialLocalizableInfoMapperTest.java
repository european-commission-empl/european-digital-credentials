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

public class CredentialLocalizableInfoMapperTest extends AbstractUnitBaseTest {

    @InjectMocks
    private CredentialLocalizableInfoMapper credentialMapper = Mappers.getMapper(CredentialLocalizableInfoMapper.class);

    CredentialLocalizableInfoDAO credentialDAO = new CredentialLocalizableInfoDAO();

    CredentialLocalizableInfoDTO credentialDTO = new CredentialLocalizableInfoDTO();

    @Before
    public void injectDependencies() throws Exception {

        credentialDAO.setPk(1L);
        credentialDAO.setCredentialProfile(Arrays.asList(new String[]{"generic","converted"}));
        credentialDAO.setDescription("someDescription");
        credentialDAO.setLang("es");
        credentialDAO.setTitle("someTitle");

        credentialDTO.setPk(1L);
        credentialDTO.setCredentialProfile(Arrays.asList(new String[]{"generic","converted"}));
        credentialDTO.setCredential(new CredentialDTO());
        credentialDTO.setDescription("someDescription");
        credentialDTO.setLang("es");
        credentialDTO.setTitle("someTitle");

    }

    @Test
    public void toDTO_shouldReturnDTO_givenOneDAO() {

        CredentialLocalizableInfoDTO credential = credentialMapper.toDTO(credentialDAO);

        Assert.assertNotNull(credential);
        Assert.assertNotNull(credential.getCredentialProfile());
        Assert.assertFalse(credential.getCredentialProfile().isEmpty());
        Assert.assertEquals(2, credential.getCredentialProfile().size());
        Assert.assertNotNull(credential.getPk());
        Assert.assertNotNull(credential.getTitle());
        Assert.assertNotNull(credential.getDescription());
        Assert.assertNotNull(credential.getLang());
        Assert.assertNull(credential.getCredential());

    }

    @Test
    public void toDAO_shouldReturnDAO_givenOneDTO() {

        CredentialLocalizableInfoDAO credential = credentialMapper.toDAO(credentialDTO);

        Assert.assertNotNull(credential);
        Assert.assertNotNull(credential.getCredentialProfile());
        Assert.assertFalse(credential.getCredentialProfile().isEmpty());
        Assert.assertEquals(2, credential.getCredentialProfile().size());
        Assert.assertNotNull(credential.getPk());
        Assert.assertNotNull(credential.getTitle());
        Assert.assertNotNull(credential.getDescription());
        Assert.assertNotNull(credential.getLang());

    }

}