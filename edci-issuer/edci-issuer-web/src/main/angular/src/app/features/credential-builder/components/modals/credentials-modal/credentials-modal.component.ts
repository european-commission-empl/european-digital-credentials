import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation,
} from '@angular/core';
import { UxAppShellService } from '@eui/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { EuropassCredentialSpecView } from '@shared/swagger';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-credentials-modal',
    templateUrl: './credentials-modal.component.html',
    styleUrls: ['./credentials-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class CredentialsModalComponent implements OnInit, OnDestroy {
    @Input() modalId = 'credentialModal';
    @Input() modalTitle: string;
    @Input() editCredentialOid?: number;
    @Output() onCloseModal: EventEmitter<boolean> = new EventEmitter();
    eventSave: Subject<void> = new Subject<void>();
    editCredential: EuropassCredentialSpecView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    isSaveDisabled = false;
    modalTitleBreadcrumb: string[];
    isLoading = false;

    constructor(
        public uxService: UxAppShellService,
        public credentialBuilderService: CredentialBuilderService
    ) {}

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSave(): void {
        this.eventSave.next();
    }

    closeModal(isEdit: boolean): void {
        this.onCloseModal.emit(isEdit);
    }

    saveForm(event: any): void {
        this.closeModal(false);
    }
}
