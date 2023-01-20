import { Component, Input } from '@angular/core';
import { AchievementTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-achievement-other-information-panel',
    templateUrl: './achievement-other-information-panel.component.html',
    styleUrls: ['./achievement-other-information-panel.component.scss'],
})
export class AchievementOtherInformationPanelComponent {
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
