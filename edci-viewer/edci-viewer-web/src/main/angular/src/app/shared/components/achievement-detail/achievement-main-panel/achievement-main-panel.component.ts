import { Component, Input } from '@angular/core';
import { AchievementTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-achievement-main-panel',
    templateUrl: './achievement-main-panel.component.html',
    styleUrls: ['./achievement-main-panel.component.scss'],
})
export class AchievementMainPanelComponent {
    private _activeAchievement: AchievementTabView;
    @Input()
    set activeAchievement(value: AchievementTabView) {
        this.isAwardedByExpanded = true;
        this._activeAchievement = value;
    }
    get activeAchievement(): AchievementTabView {
        return this._activeAchievement;
    }
    isAwardedByExpanded = true;
    isPanelExpanded = true;
    constructor() {}

    toggleAwardedBy(): void {
        this.isAwardedByExpanded = !this.isAwardedByExpanded;
    }
}
