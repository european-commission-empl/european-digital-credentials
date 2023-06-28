import { Component, OnDestroy, ViewEncapsulation } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '@environments/environment';
import { UxAppShellService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { ApiService } from '@services/api.service';
import { IssuerService } from '@services/issuer.service';
import { NexUService } from '@services/nexU.service';
import { NexUDialogComponent } from '@shared/components/nexU-dialog/nexu-dialog.component';
import { Constants } from '@shared/constants';
import {
    CredentialFileUploadResponseView,
    CredentialView,
    V1Service
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class HomeComponent implements OnDestroy {
    isXMLFile: boolean;
    files: File[] = [];
    allowedExtensions = ['.json'];
    homeCredentialsForIssuersTitle: string =
        environment.homeCredentialsForIssuersTitle;
    homeCredentialsForIssuersDescription: string =
        environment.homeCredentialsForIssuersDescription;
    homeCredentialsForIssuersDescriptionWithLink: string =
        environment.homeCredentialsForIssuersDescriptionWithLink;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        public uxService: UxAppShellService,
        private api: V1Service,
        private issuerService: IssuerService,
        private apiService: ApiService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private translateService: TranslateService,
        private nexU: NexUService,
        public dialog: MatDialog
    ) { }

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
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => this.nexU.testSeal());
    }

    goToCredentialBuilder(): void {
        localStorage.setItem('valueBack', '/credential-builder');
        this.router.navigate(['/credential-builder']);
    }

    /* On file uploaded */
    onNewDocument(files: File[], isXML: boolean) {
        this.isXMLFile = isXML;
        if (files.length > 0) {
            if (isXML) {
                this.files = files;
            } else {
                this.issuerService.files = files;
                this.files[0] = files[0];
            }
        }
        this.sendCredentialsXLS();
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
            this.apiService
                .addCredentials(this.files, this.translateService.currentLang)
                .pipe(takeUntil(this.destroy$))
                .subscribe({
                    next: (
                        fileUploadResponseView: CredentialFileUploadResponseView
                    ) => {
                        this.fileUploadResponse(fileUploadResponseView);
                    },
                    error: () => this.issuerService.closeSpinnerDialog(),
                });
        }
    }

    private fileUploadResponse(
        fileUploadResponseView: CredentialFileUploadResponseView
    ) {
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
