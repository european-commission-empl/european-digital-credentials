import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
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
    activeId: string;
    activeEntitlement: EntitlementTabView = this.shareDataService.activeEntity;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private shareDataService: ShareDataService) {}

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
}
