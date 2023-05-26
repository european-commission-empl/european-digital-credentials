import { Component, Input } from '@angular/core';
import { ActivityTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-activity-other-information-panel',
    templateUrl: './activity-other-information-panel.component.html',
    styleUrls: ['./activity-other-information-panel.component.scss'],
})
export class ActivityOtherInformationPanelComponent {
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
    constructor() {}
}
