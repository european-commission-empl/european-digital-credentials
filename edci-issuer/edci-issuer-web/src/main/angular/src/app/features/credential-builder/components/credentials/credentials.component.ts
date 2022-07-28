import {
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '@environments/environment';
import { EuiDialogComponent } from '@eui/components/eui-dialog';
import { UxAppShellService } from '@eui/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService, OKNotification } from '@services/error.service';
import { IssuerService } from '@services/issuer.service';
import {
    AssessmentSpecLiteView,
    CredentialFileUploadResponseView,
    CredentialView,
    EntitlementSpecLiteView,
    EuropassCredentialSpecLiteView,
    EuropassCredentialSpecView,
    LearningAchievementSpecLiteView,
    OrganizationSpecView,
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesEuropassCredentialSpecLiteView,
    PagedResourcesLearningAchievementSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    ResourceOrganizationSpecView,
    V1Service,
} from '@shared/swagger';
import { get as _get } from 'lodash';
import { forkJoin, Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-credentials',
    templateUrl: './credentials.component.html',
    styleUrls: ['./credentials.component.scss'],
})
export class CredentialsComponent implements OnInit, OnDestroy {
    @ViewChild('inputFile') inputFile: ElementRef;
    @ViewChild('uploadDialog') dialog: EuiDialogComponent;
    @ViewChild('issuingDialogModal') issuingDialogModal: EuiDialogComponent;

    @Input() openModal: boolean = false;
    onCredentialChange = new EventEmitter<string>();
    credentials: Array<EuropassCredentialSpecLiteView> = [];
    activeCredential: EuropassCredentialSpecLiteView;
    editCredentialOid: number;
    issueCredentialOid: number;
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
        private uxService: UxAppShellService,
        private api: V1Service,
        private notificationService: NotificationService,
        private credentialBuilderService: CredentialBuilderService,
        private issuerService: IssuerService,
        private router: Router,
        private notifService: NotificationService,
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
        this.credentialBuilderService.issuingModalHeaderTitleObservable.subscribe((res) => {
            this.issuingDialogHeaderTitle = res;
        });
        this.credentialBuilderService.issuingModalStateObservable.subscribe((res) => {
            this.issuingDialogModalState = res;
        });
        this.credentialBuilderService.closeIssuingModalObservable.subscribe((res) => {
            if (res === 'close') {
                /*if(this.issuingDialogModal){
                    this.issuingDialogModal.closeDialog();
                }*/
            }
        });
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
            .pipe(takeUntil(this.destroy$))
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
        this.issuingModeModal = true;
        this.cleanUploadErrors();
        this.credentialBuilderService.setOidSelected(oid);
        this.credentialBuilderService.setIssuingModalHeaderTitle(this.translateService.instant('credential-builder.customise-data.title'));
        this.credentialBuilderService.setIssuingModalState('showCustomizableData');
        this.uxService.openModal('issuingModal');
        // this.issuingDialogModal.openDialog();
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.page;
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
        // this.uxService.openModal('issuingModeModal');
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

    public comodin(metodo?: string) {
    }

    public backIssuingModal() {
        this.credentialBuilderService.setCloseIssuingModal('back');
    }

    public closeIssuingModal() {
        this.uxService.closeModal('issuingModal');
        // this.issuingDialogModal.closeDialog();
        this.credentialBuilderService.setCloseIssuingModal('close');
    }

    public sendForm() {
        this.credentialBuilderService.setCloseIssuingModal('sendForm');
    }

    public readFile(event: FileList) {
        this.inputFileIsEnabled = false;
        if (event.length === 0) {
            return false;
        }

        this.cleanUploadErrors();
        const file = event[0];
        if (this.validateFile(file)) {
            this.credentialBuilderService.setIssuingModalState('loading');
            this.pushFile(file);
            this.inputFile.nativeElement.value = '';
        }
    }

    public openDynamicModal(): void {
        this.closeIssuingModeModal();
        this.inputFileIsEnabled = true;
        this.dialog.dismissLabel =
            this.translateService.instant('common.cancel');
        this.dialog.acceptLabel =
            this.translateService.instant('common.upload');
        this.dialog.openDialog();
    }

    public onAccept(): void {
        this.dialog.closeDialog();
        this.openDialog();
    }

    public onDismiss(event: any): void {
        this.issuingModeModal = event;
        // this.credentialBuilderService.setCloseIssuingModal(!event);
    }

    public issueCredential() {
        const issueCredential: EuropassCredentialSpecLiteView =
            this.credentials.find(
                (credential: EuropassCredentialSpecLiteView) => {
                    return credential.oid === this.issueCredentialOid;
                }
            );
        this.defaultLanguageToIssue = issueCredential.defaultLanguage;
        this.languagesToIssue = this.toUXLanguageObject(
            issueCredential.additionalInfo.languages
        );
        this.closeIssuingModeModal();
        this.issueModal = true;
        this.uxService.openModal('issueModal');
    }

    private pushFile(file: File) {
        this.isLoadingRecipientTemplate = true;
        this.uploads = [];
        this.uploads.push(file);
        this.api.issueCredentialsFromRecipientsXLS(file, this.selectedLanguage).subscribe((res: any) => {
            if (res.valid) {
                this.issuerService.setCredentials(
                    <CredentialView[]>res.credentials
                );
                this.credentialBuilderService.setCloseIssuingModal('close');
                this.router.navigate(['/create/overview']);
            }
        },
        (err) => {
            if (err.error && err.error.message) {
                this.credentialBuilderService.setCloseIssuingModal('close');
            }
        });
    }

    private cleanUploadErrors() {
        this.error = false;
        this.errorOfValidation = false;
        this.errorMessage = '';
        this.errorOfValidationMessage = '';
    }

    private validateFile(file: File): boolean {
        if (!this.validateSize(file)) {
            this.error = true;
            this.errorMessage = `${this.translateService.instant(
                'file-upload.no-more-than'
            )} ${environment.maxUploadSizeMB} MB`;
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

    private validateSize(file): boolean {
        return file.size < 5242880;
    }

    private validateFileExtension(file): boolean {
        const extension = file.name.split('.').pop();
        const validExtensions = ['xls', 'xlsx', 'xlsm'];
        return validExtensions.some((item: string) => item === extension);
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
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (data: PagedResourcesEuropassCredentialSpecLiteView) => {
                    this.credentials = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeCredential = this.credentials[0];
                    this.firstLoad = false;
                    this.loading = false;
                },
                error: () => {
                    this.firstLoad = false;
                    this.loading = false;
                },
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
