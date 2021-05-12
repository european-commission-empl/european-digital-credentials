import { Component, OnDestroy, ViewEncapsulation } from '@angular/core';
import { MatDialog } from '@angular/material';
import { ActivatedRoute, Router } from '@angular/router';
import { UxService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { IssuerService } from '@services/issuer.service';
import { NexUService } from '@services/nexU.service';
import { NexUDialogComponent } from '@shared/components/nexU-dialog/nexu-dialog.component';
import { DocumentUpload } from '@shared/models/upload.model';
import {
    CredentialView,
    CredentialFileUploadResponseView,
    V1Service,
    CLElementBasicView,
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { Constants } from '@shared/constants';
import { environment } from '@environments/environment';

@Component({
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class HomeComponent implements OnDestroy {
    isXMLFile: boolean;
    files: DocumentUpload[] = [];
    templates: CLElementBasicView[] = [];
    xmlExtension = ['xml'];
    selectedTemplate: CLElementBasicView;
    homeCredentialsForIssuersTitle: string =
        environment.homeCredentialsForIssuersTitle;
    homeCredentialsForIssuersDescription: string =
        environment.homeCredentialsForIssuersDescription;
    homeCredentialsForIssuersDescriptionWithLink: string =
        environment.homeCredentialsForIssuersDescriptionWithLink;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        public uxService: UxService,
        private api: V1Service,
        private issuerService: IssuerService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private translateService: TranslateService,
        private nexU: NexUService,
        public dialog: MatDialog
    ) {}

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    /* Test your seal Creation Device */
    testYourDevice() {
        const dialogRef = this.dialog.open(NexUDialogComponent, {
            backdropClass: 'blueBackdrop',
            maxWidth: '100vw',
            minWidth: '100vw',
            maxHeight: '100vh',
            minHeight: '100vh',
        });

        dialogRef
            .afterOpened()
            .takeUntil(this.destroy$)
            .subscribe(() => this.nexU.testSeal());
    }

    goToCredentialBuilder(): void {
        this.router.navigate(['/credential-builder']);
    }

    openDownloadModal(): void {
        this.api
            .getAvailableTemplates()
            .takeUntil(this.destroy$)
            .subscribe((templates: CLElementBasicView[]) => {
                this.templates = templates;
            });
        this.uxService.openModal('downloadXLS');
    }

    /* Download XLS */
    downloadXLS() {
        this.issuerService.downloadTemplate(this.selectedTemplate.label);
        this.selectedTemplate = undefined;
        this.uxService.closeModal('downloadXLS');
    }

    /* On file uploaded */
    onNewDocument(files: DocumentUpload[], isXML: boolean) {
        this.isXMLFile = isXML;
        if (files.length > 0 && files[0]) {
            this.issuerService.setFile(files[0]);
            this.files[0] = files[0];
            this.sendCredentialsXLS();
        }
    }

    navigateHowToPrepareYourData(): void {
        window.open(
            Constants.PREPARE_YOUR_DATA_URL,
            `__blank${new Date().getTime()}`
        );
    }

    /* Send credentials */
    private sendCredentialsXLS() {
        this.issuerService.openSpinnerDialog();
        if (this.isXMLFile) {
            this.api
                .addCredentials(
                    this.files[0].file,
                    this.translateService.currentLang
                )
                .takeUntil(this.destroy$)
                .subscribe(
                    (fileUploadResponseView: CredentialFileUploadResponseView) => {
                        this.fileUploadResponse(fileUploadResponseView);
                    },
                    () => this.issuerService.closeSpinnerDialog()
                );
        } else {
            this.api
                .uploadCredential(
                    this.files[0].file,
                    this.translateService.currentLang
                )
                .takeUntil(this.destroy$)
                .subscribe(
                    (fileUploadResponseView: CredentialFileUploadResponseView) => {
                        this.fileUploadResponse(fileUploadResponseView);
                    },
                    () => this.issuerService.closeSpinnerDialog()
                );
        }
    }

    private fileUploadResponse(fileUploadResponseView: CredentialFileUploadResponseView) {
        if (fileUploadResponseView.valid) {
            // To check
            this.issuerService.setCredentials(
                <CredentialView[]>fileUploadResponseView.credentials
            );
            this.router.navigate(['/create/overview'], {
                relativeTo: this.activatedRoute,
            });
        } else {
            // To adjust
            this.router.navigate(['/create/adjust'], {
                relativeTo: this.activatedRoute,
            });
        }
        this.issuerService.closeSpinnerDialog();
    }
}
