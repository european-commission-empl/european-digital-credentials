import { Component, OnInit, OnDestroy, Input, OnChanges, SimpleChanges } from '@angular/core';
import { UxService } from '@eui/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import {
    LearningActivitySpecView,
    V1Service,
    LearningActivitySpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    OrganizationSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
} from '@shared/swagger';
import { Subject, forkJoin, Observable } from 'rxjs';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';

@Component({
    selector: 'edci-activities',
    templateUrl: './activities.component.html',
    styleUrls: ['./activities.component.scss'],
})
export class ActivitiesComponent implements OnInit, OnDestroy, OnChanges {
    activities: Array<LearningActivitySpecView> = [];
    activeActivity: LearningActivitySpecLiteView;
    activityDetails: LearningActivitySpecView;
    subActivities: Array<LearningActivitySpecLiteView> = [];
    directedBy: OrganizationSpecLiteView;
    editActivityOid: number;
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
    loadingDetails: boolean = false;
    stopResources$: Subject<boolean> = new Subject<boolean>();
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
        this.api
            .deleteLearningActivity(oid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(() => {
                this.loading = true;
                this.getActivityList(this.totalItems - 1);
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
        this.editActivityOid = oid;
        this.openModal = true;
        this.modalTitle = this.translateService.instant(
            'credential-builder.activities-tab.editActivity'
        );
        this.uxService.openModal('activityModal');
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateLearningActivity(oid, this.translateService.currentLang)
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
        this.getActivityDetails(oid);
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.first / this.perPage;
        this.getActivityList();
    }

    onSort(event): void {
        const order = event.order === 1 ? 'ASC' : 'DESC';
        if (event.field !== this.sort || order !== this.direction) {
            this.loading = true;
            this.sort = event.field;
            this.direction = order;
            this.getActivityList();
        }
    }

    newActivity(): void {
        this.modalTitle = this.translateService.instant(
            'credential-builder.activities-tab.createActivity'
        );
        this.editActivityOid = null;
        this.openModal = true;
        this.uxService.openModal('activityModal');
    }

    closeModal(closeInfo: {isEdit: boolean, oid?: string}): void {
        this.uxService.closeModal('activityModal');
        this.openModal = false;
        // If it's true, move to page 1 and trigger onPageEvent.
        if (closeInfo.isEdit) {
            this.modalEdited = true;
            if (!this.activities.length) {
                this.getActivityList();
            } else {
                this.moveToFirstPage();
            }
        }
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
        this.listActivities();
    }

    private listActivities(): void {
        this.api
            .listLearningActivity(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                '',
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (data: PagedResourcesLearningActivitySpecLiteView) => {
                    this.activities = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeActivity = this.activities[0];
                    this.loading = false;
                    this.firstLoad = false;
                },
                () => {
                    this.loading = false;
                    this.firstLoad = false;
                }
            );
    }

    private getActivityDetails(oid: number): void {
        if (
            this.modalEdited ||
            _get(this.activityDetails, 'oid', null) !== oid
        ) {
            this.resetDetails();
            this.modalEdited = false;
            this.loadingDetails = true;
            this.api
                .getLearningActivity(oid, this.translateService.currentLang)
                .takeUntil(this.destroy$)
                .subscribe(
                    (activity: LearningActivitySpecView) => {
                        this.stopResources$.next(true);
                        this.activityDetails = activity;
                        this.availableLanguages =
                            activity.additionalInfo.languages;
                        this.setLanguage(this.translateService.currentLang);
                        this.getActivityResources(oid);
                    },
                    () => {}
                );
        }
    }
    private getActivityResources(oid: number): void {
        forkJoin({
            subActivities: this.getSubActivities(oid),
            directedBy: this.getDirectedBy(oid),
        })
            .takeUntil(this.stopResources$)
            .subscribe((resources) => {
                this.subActivities = resources.subActivities.content;
                this.directedBy = resources.directedBy.content[0];
                this.loadingDetails = false;
            });
    }

    private getSubActivities(
        oid: number
    ): Observable<PagedResourcesLearningActivitySpecLiteView> {
        return this.api.listHasActPart(oid, this.translateService.currentLang);
    }

    private getDirectedBy(
        oid: number
    ): Observable<PagedResourcesOrganizationSpecLiteView> {
        return this.api.listDirectedBy(oid, this.translateService.currentLang);
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

    private resetDetails(): void {
        this.activityDetails = null;
        this.subActivities = null;
        this.directedBy = null;
    }
}
