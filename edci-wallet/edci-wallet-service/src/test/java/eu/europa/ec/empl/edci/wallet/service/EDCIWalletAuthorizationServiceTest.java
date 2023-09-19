package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.repository.ShareLinkRepository;
import eu.europa.ec.empl.edci.wallet.repository.WalletRepository;
import eu.europa.ec.empl.edci.wallet.service.service.EDCIWalletAuthorizationService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.*;

public class EDCIWalletAuthorizationServiceTest extends AbstractUnitBaseTest {

    @Spy
    @InjectMocks
    EDCIWalletAuthorizationService edciWalletAuthorizationService;

    @Mock
    private ShareLinkRepository shareLinkRepository;

    @Mock
    private WalletRepository walletRepository;

    @Test
    public void isAuthorized_shouldCheckUserIdTrue_whenCalledWithValidUserIdAndNoMockUserActive() throws Exception {

        Mockito.doReturn(true).when(edciWalletAuthorizationService).isUser(ArgumentMatchers.anyString());

        boolean authorized = edciWalletAuthorizationService.isAuthorized("userId");

        Mockito.verify(edciWalletAuthorizationService).isUser(ArgumentMatchers.any());
        Assert.assertTrue(authorized);

    }

    @Test
    public void isAuthorized_shouldCheckUserIdFalse_whenCalledWithInvalidUserIdAndNoMockUserActive() throws Exception {

        Mockito.doReturn(false).when(edciWalletAuthorizationService).isUser(ArgumentMatchers.anyString());

        boolean authorized = edciWalletAuthorizationService.isAuthorized("userId");

        Mockito.verify(edciWalletAuthorizationService).isUser(ArgumentMatchers.any());
        Assert.assertFalse(authorized);

    }

    @Test
    public void isAuthorized_shouldCheckUserIdTrue_whenCalledWithValidUserIdAndMockUserActive() throws Exception {

        Mockito.doReturn(true).when(edciWalletAuthorizationService).isUser(ArgumentMatchers.anyString());

        boolean authorized = edciWalletAuthorizationService.isAuthorized("userId");

        Mockito.verify(edciWalletAuthorizationService).isUser(ArgumentMatchers.any());
        Assert.assertTrue(authorized);

    }

    @Test
    public void isAuthorized_shouldCheckUserIdFalse_whenCalledWithInvalidUserIdAndMockUserActive() throws Exception {

        Mockito.doReturn(false).when(edciWalletAuthorizationService).isUser(ArgumentMatchers.anyString());

        boolean authorized = edciWalletAuthorizationService.isAuthorized("userId");

        Mockito.verify(edciWalletAuthorizationService).isUser(ArgumentMatchers.any());
        Assert.assertFalse(authorized);

    }

    @Test
    public void isAuthorizedAddress_shouldCheckWalletAddressTrue_whenCalledWithValidWalletAddressBelongingToUserIdInToken() throws Exception {

        Mockito.doReturn(new WalletDAO() {{
            setUserId("userId");
        }}).when(walletRepository).fetchByWalletAddress(ArgumentMatchers.anyString());
        Mockito.doReturn(true).when(edciWalletAuthorizationService).isAuthorized(ArgumentMatchers.anyString());

        boolean authorized = edciWalletAuthorizationService.isAuthorizedAddress("walletAddress");

        Mockito.verify(edciWalletAuthorizationService).isAuthorized("userId");
        Assert.assertTrue(authorized);

    }

    @Test
    public void isAuthorizedAddress_shouldCheckWalletAddressFalse_whenCalledWithValidWalletAddressNotBelongingToUserIdInToken() throws Exception {

        Mockito.doReturn(new WalletDAO() {{
            setUserId("userId");
        }}).when(walletRepository).fetchByWalletAddress(ArgumentMatchers.anyString());
        Mockito.doReturn(false).when(edciWalletAuthorizationService).isAuthorized(ArgumentMatchers.anyString());

        boolean authorized = edciWalletAuthorizationService.isAuthorizedAddress("walletAddress");

        Mockito.verify(edciWalletAuthorizationService).isAuthorized("userId");
        Assert.assertFalse(authorized);

    }

    @Test(expected = EDCINotFoundException.class)
    public void isAuthorizedAddress_shouldshouldThrowNotFoundException_whenWalletAddressDoesNotBelongToAnyWallet() throws Exception {

        Mockito.doReturn(null).when(walletRepository).fetchByWalletAddress(ArgumentMatchers.anyString());

        boolean authorized = edciWalletAuthorizationService.isAuthorizedAddress("walletAddress");

    }

    @Test
    public void isAuthorizedSharelink_shouldCheckShareHashTrue_whenCalledWithValidShareHashBelongingToUserIdInToken() throws Exception {

        WalletDAO wallet = new WalletDAO() {{
            setUserId("userId");
        }};
        CredentialDAO credential = new CredentialDAO() {{
            setWallet(wallet);
        }};
        ShareLinkDAO shareLink = new ShareLinkDAO() {{
            setCredential(credential);
        }};
        Mockito.doReturn(shareLink).when(shareLinkRepository).fetchBySharedURL(ArgumentMatchers.anyString());
        Mockito.doReturn(true).when(edciWalletAuthorizationService).isAuthorized(ArgumentMatchers.anyString());

        boolean authorized = edciWalletAuthorizationService.isAuthorizedSharelink("shareHash");

        Mockito.verify(edciWalletAuthorizationService).isAuthorized("userId");
        Assert.assertTrue(authorized);

    }

    @Test
    public void isAuthorizedSharelink_shouldCheckShareHashFalse_whenCalledWithValidShareHashNotBelongingToUserIdInToken() throws Exception {

        WalletDAO wallet = new WalletDAO() {{
            setUserId("userId");
        }};
        CredentialDAO credential = new CredentialDAO() {{
            setWallet(wallet);
        }};
        ShareLinkDAO shareLink = new ShareLinkDAO() {{
            setCredential(credential);
        }};
        Mockito.doReturn(shareLink).when(shareLinkRepository).fetchBySharedURL(ArgumentMatchers.anyString());
        Mockito.doReturn(false).when(edciWalletAuthorizationService).isAuthorized(ArgumentMatchers.anyString());

        boolean authorized = edciWalletAuthorizationService.isAuthorizedSharelink("shareHash");

        Mockito.verify(edciWalletAuthorizationService).isAuthorized("userId");
        Assert.assertFalse(authorized);

    }

    @Test(expected = EDCINotFoundException.class)
    public void isAuthorizedSharelink_shouldThrowNotFoundException_whenShareHashDoesNotExists() throws Exception {

        Mockito.doReturn(null).when(shareLinkRepository).fetchBySharedURL(ArgumentMatchers.anyString());

        boolean authorized = edciWalletAuthorizationService.isAuthorizedSharelink("shareHash");

    }

}