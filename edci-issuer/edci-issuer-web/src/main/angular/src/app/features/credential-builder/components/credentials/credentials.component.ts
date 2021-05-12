import { Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UxButtonConfig, UxDynamicModalConfig, UxDynamicModalService, UxService } from '@eui/core';
import { Router } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { NotificationService } from '@services/error.service';
import {
    AssessmentSpecLiteView, CredentialFileUploadResponseView, CredentialView,
    EntitlementSpecLiteView,
    EuropassCredentialSpecLiteView,
    EuropassCredentialSpecView, IssueBuildCredentialView,
    LearningAchievementSpecLiteView,
    OrganizationSpecView,
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesEuropassCredentialSpecLiteView,
    PagedResourcesLearningAchievementSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView, RecipientDataView,
    ResourceOrganizationSpecView,
    V1Service,
} from '@shared/swagger';
import { get as _get } from 'lodash';
import { forkJoin, Observable, Subject } from 'rxjs';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { IssuerService } from '@services/issuer.service';
import { DocumentUpload } from '@shared/models/upload.model';

@Component({
    selector: 'edci-credentials',
    templateUrl: './credentials.component.html',
    styleUrls: ['./credentials.component.scss'],
})
export class CredentialsComponent implements OnInit, OnDestroy {

    @ViewChild('inputFile') inputFile: ElementRef;
    @Input() openModal: boolean = false;
    onCredentialChange = new EventEmitter<string>();
    credentials: Array<EuropassCredentialSpecLiteView> = [];
    activeCredential: EuropassCredentialSpecLiteView;
    credentialDetails: EuropassCredentialSpecView;
    editCredentialOid: number;
    issueCredentialOid: number;
    achieved: LearningAchievementSpecLiteView[];
    entitledTo: EntitlementSpecLiteView[];
    performed: AssessmentSpecLiteView[];
    issuer: OrganizationSpecView;
    selectedLanguage: string = this.translateService.currentLang;
    availableLanguages: string[];
    modalTitle: string;
    modalEdited: boolean = false;
    issueModal: boolean = false;
    loading: boolean = true;
    firstLoad: boolean = true;
    totalItems: number = 0;
    perPage: number = 7;
    page: number = 0;
    sort: string = 'auditDAO.updateDate';
    direction: string = 'DESC';
    loadingDetails: boolean = false;
    destroy$: Subject<boolean> = new Subject<boolean>();
    stopResources$: Subject<boolean> = new Subject<boolean>();
    defaultLanguageToIssue: string;
    languagesToIssue: {code: string}[];
    issuingModeModal: boolean;
    error: boolean;
    errorMessage: string;
    errorOfValidation: boolean;
    errorOfValidationMessage: string;
    inputFileIsEnabled: boolean;
    isLoadingRecipientTemplate: boolean;
    uploads: Array<DocumentUpload> = [];
    deletedList: Array<DocumentUpload> = [];

