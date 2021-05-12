import { UxAutoCompleteItem } from '@eui/core';
import {
    AssessmentSpecLiteView,
    CodeDTView,
    EntitlementSpecLiteView,
    LearningAchievementSpecLiteView,
    LearningActivitySpecLiteView,
    OrganizationSpecLiteView,
    OrganizationSpecView
} from '@shared/swagger';

export interface SelectedItemList extends UxAutoCompleteItem {
    achievement?: LearningAchievementSpecLiteView;
    activity?: LearningActivitySpecLiteView;
    assessment?: AssessmentSpecLiteView;
    entitlement?: EntitlementSpecLiteView;
    entity?: CodeDTView;
    organization?: OrganizationSpecLiteView | OrganizationSpecView;
}
