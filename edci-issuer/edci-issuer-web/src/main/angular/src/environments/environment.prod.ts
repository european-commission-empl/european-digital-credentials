// used by serve / when no configuration is provided on the build script
// run npm run build

export const environment = {
    production: true,
    hasLabelsOnly: false,
    apiBaseUrl: 'https://europa.eu/europass/digital-credentials/issuer',
    viewerBaseUrl: 'https://europa.eu/europass/digital-credentials/viewer',
    enableDevToolRedux: true,
    loginUrl: '/auth/oidc/eulogin',
    logoutUrl: '/auth/oidc/eulogin/logout',
    europassRoot: 'https://europa.eu/europass',
    isMockUser: false,
    csrfEnabled: true,
    hasBranding: true,
    headerImagePath: 'assets/images/logo_countries/',
    homeMainTitle: 'home.main-info.title',
    homeMainDescription: 'home.main-info.description',
    concentText: 'credential-builder.concent',
    homeCredentialsForIssuersTitle:
        'home.menu-links.credentials-for-issuers.title',
    homeCredentialsForIssuersDescription:
        'home.menu-links.credentials-for-issuers.content.description-1',
    homeCredentialsForIssuersDescriptionWithLink:
        'home.menu-links.credentials-for-issuers.content.description-2',
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
