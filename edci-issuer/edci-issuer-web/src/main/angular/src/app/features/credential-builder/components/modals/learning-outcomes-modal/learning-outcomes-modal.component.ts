import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { LearningOutcomeSpecView } from '@shared/swagger';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-learning-outcomes-modal',
    templateUrl: './learning-outcomes-modal.component.html',
    styleUrls: ['./learning-outcomes-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class LearningOutcomesModalComponent implements OnInit, OnDestroy {
    @Input() modalTitle: string;
    @Input() modalId = 'learningOutcomeModal';
    @Input() editLearningOutcomeOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        displayName: string;
    }> = new EventEmitter();
    eventSave: Subject<void> = new Subject<void>();
    editLearningOutcome: LearningOutcomeSpecView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    isSaveDisabled = false;
    isLoading = false;
    modalTitleBreadcrumb: string[];

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private translateService: TranslateService
    ) {}

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editLearningOutcomeOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.learning-outcomes-tab.editLearningOutcome'
            );
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.learning-outcomes-tab.createLearningOutcome'
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

    saveForm(event: LearningOutcomeSpecView): void {
        this.closeModal(false, event.oid, event.displayName);
    }
}
