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
import { TextDTView } from './textDTView';
import { WebDocumentDCView } from './webDocumentDCView';


export interface LearningActSpecificationDCView { 
    identifier?: Array<IdentifierDTView>;
    learningActivityType?: Array<CodeDTView>;
    title?: TextDTView;
    altLabel?: TextDTView;
    description?: NoteDTView;
    additionalNote?: Array<NoteDTView>;
    homePage?: Array<WebDocumentDCView>;
    supplementaryDocument?: Array<WebDocumentDCView>;
    volumeOfLearning?: number;
    language?: Array<CodeDTView>;
    mode?: Array<CodeDTView>;
    contactHours?: Array<string>;
}
