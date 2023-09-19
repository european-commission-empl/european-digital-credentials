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
import { ConceptView } from './conceptView';


export interface MediaObjectView { 
    id: string;
    type?: string;
    contentUrl?: string;
    content: string;
    contentEncoding: ConceptView;
    contentType: ConceptView;
    contentSize?: number;
    attachmentType?: ConceptView;
    title?: { [key: string]: Array<string>; };
    description?: { [key: string]: Array<string>; };
}
