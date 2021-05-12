// used by serve / when no configuration is provided on the build script
// run npm run build

export const environment = {
    production: false,
    enableDevToolRedux: true,
    hasLabelsOnly: true,
    apiBaseUrl: 'http://localhost:8080/europass2/edci-issuer',
    viewerBaseUrl: 'http://localhost:8080/europass2/edci-viewer',
    loginUrl: '/auth/oidc/eulogin',
    logoutUrl: '/auth/oidc/eulogin/logout',
    europassRoot: 'https://webgate.acceptance.ec.europa.eu/europass',
    isMockUser: true,
    csrfEnabled: false,
    hasBranding: false,
    headerImagePath: 'imagePath',
    homeMainTitle: 'translated.label.main.title',
    homeMainDescription: 'translated.label.main.description',
    concentText: 'translated.label.concentText',
    homeCredentialsForIssuersTitle: 'translated.label.home.credentials.title',
    homeCredentialsForIssuersDescription:
        'translated.label.home.credentials.description',
    homeCredentialsForIssuersDescriptionWithLink:
        'translated.label.home.credentials.description.link',
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
