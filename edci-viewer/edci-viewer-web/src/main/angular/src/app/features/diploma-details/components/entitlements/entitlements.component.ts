import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { EntitlementTabView } from 'src/app/shared/swagger/model/entitlementTabView';

@Component({
    selector: 'edci-viewer-entitlements',
    templateUrl: './entitlements.component.html',
    styleUrls: ['./entitlements.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class EntitlementsComponent implements OnInit, OnDestroy {
    entitlements: EntitlementTabView[] = this.shareDataService.entitlements;
    activeId: string;
    activeEntitlement: EntitlementTabView = this.shareDataService.activeEntity;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private shareDataService: ShareDataService
    ) {
        // this.activatedRoute.queryParams
        //     .takeUntil(this.destroy$)
        //     .subscribe((params) => {
        //         this.activeId = params.id;
        //         if (this.entitlements) {
        //             if (!this.activeId) {
        //                 this.setInitialActiveEntitlement();
        //             } else {
        //                 this.setEntitlementById();
        //             }
        //         }
        //     });
    }

    ngOnInit() {
        this.shareDataService
            .changeEntitySelection()
            .takeUntil(this.destroy$)
            .subscribe((entitlement) => {
                this.activeEntitlement = entitlement;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    // onSelect(event) {
    //     this.entitlements.forEach((entitlement: EntitlementTabView) => {
    //         entitlement['active'] = false;
    //         if (entitlement.subEntitlements) {
    //             entitlement.subEntitlements.forEach(
    //                 (part: EntitlementTabView) => {
    //                     part['active'] = false;
    //                 }
    //             );
    //         }
    //     });
    //     event.active = true;
    //     this.activeEntitlement = event;
    //     this.setQueryParams();
    // }

    // private setInitialActiveEntitlement() {
    //     this.entitlements.forEach(
    //         (entitlement: EntitlementTabView, i: number) => {
    //             if (i === 0) {
    //                 entitlement['active'] = true;
    //             } else {
    //                 entitlement['active'] = false;
    //             }
    //             if (entitlement.subEntitlements) {
    //                 entitlement.subEntitlements.forEach(
    //                     (part: EntitlementTabView) => {
    //                         part['active'] = false;
    //                     }
    //                 );
    //             }
    //         }
    //     );
    //     this.activeEntitlement = this.entitlements[0];
    //     this.setQueryParams();
    // }

    // private setEntitlementById() {
    //     this.entitlements.forEach((entitlement: EntitlementTabView) => {
    //         if (entitlement.id === this.activeId) {
    //             entitlement['active'] = true;
    //             this.activeEntitlement = entitlement;
    //         } else {
    //             entitlement['active'] = false;
    //         }
    //         if (entitlement.subEntitlements) {
    //             entitlement.subEntitlements.forEach(
    //                 (part: EntitlementTabView) => {
    //                     if (part.id === this.activeId) {
    //                         part['active'] = true;
    //                         this.activeEntitlement = part;
    //                     } else {
    //                         part['active'] = false;
    //                     }
    //                 }
    //             );
    //         }
    //     });
    // }

    // private setQueryParams() {
    //     this.router.navigate([], {
    //         relativeTo: this.activatedRoute,
    //         queryParams: { id: this.activeEntitlement.id },
    //         queryParamsHandling: 'merge',
    //     });
    // }
}
