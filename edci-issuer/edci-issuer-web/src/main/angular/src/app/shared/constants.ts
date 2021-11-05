export abstract class Constants {
    public static readonly MIN_CHAR_SEARCH = 1;
    public static readonly MAX_LENGTH_DEFAULT = 255;
    public static readonly MAX_LENGTH_LONG = 4000;
    public static readonly MAX_LENGTH_INTEGERS = 9;
    public static readonly SHORT_DATE = 'YYYY-MM-DD';
    public static readonly LONG_DATE = 'YYYY-MM-DDTHH:mm:ssZ';
    public static readonly EMPTY_RESULT_ID = '-1';
    public static readonly URL_REGULAR_EXPRESSION = new RegExp(
        '^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]'
    );
    public static readonly INTEGER_REGULAR_EXPRESSION = new RegExp('^[0-9]*$');
    public static readonly PREPARE_YOUR_DATA_URL = 'https://europa.eu/europass/en/preparing-credentials-europass-digital-credentials';
    public static readonly BATCH_STATUS = {
        FAILED: 'FAILED',
        STOPPED: 'STOPPED',
        COMPLETED: 'COMPLETED',
        STARTED: 'STARTED'
    };
}

export type Entities = 'achievement' | 'organization' | 'activity' | 'entitlement'
                    | 'htmlTemplate' | 'assessment' | 'credential' | 'learningOutcome' | 'subAchievement';
