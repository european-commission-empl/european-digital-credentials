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
import { XmlTrustedService } from './xmlTrustedService';


export interface XmlTrustedServiceProvider { 
    countryCode: string;
    trustedServices?: Array<XmlTrustedService>;
    tspname?: string;
    tspregistrationIdentifier?: string;
}