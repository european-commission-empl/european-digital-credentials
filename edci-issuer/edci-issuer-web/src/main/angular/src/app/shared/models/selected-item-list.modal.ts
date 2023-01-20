import { EuiAutoCompleteItem } from '@eui/components/eui-autocomplete';
import {
    AssessmentSpecLiteView,
    CodeDTView,
    EntitlementSpecLiteView,
    LearningAchievementSpecLiteView,
    LearningActivitySpecLiteView,
    OrganizationSpecLiteView,
    OrganizationSpecView
} from '@shared/swagger';

export interface SelectedItemList extends EuiAutoCompleteItem {
    achievement?: LearningAchievementSpecLiteView;
    activity?: LearningActivitySpecLiteView;
    assessment?: AssessmentSpecLiteView;
    entitlement?: EntitlementSpecLiteView;
    organization?: OrganizationSpecLiteView | OrganizationSpecView;
    entity?: CodeDTView;
}
