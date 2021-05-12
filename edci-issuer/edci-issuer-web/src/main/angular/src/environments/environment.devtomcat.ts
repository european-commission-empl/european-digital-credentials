// activated by running :
// npm run build -- --configuration=devtomcat
// OR
// npm run build-devtomcat

export const environment = {
    production: true,
    enableDevToolRedux: true,
    hasLabelsOnly: true,
    apiBaseUrl: 'https://dev.everisdx.io:7443/europass2/edci-issuer',
    viewerBaseUrl: 'https://dev.everisdx.io:7443/europass2/edci-viewer',
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
