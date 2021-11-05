import { Component, Input } from '@angular/core';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { ActivityTabView } from '../../swagger';

@Component({
    selector: 'edci-viewer-activity-detail',
    templateUrl: './activity-detail.component.html',
    styleUrls: ['./activity-detail.component.scss'],
})
export class ActivityDetailComponent {
    @Input() activeActivity: ActivityTabView;

    constructor(private entityLinkService: EntityLinkService) {}

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }

    // linkToActivity(activity: ActivityTabView): void {
    //     if (this.entityLinkService.isActivityAvailable(activity.id)) {
    //         this.goToLinkedDestination(activity.id, 'activities');
    //     } else {
    //         this.openSubActivityModal(activity);
    //     }
    // }

    // linkToAchievement(achievement: AchievementTabView): void {
    //     if (this.entityLinkService.isAchievementAvailable(achievement.id)) {
    //         this.goToLinkedDestination(achievement.id, 'achievements');
    //     } else {
    //         this.openInfluencesModal(achievement);
    //     }
    // }

    // openDirectedByModal(organization: OrganizationTabView): void {
    //     organization['isModal'] = true;
    //     const info: EntityModalInformation = {
    //         id: new Date().toISOString(),
    //         entityName: 'organization',
    //         entity: organization,
    //         modalTitle: this.translateService.instant(
    //             'details.activities-tab.directedBy'
    //         ),
    //     };
    //     this.entityLinkService.sendEntityModalInformation(info);
    // }

    // private openSubActivityModal(activity: ActivityTabView): void {
    //     const info: EntityModalInformation = {
    //         id: activity.id,
    //         entityName: 'activity',
    //         entity: activity,
    //         modalTitle: this.translateService.instant(
    //             'details.activities-tab.subActivity'
    //         ),
    //     };
    //     this.entityLinkService.sendEntityModalInformation(info);
    // }

    // private openInfluencesModal(achievements: AchievementTabView): void {
    //     const info: EntityModalInformation = {
    //         id: achievements.id,
    //         entityName: 'activity',
    //         entity: achievements,
    //         modalTitle: this.translateService.instant(
    //             'details.activities-tab.influences'
    //         ),
    //     };
    //     this.entityLinkService.sendEntityModalInformation(info);
    // }

    // private goToLinkedDestination(id: string, destination: string) {
    //     this.router.navigate([`/diploma-details/${destination}`], {
    //         queryParams: { id: id },
    //         queryParamsHandling: 'merge',
    //     });
    // }
}
