import {
    OrganizationTabView,
    AchievementTabView,
    ActivityTabView,
    EntitlementTabView,
    AssessmentTabView,
} from '../swagger';

export interface EntityModalInformation {
    id: string;
    entityName:
        | 'achievement'
        | 'activity'
        | 'assessment'
        | 'entitlement'
        | 'organization';
    modalTitle: string;
    entity:
        | OrganizationTabView
        | AchievementTabView
        | ActivityTabView
        | AssessmentTabView
        | EntitlementTabView;
}
