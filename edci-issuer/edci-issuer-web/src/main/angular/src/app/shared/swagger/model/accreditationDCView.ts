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
import { CodeDTView } from './codeDTView';
import { IdentifierDTView } from './identifierDTView';
import { NoteDTView } from './noteDTView';
import { ScoreDTView } from './scoreDTView';
import { TextDTView } from './textDTView';
import { WebDocumentDCView } from './webDocumentDCView';


export interface AccreditationDCView { 
    id?: string;
    identifier?: Array<IdentifierDTView>;
    accreditationType?: CodeDTView;
    title?: TextDTView;
    description?: NoteDTView;
    decision?: ScoreDTView;
    report?: WebDocumentDCView;
    limitField?: Array<CodeDTView>;
    limitEqfLevel?: Array<CodeDTView>;
    limitJurisdiction?: Array<CodeDTView>;
    issueDate?: string;
    reviewDate?: string;
    expiryDate?: string;
    additionalNote?: Array<NoteDTView>;
    homePage?: Array<WebDocumentDCView>;
    supplementaryDocument?: Array<WebDocumentDCView>;
}
