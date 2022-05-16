import { Component, OnInit, OnDestroy, Input, OnChanges, SimpleChanges } from '@angular/core';
import {
    EntitlementSpecView,
    V1Service,
    EntitlementSpecLiteView,
    PagedResourcesEntitlementSpecLiteView,
    ResourceOrganizationSpecView,
    OrganizationSpecView,
    PagedResourcesOrganizationSpecLiteView,
    OrganizationSpecLiteView,
} from '@shared/swagger';
import { Subject, forkJoin, Observable } from 'rxjs';
import { UxService } from '@eui/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';

@Component({
    selector: 'edci-entitlements',
    templateUrl: './entitlements.component.html',
    styleUrls: ['./entitlements.component.scss'],
})
export class EntitlementsComponent implements OnInit, OnDestroy, OnChanges {
    entitlements: Array<EntitlementSpecLiteView> = [];
    activeEntitlement: EntitlementSpecLiteView;
    entitlementDetails: EntitlementSpecView;
    subEntitlements: EntitlementSpecLiteView[] = [];
    validWith: OrganizationSpecLiteView[] = [];
    entitlementEditOid: number;
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
        this.getEntitlementList();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.openModal && changes.openModal.currentValue) {
            this.newEntitlement();
        }
    }

    onDelete(oid: number): void {
        this.api
            .deleteEntitlement(oid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(() => {
                this.loading = true;
                this.getEntitlementList(this.totalItems - 1);
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
        this.entitlementEditOid = oid;
        this.openModal = true;
        this.modalTitle = this.translateService.instant(
            'credential-builder.entitlements-tab.editEntitlement'
        );
        this.uxService.openModal('entitlementModal');
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateEntitlement(oid, this.translateService.currentLang)
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
        this.getEntitlementDetails(oid);
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.first / this.perPage;
        this.getEntitlementList();
    }
    onSort(event): void {
        const order = event.order === 1 ? 'ASC' : 'DESC';
        if (event.field !== this.sort || order !== this.direction) {
            this.loading = true;
            this.sort = event.field;
            this.direction = order;
            this.getEntitlementList();
        }
    }

    newEntitlement(): void {
        this.entitlementEditOid = null;
        this.modalTitle = this.translateService.instant(
            'credential-builder.entitlements-tab.createEntitlement'
        );
        this.openModal = true;
        this.uxService.openModal('entitlementModal');
    }

    closeModal(closeInfo: {isEdit: boolean, oid?: string}): void {
        this.uxService.closeModal('entitlementModal');
        this.openModal = false;
        // If it's true, move to page 1 and trigger onPageEvent.
        if (closeInfo.isEdit) {
            this.modalEdited = true;
            if (!this.entitlements.length) {
                this.getEntitlementList();
            } else {
                this.moveToFirstPage();
            }
        }
    }

    private moveToFirstPage() {
        this.credentialBuilderService.redirectToPage.next(0);
    }

    private getEntitlementList(itemsLeft?: number): void {
        // Prevents asking for the items of an empty page
        if (
            !!itemsLeft &&
            itemsLeft / this.perPage <= this.page &&
            this.page !== 0
        ) {
            this.page = this.page - 1;
        }
        this.listEntitlements();
    }

    private listEntitlements(): void {
        this.api
            .listEntitlement(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                '',
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (data: PagedResourcesEntitlementSpecLiteView) => {
                    this.entitlements = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeEntitlement = this.entitlements[0];
                    this.loading = false;
                    this.firstLoad = false;
                },
                () => {
                    this.loading = false;
                    this.firstLoad = false;
                }
            );
    }

    private getEntitlementDetails(oid: number): void {
        if (
            this.modalEdited ||
            _get(this.entitlementDetails, 'oid', null) !== oid
        ) {
            this.resetDetails();
            this.modalEdited = false;
            this.loadingDetails = true;
            this.api
                .getEntitlement(oid, this.translateService.currentLang)
                .takeUntil(this.destroy$)
                .subscribe(
                    (entitlement: EntitlementSpecView) => {
                        this.stopResources$.next(true);
                        this.entitlementDetails = entitlement;
                        this.availableLanguages =
                            entitlement.additionalInfo.languages;
                        this.setLanguage(this.translateService.currentLang);
                        this.getEntitlementResources(oid);
                    },
                    () => {}
                );
        }
    }

    private getEntitlementResources(oid: number): void {
        forkJoin({
            subEntitlements: this.getSubEntitlements(oid),
            validWith: this.getValidWith(oid),
        })
            .takeUntil(this.stopResources$)
            .subscribe((resources) => {
                this.validWith = resources.validWith.content;
                this.subEntitlements = resources.subEntitlements.content;
                this.loadingDetails = false;
            });
    }

    private getValidWith(
        oid: number
    ): Observable<PagedResourcesOrganizationSpecLiteView> {
        return this.api.getValidWith(oid, this.translateService.currentLang);
    }

    private getSubEntitlements(
        oid: number
    ): Observable<PagedResourcesEntitlementSpecLiteView> {
        return this.api.listHasEntPart(oid, this.translateService.currentLang);
    }

    private setLanguage(lang: string): void {
        this.selectedLanguage = this.activeEntitlement.defaultLanguage;
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
        this.entitlementDetails = null;
        this.validWith = null;
        this.subEntitlements = null;
    }
}
