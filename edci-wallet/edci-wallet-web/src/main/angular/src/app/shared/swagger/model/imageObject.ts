/**
 * Wallet API
 * API Wallet description
 *
 * OpenAPI spec version: 1.0.0
 * Contact: edci.support@eu.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { Code } from './code';


export interface ImageObject { 
    id?: string;
    contentType?: Code;
    contentEncoding?: Code;
    contentSize?: number;
<<<<<<< HEAD
    content?: Array<string>;
    contentUrl?: string;
=======
    content: Array<string>;
    contentUrl?: string;
    identifiableName?: string;
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
}
