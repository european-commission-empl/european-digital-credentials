import { Component, Input } from '@angular/core';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { AchievementTabView } from '../../swagger';

@Component({
    selector: 'edci-viewer-achievement-detail',
    templateUrl: './achievement-detail.component.html',
    styleUrls: ['./achievement-detail.component.scss'],
})
export class AchievementDetailComponent {
    private _activeAchievement: AchievementTabView;
    @Input()
    set activeAchievement(value: AchievementTabView) {
        this.isLearningActivitiesExpanded = false;
        this.isEntitlesOwnerToExpanded = false;
        this.isSubAchievementsExpanded = false;
        this.isProvenByExpanded = false;
        this._activeAchievement = value;
    }
    get activeAchievement(): AchievementTabView {
        return this._activeAchievement;
    }

    isLearningActivitiesExpanded = true;
    isEntitlesOwnerToExpanded = true;
    isSubAchievementsExpanded = true;
    isProvenByExpanded = true;

    constructor(private entityLinkService: EntityLinkService) {}

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }
}
