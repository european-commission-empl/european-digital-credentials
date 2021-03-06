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
import { CodeDTView } from './codeDTView';


export interface RecipientDataView { 
    firstName?: string;
    lastName?: string;
    dateOfBirth?: string;
    citizenshipCountry?: Array<CodeDTView>;
    nationalIdentifier?: string;
    nationalIdentifierSpatialId?: string;
    placeOfBirthCountry?: CodeDTView;
    address?: string;
    addressCountry?: CodeDTView;
    gender?: CodeDTView;
    emailAddress?: string;
    walletAddress?: string;
    assessmentGrades?: { [key: string]: string; };
}
