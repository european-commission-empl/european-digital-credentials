import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { EntityModalInformation } from 'src/app/shared/model/entityModalInformation';
import {
    AchievementTabView,
    ActivityTabView,
    AssessmentTabView,
    EntitlementTabView,
} from 'src/app/shared/swagger';
import { ShareDataService } from './share-data.service';

@Injectable({
    providedIn: 'root',
})
export class EntityLinkService {
    private newModalInformationSource = new Subject<EntityModalInformation>();
    newModalInformation$ = this.newModalInformationSource.asObservable();

    constructor(private shareDataService: ShareDataService) {}

    sendEntityModalInformation(modalInformation: EntityModalInformation) {
        this.newModalInformationSource.next(modalInformation);
    }

    isAchievementAvailable(achievementId: string): boolean {
        const achievementList: AchievementTabView[] = this.shareDataService
            .achievements;
        const isItemAvailable: boolean = this.checkEntityAvailability(
            achievementId,
            achievementList,
            'subAchievements'
        );
        return this.shareDataService.modalsOpen === 0 && isItemAvailable;
    }

    isActivityAvailable(activityId: string): boolean {
        const activityList: ActivityTabView[] = this.shareDataService
            .activities;
        const isItemAvailable: boolean = this.checkEntityAvailability(
            activityId,
            activityList,
            'subActivities'
        );
        return this.shareDataService.modalsOpen === 0 && isItemAvailable;
    }

    isEntitlementAvailable(entitlementId: string): boolean {
        const entitlementList: EntitlementTabView[] = this.shareDataService
            .entitlements;
        const isItemAvailable: boolean = this.checkEntityAvailability(
            entitlementId,
            entitlementList,
            'subEntitlements'
        );
        return this.shareDataService.modalsOpen === 0 && isItemAvailable;
    }

    private checkEntityAvailability(
        entityId: string,
        entityList,
        subEntityName: string
    ): boolean {
        let isAvailable: boolean = false;
        if (entityList) {
            entityList.forEach(
                (
                    entity:
                        | ActivityTabView
                        | AssessmentTabView
                        | EntitlementTabView
                        | AchievementTabView
                ) => {
                    if (entity.id === entityId) {
                        isAvailable = true;
                    }
                    if (entity[subEntityName]) {
                        entity[subEntityName].forEach((subEntity) => {
                            if (subEntity.id === entityId) {
                                isAvailable = true;
                            }
                        });
                    }
                }
            );
        }
        return isAvailable;
    }
}
