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
import { DurationFieldType } from './durationFieldType';
import { PeriodType } from './periodType';


export interface Period { 
    months?: number;
    hours?: number;
    millis?: number;
    minutes?: number;
    seconds?: number;
<<<<<<< HEAD
    years?: number;
    days?: number;
=======
    days?: number;
    years?: number;
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
    weeks?: number;
    periodType?: PeriodType;
    values?: Array<number>;
    fieldTypes?: Array<DurationFieldType>;
}