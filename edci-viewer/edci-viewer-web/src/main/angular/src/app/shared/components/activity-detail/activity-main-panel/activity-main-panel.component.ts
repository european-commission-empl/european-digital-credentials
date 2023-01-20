import { Component, Input } from '@angular/core';
import { ActivityTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-activity-main-panel',
    templateUrl: './activity-main-panel.component.html',
    styleUrls: ['./activity-main-panel.component.scss'],
})
export class ActivityMainPanelComponent {
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
