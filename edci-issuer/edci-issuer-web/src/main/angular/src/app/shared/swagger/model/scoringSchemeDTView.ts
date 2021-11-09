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
import { IdentifierDTView } from './identifierDTView';
import { NoteDTView } from './noteDTView';
import { TextDTView } from './textDTView';
import { WebDocumentDCView } from './webDocumentDCView';


export interface ScoringSchemeDTView { 
    identifier?: Array<IdentifierDTView>;
    title?: TextDTView;
    description?: NoteDTView;
    supplementaryDocument?: Array<WebDocumentDCView>;
}