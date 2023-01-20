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
import { LearningAchievementSpecView } from '@shared/swagger';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-achievements-modal',
    templateUrl: './achievements-modal.component.html',
    styleUrls: ['./achievements-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AchievementsModalComponent implements OnInit, OnDestroy {
    @Input() modalTitle: string;
    @Input() modalId = 'achievementModal';
    @Input() editAchievementOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        displayName: string;
    }> = new EventEmitter();
    eventSave: Subject<void> = new Subject<void>();
    editAchievement: LearningAchievementSpecView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    isSaveDisabled = false;
    modalTitleBreadcrumb: string[];
    isLoading = false;

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private translateService: TranslateService
    ) {}

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editAchievementOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.achievements-tab.editAchievement'
            );
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.achievements-tab.createAchievement'
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

    public saveForm(event: LearningAchievementSpecView): void {
        this.closeModal(false, event.oid, event.displayName);
    }

}
