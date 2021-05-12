// activated by running :
// npm run build -- --configuration=qatomcat
// OR
// npm run build-qatomcat

export const environment = {
    production: true,
    serviceWorker: true,
    enableDevToolRedux: true,
    hasLabelsOnly: true,
    apiBaseUrl: 'https://dev.everisdx.io:443/europass2/edci-viewer',
    issuerBaseUrl: 'https://dev.everisdx.io:443/europass2/edci-issuer',
    walletBaseUrl: 'https://dev.everisdx.io:443/europass2/edci-wallet',
    ePortfolioUrl:
        // tslint:disable-next-line: max-line-length
        'https://webgate.acceptance.ec.europa.eu/europass/eportfolio/api/europass-auth/authenticate?redirect_uri=https://webgate.acceptance.ec.europa.eu/d/empl/europass/',
    europassRoot: 'https://webgate.acceptance.ec.europa.eu/europass',
    downloadCredentialUrl: '/api/v1/credentials/{walletAddress}/verifiable',
    walletAddressParameter: '{walletAddress}',
    downloadSharedCredentialUrl: '/api/v1/sharelinks/{shareHash}/presentation',
    shareHashParameter: '{shareHash}',
    loginUrl: '/auth/oidc/eulogin',
    logoutUrl: '/auth/oidc/eulogin/logout',
    isMockUser: false,
    csrfEnabled: false,
    hasBranding: true,
    headerImagePath: 'assets/images/logo_countries/',
    homeMainTitle: 'home.main-info.title',
    homeMainDescription: 'home.main-info.description',
    homeMenuMainTitle: 'home.credentialsForCitizens',
    homeMenuDescription: 'home.menu.description',
};
