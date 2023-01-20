export abstract class Constants {
    public static readonly MIN_CHAR_SEARCH = 1;
    public static readonly MAX_LENGTH_DEFAULT = 255;
    public static readonly MAX_LENGTH_LABELS = 30;
    public static readonly MAX_LENGTH_LONG = 4000;
    public static readonly MAX_LENGTH_INTEGERS = 9;
    public static readonly SHORT_DATE = 'YYYY-MM-DD';
    public static readonly LONG_DATE = 'YYYY-MM-DDTHH:mm:ssZ';
    public static readonly MEDIUM_DATE = 'DD/MM/YYYY HH:mm';
    public static readonly EMPTY_RESULT_ID = '-1';
    public static readonly URL_REGULAR_EXPRESSION = new RegExp(
        '^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]'
    );
    public static readonly INTEGER_REGULAR_EXPRESSION = new RegExp('^[0-9]*$');
    public static readonly PREPARE_YOUR_DATA_URL =
        'https://europa.eu/europass/en/preparing-credentials-europass-digital-credentials';
    public static readonly BATCH_STATUS = {
        FAILED: 'FAILED',
        STOPPED: 'STOPPED',
        COMPLETED: 'COMPLETED',
        STARTED: 'STARTED',
    };
    public static readonly OPEN_ENTITY_MODAL_CLICK_EVENT =
        'open-entity-modal-click-event';
    public static readonly EUI_INTERNAL_ID_ATTRIBUTE =
        'ng-reflect-eui-internal-id';
}

export type Entities =
    | 'achievement'
    | 'organization'
    | 'activity'
    | 'entitlement'
    | 'htmlTemplate'
    | 'assessment'
    | 'credential'
    | 'learningOutcome'
    | 'subAchievement';

export const ENTITES_SEARCH_FIELDS = {
    achievement: ['\'title.contents.content', '\'label'],
    organization: ['\'preferredName.contents.content', '\'label'],
    activity: ['\'title.contents.content', '\'label'],
    entitlement: ['\'title.contents.content', '\'label'],
    htmlTemplate: ['\'label'],
    assessment: ['\'title.contents.content', '\'label'],
    credential: ['\'title.contents.content', '\'label'],
    learningOutcome: ['\'name.contents.content', '\'label']
};

export const TIME_FORMAT = {
    parse: {
        dateInput: 'DD/MM/YYYY HH:mm',
    },
    display: {
        dateInput: 'DD/MM/YYYY HH:mm',
        monthYearLabel: 'DD/MM/YYYY HH:mm',
        dateA11yLabel: 'LL',
        monthYearA11yLabel: 'DD/MM/YYYY HH:mm',
    },
};

export const MODAL_LIMITS = 2;
