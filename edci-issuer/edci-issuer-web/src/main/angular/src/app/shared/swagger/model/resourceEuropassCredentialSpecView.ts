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
import { IdentifierDTView } from './identifierDTView';
import { Link } from './link';
import { NoteDTView } from './noteDTView';
import { SubresourcesOids } from './subresourcesOids';
import { TextDTView } from './textDTView';


export interface ResourceEuropassCredentialSpecView { 
    oid?: number;
    additionalInfo?: AdditionalInfo;
    defaultTitle: string;
    defaultLanguage: string;
    title: TextDTView;
    description?: NoteDTView;
    type: CodeDTView;
    issuanceDate?: string;
    expirationDate?: string;
    identifier?: Array<IdentifierDTView>;
    relAchieved?: SubresourcesOids;
    relPerformed?: SubresourcesOids;
    relEntitledTo?: SubresourcesOids;
    relIssuer?: SubresourcesOids;
    relDiploma?: SubresourcesOids;
    readonly links?: Array<Link>;
}
