import { Component, Input } from '@angular/core';
import { EntitlementTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-entitlement-information-panel',
    templateUrl: './entitlement-information-panel.component.html',
    styleUrls: ['./entitlement-information-panel.component.scss'],
})
export class EntitlementInformationPanelComponent {
    private _activeEntitlement: EntitlementTabView;
    @Input()
    set activeEntitlement(value: EntitlementTabView) {
        this.isPanelExpanded = true;
        this._activeEntitlement = value;
    }
    get activeEntitlement(): EntitlementTabView {
        return this._activeEntitlement;
    }

    isPanelExpanded: boolean = true;

    constructor() {}
}
