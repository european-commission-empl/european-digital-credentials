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
import { GradingSchemeFieldView } from './gradingSchemeFieldView';
import { IdentifierFieldView } from './identifierFieldView';
import { LinkFieldView } from './linkFieldView';
import { NoteFieldView } from './noteFieldView';


export interface AssessmentSpecTabView { 
    altLabel?: Array<string>;
    category?: Array<string>;
    description?: Array<string>;
    homepage?: Array<LinkFieldView>;
    identifier?: Array<IdentifierFieldView>;
    dateModified?: string;
    additionalNote?: Array<NoteFieldView>;
    supplementaryDocument?: Array<LinkFieldView>;
    title?: string;
    dcType?: Array<string>;
    mode?: Array<string>;
    language?: Array<string>;
    gradingScheme?: GradingSchemeFieldView;
}
