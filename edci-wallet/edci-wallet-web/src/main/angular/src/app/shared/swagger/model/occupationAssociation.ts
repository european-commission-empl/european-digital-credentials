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
import { Note } from './note';
import { Standard } from './standard';
import { Text } from './text';


export interface OccupationAssociation { 
    associationType?: Code;
    description?: Text;
    issueDate?: Date;
    isAssociationFor?: string;
    targetFramework?: Standard;
    targetFrameworkVersion?: string;
    targetResource?: string;
    targetNotation?: string;
    targetName?: Text;
    targetDescription?: Note;
    targetUrl?: Array<string>;
}
