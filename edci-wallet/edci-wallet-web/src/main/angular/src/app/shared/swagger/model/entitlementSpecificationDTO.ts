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
<<<<<<< HEAD
import { EscoOccupationAssociation } from './escoOccupationAssociation';
import { Identifier } from './identifier';
import { LearningSpecificationDTO } from './learningSpecificationDTO';
import { Note } from './note';
import { OccupationAssociation } from './occupationAssociation';
=======
import { Identifier } from './identifier';
import { LearningSpecificationDTO } from './learningSpecificationDTO';
import { Note } from './note';
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
import { OrganizationDTO } from './organizationDTO';
import { Text } from './text';
import { WebDocumentDTO } from './webDocumentDTO';


export interface EntitlementSpecificationDTO { 
<<<<<<< HEAD
    id?: string;
    pk?: string;
    identifier?: Array<Identifier>;
    entitlementType?: Code;
=======
    id: string;
    pk?: string;
    identifier?: Array<Identifier>;
    entitlementType: Code;
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
    title?: Text;
    alternativeLabel?: Array<Text>;
    description?: Note;
    additionalNote?: Array<Note>;
    homePage?: Array<WebDocumentDTO>;
    supplementaryDocument?: Array<WebDocumentDTO>;
<<<<<<< HEAD
    status?: Code;
    limitOrganization?: Array<OrganizationDTO>;
    limitJurisdiction?: Array<Code>;
    limitOccupation?: Array<EscoOccupationAssociation>;
    limitNationalOccupation?: Array<OccupationAssociation>;
=======
    status: Code;
    limitOrganization?: Array<OrganizationDTO>;
    limitJurisdiction?: Array<Code>;
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
    mayResultFrom?: Array<LearningSpecificationDTO>;
    hasPart?: Array<EntitlementSpecificationDTO>;
    specializationOf?: Array<EntitlementSpecificationDTO>;
    identifiableName?: string;
}