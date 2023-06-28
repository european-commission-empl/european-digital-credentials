import { Component, Input } from '@angular/core';
import { AchievementTabView } from 'src/app/shared/swagger';
import { TranslateService } from '@ngx-translate/core';

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

    isPanelExpanded = false;

    constructor(private translateService: TranslateService) {}

    getVolumeOfLearning(): string {
        const hourLabel = this.activeAchievement?.specifiedBy?.volumeOfLearning === '1'
            ? this.translateService.instant('hour')
            : this.translateService.instant('hours');
        return this.activeAchievement?.specifiedBy?.volumeOfLearning + ' ' + hourLabel;
    }

    getMaximumDuration(): string {
        const monthLabel = this.activeAchievement?.specifiedBy?.maximumDuration === '1'
            ? this.translateService.instant('month')
            : this.translateService.instant('months');
        return this.activeAchievement.specifiedBy.maximumDuration + ' ' + monthLabel;
    }
}
