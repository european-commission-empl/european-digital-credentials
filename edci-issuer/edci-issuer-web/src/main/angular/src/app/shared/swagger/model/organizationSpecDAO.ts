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
import { AccreditationDCDAO } from './accreditationDCDAO';
import { AuditDAO } from './auditDAO';
import { CodeDTDAO } from './codeDTDAO';
import { ContactPointDCDAO } from './contactPointDCDAO';
import { IdentifierDTDAO } from './identifierDTDAO';
import { ImageObjectDTDAO } from './imageObjectDTDAO';
import { LegalIdentifierDTDAO } from './legalIdentifierDTDAO';
import { LocationDCDAO } from './locationDCDAO';
import { NoteDTDAO } from './noteDTDAO';
import { TextDTDAO } from './textDTDAO';
import { WebDocumentDCDAO } from './webDocumentDCDAO';


export interface OrganizationSpecDAO { 
    pk?: number;
    languages?: Array<string>;
    defaultTitle?: string;
    defaultLanguage?: string;
    identifier?: Array<IdentifierDTDAO>;
    type?: Array<CodeDTDAO>;
    preferredName?: TextDTDAO;
    alternativeName?: Array<TextDTDAO>;
    note?: Array<NoteDTDAO>;
    hasLocation?: Array<LocationDCDAO>;
    contactPoint?: Array<ContactPointDCDAO>;
    legalIdentifier?: LegalIdentifierDTDAO;
    vatIdentifier?: Array<LegalIdentifierDTDAO>;
    taxIdentifier?: Array<LegalIdentifierDTDAO>;
    homePage?: Array<WebDocumentDCDAO>;
    hasAccreditation?: Array<AccreditationDCDAO>;
    logo?: ImageObjectDTDAO;
    unitOf?: OrganizationSpecDAO;
    hasUnit?: Array<OrganizationSpecDAO>;
    auditDAO?: AuditDAO;
}
