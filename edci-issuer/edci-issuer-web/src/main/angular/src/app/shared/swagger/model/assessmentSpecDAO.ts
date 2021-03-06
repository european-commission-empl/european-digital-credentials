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
import { AuditDAO } from './auditDAO';
import { CodeDTDAO } from './codeDTDAO';
import { IdentifierDTDAO } from './identifierDTDAO';
import { NoteDTDAO } from './noteDTDAO';
import { TextDTDAO } from './textDTDAO';
import { WebDocumentDCDAO } from './webDocumentDCDAO';


export interface AssessmentSpecDAO { 
    pk?: number;
    languages?: Array<string>;
    defaultTitle?: string;
    defaultLanguage?: string;
    identifier?: Array<IdentifierDTDAO>;
    assessmentType?: Array<CodeDTDAO>;
    title?: TextDTDAO;
    alternativeLabel?: Array<TextDTDAO>;
    description?: TextDTDAO;
    additionalNote?: Array<NoteDTDAO>;
    homePage?: Array<WebDocumentDCDAO>;
    supplementaryDocument?: Array<WebDocumentDCDAO>;
    language?: Array<CodeDTDAO>;
    mode?: CodeDTDAO;
    gradingSchemesNote?: NoteDTDAO;
    hasPart?: Array<AssessmentSpecDAO>;
    auditDAO?: AuditDAO;
}
