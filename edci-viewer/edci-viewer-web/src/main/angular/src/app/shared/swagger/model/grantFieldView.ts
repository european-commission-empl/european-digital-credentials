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
import { LinkFieldView } from './linkFieldView';


export interface GrantFieldView { 
    contentUrl?: string;
    description?: string;
    supplementaryDocument?: Array<LinkFieldView>;
    title?: string;
    dcType?: string;
}
