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
<<<<<<< HEAD


export interface EuropassCredentialVerificationView { 
    label?: string;
    description?: string;
    status?: number;
=======
import { LocalizedItem } from './localizedItem';


export interface EuropassCredentialVerificationView { 
    type?: string;
    description?: Array<LocalizedItem>;
    status?: string;
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
}