    constructor(
        private translateService: TranslateService,
        private uxService: UxService,
        private api: V1Service,
        private notificationService: NotificationService,
        private credentialBuilderService: CredentialBuilderService,
        private issuerService: IssuerService,
        private uxDynamicModalService: UxDynamicModalService,
        private router: Router,
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
            this.newCredential();
        }
        this.getCredentialsList();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onDelete(oid: number): void {
        this.api
            .deleteCredential(oid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(() => {
                this.loading = true;
                this.getCredentialsList(this.totalItems - 1);
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
        this.editCredentialOid = oid;
        this.openModal = true;
        this.modalTitle = this.translateService.instant(
            'credential-builder.credentials-tab.editCredential'
        );
        this.uxService.openModal('credentialModal');
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateCredential(oid, this.translateService.currentLang)
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
        this.getCredentialDetails(oid);
    }

    onIssue(oid: number): void {
        this.issueCredentialOid = oid;
        this.issuingModeModal = true;
        this.cleanUploadErrors();
        this.openIssuingModeModal();
    }

    downloadTemplateExcel(): void {
        const issueCredential: EuropassCredentialSpecLiteView = this.credentials.find(
            (credential: EuropassCredentialSpecLiteView) => {
                return credential.oid === this.issueCredentialOid;
            }
        );
        this.isLoadingRecipientTemplate = true;
        this.issuerService.downloadRecipientTemplate(this.issueCredentialOid, issueCredential.defaultLanguage);
        this.issuerService.isExcelDownloadedSubject.subscribe(() => {
            this.isLoadingRecipientTemplate = false;
        });
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.first / event.rows;
        this.getCredentialsList();
    }

    onSort(event): void {
        const order = event.order === 1 ? 'ASC' : 'DESC';
        if (event.field !== this.sort || order !== this.direction) {
            this.loading = true;
            this.sort = event.field;
            this.direction = order;
            this.getCredentialsList();
        }
    }

    newCredential(): void {
        this.editCredentialOid = null;
        this.modalTitle = this.translateService.instant(
            'credential-builder.credentials-tab.createCredential'
        );
        this.openModal = true;
        this.uxService.openModal('credentialModal');
    }

    closeModal(isEdit: boolean): void {
        this.uxService.closeModal('credentialModal');
        this.openModal = false;
        // If it's true, move to page 1 and trigger onPageEvent.
        if (isEdit) {
            this.modalEdited = true;
            if (!this.credentials.length) {
                this.getCredentialsList();
            } else {
                this.moveToFirstPage();
            }
        }
    }

    closeIssuingModeModal(byError: boolean = false): void {
        if (!this.isLoadingRecipientTemplate || byError) {
            this.isLoadingRecipientTemplate = false;
            this.issuingModeModal = false;
            this.uxService.closeModal('issuingModeModal');
        }
    }

    openIssuingModeModal(): void {
        this.isLoadingRecipientTemplate = false;
        this.issuingModeModal = true;
        this.uxService.openModal('issuingModeModal');
    }

    closeIssueModal(): void {
        this.issueModal = false;
        this.uxService.closeModal('issueModal');
        this.issueCredentialOid = null;
    }

    public openDialog() {
        this.openIssuingModeModal();
        return this.inputFile.nativeElement.click();
    }

    readFile(event: FileList) {
        this.inputFileIsEnabled = false;
        if (event.length === 0) {
            return false;
        }

        this.cleanUploadErrors();
        const file = event[0];
        if (this.validateFile(file)) {
            this.pushFile(file);
            this.inputFile.nativeElement.value = '';
        }
    }

    pushFile(file: File) {
        this.isLoadingRecipientTemplate = true;
        if (this.uploads.length > 0) {
            this.removeFile(0);
        }
        this.uploads.push(this.createDocumentUpload(file));
        this.api.uploadRecipients(file, 'en').subscribe((res) => {
            this.issuedCredential(res.recipientDataViews);
        }, (err) => {
            if (err.error && err.error.message) {
                this.errorOfValidationMessage = err.error.message;
                this.errorOfValidation = true;
                this.isLoadingRecipientTemplate = false;
            }
        });
    }

    issuedCredential(recipients: RecipientDataView[]): void {
        const body: IssueBuildCredentialView = {
            credential: this.issueCredentialOid,
            recipients
        };
        this.api
            .issueCredential(body, 'en')
            .takeUntil(this.destroy$)
            .subscribe(
                (issuedCredential: CredentialFileUploadResponseView) => {
                    if (issuedCredential.valid) {
                        this.issuerService.setCredentials(
                            <CredentialView[]>issuedCredential.credentials
                        );
                        this.router.navigate(['/create/overview']);
                    }
                },
                () => {
                    this.closeIssuingModeModal(true);
                });
    }

    createDocumentUpload(file: File): DocumentUpload {
        return {
            id: null,
            fileName: file.name,
            date: this.formatDate(new Date()),
            status: 'Pending approval',
            visible: true,
            action: 1,
            file: file,
        };
    }

    removeFile(index: number) {
        const items = this.uploads.splice(index, 1);
        const item = items[0];

        if (item.id) {
            item.action = 2;
            this.deletedList.push(item);
        }
    }

    cleanUploadErrors() {
        this.error = false;
        this.errorOfValidation = false;
        this.errorMessage = '';
        this.errorOfValidationMessage = '';
    }

    validateFile(file: File): boolean {
        if (!this.validateSize(file)) {
            this.error = true;
            this.errorMessage =
                this.translateService.instant('file-upload.no-more-than') +
                ' ' +
                '5MB';
            return false;
        }

        if (!this.validateFileExtension(file)) {
            this.error = true;
            this.errorMessage = this.translateService.instant(
                'file-upload.wrongExtension'
            );
            return false;
        }
        return true;
    }

    validateSize(file): boolean {
        return file.size < 5242880;
    }

    validateFileExtension(file): boolean {
        const extension = file.name.split('.').pop();
        const validExtensions = ['xls', 'xlsx', 'xlsm'];
        return validExtensions.some((item: string) => item === extension);
    }

    openDynamicModal(): void {
        this.closeIssuingModeModal();
        this.inputFileIsEnabled = true;
        const config = new UxDynamicModalConfig({
            id: 'concentModal',
            content: `<p>${this.translateService.instant(
                'common.upload-concent-message'
            )}<p>`,
            titleLabel: this.translateService.instant('common.upload-xls'),
            isSizeSmall: true,
            customFooterContent: {
                right: {
                    buttons: [
                        new UxButtonConfig({
                            label: this.translateService.instant(
                                'common.cancel'
                            ),
                            typeClass: 'secondary',
                            styleClass: 'mr-3',
                            onClick: (portalHostRef, portalRef) => {
                                this.uxDynamicModalService.closeModal(
                                    portalHostRef,
                                    portalRef
                                );
                            },
                        }),
                        new UxButtonConfig({
                            label: this.translateService.instant(
                                'common.upload'
                            ),
                            typeClass: 'primary',
                            onClick: (portalHostRef, portalRef) => {
                                this.openDialog();
                                this.uxDynamicModalService.closeModal(
                                    portalHostRef,
                                    portalRef
                                );
                            },
                        }),
                    ],
                },
            },
        });
        this.uxDynamicModalService.openModal(config);
    }

    issueCredential() {
        const issueCredential: EuropassCredentialSpecLiteView = this.credentials.find(
            (credential: EuropassCredentialSpecLiteView) => {
                return credential.oid === this.issueCredentialOid;
            }
        );
        this.defaultLanguageToIssue = issueCredential.defaultLanguage;
        this.languagesToIssue = this.toUXLanguageObject(issueCredential.additionalInfo.languages);
        this.closeIssuingModeModal();
        this.issueModal = true;
        this.uxService.openModal('issueModal');
    }

    private formatDate(date: Date): string {
        let day = date.getDate().toString();
        if (day.length === 1) {
            day = '0' + day;
        }
        let month = (date.getMonth() + 1).toString();
        if (month.length === 1) {
            month = '0' + month;
        }
        const year = date.getFullYear();

        return day + '/' + month + '/' + year;
    }

    private moveToFirstPage() {
        this.credentialBuilderService.redirectToPage.next(0);
    }

    private toUXLanguageObject(languages: string[]): {code: string}[] {
        return languages.map((language: string) => {
            return { code: language };
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
        this.listCredentials();
    }

    private listCredentials(): void {
        this.api
            .listCredential(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                '',
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (data: PagedResourcesEuropassCredentialSpecLiteView) => {
                    this.credentials = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeCredential = this.credentials[0];
                    this.loading = false;
                    this.firstLoad = false;
                },
                () => {
                    this.loading = false;
                    this.firstLoad = false;
                }
            );
    }

    private getCredentialDetails(oid: number): void {
        if (
            this.modalEdited ||
            _get(this.credentialDetails, 'oid', null) !== oid
        ) {
            this.resetDetails();
            this.modalEdited = false;
            this.loadingDetails = true;
            this.api
                .getCredential(oid, this.translateService.currentLang)
                .takeUntil(this.destroy$)
                .subscribe(
                    (credential: EuropassCredentialSpecView) => {
                        this.stopResources$.next(true);
                        this.credentialDetails = credential;
                        this.availableLanguages =
                            credential.additionalInfo.languages;
                        this.setLanguage(this.translateService.currentLang);
                        this.getCredentialResources(oid);
                    },
                    () => {}
                );
        }
    }

    private getCredentialResources(oid: number): void {
        forkJoin({
            achieved: this.getAchieved(oid),
            performed: this.getPerformed(oid),
            entitledTo: this.getEntitledTo(oid),
            issuer: this.getIssuer(oid),
        })
            .takeUntil(this.stopResources$)
            .subscribe((resources) => {
                this.issuer = resources.issuer;
                this.achieved = resources.achieved.content;
                this.performed = resources.performed.content;
                this.entitledTo = resources.entitledTo.content;
                this.loadingDetails = false;
            });
    }

    private getIssuer(oid: number): Observable<ResourceOrganizationSpecView> {
        return this.api.getIssuer(oid, this.translateService.currentLang);
    }

    private getAchieved(
        oid: number
    ): Observable<PagedResourcesLearningAchievementSpecLiteView> {
        return this.api.listAchieved(oid, this.translateService.currentLang);
    }
    private getPerformed(
        oid: number
    ): Observable<PagedResourcesLearningActivitySpecLiteView> {
        return this.api.listPerformed(oid, this.translateService.currentLang);
    }

    private getEntitledTo(
        oid: number
    ): Observable<PagedResourcesEntitlementSpecLiteView> {
        return this.api.listEntitledTo(oid, this.translateService.currentLang);
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

    private resetDetails(): void {
        this.credentialDetails = null;
        this.entitledTo = null;
        this.issuer = null;
        this.achieved = null;
        this.performed = null;
    }
}
