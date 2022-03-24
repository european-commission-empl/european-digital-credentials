// used by serve / when no configuration is provided on the build script
// run npm run build

export const environment = {
    production: window['EDCIContext']
        ? window['EDCIContext'].production
        : false,
    enableDevToolRedux: window['EDCIContext']
        ? window['EDCIContext'].enableDevToolRedux
        : true,
    hasLabelsOnly: window['EDCIContext']
        ? window['EDCIContext'].hasLabelsOnly
        : true,
    issuerBaseUrl : window['EDCIContext']
        ? window['EDCIContext'].issuerBaseUrl
        : 'http://localhost:8383/europass2/edci-issuer',
    apiBaseUrl: window['EDCIContext']
        ? window['EDCIContext'].apiBaseUrl
        : 'http://localhost:8383/europass2/edci-issuer/api',
    viewerBaseUrl: window['EDCIContext']
        ? window['EDCIContext'].viewerBaseUrl
        : 'http://localhost:8282/europass2/edci-viewer',
    loginUrl: window['EDCIContext']
        ? window['EDCIContext'].loginUrl
        : '/auth/oidc/eulogin',
    logoutUrl: window['EDCIContext']
        ? window['EDCIContext'].logoutUrl
        : '/auth/oidc/eulogin/logout',
    europassRoot: window['EDCIContext']
        ? window['EDCIContext'].europassRoot
        : 'https://webgate.acceptance.ec.europa.eu/europass',
    csrfEnabled: window['EDCIContext']
        ? window['EDCIContext'].csrfEnabled
        : false,
    isMockUser: window['EDCIContext'] ? window['EDCIContext'].isMockUser : true,
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
    concentText: window['EDCIContext']
        ? window['EDCIContext'].concentText
        : 'credential-builder.concent',
    homeCredentialsForIssuersTitle: window['EDCIContext']
        ? window['EDCIContext'].homeCredentialsForIssuersTitle
        : 'home.menu-links.credentials-for-issuers.title',
    homeCredentialsForIssuersDescription: window['EDCIContext']
        ? window['EDCIContext'].homeCredentialsForIssuersDescription
        : 'home.menu-links.credentials-for-issuers.content.description-1',
    homeCredentialsForIssuersDescriptionWithLink: window['EDCIContext']
        ? window['EDCIContext'].homeCredentialsForIssuersDescriptionWithLink
        : 'home.menu-links.credentials-for-issuers.content.description-2',
    enabledLocalSealing: window['EDCIContext'] ? window['EDCIContext'].enabledLocalSealing : true
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
