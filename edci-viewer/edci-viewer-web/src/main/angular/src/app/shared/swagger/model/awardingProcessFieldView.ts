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
import { AgentView } from './agentView';
import { AssessmentTabView } from './assessmentTabView';
import { ClaimView } from './claimView';
import { IdentifierFieldView } from './identifierFieldView';
import { LocationFieldView } from './locationFieldView';
import { NoteFieldView } from './noteFieldView';


export interface AwardingProcessFieldView { 
    used?: Array<AssessmentTabView>;
    awardingBody?: Array<AgentView>;
    awardingDate?: string;
    awards?: Array<ClaimView>;
    description?: string;
    educationalSystemNote?: string;
    identifier?: Array<IdentifierFieldView>;
    location?: LocationFieldView;
    additionalNote?: Array<NoteFieldView>;
}
