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
import { TextDTView } from './textDTView';


export interface LegalIdentifierDTView { 
    notation: string;
    creator?: string;
    schemeAgency?: TextDTView;
    schemeName?: string;
    dateIssued?: string;
    dcType?: Array<CodeDTView>;
    spatialId?: CodeDTView;
}
