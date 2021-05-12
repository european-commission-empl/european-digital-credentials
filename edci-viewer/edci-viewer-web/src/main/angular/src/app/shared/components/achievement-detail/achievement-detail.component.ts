import { Component, Input, OnInit } from '@angular/core';
import {
    AchievementTabView,
    ActivityTabView,
    EntitlementTabView,
    OrganizationTabView,
    AssessmentTabView,
} from '../../swagger';
import { Router } from '@angular/router';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { EntityModalInformation } from '../../model/entityModalInformation';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'edci-viewer-achievement-detail',
    templateUrl: './achievement-detail.component.html',
    styleUrls: ['./achievement-detail.component.scss'],
})
export class AchievementDetailComponent implements OnInit {
    @Input() activeAchievement: AchievementTabView;
    isLearningOutcomeExpanded: boolean[] = [];

    constructor(
        private router: Router,
        private entityLinkService: EntityLinkService,
        private translateService: TranslateService
    ) {}

    ngOnInit() {
        if (this.activeAchievement.specifiedBy) {
            this.isLearningOutcomeExpanded = this.activeAchievement.specifiedBy
                .learningOutcome
                ? new Array(
                      this.activeAchievement.specifiedBy.learningOutcome.length
                  ).fill(false)
                : [];
        }
    }

    openAssessmentModal(assessment: AssessmentTabView): void {
        const info: EntityModalInformation = {
            id: assessment.id,
            entityName: 'assessment',
            entity: assessment,
            modalTitle: this.translateService.instant(
                'details.achievements-tab.provenBy'
            ),
        };
        this.entityLinkService.sendEntityModalInformation(info);
    }

    openAwardingBodyModal(organization: OrganizationTabView) {
        organization['isModal'] = true;
        const info: EntityModalInformation = {
            id: organization.id,
            entityName: 'organization',
            entity: organization,
            modalTitle: this.translateService.instant(
                'details.achievements-tab.awardingBody'
            ),
        };
        this.entityLinkService.sendEntityModalInformation(info);
    }

    linkToActivity(activity: ActivityTabView): void {
        if (this.entityLinkService.isActivityAvailable(activity.id)) {
            this.goToLinkedDestination(activity.id, 'activities');
        } else {
            this.openInfluencedByModal(activity);
        }
    }

    linkToEntitlement(entitlement: EntitlementTabView): void {
        if (this.entityLinkService.isEntitlementAvailable(entitlement.id)) {
            this.goToLinkedDestination(entitlement.id, 'entitlements');
        } else {
            this.openEntitlesOwnerToModal(entitlement);
        }
    }

    linkToAchievement(achievement: AchievementTabView): void {
        if (this.entityLinkService.isAchievementAvailable(achievement.id)) {
            this.goToLinkedDestination(achievement.id, 'achievements');
        } else {
            this.openSubAchievementModal(achievement);
        }
    }

    private openSubAchievementModal(achievement: AchievementTabView): void {
        const info: EntityModalInformation = {
            id: achievement.id,
            entityName: 'achievement',
            entity: achievement,
            modalTitle: this.translateService.instant(
                'details.achievements-tab.influencedBy'
            ),
        };
        this.entityLinkService.sendEntityModalInformation(info);
    }

    private openEntitlesOwnerToModal(entitlement: EntitlementTabView): void {
        const info: EntityModalInformation = {
            id: entitlement.id,
            entityName: 'entitlement',
            entity: entitlement,
            modalTitle: this.translateService.instant(
                'details.achievements-tab.entitlesOwnerTo'
            ),
        };
        this.entityLinkService.sendEntityModalInformation(info);
    }

    private openInfluencedByModal(activity: ActivityTabView) {
        const info: EntityModalInformation = {
            id: activity.id,
            entityName: 'activity',
            entity: activity,
            modalTitle: this.translateService.instant(
                'details.achievements-tab.influencedBy'
            ),
        };
        this.entityLinkService.sendEntityModalInformation(info);
    }

    private goToLinkedDestination(id: string, destination: string) {
        this.router.navigate([`/diploma-details/${destination}`], {
            queryParams: { id: id },
            queryParamsHandling: 'merge',
        });
    }
}
