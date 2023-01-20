import { Component, Input } from '@angular/core';
import { AchievementTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-achievement-information-panel',
    templateUrl: './achievement-information-panel.component.html',
    styleUrls: ['./achievement-information-panel.component.scss'],
})
export class AchievementInformationPanelComponent {
    private _activeAchievement: AchievementTabView;
    @Input()
    set activeAchievement(value: AchievementTabView) {
        this.isPanelExpanded = true;
        this._activeAchievement = value;
    }
    get activeAchievement(): AchievementTabView {
        return this._activeAchievement;
    }

    isPanelExpanded = true;

    constructor() {}
}
