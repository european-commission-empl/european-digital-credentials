package eu.europa.ec.empl.edci.wallet.service.security;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.mapper.EuropassCredentialPresentationMapper;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.*;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.wallet.mapper.WalletMapper;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.service.CredentialService;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialUtil;
import eu.europa.ec.empl.edci.wallet.service.utils.EuropassCredentialVerifyUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.ServletContext;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CredentialServiceTest extends AbstractUnitBaseTest {

    @Spy
    @InjectMocks
    CredentialService credentialService;

    @Mock
    private CredentialUtil credentialUtil;

    @Mock
    private CredentialMapper credentialMapper;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private WalletConfigService walletConfigService;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    EuropassCredentialVerifyUtil europassCredentialVerifyUtil;

    @Mock
    ServletContext servletContext;

    @Mock
    EDCIMessageService messageSource;

    @Mock
    private Validator validator;

    @Mock
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Mock
    private EuropassCredentialPresentationMapper europassCredentialPresentationMapper;

    @Mock
    private ControlledListsUtil controlledListsUtil;

    @Mock
    private XmlUtil xmlUtil;

    @Mock
    private DiplomaUtils diplomaUtils;

    private WalletDTO wallet = null;
    private CredentialDTO cred = null;
    private EuropassCredentialDTO europassCred = null;

    @Before
    public void injectDependencies() throws Exception {
        wallet = new WalletDTO();
        wallet.setUserEmail("test@test.com");
        wallet.setUserId("userId");
        wallet.setWalletAddress("wallet.address/userId");
        wallet.setTemporary(false);

        cred = new CredentialDTO();
        cred.setWalletDTO(wallet);
        cred.setUuid("qwertyuiop");
        cred.setCredentialXML("credentialXML".getBytes());

        wallet.setCredentialDTOList(new ArrayList<>());
        wallet.getCredentialDTOList().add(cred);

        europassCred = new EuropassCredentialDTO();
        europassCred.setId(new URI(EDCIConfig.Defaults.XML_CRED_UUID_PREFIX.concat(UUID.randomUUID().toString())));
    }

    @Test
    public void createCredential_shouldReturnACredential_whenCalled() throws Exception {

        Mockito.when(walletConfigService.getBoolean("allow.unsigned.credentials", false)).thenReturn(true);
        Mockito.when(walletService.fetchWalletByUserId(ArgumentMatchers.anyString())).thenReturn(wallet);
        Mockito.when(edciCredentialModelUtil.fromByteArray(ArgumentMatchers.any(byte[].class))).thenReturn(europassCred);
        Mockito.doReturn(cred).when(credentialService).addCredentialEntity(ArgumentMatchers.any(CredentialDTO.class));
        Mockito.doNothing().when(credentialService).sendCreateNotificationEmail(ArgumentMatchers.any(CredentialDTO.class));

        CredentialDTO aux = credentialService.createCredential(cred, true);

        Assert.assertNotNull(aux);

    }

    @Test
    public void createCredential_shouldSendAnEmail_afterCredentialIsCreated() throws Exception {

        Mockito.when(walletConfigService.getBoolean("allow.unsigned.credentials", false)).thenReturn(true);
        Mockito.when(walletService.fetchWalletByUserId(ArgumentMatchers.anyString())).thenReturn(wallet);
        Mockito.when(edciCredentialModelUtil.fromByteArray(ArgumentMatchers.any(byte[].class))).thenReturn(europassCred);
        Mockito.doReturn(cred).when(credentialService).addCredentialEntity(ArgumentMatchers.any(CredentialDTO.class));
        Mockito.doNothing().when(credentialService).sendCreateNotificationEmail(ArgumentMatchers.any(CredentialDTO.class));

        CredentialDTO aux = credentialService.createCredential(cred, true);

        Mockito.verify(credentialService, Mockito.times(1))
                .sendCreateNotificationEmail(ArgumentMatchers.any(CredentialDTO.class));

    }

    @Test
    public void listCredentials_should() {

        List<CredentialDTO> credList = new ArrayList<>();
        credList.add(new CredentialDTO() {{
            setUuid("SomeUUID1");
        }});
        credList.add(new CredentialDTO() {{
            setUuid("SomeUUID2");
        }});

        Mockito.when(credentialMapper.toDTOList(ArgumentMatchers.any())).thenReturn(credList);

        List<CredentialDTO> credDTOList = credentialService.listCredentials("userId");

        Assert.assertEquals(2, credDTOList.size());

    }

}