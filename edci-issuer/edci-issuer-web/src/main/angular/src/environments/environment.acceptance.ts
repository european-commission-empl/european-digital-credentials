// used by serve / when no configuration is provided on the build script
// run npm run build

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

/*
* For easier debugging in development mode, you can import the following file
* to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
*
* This import should be commented out in production mode because it will have a negative impact
* on performance if an error is thrown.
*/
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
