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
import { IdentifierFieldView } from './identifierFieldView';
import { LinkFieldView } from './linkFieldView';
import { NoteFieldView } from './noteFieldView';
import { OrganizationTabView } from './organizationTabView';


export interface AccreditationFieldView { 
    id?: string;
    identifier?: Array<IdentifierFieldView>;
    accreditationType?: LinkFieldView;
    title?: string;
    description?: string;
    decision?: string;
    report?: LinkFieldView;
    organization?: OrganizationTabView;
    limitQualification?: string;
    limitField?: Array<string>;
    limitEqfLevel?: Array<string>;
    limitJurisdiction?: Array<string>;
    accreditingAgent?: OrganizationTabView;
    issueDate?: string;
    reviewDate?: string;
    expiryDate?: string;
    additionalNote?: Array<NoteFieldView>;
    homePage?: Array<LinkFieldView>;
    supplementaryDocument?: Array<LinkFieldView>;
}
