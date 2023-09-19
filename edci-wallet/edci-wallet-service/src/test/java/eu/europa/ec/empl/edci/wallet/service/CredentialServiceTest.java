package eu.europa.ec.empl.edci.wallet.service;

public class CredentialServiceTest /*extends AbstractUnitBaseTest*/ {
/*
    @Spy
    @InjectMocks
    CredentialService credentialService;

    @Mock
    private CredentialStorageUtil credentialUtil;

    @Mock
    private CredentialMapper credentialMapper;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private WalletConfigService walletConfigService;*/

//    @Mock
//    private SpringTemplateEngine templateEngine;

//    @Mock
//    EuropassCredentialVerifyUtil europassCredentialVerifyUtil;

/*    @Mock
    ServletContext servletContext;

    @Mock
    EDCIMessageService messageSource;

    @Mock
    private Validator validator;*/

//    @Mock
//    private EDCICredentialModelUtil edciCredentialModelUtil;
//
//    @Mock
//    private EuropassCredentialPresentationMapper europassCredentialPresentationMapper;
//
//    @Mock
//    private ControlledListsUtil controlledListsUtil;
//
//    @Mock
//    private XmlUtil xmlUtil;
//
//    //@Mock
//    //private DiplomaUtils diplomaUtils;
//    //TODO: new diploma util
//
//    private WalletDTO wallet = null;
//    private CredentialDTO cred = null;
//    private EuropassCredentialDTO europassCred = null;

//    @Before
//    public void injectDependencies() throws Exception {
//        wallet = new WalletDTO();
//        wallet.setUserEmail("test@test.com");
//        wallet.setUserId("userId");
//        wallet.setWalletAddress("wallet.address/userId");
//        wallet.setTemporary(false);
//
//        cred = new CredentialDTO();
//        cred.setWalletDTO(wallet);
//        cred.setUuid("qwertyuiop");
//        cred.setCredentialXML("credentialXML".getBytes());
//
//        wallet.setCredentialDTOList(new ArrayList<>());
//        wallet.getCredentialDTOList().add(cred);
//
//        europassCred = new EuropassCredentialDTO();
//        europassCred.setId(new URI(EDCIConfig.Defaults.XML_CRED_UUID_PREFIX.concat(UUID.randomUUID().toString())));
//    }
//
//    @Test
//    public void createCredential_shouldReturnACredential_whenCalled() throws Exception {
//
//        Mockito.when(walletConfigService.getBoolean("allow.unsigned.credentials", false)).thenReturn(true);
//        Mockito.when(walletService.fetchWalletByUserId(ArgumentMatchers.anyString())).thenReturn(wallet);
//        Mockito.when(edciCredentialModelUtil.fromByteArray(ArgumentMatchers.any(byte[].class))).thenReturn(europassCred);
//        Mockito.doReturn(cred).when(credentialService).addCredentialEntity(ArgumentMatchers.any(CredentialDTO.class));
//        Mockito.doNothing().when(credentialService).sendCreateNotificationEmail(ArgumentMatchers.any(CredentialDTO.class));
//
//        CredentialDTO aux = credentialService.createCredential(cred, true);
//
//        Assert.assertNotNull(aux);
//
//    }
//
//    @Test
//    public void createCredential_shouldSendAnEmail_afterCredentialIsCreated() throws Exception {
//
//        Mockito.when(walletConfigService.getBoolean("allow.unsigned.credentials", false)).thenReturn(true);
//        Mockito.when(walletService.fetchWalletByUserId(ArgumentMatchers.anyString())).thenReturn(wallet);
//        Mockito.when(edciCredentialModelUtil.fromByteArray(ArgumentMatchers.any(byte[].class))).thenReturn(europassCred);
//        Mockito.doReturn(cred).when(credentialService).addCredentialEntity(ArgumentMatchers.any(CredentialDTO.class));
//        Mockito.doNothing().when(credentialService).sendCreateNotificationEmail(ArgumentMatchers.any(CredentialDTO.class));
//
//        CredentialDTO aux = credentialService.createCredential(cred, true);
//
//        Mockito.verify(credentialService, Mockito.times(1))
//                .sendCreateNotificationEmail(ArgumentMatchers.any(CredentialDTO.class));
//
//    }
//
//    @Test
//    public void listCredentials_should() {
//
//        List<CredentialDTO> credList = new ArrayList<>();
//        credList.add(new CredentialDTO() {{
//            setUuid("SomeUUID1");
//        }});
//        credList.add(new CredentialDTO() {{
//            setUuid("SomeUUID2");
//        }});
//
//        Mockito.when(credentialMapper.toDTOList(ArgumentMatchers.any())).thenReturn(credList);
//
//        List<CredentialDTO> credDTOList = credentialService.listCredentials("userId");
//
//        Assert.assertEquals(2, credDTOList.size());
//
//    }

}