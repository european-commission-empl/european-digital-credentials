import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { UxService } from '@eui/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import {
    LearningOutcomeSpecView,
    V1Service,
    LearningOutcomeSpecLiteView,
    PagedResourcesLearningOutcomeSpecLiteView,
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';

@Component({
    selector: 'edci-learning-outcomes',
    templateUrl: './learning-outcomes.component.html',
    styleUrls: ['./learning-outcomes.component.scss'],
})
export class LearningOutcomesComponent implements OnInit, OnDestroy {

    @Input() openModal: boolean = true;

    learningOutcomes: Array<LearningOutcomeSpecLiteView> = [];
    activeLearningOutcome: LearningOutcomeSpecLiteView;
    learningOutcomeDetails: LearningOutcomeSpecView;
    editLearningOutcomeOid: number;
    selectedLanguage: string = this.translateService.currentLang;
    availableLanguages: string[];
    modalTitle: string;
    modalEdited: boolean = false;
    loading: boolean = true;
    totalItems: number = 0;
    perPage: number = 7;
    page: number = 0;
    sort: string = 'auditDAO.updateDate';
    direction: string = 'DESC';
    selectedRowIndex: number = 0;
    firstLoad: boolean = true;
    loadingDetails: boolean = false;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private uxService: UxService,
        private translateService: TranslateService,
        private api: V1Service,
        private notificationService: NotificationService,
        private credentialBuilderService: CredentialBuilderService
    ) {
        this.translateService.onLangChange
            .takeUntil(this.destroy$)
            .subscribe((event: LangChangeEvent) => {
                if (this.availableLanguages) {
                    this.setLanguage(event.lang);
                }
            });
    }

    ngOnInit() {
        if (this.openModal) {
            this.newLearningOutcome();
        }
        this.getLearningOutcomesList();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onDelete(oid: number): void {
        this.api
            .deleteLearningOutcome(oid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(() => {
                this.loading = true;
                this.getLearningOutcomesList(this.totalItems - 1);
                this.notificationService.showNotification({
                    severity: 'success',
                    summary: this.translateService.instant('common.delete'),
                    detail: this.translateService.instant(
                        'credential-builder.operationSuccessful'
                    ),
                });
            });
    }

    onEdit(oid: number): void {
        this.editLearningOutcomeOid = oid;
        this.openModal = true;
        this.modalTitle = this.translateService.instant(
            'credential-builder.learning-outcomes-tab.editLearningOutcome'
        );
        this.uxService.openModal('learningOutcomeModal');
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateLearningOutcome(oid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(
                () => {
                    this.moveToFirstPage();
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant(
                            'common.duplicate'
                        ),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                },
                () => (this.loading = false)
            );
    }
    onSelect(oid: number): void {
        this.getLearningOutcomeDetails(oid);
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.first / this.perPage;
        this.getLearningOutcomesList();
    }
    onSort(event): void {
        const order = event.order === 1 ? 'ASC' : 'DESC';
        if (event.field !== this.sort || order !== this.direction) {
            this.loading = true;
            this.sort = event.field;
            this.direction = order;
            this.getLearningOutcomesList();
        }
    }

    newLearningOutcome(): void {
        this.editLearningOutcomeOid = null;
        this.modalTitle = this.translateService.instant(
            'credential-builder.learning-outcomes-tab.createLearningOutcome'
        );
        this.openModal = true;
        this.uxService.openModal('learningOutcomeModal');
    }

    closeModal(closeInfo: {isEdit: boolean, oid?: string}): void {
        this.uxService.closeModal('learningOutcomeModal');
        this.openModal = false;
        // If it's true, move to page 1 and trigger onPageEvent.
        if (closeInfo.isEdit) {
            this.modalEdited = true;
            if (!this.learningOutcomes.length) {
                this.getLearningOutcomesList();
            } else {
                this.moveToFirstPage();
            }
        }
    }

    private moveToFirstPage() {
        this.credentialBuilderService.redirectToPage.next(0);
    }

    private getLearningOutcomesList(itemsLeft?: number): void {
        // Prevents asking for the items of an empty page
        if (
            !!itemsLeft &&
            itemsLeft / this.perPage <= this.page &&
            this.page !== 0
        ) {
            this.page = this.page - 1;
        }
        this.listLearningOutcomes();
    }

    private listLearningOutcomes(): void {
        this.api
            .listLearningOutcome(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                '',
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (data: PagedResourcesLearningOutcomeSpecLiteView) => {
                    this.learningOutcomes = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeLearningOutcome = this.learningOutcomes[0];
                    this.loading = false;
                    this.firstLoad = false;
                },
                () => {
                    this.loading = false;
                    this.firstLoad = false;
                }
            );
    }

    private getLearningOutcomeDetails(oid: number) {
        if (
            this.modalEdited ||
            _get(this.learningOutcomeDetails, 'oid', null) !== oid
        ) {
            this.resetDetails();
            this.modalEdited = false;
            this.loadingDetails = true;
            this.api
                .getLearningOutcome(oid, this.translateService.currentLang)
                .takeUntil(this.destroy$)
                .subscribe(
                    (learningOutcome: LearningOutcomeSpecView) => {
                        this.learningOutcomeDetails = learningOutcome;
                        this.availableLanguages =
                            learningOutcome.additionalInfo.languages;
                        this.setLanguage(this.translateService.currentLang);
                        this.loadingDetails = false;
                    },
                    () => {}
                );
        }
    }

    private setLanguage(lang: string): void {
        this.selectedLanguage = this.activeLearningOutcome.defaultLanguage;
        /* let isLanguageAvailable: boolean = false;
        this.availableLanguages.forEach((language: string) => {
            if (lang === language) {
                this.selectedLanguage = lang;
                isLanguageAvailable = true;
            }
        });
        if (!isLanguageAvailable) {
            this.selectedLanguage = this.activeLearningOutcome.defaultLanguage;
        } */
    }

    private resetDetails(): void {
        this.learningOutcomeDetails = null;
    }
}
