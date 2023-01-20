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
import { EntitlemSpecificationDCView } from './entitlemSpecificationDCView';
import { IdentifierDTView } from './identifierDTView';
import { Link } from './link';
import { NoteDTView } from './noteDTView';
import { SubresourcesOids } from './subresourcesOids';
import { TextDTView } from './textDTView';


export interface ResourceEntitlementSpecView { 
    oid?: number;
    additionalInfo?: AdditionalInfo;
    displayName?: string;
    label?: string;
    defaultLanguage: string;
    title?: TextDTView;
    identifier?: Array<IdentifierDTView>;
    description?: NoteDTView;
    issuedDate?: string;
    expiryDate?: string;
    additionalNote?: Array<NoteDTView>;
    specifiedBy?: EntitlemSpecificationDCView;
    relHasPart?: SubresourcesOids;
    relValidWith?: SubresourcesOids;
    readonly links?: Array<Link>;
}
