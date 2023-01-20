import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation
} from '@angular/core';
import { UxAppShellService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { EntitlementSpecView } from '@shared/swagger';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-entitlements-modal',
    templateUrl: './entitlements-modal.component.html',
    styleUrls: ['./entitlements-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class EntitlementsModalComponent implements OnInit, OnDestroy {
    @Input() modalTitle: string;
    @Input() modalId = 'entitlementModal';
    @Input() editEntitlementOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        displayName: string;
    }> = new EventEmitter();
    eventSave: Subject<void> = new Subject<void>();
    editEntitlement: EntitlementSpecView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    isSaveDisabled = false;
    modalTitleBreadcrumb: string[];
    isLoading = false;

    constructor(
        public uxService: UxAppShellService,
        public credentialBuilderService: CredentialBuilderService,
        private translateService: TranslateService
    ) {}
    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editEntitlementOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.entitlements-tab.editEntitlement'
            );
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.entitlements-tab.createEntitlement'
            );
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSave(): void {
        this.eventSave.next();
    }

    saveForm(event: EntitlementSpecView): void {
        this.closeModal(false, event.oid, event.displayName);
    }

    closeModal(isEdit: boolean, oid?: number, displayName?: string): void {
        this.onCloseModal.emit({ isEdit, oid, displayName });
    }
}
