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


export interface IdentifierDTDAO { 
    pk?: number;
    content?: string;
    identifierSchemeId?: string;
    identifierSchemeAgencyName?: string;
    issuedDate?: Date;
    identifierType?: Array<string>;
}
