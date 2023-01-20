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
        // 'subAchievements',  'awardingBody', 'influencedBy', 'provenBy', 'entitledOwnerTo'
        const keys: string[] = [
            'subAchievements',
            'awardingBody',
            'influencedBy',
            'provenBy',
            'entitledOwnerTo',
        ];
        keys.forEach((key) => {
            if (achievement[key]) {
                hasChildren = true;
            }
        });
        return hasChildren;
    }

    activityHasChildren(activity: ActivityTabView): boolean {
        // Need to check:
        // 'directedBy', 'influenced', 'subActivities'
        let hasChildren = false;
        const keys: string[] = ['directedBy', 'influenced', 'subActivities'];
        keys.forEach((key) => {
            if (activity[key]) {
                hasChildren = true;
            }
        });
        return hasChildren;
    }

    assessmentHasChildren(assessment: AssessmentTabView): boolean {
        // Need to check:
        // 'conductedBy', 'subAssessments'
        let hasChildren = false;
        const keys: string[] = ['conductedBy', 'subAssessments'];
        keys.forEach((key) => {
            if (assessment[key]) {
                hasChildren = true;
            }
        });
        return hasChildren;
    }

    organisationHasChildren(organisation: OrganizationTabView): boolean {
        // Need to check:
        // 'parentOrganization'
        return organisation.parentOrganization ? true : false;
    }

    entitlementHasChildren(entitlement: EntitlementTabView): boolean {
        // Need to check:
        // 'subEntitlements'
        return entitlement.subEntitlements ? true : false;
    }

    subjectHasChildren(subject: CredentialSubjectTabView): boolean {
        // This case will never happen since there is no option
        // for a credential subject to have children entities.
        return false;
    }
}
