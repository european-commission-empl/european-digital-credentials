/**
 * API
 * API Swagger description
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { AdditionalInfo } from './additionalInfo';
import { CodeDTView } from './codeDTView';
import { Link } from './link';
import { NoteDTView } from './noteDTView';
import { SubresourcesOids } from './subresourcesOids';
import { TextDTView } from './textDTView';
import { WebDocumentDCView } from './webDocumentDCView';


export interface ResourceAccreditationSpecView { 
    oid?: number;
    additionalInfo?: AdditionalInfo;
    displayName?: string;
    label?: string;
    defaultLanguage: string;
    title?: TextDTView;
    dcType?: CodeDTView;
    description?: TextDTView;
    report?: WebDocumentDCView;
    status?: CodeDTView;
    decision?: CodeDTView;
    limitField?: Array<CodeDTView>;
    limitEQFLevel?: Array<CodeDTView>;
    limitJurisdiction?: Array<CodeDTView>;
    additionalNote?: Array<NoteDTView>;
    dateIssued?: string;
    languages?: Array<string>;
    expiryDate?: string;
    reviewDate?: string;
    supplementaryDocument?: Array<WebDocumentDCView>;
    homepage?: Array<WebDocumentDCView>;
    relAccreditingAgent?: SubresourcesOids;
    relOrganisation?: SubresourcesOids;
    readonly links?: Array<Link>;
}
