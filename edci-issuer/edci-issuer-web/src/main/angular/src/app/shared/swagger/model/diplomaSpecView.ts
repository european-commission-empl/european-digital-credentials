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
import { AdditionalInfo } from './additionalInfo';
import { LabelDTView } from './labelDTView';
import { MediaObjectDTView } from './mediaObjectDTView';


export interface DiplomaSpecView { 
    oid?: number;
    additionalInfo?: AdditionalInfo;
    displayName?: string;
    label?: string;
    defaultLanguage: string;
    html: string;
    format: string;
    background?: MediaObjectDTView;
    labels?: Array<LabelDTView>;
}
