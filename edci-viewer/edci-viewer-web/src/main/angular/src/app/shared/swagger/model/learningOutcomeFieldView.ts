/**
 * Viewer's API
 * Viewer's public API
 *
 * OpenAPI spec version: 2.0.0
 * Contact: EMPL-ELM-SUPPORT@ec.europa.eu
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { IdentifierFieldView } from './identifierFieldView';
import { LinkFieldView } from './linkFieldView';
import { NoteFieldView } from './noteFieldView';


export interface LearningOutcomeFieldView { 
    title?: string;
    dcType?: string;
    reusabilityLevel?: string;
    relatedESCOSkill?: Array<LinkFieldView>;
    relatedSkill?: Array<LinkFieldView>;
    identifier?: Array<IdentifierFieldView>;
    additionalNote?: Array<NoteFieldView>;
}
