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
import { AddressFieldView } from './addressFieldView';


export interface LocationFieldView { 
    spatialCode?: Array<string>;
    address?: Array<AddressFieldView>;
}
