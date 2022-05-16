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
import { CertificateWrapper } from './certificateWrapper';
import { RevocationWrapper } from './revocationWrapper';
import { SignatureWrapper } from './signatureWrapper';
import { TimestampWrapper } from './timestampWrapper';
import { XmlContainerInfo } from './xmlContainerInfo';
import { XmlTrustedList } from './xmlTrustedList';


export interface DiagnosticData { 
    usedCertificates?: Array<CertificateWrapper>;
<<<<<<< HEAD
    jaxbModel?: DiagnosticData;
    allSignatures?: Array<SignatureWrapper>;
    allTimestamps?: Array<TimestampWrapper>;
    zipComment?: string;
    containerType?: string;
    documentName?: string;
    signatures?: Array<SignatureWrapper>;
    trustedLists?: Array<XmlTrustedList>;
    containerInfo?: XmlContainerInfo;
    firstPolicyId?: string;
    listOfTrustedLists?: XmlTrustedList;
    firstSigningCertificateId?: string;
    signatureIdList?: Array<string>;
    containerInfoPresent?: boolean;
    firstSignatureDigestAlgorithm?: DiagnosticData.FirstSignatureDigestAlgorithmEnum;
    lotlcountryCode?: string;
    allRevocationData?: Array<RevocationWrapper>;
    mimetypeFilePresent?: boolean;
    firstSignatureFormat?: string;
    firstSignatureDate?: Date;
    allCounterSignatures?: Array<SignatureWrapper>;
    mimetypeFileContent?: string;
=======
    allSignatures?: Array<SignatureWrapper>;
    zipComment?: string;
    firstPolicyId?: string;
    allTimestamps?: Array<TimestampWrapper>;
    documentName?: string;
    containerInfo?: XmlContainerInfo;
    signatures?: Array<SignatureWrapper>;
    trustedLists?: Array<XmlTrustedList>;
    containerType?: string;
    jaxbModel?: DiagnosticData;
    firstSignatureFormat?: string;
    allCounterSignatures?: Array<SignatureWrapper>;
    allRevocationData?: Array<RevocationWrapper>;
    mimetypeFilePresent?: boolean;
    mimetypeFileContent?: string;
    firstSigningCertificateId?: string;
    listOfTrustedLists?: XmlTrustedList;
    signatureIdList?: Array<string>;
    firstSignatureDigestAlgorithm?: DiagnosticData.FirstSignatureDigestAlgorithmEnum;
    containerInfoPresent?: boolean;
    lotlcountryCode?: string;
    firstSignatureDate?: Date;
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
    firstSignatureId?: string;
    firstSignatureEncryptionAlgorithm?: DiagnosticData.FirstSignatureEncryptionAlgorithmEnum;
}
export namespace DiagnosticData {
    export type FirstSignatureDigestAlgorithmEnum = 'SHA1' | 'SHA224' | 'SHA256' | 'SHA384' | 'SHA512' | 'SHA3_224' | 'SHA3_256' | 'SHA3_384' | 'SHA3_512' | 'RIPEMD160' | 'MD2' | 'MD5';
    export const FirstSignatureDigestAlgorithmEnum = {
        SHA1: 'SHA1' as FirstSignatureDigestAlgorithmEnum,
        SHA224: 'SHA224' as FirstSignatureDigestAlgorithmEnum,
        SHA256: 'SHA256' as FirstSignatureDigestAlgorithmEnum,
        SHA384: 'SHA384' as FirstSignatureDigestAlgorithmEnum,
        SHA512: 'SHA512' as FirstSignatureDigestAlgorithmEnum,
        SHA3224: 'SHA3_224' as FirstSignatureDigestAlgorithmEnum,
        SHA3256: 'SHA3_256' as FirstSignatureDigestAlgorithmEnum,
        SHA3384: 'SHA3_384' as FirstSignatureDigestAlgorithmEnum,
        SHA3512: 'SHA3_512' as FirstSignatureDigestAlgorithmEnum,
        RIPEMD160: 'RIPEMD160' as FirstSignatureDigestAlgorithmEnum,
        MD2: 'MD2' as FirstSignatureDigestAlgorithmEnum,
        MD5: 'MD5' as FirstSignatureDigestAlgorithmEnum
    };
    export type FirstSignatureEncryptionAlgorithmEnum = 'RSA' | 'DSA' | 'ECDSA' | 'PLAIN_ECDSA' | 'HMAC';
    export const FirstSignatureEncryptionAlgorithmEnum = {
        RSA: 'RSA' as FirstSignatureEncryptionAlgorithmEnum,
        DSA: 'DSA' as FirstSignatureEncryptionAlgorithmEnum,
        ECDSA: 'ECDSA' as FirstSignatureEncryptionAlgorithmEnum,
        PLAINECDSA: 'PLAIN_ECDSA' as FirstSignatureEncryptionAlgorithmEnum,
        HMAC: 'HMAC' as FirstSignatureEncryptionAlgorithmEnum
    };
}
