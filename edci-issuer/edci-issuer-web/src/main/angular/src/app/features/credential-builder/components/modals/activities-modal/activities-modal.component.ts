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
import { LearningActivitySpecView, V1Service } from '@shared/swagger';
import { Subject, forkJoin } from 'rxjs';
import { take } from 'rxjs/operators';

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

        if (this.editActivityOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.activities-tab.editActivity'
            );
            this.getActivityDetails(this.editActivityOid, this.translateService.currentLang);
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
        this.closeModal(false, event?.oid, event?.displayName);
    }

    getActivityDetails(routeID, currentLanguage) {
        const observable = forkJoin({
            activityDetails: this.api.getLearningActivity(routeID, currentLanguage).pipe(take(1)),
            activityDirectedBy: this.api.listDirectedBy(routeID, currentLanguage).pipe(take(1)),
            activityAwardedBy: this.api.listAwardingBodiesAct(routeID, currentLanguage).pipe(take(1)),
            activitySubActivities: this.api.listHasActPart(routeID, currentLanguage).pipe(take(1)),
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
