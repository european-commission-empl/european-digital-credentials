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
import { Score } from './score';
import { Text } from './text';


export interface ResultCategoryDTO { 
<<<<<<< HEAD
    label?: Text;
    score?: Score;
    minScore?: Score;
    maxScore?: Score;
    count?: number;
=======
    label: Text;
    score?: Score;
    minScore?: Score;
    maxScore?: Score;
    count: number;
    identifiableName?: string;
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
}
