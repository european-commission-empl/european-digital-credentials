import { Component, Input } from '@angular/core';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { EntitlementTabView } from '../../swagger';

@Component({
    selector: 'edci-viewer-entitlement-detail',
    templateUrl: './entitlement-detail.component.html',
    styleUrls: ['./entitlement-detail.component.scss'],
})
export class EntitlementDetailComponent {
    private _activeEntitlement: EntitlementTabView;
    @Input()
    set activeEntitlement(value: EntitlementTabView) {
        this._activeEntitlement = value;
    }
    get activeEntitlement(): EntitlementTabView {
        return this._activeEntitlement;
    }

    constructor(private entityLinkService: EntityLinkService) {}

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }

}
