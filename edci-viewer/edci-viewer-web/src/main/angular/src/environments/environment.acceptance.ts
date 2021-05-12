// used by serve / when no configuration is provided on the build script
// run npm run build

export const environment = {
    production: true,
    serviceWorker: true,
    enableDevToolRedux: true,
    hasLabelsOnly: true,
    apiBaseUrl: 'https://webgate.acceptance.ec.europa.eu/europass/edci-viewer',
    issuerBaseUrl:
        'https://webgate.acceptance.ec.europa.eu/europass/edci-issuer',
    europassRoot: 'https://webgate.acceptance.ec.europa.eu/europass',
    ePortfolioUrl:
        // tslint:disable-next-line: max-line-length
        'https://webgate.acceptance.ec.europa.eu/europass/eportfolio/api/europass-auth/authenticate?redirect_uri=https://webgate.acceptance.ec.europa.eu/d/empl/europass/',
    walletBaseUrl:
        'https://webgate.acceptance.ec.europa.eu/europass/edci-wallet',
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

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
