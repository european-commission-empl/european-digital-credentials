import { Component, Input } from '@angular/core';
import { EntitlementTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-entitlement-main-panel',
    templateUrl: './entitlement-main-panel.component.html',
    styleUrls: ['./entitlement-main-panel.component.scss']
})
export class EntitlementMainPanelComponent {
    private _activeEntitlement: EntitlementTabView;

    @Input()
    set activeEntitlement(value: EntitlementTabView) {
        this.isPanelExpanded = true;
        this._activeEntitlement = value;
    }

    get activeEntitlement(): EntitlementTabView {
        return this._activeEntitlement;
    }
    isPanelExpanded = true;

    constructor() { }
}
