import {
    Component, EventEmitter,
    Input,
    OnDestroy,
    OnInit
} from '@angular/core';
import { Router } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import { IssuerService } from '@services/issuer.service';
import {
    EuropassCredentialSpecLiteView, PagedResourcesEuropassCredentialSpecLiteView, V1Service
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-credentials',
    templateUrl: './credentials.component.html',
    styleUrls: ['./credentials.component.scss'],
})
export class CredentialsComponent implements OnInit, OnDestroy {
    @Input() openModal = false;
    onCredentialChange = new EventEmitter<string>();
    credentials: Array<EuropassCredentialSpecLiteView> = [];
    activeCredential: EuropassCredentialSpecLiteView;
    editCredentialOid: number;
    issueCredentialOid: number;
    selectedLanguage: string = this.translateService.currentLang;
    availableLanguages: string[];
    modalTitle: string;
    modalEdited = false;
    issueModal = false;
    loading = true;
    totalItems = 0;
    perPage = 7;
    page = 0;
    sort = 'auditDAO.updateDate';
    noCredsAddedYet = false;
    direction = 'DESC';
    inputSearchText = '';
    destroy$: Subject<boolean> = new Subject<boolean>();
    defaultLanguageToIssue: string;
    languagesToIssue: { code: string; label: string }[];
    issuingModeModal: boolean;
    error: boolean;
    errorMessage: string;
    errorOfValidation: boolean;
    errorOfValidationMessage: string;
    inputFileIsEnabled: boolean;
    isLoadingRecipientTemplate: boolean;
    uploads: Array<File> = [];
    deletedList: Array<File> = [];

    issuingDialogHeaderTitle = '';
    issuingDialogModalState = '';

    constructor(
        private translateService: TranslateService,
        private api: V1Service,
        private notificationService: NotificationService,
        private credentialBuilderService: CredentialBuilderService,
        private issuerService: IssuerService,
        private router: Router,
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
        this.getCredentialsList();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onDelete(oid: number): void {
        this.loading = true;
        this.api
            .deleteCredential(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.getCredentialsList(this.totalItems - 1);
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
        this.router.navigateByUrl('credential-builder/credentials/' + oid);
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateCredential(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: () => {
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
                error: () => (this.loading = false),
            });
    }

    onIssue(oid: number): void {
        this.issueCredentialOid = oid;
        this.router.navigateByUrl('credential-builder/issue/fields/' + oid);
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.page;
        this.perPage = event.pageSize;
        this.getCredentialsList();
    }

    onSort(event): void {
        if (event.sort !== null && event.order !== null) {
            if (event.sort !== this.sort || event.order.toUpperCase() !== this.direction) {
                this.loading = true;
                this.sort = event.sort;
                this.direction = event.order.toUpperCase();
                this.getCredentialsList();
            }
        }
    }

    newCredential(): void {
        this.router.navigateByUrl('credential-builder/credentials');
    }

    onEmittedOCBQueryChange(query: string): void {
        this.inputSearchText = query;
        this.getCredentialsList();
    }

    private moveToFirstPage() {
        this.credentialBuilderService.redirectToPage.next(0);
    }

    private toUXLanguageObject(
        languages: string[]
    ): { code: string; label: string }[] {
        return languages.map((language: string) => {
            return { code: language, label: language };
        });
    }

    private getCredentialsList(itemsLeft?: number): void {
        // Prevents asking for the items of an empty page
        if (
            !!itemsLeft &&
            itemsLeft / this.perPage <= this.page &&
            this.page !== 0
        ) {
            this.page = this.page - 1;
        }
        if (!this.noCredsAddedYet) { this.listCredentials(); }
    }

    private listCredentials(): void {
        this.loading = true;
        this.api
            .listCredential(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                this.inputSearchText,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (data: PagedResourcesEuropassCredentialSpecLiteView) => {
                    this.credentials = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeCredential = this.credentials[0];
                    if (this.inputSearchText === '' && data.content?.length === 0) {
                        this.noCredsAddedYet = true;
                    }
                }
            }).add(() => {
                this.loading = false;
            });
    }

    private setLanguage(lang: string): void {
        this.selectedLanguage = this.activeCredential.defaultLanguage;
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
