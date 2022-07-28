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
import { AccreditationDTO } from './accreditationDTO';
import { AssessmentSpecificationDTO } from './assessmentSpecificationDTO';
import { AwardingOpportunityDTO } from './awardingOpportunityDTO';
import { Code } from './code';
import { EducationLevelAssociation } from './educationLevelAssociation';
import { EducationSubjectAssociation } from './educationSubjectAssociation';
import { EntitlementSpecificationDTO } from './entitlementSpecificationDTO';
import { Identifier } from './identifier';
import { LearningActivitySpecificationDTO } from './learningActivitySpecificationDTO';
import { LearningOutcomeDTO } from './learningOutcomeDTO';
import { LearningSpecificationDTO } from './learningSpecificationDTO';
import { Note } from './note';
import { Period } from './period';
<<<<<<< HEAD
import { QualificationAssociationTypeDTO } from './qualificationAssociationTypeDTO';
=======
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
import { Score } from './score';
import { Text } from './text';
import { WebDocumentDTO } from './webDocumentDTO';


export interface QualificationDTO { 
<<<<<<< HEAD
    id?: string;
    identifier?: Array<Identifier>;
    learningOpportunityType?: Array<Code>;
    title?: Text;
=======
    id: string;
    identifier?: Array<Identifier>;
    learningOpportunityType?: Array<Code>;
    title: Text;
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
    alternativeLabel?: Array<Text>;
    definition?: Note;
    learningOutcomeDescription?: Note;
    additionalNote?: Array<Note>;
    homePage?: Array<WebDocumentDTO>;
    supplementaryDocument?: Array<WebDocumentDTO>;
    iscedFCode?: Array<Code>;
    educationSubject?: Array<EducationSubjectAssociation>;
    volumeOfLearning?: Period;
    ectsCreditPoints?: Score;
    creditPoints?: Array<Score>;
    educationLevel?: Array<EducationLevelAssociation>;
    language?: Array<Code>;
    mode?: Array<Code>;
    learningSetting?: Code;
    maximumDuration?: Period;
    targetGroup?: Array<Code>;
    entryRequirementNote?: Note;
    learningOutcome?: Array<LearningOutcomeDTO>;
    learningActivitySpecification?: LearningActivitySpecificationDTO;
    assessmentSpecification?: AssessmentSpecificationDTO;
    entitlementSpecification?: Array<EntitlementSpecificationDTO>;
    awardingOpportunity?: Array<AwardingOpportunityDTO>;
    hasPart?: Array<LearningSpecificationDTO>;
    specializationOf?: Array<LearningSpecificationDTO>;
    eqfLevel?: Code;
    nqfLevel?: Array<Code>;
    hasAccreditation?: Array<AccreditationDTO>;
<<<<<<< HEAD
    qualificationCode?: Array<QualificationAssociationTypeDTO>;
=======
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
    partialQualification?: boolean;
    identifiableName?: string;
}
