// activated by running :
// npm run build -- --configuration=devwls
// OR
// npm run build-devwls

export const environment = {
    production: true,
    serviceWorker: true,
    enableDevToolRedux: true,
    hasLabelsOnly: true,
    apiBaseUrl: 'http://dev.everisdx.io:10001/viewer',
    issuerBaseUrl: 'http://dev.everisdx.io:10001/issuer',
    walletBaseUrl: 'http://dev.everisdx.io:10001/wallet',
    europassRoot: 'https://webgate.acceptance.ec.europa.eu/europass',
    ePortfolioUrl:
        // tslint:disable-next-line: max-line-length
        'https://webgate.acceptance.ec.europa.eu/europass/eportfolio/api/europass-auth/authenticate?redirect_uri=https://webgate.acceptance.ec.europa.eu/d/empl/europass/',
    downloadCredentialUrl: '/api/v1/credentials/{walletAddress}/verifiable',
    walletAddressParameter: '{walletAddress}',
    downloadSharedCredentialUrl: '/api/v1/sharelinks/{shareHash}/presentation',
    shareHashParameter: '{shareHash}',
    loginUrl: '/auth/oidc/eulogin',
    logoutUrl: '/auth/oidc/eulogin/logout',
    isMockUser: false,
    csrfEnabled: true,
    hasBranding: true,
    headerImagePath: 'assets/images/logo_countries/',
    homeMainTitle: 'home.main-info.title',
    homeMainDescription: 'home.main-info.description',
    homeMenuMainTitle: 'home.credentialsForCitizens',
    homeMenuDescription: 'home.menu.description'
};
