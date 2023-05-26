import {
    Component,
    OnInit,
    OnDestroy,
    Input,
    OnChanges,
    SimpleChanges,
} from '@angular/core';
import {
    OrganizationSpecView,
    V1Service,
    OrganizationSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ResourceOrganizationSpecView,
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { takeUntil, take } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
    selector: 'edci-organization',
    templateUrl: './organization.component.html',
    styleUrls: ['./organization.component.scss'],
})
export class OrganizationComponent implements OnInit, OnDestroy, OnChanges {
    organizations: Array < OrganizationSpecLiteView> = [];
    activeOrganization: OrganizationSpecLiteView;
    editOrganizationOid: number;
    modalTitle: string;
@Input() openModal = false;
modalEdited = false;
loading = true;
totalItems = 0;
perPage = 7;
page = 0;
sort = 'auditDAO.updateDate';
inputSearchText = '';
direction = 'DESC';
selectedRowIndex = 0;
selectedLanguage: string = this.translateService.currentLang;
language: string[];
destroy$: Subject < boolean> = new Subject<boolean>();
noOrgsAddedYet = false;

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
    this.getOrganizationList();
}

ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
}

onDelete(oid: number): void {
    this.api
        .deleteOrganization(oid, this.translateService.currentLang)
        .pipe(take(1))
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
    this.router.navigateByUrl('credential-builder/organizations/' + oid);
}

onDuplicate(oid: number): void {
    this.loading = true;
    this.api
        .duplicateOrganization(oid, this.translateService.currentLang)
        .pipe(take(1))
        .subscribe(
            (newOrganization: ResourceOrganizationSpecView) => {
                this.moveToFirstPage();
                this.setUnitOf(newOrganization.oid, oid);
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
    this.getOrganizationList();
}

onSort(event): void {
    if (event.sort !== null && event.order !== null) {
        if (event.sort !== this.sort || event.order.toUpperCase() !== this.direction) {
            this.loading = true;
            this.sort = event.sort;
            this.direction = event.order.toUpperCase();
            this.getOrganizationList();
        }
    }
}

onEmittedOCBQueryChange(value: string): void {
    this.inputSearchText = value;
    this.getOrganizationList();
}

newOrganization(): void {
    this.router.navigateByUrl('credential-builder/organizations');
}

private moveToFirstPage() {
    this.credentialBuilderService.redirectToPage.next(0);
}

private setUnitOf(newOid: number, oldOid: number): void {
    this.api
        .getUnitOf(oldOid, this.translateService.currentLang)
        .pipe(take(1))
        .subscribe(
            (organization: OrganizationSpecView) => {
                if (organization) {
                    this.api
                        .setUnitOf(
                            newOid,
                            { oid: [organization.oid] },
                            this.translateService.currentLang
                        )
                        .pipe(take(1))
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

    if (!this.noOrgsAddedYet) {
        this.listOrganizations();
    }
}

private listOrganizations(): void {
    this.loading = true;
    this.api
        .listOrganization(
            this.sort,
            this.direction,
            this.page,
            this.perPage,
            this.inputSearchText,
            this.translateService.currentLang
        )
        .pipe(take(1))
        .subscribe({
            next :  (data: PagedResourcesOrganizationSpecLiteView) => {
                this.organizations = data.content;
                this.totalItems = data.page.totalElements;
                this.activeOrganization = this.organizations[0];
                if (this.inputSearchText === '' && data.content?.length === 0) {
                    this.noOrgsAddedYet = true;
                }
            }
        }).add(() => this.loading = false);
}

private setLanguage(lang: string): void {
    this.selectedLanguage = this.activeOrganization.defaultLanguage;
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
