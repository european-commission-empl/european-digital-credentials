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
import { IdentifierFieldView } from './identifierFieldView';
import { LocationFieldView } from './locationFieldView';
import { PeriodOfTimeFieldView } from './periodOfTimeFieldView';


export interface AwardingOpportunityFieldView { 
    awardingBody?: Array<AgentView>;
    identifier?: Array<IdentifierFieldView>;
    location?: LocationFieldView;
    temporal?: PeriodOfTimeFieldView;
}
