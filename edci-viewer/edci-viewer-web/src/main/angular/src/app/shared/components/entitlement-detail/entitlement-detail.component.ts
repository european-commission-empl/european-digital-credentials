import { Component, Input } from '@angular/core';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { EntitlementTabView } from '../../swagger';

@Component({
    selector: 'edci-viewer-entitlement-detail',
    templateUrl: './entitlement-detail.component.html',
    styleUrls: ['./entitlement-detail.component.scss'],
})
export class EntitlementDetailComponent {
    @Input() activeEntitlement: EntitlementTabView;

    constructor(private entityLinkService: EntityLinkService) {}

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }

    // linkToEntitlement(entitlement: EntitlementTabView): void {
    //     if (this.entityLinkService.isEntitlementAvailable(entitlement.id)) {
    //         this.router.navigate([], {
    //             relativeTo: this.activatedRoute,
    //             queryParams: { id: entitlement.id },
    //             queryParamsHandling: 'merge',
    //         });
    //     } else {
    //         this.openSubEntitlementModal(entitlement);
    //     }
    // }

    // openValidWithModal(organization: OrganizationTabView): void {
    //     organization['isModal'] = true;
    //     const info: EntityModalInformation = {
    //         id: new Date().toISOString(),
    //         entityName: 'organization',
    //         entity: organization,
    //         modalTitle: this.translateService.instant(
    //             'details.entitlements-tab.validWith'
    //         ),
    //     };
    //     this.entityLinkService.sendEntityModalInformation(info);
    // }

    // private openSubEntitlementModal(entitlement: EntitlementTabView): void {
    //     const info: EntityModalInformation = {
    //         id: entitlement.id,
    //         entityName: 'entitlement',
    //         entity: entitlement,
    //         modalTitle: this.translateService.instant(
    //             'details.entitlements-tab.subEntitlement'
    //         ),
    //     };
    //     this.entityLinkService.sendEntityModalInformation(info);
    // }
}
