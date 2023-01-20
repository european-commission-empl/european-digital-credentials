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


export interface XmlValidationCertificateQualification { 
    constraint?: Array<XmlConstraint>;
    conclusion: XmlConclusion;
    id?: string;
    dateTime?: Date;
    validationTime?: XmlValidationCertificateQualification.ValidationTimeEnum;
    certificateQualification?: XmlValidationCertificateQualification.CertificateQualificationEnum;
}
export namespace XmlValidationCertificateQualification {
    export type ValidationTimeEnum = 'CERTIFICATE_ISSUANCE_TIME' | 'BEST_SIGNATURE_TIME' | 'VALIDATION_TIME';
    export const ValidationTimeEnum = {
        CERTIFICATEISSUANCETIME: 'CERTIFICATE_ISSUANCE_TIME' as ValidationTimeEnum,
        BESTSIGNATURETIME: 'BEST_SIGNATURE_TIME' as ValidationTimeEnum,
        VALIDATIONTIME: 'VALIDATION_TIME' as ValidationTimeEnum
    };
    export type CertificateQualificationEnum = 'QCERT_FOR_ESIG_QSCD' | 'QCERT_FOR_ESEAL_QSCD' | 'QCERT_FOR_ESIG' | 'QCERT_FOR_ESEAL' | 'QCERT_FOR_WSA' | 'CERT_FOR_ESIG' | 'CERT_FOR_ESEAL' | 'CERT_FOR_WSA' | 'NA';
    export const CertificateQualificationEnum = {
        QCERTFORESIGQSCD: 'QCERT_FOR_ESIG_QSCD' as CertificateQualificationEnum,
        QCERTFORESEALQSCD: 'QCERT_FOR_ESEAL_QSCD' as CertificateQualificationEnum,
        QCERTFORESIG: 'QCERT_FOR_ESIG' as CertificateQualificationEnum,
        QCERTFORESEAL: 'QCERT_FOR_ESEAL' as CertificateQualificationEnum,
        QCERTFORWSA: 'QCERT_FOR_WSA' as CertificateQualificationEnum,
        CERTFORESIG: 'CERT_FOR_ESIG' as CertificateQualificationEnum,
        CERTFORESEAL: 'CERT_FOR_ESEAL' as CertificateQualificationEnum,
        CERTFORWSA: 'CERT_FOR_WSA' as CertificateQualificationEnum,
        NA: 'NA' as CertificateQualificationEnum
    };
}
