import {
    Component,
    ElementRef,
    OnDestroy,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import { Router } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { EuiDialogComponent } from '@eui/components/eui-dialog';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { environment } from 'src/environments/environment';
import { takeUntil } from 'rxjs/operators';
import { V1Service } from '@shared/swagger';
import { DisplayErrorService } from '@services/response-error-growl.service';

@Component({
    templateUrl: './home.component.html',
    styleUrls: ['home.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class HomeComponent implements OnDestroy {
    @ViewChild('inputFile') inputFile: ElementRef;
    @ViewChild('confirmDialog') confirmDialog: EuiDialogComponent;
    uploadedXML: Blob;
    ePortfolioUrl: string =
        environment.ePortfolioUrl + this.translateService.currentLang;
    hasBranding: boolean = environment.hasBranding;
    homeMenuMainTitle: string = environment.homeMenuMainTitle;
    homeMenuDescription: string = environment.homeMenuDescription;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private router: Router,
        private shareDataService: ShareDataService,
        private translateService: TranslateService,
        private apiService: V1Service,
        private displayErrorService: DisplayErrorService
    ) {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.ePortfolioUrl = environment.ePortfolioUrl + event.lang;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    /**
     * Reads the content of an uploaded XML file and stores it as string.
     */
    readFile(event: FileList): void {
        if (event[0].type.endsWith('/xml')) {
            this.uploadedXML = event[0];
            this.openConfirmModal();
        } else {
            const reader = new FileReader();
            this.shareDataService.openSpinnerDialog();
            reader.readAsText(event[0], 'utf-8');
            reader.onloadend = () => {
                this.shareDataService.doStorageFullClear();
                this.shareDataService.diplomaJSON = reader.result as string;
                this.shareDataService.getUploadDetails('').pipe(takeUntil(this.destroy$)).subscribe({
                    next: (data) => {
                        let jsonFile = new Blob([this.shareDataService.diplomaJSON], {
                            type: 'application/ld+json',
                        });

                        this.shareDataService.setVerificationStepsObs(
                            (lang: string) => this.apiService.getCredentialVerification(jsonFile, lang)
                        );

                        this.shareDataService.setPresentationViewObs(
                            (lang: string) => this.apiService.getCredentialDetail(jsonFile, lang)
                        );

                        this.shareDataService.setDiplomaViewObs(
                            (lang: string) => this.apiService.getCredentialDiploma(jsonFile, lang)
                        );

                        this.shareDataService.toolbarLanguage = data[0].credentialMetadata.primaryLanguage;
                        this.shareDataService.changeToolbarLanguage(data[0].credentialMetadata.primaryLanguage);
                    },
                    complete: () => this.router.navigate(['diploma-details']),
                    error: () => this.shareDataService.closeSpinnerDialog()
                });

            };
        }
    }

    openConfirmModal() {
        this.confirmDialog.openDialog();
    }

    saveConfirmModal() {
        this.shareDataService.openSpinnerDialog();
        this.apiService
            .convertCredential(this.uploadedXML)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (data: string) => {
                    this.shareDataService.doStorageFullClear();
                    this.shareDataService.diplomaJSON = JSON.stringify(data);
                    this.shareDataService.getUploadDetails('').pipe(takeUntil(this.destroy$)).subscribe({
                        next: (details) => {
                            let jsonFile = new Blob([this.shareDataService.diplomaJSON], {
                                type: 'application/ld+json',
                            });

                            this.shareDataService.setVerificationStepsObs(
                                (lang: string) => this.apiService.getCredentialVerification(jsonFile, lang)
                            );

                            this.shareDataService.setPresentationViewObs(
                                (lang: string) => this.apiService.getCredentialDetail(jsonFile, lang)
                            );

                            this.shareDataService.setDiplomaViewObs(
                                (lang: string) => this.apiService.getCredentialDiploma(jsonFile, lang)
                            );

                            this.shareDataService.toolbarLanguage = details[0].credentialMetadata.primaryLanguage;
                            this.shareDataService.changeToolbarLanguage(details[0].credentialMetadata.primaryLanguage);
                        },
                        complete: () => this.router.navigate(['diploma-details']),
                        error: () => {
                            this.uploadedXML = null;
                            this.inputFile.nativeElement.value = null;
                            this.shareDataService.closeSpinnerDialog();
                        }
                    });
                },
                (err) => {
                    this.shareDataService.closeSpinnerDialog();
                    this.uploadedXML = null;
                    this.inputFile.nativeElement.value = null;
                    this.displayErrorService.showNotificationText(
                        `${this.translateService.instant(
                            'error'
                        )}`,
                        `${this.translateService.instant(err.error.message)}`,
                        true
                    );
                    this.router.navigate(['home']);
                }
            );
    }

    closeConfirmModal() {
        this.uploadedXML = null;
        this.inputFile.nativeElement.value = null;
        this.confirmDialog.closeDialog();
    }

    /**
     * Sends the click event to the input
     */
    openDialog() {
        return this.inputFile.nativeElement.click();
    }
}
