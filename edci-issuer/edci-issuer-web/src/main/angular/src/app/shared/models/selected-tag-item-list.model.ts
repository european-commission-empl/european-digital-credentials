import { EuiAutoCompleteItem } from '@eui/components/eui-autocomplete';
import {
    AssessmentSpecLiteView,
    CodeDTView,
    EntitlementSpecView,
    LearningAchievementSpecLiteView,
    LearningActivitySpecLiteView,
    LearningOutcomeSpecLiteView,
    OrganizationSpecLiteView
} from '@shared/swagger';

export interface SelectedTagItemList extends EuiAutoCompleteItem {
    achievement?: LearningAchievementSpecLiteView;
    learningOutcome?: LearningOutcomeSpecLiteView;
    activity?: LearningActivitySpecLiteView;
    assessment?: AssessmentSpecLiteView;
    organization?: OrganizationSpecLiteView;
    entitlement?: EntitlementSpecView;
    entity?: CodeDTView;
}
