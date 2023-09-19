import { Component, Input } from '@angular/core';
import { EntitlementTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-entitlement-other-information-panel',
    templateUrl: './entitlement-other-information-panel.component.html',
    styleUrls: ['./entitlement-other-information-panel.component.scss'],
})
export class EntitlementOtherInformationPanelComponent {
    private _activeEntitlement: EntitlementTabView;
    @Input()
    set activeEntitlement(value: EntitlementTabView) {
        this.isPanelExpanded = false;
        this._activeEntitlement = value;
    }
    get activeEntitlement(): EntitlementTabView {
        return this._activeEntitlement;
    }

    isPanelExpanded = false;

    constructor() {}
}
