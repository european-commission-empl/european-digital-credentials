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
import { TextDTView } from './textDTView';


export interface ResourceLearningOutcomeSpecView { 
    oid?: number;
    additionalInfo?: AdditionalInfo;
    defaultTitle: string;
    defaultLanguage: string;
    title: TextDTView;
    identifier?: Array<IdentifierDTView>;
    description?: NoteDTView;
    learningOutcomeType?: CodeDTView;
    reusabilityLevel?: CodeDTView;
    relatedESCOSkill?: Array<CodeDTView>;
    readonly links?: Array<Link>;
}