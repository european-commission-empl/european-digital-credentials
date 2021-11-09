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
import { ContactPointFieldView } from './contactPointFieldView';
import { IdentifierFieldView } from './identifierFieldView';
import { LinkFieldView } from './linkFieldView';
import { LocationFieldView } from './locationFieldView';
import { MediaObjectFieldView } from './mediaObjectFieldView';


export interface OrganizationTabView { 
    id?: string;
    homepage?: Array<LinkFieldView>;
    preferredName?: string;
    alternativeName?: Array<string>;
    location?: Array<LocationFieldView>;
    legalIdentifier?: IdentifierFieldView;
    vatIdentifier?: Array<IdentifierFieldView>;
    taxIdentifier?: Array<IdentifierFieldView>;
    identifier?: Array<IdentifierFieldView>;
    parentOrganization?: OrganizationTabView;
    logo?: MediaObjectFieldView;
    contactPoint?: Array<ContactPointFieldView>;
    depth?: number;
}