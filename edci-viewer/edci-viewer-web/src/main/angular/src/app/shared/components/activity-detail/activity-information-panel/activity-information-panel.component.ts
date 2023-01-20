import { Component, Input } from '@angular/core';
import { ActivityTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-activity-information-panel',
    templateUrl: './activity-information-panel.component.html',
    styleUrls: ['./activity-information-panel.component.scss'],
})
export class ActivityInformationPanelComponent {
    private _activeActivity: ActivityTabView;
    @Input()
    set activeActivity(value: ActivityTabView) {
        this.isPanelExpanded = true;
        this._activeActivity = value;
    }
    get activeActivity(): ActivityTabView {
        return this._activeActivity;
    }

    isPanelExpanded = true;

    constructor() {}
}
