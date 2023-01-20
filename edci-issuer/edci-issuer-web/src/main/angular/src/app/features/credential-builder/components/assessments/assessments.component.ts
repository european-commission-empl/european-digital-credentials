import {
    Component,
    OnInit,
    OnDestroy,
    Input,
    OnChanges,
    SimpleChanges,
} from '@angular/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import {
    V1Service,
    AssessmentSpecLiteView,
    PagedResourcesAssessmentSpecLiteView,
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { Router } from '@angular/router';

@Component({
    selector: 'edci-assessments',
    templateUrl: './assessments.component.html',
    styleUrls: ['./assessments.component.scss'],
})
export class AssessmentsComponent implements OnInit, OnDestroy, OnChanges {
    assessments: Array<AssessmentSpecLiteView> = [];
    activeAssessment: AssessmentSpecLiteView;
    assessmentToEditOid: number;
    modalTitle: string;
    @Input() openModal = false;
    modalEdited = false;
    loading = true;
    totalItems = 0;
    perPage = 7;
    page = 0;
    sort = 'auditDAO.updateDate';
    direction = 'DESC';
    inputSearchText = '';
    noAssessmentsAddedYet = false;
    selectedRowIndex = 0;
    availableLanguages: string[];
    selectedLanguage: string = this.translateService.currentLang;
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
        if (this.openModal) {
            this.newAssessments();
        }
        this.getAssessmentList();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.openModal && changes.openModal.currentValue) {
            this.newAssessments();
        }
    }

    onDelete(oid: number): void {
        this.loading = true;
        this.api
            .deleteAssessment(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe({next : () => {
                this.loading = true;
                this.getAssessmentList(this.totalItems - 1);
                this.notificationService.showNotification({
                    severity: 'success',
                    summary: this.translateService.instant('common.delete'),
                    detail: this.translateService.instant(
                        'credential-builder.operationSuccessful'
                    ),
                });
            }}).add(() => this.loading = false);
    }

    onEdit(oid: number): void {
        this.router.navigateByUrl('credential-builder/assessments/' + oid);
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateAssessment(oid, this.translateService.currentLang)
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
        this.getAssessmentList();
    }

    onSort(event): void {
        if (event.sort !== null && event.order !== null) {
            if (event.sort !== this.sort || event.order.toUpperCase() !== this.direction) {
                this.loading = true;
                this.sort = event.sort;
                this.direction = event.order.toUpperCase();
                this.getAssessmentList();
            }
        }
    }

    newAssessments(): void {
        this.router.navigateByUrl('credential-builder/assessments');
    }

    onEmittedOCBQueryChange(value: string): void {
        this.inputSearchText = value;
        this.getAssessmentList();
    }

    private moveToFirstPage() {
        this.credentialBuilderService.redirectToPage.next(0);
    }

    private getAssessmentList(itemsLeft?: number): void {
        // Prevents asking for the items of an empty page
        if (
            !!itemsLeft &&
            itemsLeft / this.perPage <= this.page &&
            this.page !== 0
        ) {
            this.page = this.page - 1;
        }
        if (!this.noAssessmentsAddedYet) { this.listAssessments(); }
    }

    private listAssessments(): void {
        this.api
            .listAssessment(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                this.inputSearchText,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next : (data: PagedResourcesAssessmentSpecLiteView) => {
                    this.assessments = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeAssessment = this.assessments[0];
                    if (this.inputSearchText === '' && data.content?.length === 0) {
                        this.noAssessmentsAddedYet = true;
                    }
                }
            }).add(() => this.loading = false);
    }
    private setLanguage(lang: string): void {
        this.selectedLanguage = this.activeAssessment.defaultLanguage;
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
