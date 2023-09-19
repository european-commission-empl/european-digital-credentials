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
import { LocationDCView } from './locationDCView';
import { TextDTView } from './textDTView';


export interface AwardingProcessDCView { 
    identifier?: Array<IdentifierDTView>;
    description?: TextDTView;
    additionalNote?: Array<TextDTView>;
    awardingDate?: string;
    awardingLocation?: LocationDCView;
    educationalSystemNote?: CodeDTView;
}
