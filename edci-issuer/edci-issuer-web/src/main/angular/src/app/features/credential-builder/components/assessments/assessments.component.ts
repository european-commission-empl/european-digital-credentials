import {
    Component,
    OnInit,
    OnDestroy,
    Input,
    OnChanges,
    SimpleChanges,
} from '@angular/core';
import { UxAppShellService } from '@eui/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import {
    AssessmentSpecView,
    V1Service,
    AssessmentSpecLiteView,
    PagedResourcesAssessmentSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    OrganizationSpecLiteView,
} from '@shared/swagger';
import { Subject, forkJoin, Observable } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';

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
    @Input() openModal: boolean = false;
    modalEdited: boolean = false;
    loading: boolean = true;
    totalItems: number = 0;
    perPage: number = 7;
    page: number = 0;
    sort: string = 'auditDAO.updateDate';
    direction: string = 'DESC';
    selectedRowIndex: number = 0;
    availableLanguages: string[];
    selectedLanguage: string = this.translateService.currentLang;
    firstLoad: boolean = true;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private uxService: UxAppShellService,
        private translateService: TranslateService,
        private api: V1Service,
        private notificationService: NotificationService,
        private credentialBuilderService: CredentialBuilderService
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
        this.api
            .deleteAssessment(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.loading = true;
                this.getAssessmentList(this.totalItems - 1);
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
        this.assessmentToEditOid = oid;
        this.openModal = true;
        this.modalTitle = this.translateService.instant(
            'credential-builder.assessment-tab.editAssessment'
        );
        this.uxService.openModal('assessmentModal');
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
        this.modalTitle = this.translateService.instant(
            'credential-builder.assessment-tab.createAssessment'
        );
        this.assessmentToEditOid = null;
        this.openModal = true;
        this.uxService.openModal('assessmentModal');
    }

    closeModal(closeInfo: {
        isEdit: boolean;
        oid: number;
        title: string;
    }): void {
        this.uxService.closeModal('assessmentModal');
        this.openModal = false;
        // If it's true, move to page 1 and trigger onPageEvent.
        if (closeInfo.isEdit) {
            this.modalEdited = true;
            if (!this.assessments.length) {
                this.getAssessmentList();
            } else {
                this.moveToFirstPage();
            }
        }
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
        this.listAssessments();
    }

    private listAssessments(): void {
        this.api
            .listAssessment(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                '',
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (data: PagedResourcesAssessmentSpecLiteView) => {
                    this.assessments = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeAssessment = this.assessments[0];
                    this.loading = false;
                    this.firstLoad = false;
                },
                () => {
                    this.loading = false;
                    this.firstLoad = false;
                }
            );
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
