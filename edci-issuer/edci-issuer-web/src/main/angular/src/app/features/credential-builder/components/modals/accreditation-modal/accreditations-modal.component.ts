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
import { AccreditationSpecView, V1Service } from '@shared/swagger';
import { Subject, forkJoin } from 'rxjs';
import { take } from 'rxjs/operators';

@Component({
    selector: 'edci-accreditations-modal',
    templateUrl: './accreditations-modal.component.html',
    styleUrls: ['./accreditations-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AccreditationsModalComponent implements OnInit, OnDestroy {
    @Input() modalTitle: string;
    @Input() modalId = 'accreditationModal';
    @Input() editAccreditationOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        displayName: string;
    }> = new EventEmitter();
    eventSave: Subject<void> = new Subject<void>();
    editAccreditation: AccreditationSpecView;
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
    ) {}

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editAccreditationOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.accreditation-tab.editAccreditation'
            );
            this.getAccreditationDetails(this.editAccreditationOid, this.translateService.currentLang);
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.accreditation-tab.createAccreditation'
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

    saveForm(event: AccreditationSpecView) {
        this.closeModal(false, event?.oid, event?.displayName);
    }

    getAccreditationDetails(routeID, currentLanguage) {
        const observable = forkJoin({
            accreditationDetails: this.api.getAccreditation(routeID, currentLanguage).pipe(take(1)),
            accreditationAccreditingAgent: this.api.getAccreditingAgent(routeID, currentLanguage).pipe(take(1)),
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
