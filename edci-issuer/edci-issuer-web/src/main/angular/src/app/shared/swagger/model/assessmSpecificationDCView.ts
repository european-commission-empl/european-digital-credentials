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
import { LearningSpecificationDCView } from './learningSpecificationDCView';
import { NoteDTView } from './noteDTView';
import { ScoringSchemeDTView } from './scoringSchemeDTView';
import { TextDTView } from './textDTView';
import { WebDocumentDCView } from './webDocumentDCView';


export interface AssessmSpecificationDCView { 
    identifier?: Array<IdentifierDTView>;
    assessmentType?: Array<CodeDTView>;
    title?: TextDTView;
    alternativeLabel?: Array<TextDTView>;
    description?: NoteDTView;
    additionalNote?: Array<NoteDTView>;
    homePage?: Array<WebDocumentDCView>;
    supplementaryDocument?: Array<WebDocumentDCView>;
    language?: Array<CodeDTView>;
    mode?: CodeDTView;
    gradingSchemes?: ScoringSchemeDTView;
    proves?: Array<LearningSpecificationDCView>;
}
