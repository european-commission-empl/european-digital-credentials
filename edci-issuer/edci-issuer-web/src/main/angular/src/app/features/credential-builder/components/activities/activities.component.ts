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
    LearningActivitySpecView,
    V1Service,
    LearningActivitySpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { Router } from '@angular/router';

@Component({
    selector: 'edci-activities',
    templateUrl: './activities.component.html',
    styleUrls: ['./activities.component.scss'],
})
export class ActivitiesComponent implements OnInit, OnDestroy, OnChanges {
    activities: Array<LearningActivitySpecView> = [];
    activeActivity: LearningActivitySpecLiteView;
    editActivityOid: number;
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
    noActivitiesAddedYet = false;
    onActivitiesInputSearch = false;
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
        this.getActivityList();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.openModal && changes.openModal.currentValue) {
            this.newActivity();
        }
    }

    onDelete(oid: number): void {
        this.loading = true;
        this.api
            .deleteLearningActivity(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe({next : () => {
                this.loading = true;
                this.getActivityList(this.totalItems - 1);
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
        this.router.navigateByUrl('credential-builder/activities/' + oid);
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateLearningActivity(oid, this.translateService.currentLang)
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
        this.getActivityList();
    }

    onSort(event): void {
        if (event.sort !== null && event.order !== null) {
            if (event.sort !== this.sort || event.order.toUpperCase() !== this.direction) {
                this.loading = true;
                this.sort = event.sort;
                this.direction = event.order.toUpperCase();
                this.getActivityList();
            }
        }
    }

    newActivity(): void {
        this.router.navigateByUrl('credential-builder/activities');
    }

    onEmittedOCBQueryChange(value: string): void {
        this.inputSearchText = value;
        this.getActivityList();
    }

    private moveToFirstPage() {
        this.credentialBuilderService.redirectToPage.next(0);
    }

    private getActivityList(itemsLeft?: number) {
        // Prevents asking for the items of an empty page
        if (
            !!itemsLeft &&
            itemsLeft / this.perPage <= this.page &&
            this.page !== 0
        ) {
            this.page = this.page - 1;
        }
        if (!this.noActivitiesAddedYet) { this.listActivities(); }
    }

    private listActivities(): void {
        this.loading = true;
        this.api
            .listLearningActivity(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                this.inputSearchText,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next :  (data: PagedResourcesLearningActivitySpecLiteView) => {
                    this.activities = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeActivity = this.activities[0];
                    if (this.inputSearchText === '' && data.content?.length === 0) {
                        this.noActivitiesAddedYet = true;
                    }
                }
            }).add(() => this.loading = false);
    }

    private setLanguage(lang: string): void {
        this.selectedLanguage = this.activeActivity.defaultLanguage;
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
