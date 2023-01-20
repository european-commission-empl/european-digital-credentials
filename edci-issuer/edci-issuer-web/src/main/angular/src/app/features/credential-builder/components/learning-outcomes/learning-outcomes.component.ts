import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import {
    V1Service,
    LearningOutcomeSpecLiteView,
    PagedResourcesLearningOutcomeSpecLiteView,
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { takeUntil } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
    selector: 'edci-learning-outcomes',
    templateUrl: './learning-outcomes.component.html',
    styleUrls: ['./learning-outcomes.component.scss'],
})
export class LearningOutcomesComponent implements OnInit, OnDestroy {
    @Input() openModal = true;
    learningOutcomes: Array<LearningOutcomeSpecLiteView> = [];
    activeLearningOutcome: LearningOutcomeSpecLiteView;
    editLearningOutcomeOid: number;
    selectedLanguage: string = this.translateService.currentLang;
    availableLanguages: string[];
    noLearningOutcomesAddedYet = false;
    modalTitle: string;
    modalEdited = false;
    loading = true;
    totalItems = 0;
    perPage = 7;
    page = 0;
    sort = 'auditDAO.updateDate';
    direction = 'DESC';
    inputSearchText = '';
    selectedRowIndex = 0;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private translateService: TranslateService,
        private api: V1Service,
        private notificationService: NotificationService,
        private credentialBuilderService: CredentialBuilderService,
        private router: Router
    ) {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                if (this.availableLanguages) {
                    this.setLanguage(event.lang);
                }
            });
    }

    ngOnInit() {
        this.getLearningOutcomesList();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onDelete(oid: number): void {
        this.loading = true;
        this.api
            .deleteLearningOutcome(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
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
            }).add(() => this.loading = false);
    }

    onEdit(oid: number): void {
        this.router.navigateByUrl('credential-builder/learning-outcomes/' + oid);
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateLearningOutcome(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                () => {
                    this.moveToFirstPage();
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary:
                            this.translateService.instant('common.duplicate'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                },
                () => (this.loading = false)
            );
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.page;
        this.perPage = event.pageSize;
        this.getLearningOutcomesList();
    }

    onSort(event): void {
        if (event.sort !== null && event.order !== null) {
            if (event.sort !== this.sort || event.order.toUpperCase() !== this.direction) {
                this.loading = true;
                this.sort = event.sort;
                this.direction = event.order.toUpperCase();
                this.getLearningOutcomesList();
            }
        }
    }

    newLearningOutcome(): void {
        this.router.navigateByUrl('credential-builder/learning-outcomes');
    }

    onEmittedOCBQueryChange(value: string): void {
        this.inputSearchText = value;
        this.getLearningOutcomesList();
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
        if (!this.noLearningOutcomesAddedYet) { this.listLearningOutcomes(); }
    }

    private listLearningOutcomes(): void {
        this.loading = true;
        this.api
            .listLearningOutcome(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                this.inputSearchText,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({ next:
                (data: PagedResourcesLearningOutcomeSpecLiteView) => {
                    this.learningOutcomes = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeLearningOutcome = this.learningOutcomes[0];
                    if (this.inputSearchText === '' && data.content?.length === 0) {
                        this.noLearningOutcomesAddedYet = true;
                    }
                },
            }).add(() => this.loading = false);
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

}
