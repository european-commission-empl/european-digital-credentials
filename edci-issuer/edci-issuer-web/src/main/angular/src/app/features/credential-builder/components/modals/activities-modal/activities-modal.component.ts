import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output, ViewEncapsulation
} from '@angular/core';
import { UxAppShellService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { LearningActivitySpecView } from '@shared/swagger';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-activities-modal',
    templateUrl: './activities-modal.component.html',
    styleUrls: ['./activities-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class ActivitiesModalComponent implements OnInit, OnDestroy {
    modalTitleBreadcrumb: string[];
    @Input() editActivityOid?: number;
    @Input() modalTitle: string;
    @Input() modalId = 'activityModal';
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        displayName: string;
    }> = new EventEmitter();

    eventSave: Subject<void> = new Subject<void>();
    destroy$: Subject<boolean> = new Subject<boolean>();
    activityBody: LearningActivitySpecView;
    isSaveDisabled = false;
    isLoading = false;

    constructor(
        public uxService: UxAppShellService,
        public credentialBuilderService: CredentialBuilderService,
        private translateService: TranslateService,
    ) {}

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;

        if (this.editActivityOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.activities-tab.editActivity'
            );
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.activities-tab.createActivity'
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

    saveForm(event: LearningActivitySpecView): void {
        this.closeModal(false, event.oid, event.displayName);
    }

}
