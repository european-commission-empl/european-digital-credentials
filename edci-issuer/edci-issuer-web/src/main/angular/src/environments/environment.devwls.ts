// activated by running :
// npm run build -- --configuration=devwls
// OR
// npm run build-devwls

export const environment = {
    production: true,
    enableDevToolRedux: true,
    hasLabelsOnly: true,
    apiBaseUrl: 'http://dev.everisdx.io:10001/issuer',
    viewerBaseUrl: 'http://dev.everisdx.io:10001/viewer',
    loginUrl: '/auth/oidc/eulogin',
    logoutUrl: '/auth/oidc/eulogin/logout',
    europassRoot: 'https://webgate.acceptance.ec.europa.eu/europass',
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
