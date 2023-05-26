package integration.eu.europa.ec.empl.edci.wallet.service;

import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VerifiablePresentationITest extends AbstractIntegrationBaseTest {

//    @InjectMocks
//    @Spy
//    CredentialService credentialService;
//
//    @InjectMocks
//    @Spy
//    EDCICredentialModelUtil europass2CredentialUtil;
//
//    @Spy
//    private JsonUtil jsonUtil;
//
//    @Spy
//    private XmlUtil xmlUtil;
//
//    @Spy
//    private EuropassCredentialPresentationMapper europassCredentialPresentationMapper = Mappers.getMapper(EuropassCredentialPresentationMapper.class);
//
//    @Spy
//    private PresentationCommonsMapper presentationCommonsMapper = Mappers.getMapper(PresentationCommonsMapper.class);
//
//    @Spy
//    private SpringTemplateEngine templateEngine;
//
//    @Spy
//    private ControlledListsUtil controlledListsUtil;

    //@InjectMocks
    //@Spy
    //private DiplomaUtils diplomaUtils;
    //TODO: new diploma util

//    @InjectMocks
//    @Spy
//    private HtmlSanitizerUtil htmlSanitizerUtil;
//
//    @Mock
//    private EDCIMessageService edciMessageService;
//
//    @Mock
//    private ShareLinkService shareLinkService;
//
//    @Mock
//    private WalletConfigService walletConfigService;
//
//    @Spy
//    private CredentialStorageUtil credentialUtil;
//
//    @Spy
//    private ImageUtil imageUtil;
//
//    @InjectMocks
//    @Spy
//    protected EDCICredentialModelUtil edciCredentialModelUtil;
//
//    EuropassPresentationDTO presentationDTO = new EuropassPresentationDTO();
//    EuropassCredentialDTO europassCredentialDTO;

    @Before
    public void prepareInputs() throws Exception {

//        Mockito.doReturn("mocked").when(edciMessageService).getMessage(Mockito.anyString());
//
//        //Shame
//        ReflectionTestUtils.setField(europassCredentialPresentationMapper, "presentationCommonsMapper", presentationCommonsMapper);
//
//        File initialFile = new File("src/test/resources/verifiablePresentation/in/verifiable_presentation_credential_VP.xml");
//        presentationDTO = (EuropassPresentationDTO) edciCredentialModelUtil.fromFile(initialFile);
//        InputStream targetStream = new FileInputStream(initialFile);
//        presentationDTO = xmlUtil.fromInputStream(targetStream, EuropassPresentationDTO.class);
//        presentationDTO.setExpirationDate(new Date());

//        File initialFile = new File("src/test/resources/verifiablePresentation/in/verifiable_presentation_credential.xml");
//        InputStream targetStream = new FileInputStream(initialFile);
//        europassCredentialDTO = xmlUtil.fromInputStream(targetStream, EuropassCredentialDTO.class);

//        presentationDTO.setVerifications(new ArrayList<VerificationCheckDTO>() {{
//            add(new VerificationCheckDTO());
//        }});
//
//        presentationDTO.setVerifiableCredential(europassCredentialDTO);
//
//        List<VerificationCheckDTO> verifCheckList = new ArrayList<>();
//
//        VerificationCheckDTO vc = new VerificationCheckDTO();
//        vc.setStatusCode(0);
//        vc.setVerificationStep(VerificationSteps.SEAL);
//        vc.setDescription(new Text("seal", "es"));
//
//        VerificationCheckDTO vc1 = new VerificationCheckDTO();
//        vc1.setStatusCode(1);
//        vc1.setVerificationStep(VerificationSteps.FORMAT);
//        vc.setDescription(new Text("format", "es"));
//
//        VerificationCheckDTO vc2 = new VerificationCheckDTO();
//        vc2.setStatusCode(2);
//        vc2.setVerificationStep(VerificationSteps.REVOCATION);
//        vc.setDescription(new Text("revok", "es"));
//
//        verifCheckList.add(vc);
//        verifCheckList.add(vc1);
//        verifCheckList.add(vc2);
//
//        presentationDTO.setVerifications(verifCheckList);

    }

    @Test
    public void dummyTest() {
        boolean isDone = true;
        Assert.assertTrue(isDone);
    }
//    @Test
//    public void toEuropassCredentialPresentationView_shouldBuildAnEuropassCredentialPresentationView_whenMapperIsCalled() {
//
//        EuropassCredentialPresentationView europassCredentialDetailView = europassCredentialPresentationMapper.toEuropassCredentialPresentationView(europassCredentialDTO, true);
//
//        Assert.assertNotNull(europassCredentialDetailView);
//        Assert.assertEquals(20, europassCredentialDetailView.getAssessmentsList().size());
//        Assert.assertEquals(1, europassCredentialDetailView.getOrganizationsList().size());
//
//    }

   /* @Test
    public void downloadVerifiablePresentationPDF_shouldBuildPDF_whenHavingAValidCredential() throws Exception {

        try {
            FileUtils.cleanDirectory(new File("src/test/resources/verifiablePresentation/out"));
        } catch (Throwable t) {
        }

        Mockito.doReturn("https://webgate.acceptance.ec.europa.eu/europass/eportfolio/api/office/generate/pdf")
                .when(credentialService).getPdfDownloadUrl();
        Mockito.doReturn("https://dgempl-single-portal-demo-1.arhs-developments.com/europass/eportfolio/api/office/generate/png")
                .when(imageUtil).getImageDownloadUrl();
        Mockito.doReturn(new ShareLinkDTO())
                .when(shareLinkService).createShareLink(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Date.class));
        Mockito.doReturn("https://www.google.com")
                .when(walletConfigService).getString(ArgumentMatchers.anyString());

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("verifiablePresentation/in/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(templateResolver);

        byte[] pdfBytes = credentialService.downloadVerifiablePresentationPDF(presentationDTO, "https://dev.everisdx.io/europass2/edci-viewer/#/shareview/38PCz2kDp4JkAzWl", EDCIWalletConstants.CREDENTIAL_PDF_TYPE_FULL).getBody().getByteArray();

        FileUtils.writeByteArrayToFile(new File("src/test/resources/verifiablePresentation/out/verifiable_presentation"
                + new SimpleDateFormat("HHmmss").format(new Date()) + ".pdf"), pdfBytes);
        Assert.assertTrue(true);
    }

    @Test
    public void downloadDiplomaImg_shouldBuildImage_whenHavingAValidCredential() throws Exception {

        try {
            FileUtils.cleanDirectory(new File("src/test/resources/verifiablePresentation/out"));
        } catch (Throwable t) {
        }

        Mockito.doReturn("https://dgempl-single-portal-demo-1.arhs-developments.com/europass/eportfolio/api/office/generate/png")
                .when(imageUtil).getImageDownloadUrl();
        Mockito.doReturn("https://www.google.com")
                .when(walletConfigService).getString(ArgumentMatchers.anyString());

        byte[] pdfBytes = diplomaUtils.generateDiplomaImage(presentationDTO);

        FileUtils.writeByteArrayToFile(new File("src/test/resources/verifiablePresentation/out/diploma"
                + new SimpleDateFormat("HHmmss").format(new Date()) + ".jpg"), pdfBytes);

    }*/
}