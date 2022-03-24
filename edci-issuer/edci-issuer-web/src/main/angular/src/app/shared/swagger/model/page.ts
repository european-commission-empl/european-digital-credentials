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
import { Pageable } from './pageable';
import { Sort } from './sort';


export interface Page { 
    totalElements?: number;
    totalPages?: number;
    size?: number;
    content?: Array<any>;
    number?: number;
    last?: boolean;
    sort?: Sort;
    numberOfElements?: number;
    first?: boolean;
    pageable?: Pageable;
    empty?: boolean;
}
