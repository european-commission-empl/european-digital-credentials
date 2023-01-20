import { EuiEnvConfig } from '@eui/core';

export const environment: EuiEnvConfig = {
    production: true,
    enableDevToolRedux: false,
    envDynamicConfig: {
        uri: 'assets/env-json-config.json',
        deepMerge: true,
        merge: ['modules'],
    },
};
