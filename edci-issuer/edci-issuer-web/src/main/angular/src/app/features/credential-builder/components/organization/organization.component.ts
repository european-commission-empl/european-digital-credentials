import { Component, OnInit, OnDestroy, Input, OnChanges, SimpleChanges } from '@angular/core';
import {
    OrganizationSpecView,
    V1Service,
    OrganizationSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ResourceOrganizationSpecView,
} from '@shared/swagger';
import { Subject, forkJoin, Observable } from 'rxjs';
import { UxService } from '@eui/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';

@Component({
    selector: 'edci-organization',
    templateUrl: './organization.component.html',
    styleUrls: ['./organization.component.scss'],
})
export class OrganizationComponent implements OnInit, OnDestroy, OnChanges {
    organizations: Array<OrganizationSpecLiteView> = [];
    activeOrganization: OrganizationSpecLiteView;
    organizationDetails: OrganizationSpecView;
    editOrganizationOid: number;
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
    parentOrganizationName: string;
    selectedLanguage: string = this.translateService.currentLang;
    availableLanguages: string[];
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
        if (this.openModal) {
            this.newOrganization();
        }
        this.getOrganizationList();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onDelete(oid: number): void {
        this.api
            .deleteOrganization(oid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(() => {
                this.loading = true;
                this.getOrganizationList(this.totalItems - 1);
                this.notificationService.showNotification({
                    severity: 'success',
                    summary: this.translateService.instant('common.delete'),
                    detail: this.translateService.instant(
                        'credential-builder.operationSuccessful'
                    ),
                });
            });
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.openModal && changes.openModal.currentValue) {
            this.newOrganization();
        }
    }

    onEdit(oid: number): void {
        this.editOrganizationOid = oid;
        this.openModal = true;
        this.modalTitle = this.translateService.instant(
            'credential-builder.organizations-tab.editOrganization'
        );
        this.uxService.openModal('organizationModal');
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateOrganization(oid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(
                (newOrganization: ResourceOrganizationSpecView) => {
                    this.moveToFirstPage();
                    this.setUnitOf(newOrganization.oid, oid);
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
        this.getOrganizationDetails(oid);
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.first / this.perPage;
        this.getOrganizationList();
    }
    onSort(event): void {
        const order = event.order === 1 ? 'ASC' : 'DESC';
        if (event.field !== this.sort || order !== this.direction) {
            this.loading = true;
            this.sort = event.field;
            this.direction = order;
            this.getOrganizationList();
        }
    }

    newOrganization(): void {
        this.editOrganizationOid = null;
        this.modalTitle = this.translateService.instant(
            'credential-builder.organizations-tab.createOrganization'
        );
        this.openModal = true;
        this.uxService.openModal('organizationModal');
    }

    closeModal(closeInfo: {isEdit: boolean, oid?: string}): void {
        this.uxService.closeModal('organizationModal');
        this.openModal = false;
        // If it's true, move to page 1 and trigger onPageEvent.
        if (closeInfo.isEdit) {
            this.modalEdited = true;
            if (!this.organizations.length) {
                this.getOrganizationList();
            } else {
                this.moveToFirstPage();
            }
        }
    }

    private moveToFirstPage() {
        this.credentialBuilderService.redirectToPage.next(0);
    }

    private setUnitOf(newOid: number, oldOid: number): void {
        this.api
            .getUnitOf(oldOid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(
                (organization: OrganizationSpecView) => {
                    if (organization) {
                        this.api
                            .setUnitOf(
                                newOid,
                                { oid: [organization.oid] },
                                this.translateService.currentLang
                            )
                            .takeUntil(this.destroy$)
                            .subscribe();
                    }
                },
                () => (this.loading = false)
            );
    }

    private getOrganizationList(itemsLeft?: number): void {
        // Prevents asking for the items of an empty page
        if (
            !!itemsLeft &&
            itemsLeft / this.perPage <= this.page &&
            this.page !== 0
        ) {
            this.page = this.page - 1;
        }
        this.listOrganizations();
    }

    private listOrganizations(): void {
        this.api
            .listOrganization(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                '',
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (data: PagedResourcesOrganizationSpecLiteView) => {
                    this.organizations = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeOrganization = this.organizations[0];
                    this.loading = false;
                    this.firstLoad = false;
                },
                () => (this.loading = false)
            );
    }

    private getOrganizationDetails(oid: number): void {
        if (
            this.modalEdited ||
            _get(this.organizationDetails, 'oid', null) !== oid
        ) {
            this.resetDetails();
            this.modalEdited = false;
            this.loadingDetails = true;
            this.api
                .getOrganization(oid, this.translateService.currentLang)
                .takeUntil(this.destroy$)
                .subscribe(
                    (organization: OrganizationSpecView) => {
                        this.stopResources$.next(true);
                        this.organizationDetails = organization;
                        this.availableLanguages =
                            organization.additionalInfo.languages;
                        this.setLanguage(this.translateService.currentLang);
                        this.getOrganizationResources(oid);
                    },
                    () => {}
                );
        }
    }

    private getOrganizationResources(oid: number): void {
        forkJoin({
            unitOf: this.getUnitOfActiveOrganization(oid),
        })
            .takeUntil(this.stopResources$)
            .subscribe((resources) => {
                this.parentOrganizationName = resources.unitOf
                    ? resources.unitOf.defaultTitle
                    : null;
                this.loadingDetails = false;
            });
    }

    private getUnitOfActiveOrganization(
        oid
    ): Observable<ResourceOrganizationSpecView> {
        return this.api.getUnitOf(oid, this.translateService.currentLang);
    }

    private setLanguage(lang: string): void {
        this.selectedLanguage = this.activeOrganization.defaultLanguage;
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
        this.organizationDetails = null;
        this.parentOrganizationName = null;
    }
}
