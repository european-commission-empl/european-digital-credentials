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


export interface ContactPointFieldView { 
    address?: Array<AddressFieldView>;
    phone?: Array<string>;
    email?: Array<string>;
    walletAddress?: Array<string>;
}