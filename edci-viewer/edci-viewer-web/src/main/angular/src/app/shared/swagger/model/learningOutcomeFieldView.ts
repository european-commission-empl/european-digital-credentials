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
import { IdentifierFieldView } from './identifierFieldView';
import { LinkFieldView } from './linkFieldView';


export interface LearningOutcomeFieldView { 
    name?: string;
    description?: string;
    type?: string;
    reusabilityLevel?: string;
    relatedESCOSkill?: Array<LinkFieldView>;
    relatedSkill?: Array<LinkFieldView>;
    identifier?: Array<IdentifierFieldView>;
}
