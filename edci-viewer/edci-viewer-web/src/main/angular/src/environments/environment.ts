import { EuiEnvConfig } from '@eui/core';

export const environment: EuiEnvConfig = {
    viewerBaseUrl: window['EDCIContext']
        ? window['EDCIContext'].viewerBaseUrl
        : 'http://localhost:4200',
    production: window['EDCIContext']
        ? window['EDCIContext'].production
        : false,
    serviceWorker: window['EDCIContext']
        ? window['EDCIContext'].serviceWorker
        : false,
    enableDevToolRedux: window['EDCIContext']
        ? window['EDCIContext'].enableDevToolRedux
        : true,
    hasLabelsOnly: window['EDCIContext']
        ? window['EDCIContext'].hasLabelsOnly
        : true,
    apiBaseUrl: window['EDCIContext']
        ? window['EDCIContext'].apiBaseUrl
        : 'http://localhost:8282/europass2/edci-viewer/api',
    issuerBaseUrl: window['EDCIContext']
        ? window['EDCIContext'].issuerBaseUrl
        : 'http://localhost:8383/europass2/edci-issuer',
    walletBaseUrl: window['EDCIContext']
        ? window['EDCIContext'].walletBaseUrl
        : 'http://localhost:8181/europass2/edci-wallet',
    europassRoot: window['EDCIContext']
        ? window['EDCIContext'].europassRoot
        : 'https://webgate.acceptance.ec.europa.eu/europass',
    ePortfolioUrl: window['EDCIContext']
        ? window['EDCIContext'].ePortfolioUrl
        : // tslint:disable-next-line: max-line-length
        'https://webgate.acceptance.ec.europa.eu/europass/eportfolio/api/europass-auth/authenticate?redirect_uri=https://webgate.acceptance.ec.europa.eu/d/empl/europass/',
    downloadCredentialUrl: window['EDCIContext']
        ? window['EDCIContext'].downloadCredentialUrl
        : '/v1/credentials/verifiable',
    walletAddressParameter: window['EDCIContext']
        ? window['EDCIContext'].walletAddressParameter
        : '{walletAddress}',
    walletAddress : window['EDCIContext'] ? window['EDCIContext'].walletAddress : 'europass.eu/',
    downloadSharedCredentialUrl: window['EDCIContext']
        ? window['EDCIContext'].downloadSharedCredentialUrl
        : '/v1/sharelinks/{shareHash}/presentation',
    shareHashParameter: window['EDCIContext']
        ? window['EDCIContext'].shareHashParameter
        : '{shareHash}',
    loginUrl: window['EDCIContext']
        ? window['EDCIContext'].loginUrl
        : '/auth/oidc/eulogin',
    logoutUrl: window['EDCIContext']
        ? window['EDCIContext'].logoutUrl
        : '/auth/oidc/eulogin/logout',
    isMockUser: window['EDCIContext']
        ? window['EDCIContext'].isMockUser === true
        : true,
    csrfEnabled: window['EDCIContext']
        ? window['EDCIContext'].csrfEnabled
        : false,
    hasBranding: window['EDCIContext']
        ? window['EDCIContext'].hasBranding
        : true,
    headerImagePath: window['EDCIContext']
        ? window['EDCIContext'].headerImagePath
        : 'assets/images/logo_countries/',
    homeMainTitle: window['EDCIContext']
        ? window['EDCIContext'].homeMainTitle
        : 'home.main-info.title',
    homeMainDescription: window['EDCIContext']
        ? window['EDCIContext'].homeMainDescription
        : 'home.main-info.description',
    homeMenuMainTitle: window['EDCIContext']
        ? window['EDCIContext'].homeMenuMainTitle
        : 'home.credentialsForCitizens',
    homeMenuDescription: window['EDCIContext']
        ? window['EDCIContext'].homeMenuDescription
        : 'home.menu.description',
    displayDownloadOriginal: window['EDCIContext']
        ? window['EDCIContext'].displayDownloadOriginal
        : true,
};
