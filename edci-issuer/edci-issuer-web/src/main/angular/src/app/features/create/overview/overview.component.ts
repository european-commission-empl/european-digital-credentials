import { SelectionModel } from '@angular/cdk/collections';
import { Location } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import {
    Component,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
    ViewEncapsulation,
    EventEmitter
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { FileUploadResponseView } from '@core/models/DTO/fileUploadResponseView.model';
import { Progress } from '@core/models/DTO/progress.model';
import { environment } from '@environments/environment';
import { UxAppShellService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { ApiService } from '@services/api.service';
import { NotificationService, OKNotification } from '@services/error.service';
import { IssuerService } from '@services/issuer.service';
import { NexUService } from '@services/nexU.service';
import { Constants } from '@shared/constants';
import {
    AssessmentSpecView,
    CredentialView,
    EntitlementSpecView,
    EuropassCredentialSpecView,
    LearningAchievementSpecView,
    LearningActivitySpecView,
    LearningOutcomeSpecView,
    LocalSignatureRequestView,
    OrganizationSpecView,
    StatusView,
    V1Service,
} from '@shared/swagger';
import { CredentialDownloadView } from '@shared/swagger/model/credentialDownloadView';
import { interval, Subject, Subscription } from 'rxjs';
import { takeUntil, switchMap } from 'rxjs/operators';

@Component({
    selector: 'edci-overview',
    templateUrl: './overview.component.html',
    styleUrls: ['./overview.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class OverviewComponent implements OnInit, OnDestroy {
    first: number = null;
    selectedRowIndex: number = 0;
    @Output() onPage: EventEmitter<any> = new EventEmitter();
    @Output() onSort: EventEmitter<any> = new EventEmitter();
    @Output() onSelectItem: EventEmitter<number> = new EventEmitter();

    dataCredentialsTable: CredentialView[] = [];

    /* Mat table */
    displayedColumns: string[] = [
        'select',
        'studentName',
        'course',
        'valid',
        'sealed',
        'sent',
        'received',
        'actions',
    ];
    datesOfCredentialSealed: { uuid: string; date: Date } = {} as {
        uuid: string;
        date: Date;
    };
    dataSource: MatTableDataSource<CredentialView>;
    selection = new SelectionModel<CredentialView>(true, []);
    isLoadingResults: boolean = false;
    sealed: boolean = false;
    sent: boolean = false;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSizeOptions: number[] = [10, 25, 50, 100];
    validationErrors: string[];
    signatureByteErrors: boolean[] = [];

    /* Export */
    files: File[] = [];
    progressInfoSubscription: Subscription;
    actualExport = new Progress('');
    showProgressbar = false;
    deliveryOngoing = false;
    percentUploadedDone: number | any;

    /* Renderer */
    downloadCredentialSubscription: Subscription;
    isSingle: boolean;
    uuids: string[];

    formRadio: FormGroup = new FormGroup({
        presentation: new FormControl(false),
    });

    localSealingForm: FormGroup = new FormGroup({
        password: new FormControl(),
    });

    sealFailed: boolean = false;
    downloadCSVEnabled: boolean;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    get localCertPassword() {
        return this.localSealingForm.get('password');
    }

    set localCertPassword(newPass) {
        this.localSealingForm.setValue({
            password: newPass,
        });
    }

    get presentation() {
        return this.formRadio.get('presentation');
    }

    set presentation(status: any) {
        this.formRadio.setValue({
            presentation: status,
        });
    }

    get isSealAvailable() {
        return (
            this.selection.selected.length > 0 &&
            !this.selection.selected.find(
                (item: CredentialView) => !item.valid
            ) &&
            !this.selection.selected.find((item: CredentialView) => item.sealed)
        );
    }

    get isSendAvailable() {
        return (
            this.selection.selected.length > 0 &&
            !this.selection.selected.find(
                (item: CredentialView) => !item.sealed
            ) &&
            !this.selection.selected.find(
                (item: CredentialView) => item.sent !== null
            )
        );
    }

    constructor(
        public uxService: UxAppShellService,
        private api: V1Service,
        private apiService: ApiService,
        private issuerService: IssuerService,
        private nexU: NexUService,
        private notifService: NotificationService,
        private translateService: TranslateService,
        private location: Location,
        private router: Router
    ) {}

    ngOnInit() {
        this.files = this.issuerService.files;
        // Get CredentialListView
        this.loadCredentialsData();
    }

    ngOnDestroy() {
        if (this.downloadCredentialSubscription) {
            this.downloadCredentialSubscription.unsubscribe();
        }
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    presentationSelected() {
        this.uxService.closeModal('selectPresentationTypeModal');
        this.isLoadingResults = true;
        this.issuerService.presentation = this.presentation.value ? 'VP' : 'EC';
        const uuids = this.selection.selected.map(
            (cred: CredentialView) => cred.uuid
        );
        this.uuids = uuids;
        environment.enabledLocalSealing
        ? this.openLocalSealingPassModal()
        : this.toSeal();
    }

    closePresentationModal(): void {
        this.uxService.closeModal('selectPresentationTypeModal');
    }

    closeLocalSealingPassModal(): void {
        this.isLoadingResults = false;
        this.uxService.closeModal('localSealingPassModal');
    }

    openPresentationModal() {
        this.presentation = false;
        this.uxService.openModal('selectPresentationTypeModal');
    }

    sealCredentials() {
        if (this.selection.selected.length >= 0) {

            this.openPresentationModal();

        }
    }

    openLocalSealingPassModal(): void {
        this.uxService.openModal('localSealingPassModal');
    }

    downloadCSV() {
        if (this.downloadCSVEnabled) {
            const JSONtoTransform: any[] = [];
            this.selection.selected.forEach((credential: CredentialView) => {
                if (credential.sent || credential.received) {
                    const date = this.datesOfCredentialSealed[credential.uuid]
                        ? this.datesOfCredentialSealed[credential.uuid].date
                        : '';
                    JSONtoTransform.push({
                        'Time of Issue': date,
                        'UUID of Credential Issued': credential.uuid,
                        'Name of Student it is Issued to':
                            credential.studentName,
                        'Name of Credential': credential.course,
                        'Sent by email': !!credential.sent,
                        'Stored in wallet': !!credential.received,
                    });
                }
            });
            const generatedCSV =
                this.issuerService.generateCSVfromJSON(JSONtoTransform);
            const dateToFileName = this.issuerService.generateUTCDate(
                new Date(),
                true
            );
            const fileName = `report_${dateToFileName}.csv`;
            this.issuerService.downloadCSV({ fileName, text: generatedCSV });
        }
    }

    checkBoxCredentialClicked(row: any): void {
        this.selection.toggle(row);
        this.setDownloadCSVButtonIfShouldBeEnabled();
    }

    toLocalSeal() {
        this.issuerService.openSpinnerDialog();
        this.isLoadingResults = true;
        let password = this.localCertPassword.value;
        let localSignatureRequestView: LocalSignatureRequestView = {};
        localSignatureRequestView.certPassword = password;
        localSignatureRequestView.credentialViews = this.selection.selected;
        localSignatureRequestView.signOnBehalf = this.issuerService.presentation;
        this.api
            .sealCredentialsLocalCertificate(
                localSignatureRequestView,
                this.translateService.currentLang
            )
            .subscribe(
                (credentials: CredentialView[]) => {
                    this.issuerService.setCredentials(credentials);
                    this.sealed = true;
                    this.loadCredentialsData();
                    this.isLoadingResults = false;
                    this.issuerService.closeSpinnerDialog();
                    this.doManageSealingErrors(credentials);
                    credentials.forEach((sealedCredential: CredentialView) => {
                        this.datesOfCredentialSealed[sealedCredential.uuid] = {
                            date: new Date(),
                        };
                    });
                },
                () => {
                    this.sealFailed = true;
                    this.issuerService.closeSpinnerDialog();
                    this.isLoadingResults = false;
                }
            );
        this.closeLocalSealingPassModal();
    }

    doManageSealingErrors(sealedCredentials: CredentialView[]) {
        const hasError = this.checkSealingErrors(sealedCredentials);

        const message: OKNotification = {
            severity: hasError ? 'warn' : 'success',
            summary: hasError
                ? this.translateService.instant(
                      'seal-credential.message.seal-ko'
                  )
                : this.translateService.instant(
                      'seal-credential.message.seal-ok'
                  ),
        };

        this.notifService.showNotification(message);
    }
    /**
     * Sign credentials selected
     */
    toSeal() {
        this.nexU
            .toSeal(this.uuids)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (sealedCredentials: CredentialView[]) => {
                    let credentials = this.issuerService.getCredentials();
                    for (let i = 0; i < credentials.length; i++) {
                        let cred = sealedCredentials.find(
                            (el) => credentials[i].uuid === el.uuid
                        );
                        if (cred) {
                            credentials[i] = cred;
                        }
                    }

                    sealedCredentials.forEach(
                        (sealedCredential: CredentialView) => {
                            this.datesOfCredentialSealed[
                                sealedCredential.uuid
                            ] = { date: new Date() };
                        }
                    );

                    this.issuerService.setCredentials(credentials);
                    this.sealed = true;
                    this.loadCredentialsData();
                    this.isLoadingResults = false;

                    this.dataCredentialsTable.forEach(
                        (credential: CredentialView) => {
                            const sealingErrors = this.getErrorMessage(
                                credential.uuid
                            );
                            if (sealingErrors) {
                                credential.sealed = false;
                                credential.sealingErrors = [sealingErrors];
                            }
                        }
                    );

                    this.doManageSealingErrors(sealedCredentials);
                    /*
                this.dataSource.data.forEach((credential: CredentialView) => {
                    const sealingErrors = this.getErrorMessage(credential.uuid);
                    if (sealingErrors) {
                        credential.sealed = false;
                        credential.sealingErrors = [sealingErrors];
                    }
                });

                const hasError = this.checkSealingErrors(sealedCredentials);

                const message: OKNotification = {
                    severity: hasError ? 'warn' : 'success',
                    summary: hasError
                        ? this.translateService.instant(
                              'seal-credential.message.seal-ko'
                          )
                        : this.translateService.instant(
                              'seal-credential.message.seal-ok'
                          ),
                };

                this.notifService.showNotification(message);*/
                },
                () => {
                    this.sealFailed = true;
                    this.isLoadingResults = false;
                }
            );
    }

    getErrorMessage(uuid: string): string {
        let errorMessage: string = null;
        const error = this.issuerService.signatureBytesErrors.find(
            (x) => x.uuid === uuid
        );
        if (error) {
            errorMessage = error.errorMessage;
        }
        return errorMessage;
    }

    // seal-credential.message.seal-ko

    /**
     * Send credentials
     */
    toSend(): void {
        if (this.selection.selected.length <= 0) {
            return null;
        }

        /* Check if response has errors from send or received information and build an OKNotification */
        const checkSendOrReceivedErrors = (
            credentials: CredentialView[]
        ): boolean => {
            return credentials.some(
                (cred: CredentialView) =>
                    cred.sendErrors.length > 0 || cred.receivedErrors.length > 0
            );
        };

        this.isLoadingResults = true;

        this.nexU
            .toSend(this.selection.selected)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (data: CredentialView[]) => {
                    let credentials = this.issuerService.getCredentials();
                    for (let i = 0; i < credentials.length; i++) {
                        let cred = data.find(
                            (el) => credentials[i].uuid === el.uuid
                        );
                        if (cred) {
                            credentials[i] = cred;
                        }
                    }

                    this.issuerService.setCredentials(credentials);
                    this.loadCredentialsData();
                    this.setDownloadCSVButtonIfShouldBeEnabled();
                    this.isLoadingResults = false;
                    this.sent = true;
                    const hasError: boolean = checkSendOrReceivedErrors(data);
                    const message: OKNotification = {
                        severity: hasError ? 'warn' : 'success',
                        summary: hasError
                            ? this.translateService.instant(
                                  'seal-credential.message.send-or-seal-ko'
                              )
                            : this.translateService.instant(
                                  'seal-credential.message.send-ok'
                              ),
                    };
                    this.notifService.showNotification(message);
                },
                () => (this.isLoadingResults = false)
            );
    }

    /**
     * Remove credential
     */
    deleteCredential(uuid: string): void {
        if (!uuid) {
            return null;
        }

        this.issuerService
            .deleteCredential(uuid)
            .pipe(takeUntil(this.destroy$))
            .subscribe((status: StatusView) => {
                if (status) {
                    this.issuerService.setCredentials(
                        this.dataCredentialsTable.filter((element) => element.uuid !== uuid)
                    );
                    this.loadCredentialsData();
                }
            });
    }

    /**
     * Preview certificate/s by uuids
     *
     * @param {boolean} isSingle flag to available accordion or standalone diploma
     * @param {string} uuid if isSingle, uuid shouldn't be null
     */
    previewCertificates(isSingle: boolean, uuid: string = null) {
        let uuids: string[] = [];

        /* Single preview */
        if (isSingle && uuid) {
            uuids[0] = uuid;
        } else {
            /* Multiple carousel preview */
            uuids = this.dataCredentialsTable.map(
                (credential: CredentialView) => credential.uuid
            );
        }

        this.isSingle = isSingle;
        this.uuids = uuids;

        this.loadComponent();
    }

    downloadSelectedCredentials() {
        const credentialsToDownload: CredentialDownloadView =
            this.getCredentialsToDownload();
        this.api
            .downloadTempCredentials(
                credentialsToDownload,
                this.translateService.currentLang,
                'response'
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((response: HttpResponse<Blob>) => {
                const contentDispositionHeader = response.headers.get(
                    'content-disposition'
                );
                const fileName = contentDispositionHeader
                    ? contentDispositionHeader
                          .split(';')[1]
                          .trim()
                          .split('=')[1]
                          .replace(/\//g, '')
                          .replace(/['"]+/g, '')
                    : 'credentials.zip';

                // It is necessary to create a new blob object with mime-type explicitly set
                // otherwise only Chrome works like it should
                const newBlob = new Blob([response.body], {
                    type: response.body.type,
                });

                this.generateBlobLink(newBlob, fileName);
            });
    }

    onNewDocument(files: File[]) {
        if (files.length > 0 && files[0]) {
            this.issuerService.files = files;
            this.files[0] = files[0];
            this.sent = this.sealed = false;
        }
    }

    /* Mat action checkbox */
    /** Whether the number of selected elements matches the total number of rows. */
    isAllSelected() {
        const numSelected = this.selection.selected.length;
        const numRows = this.dataCredentialsTable.length;
        return numSelected === numRows;
    }

    /** Selects all rows if they are not all selected; otherwise clear selection. */
    masterToggle() {
        this.isAllSelected()
            ? this.selection.clear()
            : this.dataCredentialsTable.forEach((row) => this.selection.select(row));
        this.setDownloadCSVButtonIfShouldBeEnabled();
    }

    /** The label for the checkbox on the passed row */
    checkboxLabel(row?: CredentialView): string {
        if (!row) {
            return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
        }
        return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${
            this.dataCredentialsTable.indexOf(row) + 1
        }`;
    }

    onClose() {
        this.isSingle = null;
        this.uuids = null;
    }

    goToPrevious() {
        this.location.back();
    }

    goToHome() {
        this.router.navigate(['home']);
    }

    openValidationErrorsModal(validationErrors) {
        this.validationErrors = validationErrors;
        this.uxService.openModal('validationErrors');
    }

    setDownloadCSVButtonIfShouldBeEnabled(): any {
        if (this.selection.selected.length) {
            for (let i = 0; i < this.selection.selected.length; i++) {
                if (
                    !this.selection.selected[i].sent &&
                    !this.selection.selected[i].received
                ) {
                    this.downloadCSVEnabled = false;
                    return;
                }
            }
            this.downloadCSVEnabled = true;
        } else {
            this.downloadCSVEnabled = false;
        }
    }

    onPageChange(event) {
        this.first = event.first;
        this.onPage.emit(event);
    }

    onSortChange(event) {
        this.onSort.emit(event);
    }

    onRowSelect(rowIndex: number, oid: number) {
        this.selectedRowIndex = rowIndex;
        this.onSelectItem.emit(oid);
    }

    /**
     * Generate link to download Blob
     * @param blob
     */
    private generateBlobLink(blob: Blob, fileName: string): void {
        // IE doesn't allow using a blob object directly as link href
        // instead it is necessary to use msSaveOrOpenBlob
        let nav = (window.navigator as any);
        if (nav && nav.msSaveOrOpenBlob) {
            nav.msSaveOrOpenBlob(blob);
            return;
        }

        // For other browsers:
        // Create a link pointing to the ObjectURL containing the blob.
        const data = window.URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = data;
        link.download = fileName;
        // this is necessary as link.click() does not work on the latest firefox
        link.dispatchEvent(
            new MouseEvent('click', {
                bubbles: true,
                cancelable: true,
                view: window,
            })
        );

        setTimeout(function () {
            // For Firefox it is necessary to delay revoking the ObjectURL
            window.URL.revokeObjectURL(data);
            link.remove();
        }, 100);
    }

    private getCredentialsToDownload(): CredentialDownloadView {
        const uuidList: string[] = [];
        this.selection.selected.forEach((credential) => {
            uuidList.push(credential.uuid);
        });
        return { uuid: uuidList };
    }

    private checkSealingErrors(credentials: CredentialView[]): boolean {
        return credentials.some((cred: CredentialView) => !cred.sealed);
    }

    /**
     * Load credentials data
     */
    private loadCredentialsData() {
        this.selection.clear();
        this.dataCredentialsTable = this.issuerService.getCredentials();
        this.dataCredentialsTable.filter(row => row.valid).forEach(row => this.selection.select(row));
        /*
        this.dataSource = new MatTableDataSource<CredentialView>(
            this.issuerService.getCredentials()
        );
        if (this.dataSource) {
            this.dataSource.data.forEach((row: CredentialView) => {
                if (row.valid) {
                    this.selection.select(row);
                }
            });
        }
        this.dataSource.paginator = this.paginator;*/
    }

    /** Progress bar */
    private getProgressInfo() {
        this.progressInfoSubscription = interval(3000).pipe(
            switchMap(() => this.getBatchStatus()))
            .pipe(takeUntil(this.destroy$))
            .subscribe((data) => {
                this.actualExport = data;
                /** Failed */
                if (
                    this.actualExport.batchStatus ===
                    Constants.BATCH_STATUS.FAILED
                ) {
                    this.progressInfoSubscription.unsubscribe();
                    this.deliveryOngoing = false;
                    return;
                }
                /** Stop by the user */
                if (
                    this.actualExport.batchStatus ===
                    Constants.BATCH_STATUS.STOPPED
                ) {
                    this.progressInfoSubscription.unsubscribe();
                    this.deliveryOngoing = false;
                    return;
                }
                /** Completed */
                if (
                    this.actualExport.batchStatus ===
                    Constants.BATCH_STATUS.COMPLETED
                ) {
                    this.progressInfoSubscription.unsubscribe();
                    this.deliveryOngoing = false;
                    // this.getExportHistory();
                    return;
                }
                /** On going */
                if (
                    this.actualExport.batchStatus ===
                    Constants.BATCH_STATUS.STARTED
                ) {
                    return;
                }
            });
    }

    private getBatchStatus() {
        return this.issuerService.getBatchStatus();
    }

    /** Download credential and load xml to show */
    private loadComponent() {
        let europassCredentialXML = null;
        const uuid = this.uuids[0];
        // Get first XML by uuid
        this.downloadCredentialSubscription = this.issuerService
            .downloadCredential(uuid)
            .pipe(takeUntil(this.destroy$))
            .subscribe((data) => {
                const reader = new FileReader();
                reader.readAsText(data, 'utf-8');
                reader.onloadend = () => {
                    europassCredentialXML = reader.result as string;
                    let formElement = document.createElement('form');
                    formElement.method = 'POST';
                    formElement.target = 'preview_popup';
                    formElement.action = `${environment.viewerBaseUrl}/mvc/preview`;
                    let inputElement = document.createElement('input');
                    inputElement.type = 'text';
                    inputElement.name = 'xml';
                    inputElement.value = europassCredentialXML;
                    formElement.appendChild(inputElement);
                    document.body.appendChild(formElement);
                    let popUp = window.open(
                        'about:blank',
                        'preview_popup',
                        // tslint:disable-next-line: max-line-length
                        'width=1080,height=650,toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes'
                    );
                    formElement.submit();
                    formElement.remove();
                };
            });
    }

}
