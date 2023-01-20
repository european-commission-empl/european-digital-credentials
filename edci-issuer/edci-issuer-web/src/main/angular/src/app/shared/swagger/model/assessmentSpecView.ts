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
import { AssessmSpecificationDCView } from './assessmSpecificationDCView';
import { CodeDTView } from './codeDTView';
import { IdentifierDTView } from './identifierDTView';
import { NoteDTView } from './noteDTView';
import { ResultDistributionDTView } from './resultDistributionDTView';
import { ScoreDTView } from './scoreDTView';
import { ShortenedGradingDTView } from './shortenedGradingDTView';
import { SubresourcesOids } from './subresourcesOids';
import { TextDTView } from './textDTView';


export interface AssessmentSpecView { 
    oid?: number;
    additionalInfo?: AdditionalInfo;
    displayName?: string;
    label?: string;
    defaultLanguage: string;
    title: TextDTView;
    identifier?: Array<IdentifierDTView>;
    description?: TextDTView;
    additionalNote?: Array<NoteDTView>;
    grade?: ScoreDTView;
    shortenedGrading?: ShortenedGradingDTView;
    resultDistribution?: ResultDistributionDTView;
    issuedDate?: string;
    idVerification?: CodeDTView;
    specifiedBy?: AssessmSpecificationDCView;
    relHasPart?: SubresourcesOids;
    relAssessedBy?: SubresourcesOids;
}
