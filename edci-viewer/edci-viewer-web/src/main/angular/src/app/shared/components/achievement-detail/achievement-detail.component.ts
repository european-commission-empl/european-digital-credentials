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
        this.isLearningActivitiesExpanded = true;
        this.isEntitlesOwnerToExpanded = true;
        this.isSubAchievementsExpanded = true;
        this._activeAchievement = value;
    }
    get activeAchievement(): AchievementTabView {
        return this._activeAchievement;
    }

    isLearningActivitiesExpanded: boolean = true;
    isEntitlesOwnerToExpanded: boolean = true;
    isSubAchievementsExpanded: boolean = true;

    constructor(private entityLinkService: EntityLinkService) {}

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }
}
