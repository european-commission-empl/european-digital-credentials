import { Component, Input } from '@angular/core';
import { ActivityTabView } from 'src/app/shared/swagger';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'edci-activity-information-panel',
    templateUrl: './activity-information-panel.component.html',
    styleUrls: ['./activity-information-panel.component.scss'],
})
export class ActivityInformationPanelComponent {
    private _activeActivity: ActivityTabView;
    @Input()
    set activeActivity(value: ActivityTabView) {
        this.isPanelExpanded = false;
        this._activeActivity = value;
    }
    get activeActivity(): ActivityTabView {
        return this._activeActivity;
    }

    isPanelExpanded = false;

    constructor(private translateService: TranslateService) {}

    getVolumeOfLearning(): string {
        const hourLabel = this.activeActivity?.specifiedBy?.volumeOfLearning === '1'
            ? this.translateService.instant('hour')
            : this.translateService.instant('hours');
        return this.activeActivity?.specifiedBy?.volumeOfLearning + ' ' + hourLabel;
    }

    getActualWorkload(): string {
        const hourLabel = this.activeActivity?.workload === '1'
            ? this.translateService.instant('hour')
            : this.translateService.instant('hours');
        return this.activeActivity?.workload + ' ' + hourLabel;
    }

    getContactHours(): string[] {
        return this.activeActivity.specifiedBy.contactHour.map((contactHour) => {
            const hourLabel = contactHour === '1'
                ? this.translateService.instant('hour')
                : this.translateService.instant('hours');
            return contactHour + ' ' + hourLabel;
        });

    }
}
