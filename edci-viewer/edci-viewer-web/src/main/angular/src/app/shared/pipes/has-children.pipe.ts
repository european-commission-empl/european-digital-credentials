import { Pipe, PipeTransform } from '@angular/core';
import {
    AchievementTabView,
    ActivityTabView,
    AssessmentTabView,
    CredentialSubjectTabView,
    EntitlementTabView,
    OrganizationTabView,
} from '../swagger';

@Pipe({
    name: 'edciHasChildren',
})
export class HasChildrenPipe implements PipeTransform {
    constructor() {}
    transform(
        object:
            | OrganizationTabView
            | AchievementTabView
            | ActivityTabView
            | EntitlementTabView
            | AssessmentTabView
            | CredentialSubjectTabView,
        objectType:
            | 'achievement'
            | 'activity'
            | 'assessment'
            | 'organisation'
            | 'entitlement'
            | 'subject'
    ): boolean {
        return this[`${objectType}HasChildren`](object);
    }

    achievementHasChildren(achievement: AchievementTabView): boolean {
        let hasChildren = false;
        // Need to check:
        // 'subAchievements',  'awardedBy.awardingBody', 'influencedBy', 'provenBy', 'entitledOwnerTo'
        const keys: string[] = [
            'subAchievements',
            'influencedBy',
            'provenBy',
            'entitlesTo',
        ];
        keys.forEach((key) => {
            if (achievement[key] && achievement[key]?.length > 0) {
                hasChildren = true;
            }
        });

        return hasChildren;
    }

    activityHasChildren(activity: ActivityTabView): boolean {
        // Need to check:
        // 'directedBy', 'influenced', 'subActivities'
        return (activity.subActivities && activity.subActivities.length > 0) ? true : false;
    }

    assessmentHasChildren(assessment: AssessmentTabView): boolean {
        // Need to check:
        // 'subAssessments'
        return (assessment.subAssessments && assessment.subAssessments.length > 0) ? true : false;
    }

    organisationHasChildren(organisation: OrganizationTabView): boolean {
        return false;
    }

    entitlementHasChildren(entitlement: EntitlementTabView): boolean {
        // Need to check:
        // 'subEntitlements'
        return (entitlement.subEntitlements && entitlement.subEntitlements.length > 0) ? true : false;
    }

    subjectHasChildren(subject: CredentialSubjectTabView): boolean {
        // This case will never happen since there is no option
        // for a credential subject to have children entities.
        return false;
    }
}
