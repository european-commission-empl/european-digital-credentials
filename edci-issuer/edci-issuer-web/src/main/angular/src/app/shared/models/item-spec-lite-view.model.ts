import { AdditionalInfo, TextDTView } from '@shared/swagger';

// Added type that has all properties of the *SpecLiteView items
export interface ItemSpecLiteView {
    oid?: number;
    additionalInfo?: AdditionalInfo;
    displayName?: string;
    label?: string;
    defaultLanguage: string;
    title?: TextDTView;
    preferredName?: TextDTView;
}
