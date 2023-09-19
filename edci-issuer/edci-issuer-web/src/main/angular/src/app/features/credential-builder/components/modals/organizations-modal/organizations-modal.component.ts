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
import { OrganizationSpecView, V1Service } from '@shared/swagger';
import { Subject, forkJoin } from 'rxjs';
import { take } from 'rxjs/operators';

@Component({
    selector: 'edci-organizations-modal',
    templateUrl: './organizations-modal.component.html',
    styleUrls: ['./organizations-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class OrganizationsModalComponent implements OnInit, OnDestroy {
    @Input() modalTitle: string;
    @Input() modalId = 'organizationModal';
    @Input() editOrganizationOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        displayName: string;
    }> = new EventEmitter();
    eventSave: Subject<void> = new Subject<void>();
    editOrganization: OrganizationSpecView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    modalTitleBreadcrumb: string[];
    isSaveDisabled = false;
    isLoading = false;
    modalData: any;

    constructor(
        public uxService: UxAppShellService,
        public credentialBuilderService: CredentialBuilderService,
        private translateService: TranslateService,
        private api: V1Service,
    ) { }

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editOrganizationOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.organizations-tab.editOrganization'
            );
            this.getOrganizationDetails(this.editOrganizationOid, this.translateService.currentLang);
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.organizations-tab.createOrganization'
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

    closeModal(isEdit: boolean, oid?: number, displayName?: string): void {
        this.onCloseModal.emit({ isEdit, oid, displayName });
    }

    saveForm(event: OrganizationSpecView) {
        this.closeModal(false, event?.oid, event?.displayName);
    }

    getOrganizationDetails(routeID, currentLanguage) {
        const observable = forkJoin({
            organizationDetails: this.api.getOrganization(routeID, currentLanguage).pipe(take(1)),
            accreditation: this.api.listOrgsAccreditation(routeID, currentLanguage).pipe(take(1)),
        });

        this.isLoading = true;
        observable.subscribe({
            next: value => {
                this.modalData = value;
                this.isLoading = false;
            },
            error: () => {
                this.isLoading = false;
            }
        });
    }
}
