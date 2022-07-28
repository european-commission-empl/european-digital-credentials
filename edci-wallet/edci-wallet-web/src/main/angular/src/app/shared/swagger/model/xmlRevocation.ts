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
import { XmlBasicSignature } from './xmlBasicSignature';
import { XmlChainItem } from './xmlChainItem';
import { XmlDigestAlgoAndValue } from './xmlDigestAlgoAndValue';
import { XmlSigningCertificate } from './xmlSigningCertificate';


export interface XmlRevocation { 
    origin: string;
    source: string;
    sourceAddress?: string;
    available?: boolean;
    status?: boolean;
    reason?: string;
    productionDate?: Date;
    thisUpdate?: Date;
    nextUpdate?: Date;
    revocationDate?: Date;
    expiredCertsOnCRL?: Date;
    archiveCutOff?: Date;
    certHashExtensionPresent?: boolean;
    certHashExtensionMatch?: boolean;
    digestAlgoAndValues?: Array<XmlDigestAlgoAndValue>;
    basicSignature?: XmlBasicSignature;
    signingCertificate?: XmlSigningCertificate;
    certificateChain?: Array<XmlChainItem>;
    base64Encoded?: Array<string>;
    id?: string;
}