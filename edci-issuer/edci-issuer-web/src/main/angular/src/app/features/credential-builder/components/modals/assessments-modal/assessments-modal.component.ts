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
import { AssessmentSpecView } from '@shared/swagger';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-assessments-modal',
    templateUrl: './assessments-modal.component.html',
    styleUrls: ['./assessments-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AssessmentsModalComponent implements OnInit, OnDestroy {
    @Input() modalTitle: string;
    @Input() modalId = 'assessmentModal';
    @Input() editAssessmentOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        displayName: string;
    }> = new EventEmitter();
    eventSave: Subject<void> = new Subject<void>();
    editAssessment: AssessmentSpecView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    isSaveDisabled = false;
    isLoading = false;
    modalTitleBreadcrumb: string[];

    constructor(
        public uxService: UxAppShellService,
        public credentialBuilderService: CredentialBuilderService,
        private translateService: TranslateService,
    ) {}

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editAssessmentOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.assessment-tab.editAssessment'
            );
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.assessment-tab.createAssessment'
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

    public saveForm(event: AssessmentSpecView): void {
        this.closeModal(false, event.oid, event.displayName);
    }

}
