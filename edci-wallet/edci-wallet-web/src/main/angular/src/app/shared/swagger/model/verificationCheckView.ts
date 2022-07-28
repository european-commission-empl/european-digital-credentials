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
import { LocalizedItem } from './localizedItem';


export interface VerificationCheckView { 
    type?: string;
    description?: LocalizedItem;
    status?: string;
    statusCode?: number;
    verificationStep?: VerificationCheckView.VerificationStepEnum;
}
export namespace VerificationCheckView {
    export type VerificationStepEnum = 'FORMAT' | 'SEAL' | 'OWNER' | 'REVOCATION' | 'ACCREDITATION' | 'VALIDITY';
    export const VerificationStepEnum = {
        FORMAT: 'FORMAT' as VerificationStepEnum,
        SEAL: 'SEAL' as VerificationStepEnum,
        OWNER: 'OWNER' as VerificationStepEnum,
        REVOCATION: 'REVOCATION' as VerificationStepEnum,
        ACCREDITATION: 'ACCREDITATION' as VerificationStepEnum,
        VALIDITY: 'VALIDITY' as VerificationStepEnum
    };
}
