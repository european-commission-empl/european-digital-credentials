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
import { XmlConclusion } from './xmlConclusion';
import { XmlConstraint } from './xmlConstraint';


export interface XmlValidationProcessBasicSignatures { 
    constraint?: Array<XmlConstraint>;
    conclusion: XmlConclusion;
    bestSignatureTime?: Date;
}
