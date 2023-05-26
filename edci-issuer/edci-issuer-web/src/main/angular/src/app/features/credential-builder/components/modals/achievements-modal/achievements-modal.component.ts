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
import { LearningAchievementSpecView, V1Service } from '@shared/swagger';
import { Subject, forkJoin } from 'rxjs';
import { take } from 'rxjs/operators';

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
    modalData: any;

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private translateService: TranslateService,
        private api: V1Service,
    ) {}

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editAchievementOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.achievements-tab.editAchievement'
            );
            this.getAchievementDetails(this.editAchievementOid, this.translateService.currentLang);
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
        this.closeModal(false, event?.oid, event?.displayName);
    }

    getAchievementDetails(routeID, currentLanguage) {
        const observable = forkJoin({
            achievementDetails: this.api.getLearningAchievement(routeID, currentLanguage).pipe(take(1)),
            achievementSubAchievements: this.api.listSubAchievements(routeID, currentLanguage).pipe(take(1)),
            achievementProvenBy: this.api.getProvenBy(routeID, currentLanguage).pipe(take(1)),
            achievementLearningOutcome: this.api.listLearningOutcomes(routeID, currentLanguage).pipe(take(1)),
            achievementInfluencedBy: this.api.listInfluencedBy(routeID, currentLanguage).pipe(take(1)),
            achievementEntitledTo: this.api.listEntitlesTo(routeID, currentLanguage).pipe(take(1)),
            achievementAwardingBodies: this.api.listAwardingBodiesAch(routeID, currentLanguage).pipe(take(1))
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
