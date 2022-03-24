// used by serve / when no configuration is provided on the build script
// run npm run build

export const environment = {
    production: window['EDCIContext'].production === 'true',
    serviceWorker: window['EDCIContext'].serviceWorker === 'true',
    enableDevToolRedux: window['EDCIContext'].enableDevToolRedux === 'true',
    hasLabelsOnly: window['EDCIContext'].hasLabelsOnly === 'true',
    viewerBaseUrl: window['EDCIContext'].viewerBaseUrl,
    apiBaseUrl: window['EDCIContext'].apiBaseUrl,
    issuerBaseUrl: window['EDCIContext'].issuerBaseUrl,
    walletBaseUrl: window['EDCIContext'].walletBaseUrl,
    europassRoot: window['EDCIContext'].europassRoot,
    ePortfolioUrl: window['EDCIContext'].ePortfolioUrl,
    downloadCredentialUrl: window['EDCIContext'].downloadCredentialUrl,
    walletAddressParameter: window['EDCIContext'].walletAddressParameter,
    downloadSharedCredentialUrl: window['EDCIContext'].downloadSharedCredentialUrl,
    shareHashParameter: window['EDCIContext'].shareHashParameter,
    loginUrl: window['EDCIContext'].loginUrl,
    logoutUrl: window['EDCIContext'].logoutUrl,
    isMockUser: window['EDCIContext'].isMockUser === 'true',
    csrfEnabled: window['EDCIContext'].csrfEnabled === 'true',
    hasBranding: window['EDCIContext'].hasBranding === 'true',
    headerImagePath: window['EDCIContext'].headerImagePath,
    homeMainTitle: window['EDCIContext'].homeMainTitle,
    homeMainDescription: window['EDCIContext'].homeMainDescription,
    homeMenuMainTitle: window['EDCIContext'].homeMenuMainTitle,
    homeMenuDescription: window['EDCIContext'].homeMenuDescription,
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
