import { UxAutoCompleteTagItem } from '@eui/core';
import {
    AssessmentSpecLiteView,
    CodeDTView,
    EntitlementSpecView,
    LearningAchievementSpecLiteView,
    LearningActivitySpecLiteView,
    LearningOutcomeSpecLiteView,
    OrganizationSpecLiteView
} from '@shared/swagger';

export interface SelectedTagItemList extends UxAutoCompleteTagItem {
    achievement?: LearningAchievementSpecLiteView;
    learningOutcome?: LearningOutcomeSpecLiteView;
    activity?: LearningActivitySpecLiteView;
    assessment?: AssessmentSpecLiteView;
    organization?: OrganizationSpecLiteView;
    entitlement?: EntitlementSpecView;
    entity?: CodeDTView;
}
