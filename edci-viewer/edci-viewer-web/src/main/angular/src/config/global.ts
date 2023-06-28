import { GlobalConfig } from '@eui/core';

export const GLOBAL: GlobalConfig = {
    appTitle: 'CSDR-app',
    i18n: {
        i18nService: {
            defaultLanguage: 'en',
            languages: ['bg', 'cs', 'da', 'de', 'et', 'el', 'en', 'es', 'fr',
                'ga', 'it', 'lv', 'lt', 'hu', 'mt', 'nl', 'pl', 'pt', 'ro', 'sk', 'sl', 'fi', 'sv', 'hr',
                {
                    'code': 'is',
                    'label': 'íslenska'
                },
                {
                    'code': 'sr',
                    'label': 'srpski'
                },
                {
                    'code': 'mk',
                    'label': 'македонски јазик'
                },
                {
                    'code': 'no',
                    'label': 'norsk'
                },
                {
                    'code': 'tr',
                    'label': 'türkçe'
                }
            ],
        },
        i18nLoader: {
            i18nFolders: ['i18n-eui', 'i18n', 'i18n-ecl'],
        },
    },
    user: {
        defaultUserPreferences: {
            dashboard: { },
            lang: 'en',
        },
    },
    isShowConnectionStatus: false,
    mockMode: false
};
