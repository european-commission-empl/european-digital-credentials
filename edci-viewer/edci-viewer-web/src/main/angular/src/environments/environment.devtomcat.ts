// activated by running :
// npm run build -- --configuration=devtomcat
// OR
// npm run build-devtomcat

export const environment = {
    production: true,
    serviceWorker: true,
    enableDevToolRedux: true,
    hasLabelsOnly: true,
    apiBaseUrl: 'https://dev.everisdx.io:7443/europass2/edci-viewer',
    issuerBaseUrl: 'https://dev.everisdx.io:7443/europass2/edci-issuer',
    walletBaseUrl: 'https://dev.everisdx.io:7443/europass2/edci-wallet',
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
