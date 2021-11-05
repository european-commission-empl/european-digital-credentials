import { SelectionModel } from '@angular/cdk/collections';
import { Location } from '@angular/common';
import {
    Component,
    OnDestroy,
    OnInit,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import { environment } from '@environments/environment';
import { MatPaginator, MatTableDataSource } from '@angular/material';
import { Router } from '@angular/router';
import { FileUploadResponseView } from '@core/models/DTO/fileUploadResponseView.model';
import { Progress } from '@core/models/DTO/progress.model';
import { UxService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { ApiService } from '@services/api.service';
import { NotificationService, OKNotification } from '@services/error.service';
import { IssuerService } from '@services/issuer.service';
import { NexUService } from '@services/nexU.service';
import { DocumentUpload } from '@shared/models/upload.model';
import {
    CredentialView,
    LocalSignatureRequestView,
    StatusView,
    V1Service,
} from '@shared/swagger';
import { interval, Subject, Subscription } from 'rxjs';
import { FormGroup, FormControl } from '@angular/forms';
import { takeUntil } from 'rxjs/operators';
import { Constants } from '@shared/constants';

@Component({
    selector: 'edci-overview',
    templateUrl: './overview.component.html',
    styleUrls: ['./overview.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class OverviewComponent implements OnInit, OnDestroy {
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
    files: DocumentUpload[] = [];
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
        public uxService: UxService,
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
        this.files[0] = this.issuerService.getFile();
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

        this.toSeal();
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
            environment.enabledLocalSealing
                ? this.openLocalSealingPassModal()
                : this.openPresentationModal();
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

    public doManageSealingErrors(sealedCredentials: CredentialView[]) {
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
    public toSeal() {
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

                    this.dataSource.data.forEach(
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
    public toSend(): void {
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
    public deleteCredential(uuid: string): void {
        if (!uuid) {
            return null;
        }

        this.issuerService
            .deleteCredential(uuid)
            .pipe(takeUntil(this.destroy$))
            .subscribe((status: StatusView) => {
                if (status) {
                    this.issuerService.setCredentials(
                        this.dataSource.data.filter(
                            (element) => element.uuid !== uuid
                        )
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
    public previewCertificates(isSingle: boolean, uuid: string = null) {
        let uuids: string[] = [];

        /* Single preview */
        if (isSingle && uuid) {
            uuids[0] = uuid;
        } else {
            /* Multiple carousel preview */
            uuids = this.dataSource.data.map(
                (credential: CredentialView) => credential.uuid
            );
        }

        this.isSingle = isSingle;
        this.uuids = uuids;

        this.loadComponent();
    }

    public editCertificate() {
        return;
    }

    public onNewDocument(files: DocumentUpload[]) {
        if (files.length > 0 && files[0]) {
            this.issuerService.setFile(files[0]);
            this.files[0] = files[0];
            this.sent = this.sealed = false;
            this.sendCredentials();
        }
    }

    /* Mat action checkbox */
    /** Whether the number of selected elements matches the total number of rows. */
    public isAllSelected() {
        const numSelected = this.selection.selected.length;
        const numRows = this.dataSource.data.length;
        return numSelected === numRows;
    }

    /** Selects all rows if they are not all selected; otherwise clear selection. */
    public masterToggle() {
        this.isAllSelected()
            ? this.selection.clear()
            : this.dataSource.data.forEach((row) => this.selection.select(row));
        this.setDownloadCSVButtonIfShouldBeEnabled();
    }

    /** The label for the checkbox on the passed row */
    public checkboxLabel(row?: CredentialView): string {
        if (!row) {
            return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
        }
        return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${
            this.dataSource.data.indexOf(row) + 1
        }`;
    }

    public onClose() {
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

    private checkSealingErrors(credentials: CredentialView[]): boolean {
        return credentials.some((cred: CredentialView) => !cred.sealed);
    }

    /* Send credentials */
    private sendCredentials() {
        this.issuerService.openSpinnerDialog();

        this.api
            .uploadCredential(
                this.files[0].file,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (fileUploadResponseView: FileUploadResponseView) => {
                    if (fileUploadResponseView.valid) {
                        // To check
                        this.issuerService.setCredentials(
                            <CredentialView[]>fileUploadResponseView.credentials
                        );
                        this.loadCredentialsData();
                    }
                    this.issuerService.closeSpinnerDialog();
                },
                () => this.issuerService.closeSpinnerDialog()
            );
    }

    /**
     * Load credentials data
     */
    private loadCredentialsData() {
        this.selection.clear();
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
        this.dataSource.paginator = this.paginator;
    }

    /** Progress bar */
    private getProgressInfo() {
        this.progressInfoSubscription = interval(3000)
            .switchMap(() => this.getBatchStatus())
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
                };
            });
    }
}
