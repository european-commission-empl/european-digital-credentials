import {
    Component,
OnInit,
OnDestroy,
Input,
OnChanges,
SimpleChanges,
} from '@angular/core';
import {
V1Service,
EntitlementSpecLiteView,
PagedResourcesEntitlementSpecLiteView,
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { takeUntil } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
selector: 'edci-entitlements',
templateUrl: './entitlements.component.html',
styleUrls: ['./entitlements.component.scss'],
})
export class EntitlementsComponent implements OnInit, OnDestroy, OnChanges {
entitlements: Array < EntitlementSpecLiteView> = [];
activeEntitlement: EntitlementSpecLiteView;
entitlementEditOid: number;
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
noEntitlementsAddedYet = false;
selectedRowIndex = 0;
selectedLanguage: string = this.translateService.currentLang;
language: string[];
firstLoad = true;
destroy$: Subject < boolean> = new Subject<boolean>();

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
                if (this.language) {
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
        this.loading = true;
        this.api
            .deleteEntitlement(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
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
            }).add(() => this.loading = false);
    }

    onEdit(oid: number): void {
        this.router.navigateByUrl('credential-builder/entitlements/' + oid);
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateEntitlement(oid, this.translateService.currentLang)
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
        this.getEntitlementList();
    }

    onSort(event): void {
        if (event.sort !== null && event.order !== null) {
            if (event.sort !== this.sort || event.order.toUpperCase() !== this.direction) {
                this.loading = true;
                this.sort = event.sort;
                this.direction = event.order.toUpperCase();
                this.getEntitlementList();
            }
        }
    }

    newEntitlement(): void {
        this.router.navigateByUrl('credential-builder/entitlements');
    }

    onEmittedOCBQueryChange(value: string): void {
        this.inputSearchText = value;
        this.getEntitlementList();
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
        if (!this.noEntitlementsAddedYet) { this.listEntitlements(); }
    }

    private listEntitlements(): void {
        this.loading = true;
        this.api
            .listEntitlement(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                this.inputSearchText,
                this.translateService.currentLang
)
.pipe(takeUntil(this.destroy$))
            .subscribe({next :
                (data: PagedResourcesEntitlementSpecLiteView) => {
                    this.entitlements = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeEntitlement = this.entitlements[0];
                    if (this.inputSearchText === '' && data.content?.length === 0) {
                        this.noEntitlementsAddedYet = true;
                    }
                }}).add(() => this.loading = false);
    }

    private setLanguage(lang: string): void {
        this.selectedLanguage = this.activeEntitlement.defaultLanguage;
        /* let isLanguageAvailable: boolean = false;
        this.language.forEach((language: string) => {
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
