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
import { EuropassCredentialSpecLiteView } from './europassCredentialSpecLiteView';
import { Link } from './link';
import { PageMetadata } from './pageMetadata';


export interface PagedResourcesEuropassCredentialSpecLiteView { 
    readonly links?: Array<Link>;
    readonly content?: Array<EuropassCredentialSpecLiteView>;
    readonly page?: PageMetadata;
}
