// activated by running :
// npm run build -- --configuration=qatomcat
// OR
// npm run build-qatomcat

export const environment = {
    production: window['EDCIContext'].production === 'true',
    enableDevToolRedux: window['EDCIContext'].enableDevToolRedux === 'true',
    hasLabelsOnly: window['EDCIContext'].hasLabelsOnly === 'true',
    issuerBaseUrl : window['EDCIContext'].issuerBaseUrl,
    apiBaseUrl: window['EDCIContext'].apiBaseUrl,
    viewerBaseUrl: window['EDCIContext'].viewerBaseUrl,
    loginUrl: window['EDCIContext'].loginUrl,
    logoutUrl: window['EDCIContext'].logoutUrl,
    europassRoot: window['EDCIContext'].europassRoot,
    csrfEnabled: window['EDCIContext'].csrfEnabled === 'true',
    isMockUser: window['EDCIContext'].isMockUser === 'true',
    hasBranding: window['EDCIContext'].hasBranding === 'true',
    headerImagePath: window['EDCIContext'].headerImagePath,
    homeMainTitle: window['EDCIContext'].homeMainTitle,
    homeMainDescription: window['EDCIContext'].homeMainDescription,
    concentText: window['EDCIContext'].concentText,
    homeCredentialsForIssuersTitle: window['EDCIContext'].homeCredentialsForIssuersTitle,
    homeCredentialsForIssuersDescription: window['EDCIContext'].homeCredentialsForIssuersDescription,
    homeCredentialsForIssuersDescriptionWithLink: window['EDCIContext'].homeCredentialsForIssuersDescriptionWithLink,
    enabledLocalSealing: window['EDCIContext'].enabledLocalSealing === 'true'
};
