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
import { AchievementDTO } from './achievementDTO';
import { AssessmentDTO } from './assessmentDTO';
import { GradeDetailsDTO } from './gradeDetailsDTO';
import { LearningActivityDTO } from './learningActivityDTO';
import { LearningSpecificationDTO } from './learningSpecificationDTO';


export interface NQFQualificationAwardDTO { 
    id?: string;
    identifier?: string;
    title?: string;
    description?: string;
    issuedDate?: Date;
    partReferences?: Array<string>;
    hasPart?: Array<AchievementDTO>;
    derivedReferences?: Array<string>;
    influencedReferences?: Array<string>;
    wasInfluencedBy?: Array<LearningActivityDTO>;
    wasDerivedFrom?: Array<AssessmentDTO>;
    entitlement?: string;
    gradeDetailsDTO?: GradeDetailsDTO;
    learningSpecificationDTO?: LearningSpecificationDTO;
}
