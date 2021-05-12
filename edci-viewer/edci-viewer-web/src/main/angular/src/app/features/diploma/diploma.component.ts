import { Location } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
    AfterViewInit,
    Component,
    ElementRef,
    OnDestroy,
    OnInit,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import html2canvas from 'html2canvas';
import { Subject } from 'rxjs';
import { DisplayErrorService } from 'src/app/core/services/response-error-growl.service';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { EuropassDiplomaView, V1Service } from 'src/app/shared/swagger';
import { environment } from 'src/environments/environment';

@Component({
    selector: 'edci-viewer-diploma',
    templateUrl: './diploma.component.html',
    styleUrls: ['./diploma.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class DiplomaComponent implements OnInit, AfterViewInit, OnDestroy {
    @ViewChild('cert', { read: ElementRef }) cert: ElementRef;
    html: string;
    isLoading: boolean = false;
    language: string = this.translateService.currentLang;
    basePath: string = environment.apiBaseUrl;
    destroy$: Subject<boolean> = new Subject<boolean>();
    isPreview: boolean;
    linkExpirationDate: string;
    uploadFromDiplomaView: boolean = false;
    primaryLanguage: string;
    availableLanguages: string[];

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private translateService: TranslateService,
        private apiService: V1Service,
        private location: Location,
        private displayErrorService: DisplayErrorService,
        private shareDataService: ShareDataService
    ) {}

    ngOnInit() {
        if (this.shareDataService.toolbarLanguage) {
            this.language = this.shareDataService.toolbarLanguage;
            this.primaryLanguage = this.language;
            this.checkOrigin(true);
        } else {
            this.language = this.translateService.currentLang;
            this.checkOrigin();
        }
    }

    ngAfterViewInit() {
        this.mutationObserver();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onLanguageChange(languageCode: string): void {
        if (this.language !== languageCode) {
            this.language = languageCode;
            this.shareDataService.toolbarLanguage = languageCode;
            this.shareDataService.changeToolbarLanguage(languageCode);
            this.checkOrigin(true);
        }
    }

    newCredentialUpload(): void {
        this.isLoading = true;
        this.uploadFromDiplomaView = true;
        this.language = this.shareDataService.toolbarLanguage || undefined;
        this.getDiploma(this.shareDataService.uploadedXML, this.language, false, true);
    }

    /**
     * Checks which flow the viewer
     * is coming from
     */
    private checkOrigin(byNewLang: boolean = false): void {
        this.isLoading = true;
        this.language = this.shareDataService.toolbarLanguage || undefined;
        if (this.isShareView()) {
            this.getDiplomaFromShareView(this.language, byNewLang);
        } else if (this.isView()) {
            this.getDiplomaFromView(this.language, byNewLang);
        } else {
            this.getDiploma(this.getXML(), this.language, byNewLang);
        }
    }

    /*
     * MutationObserver to get notified when diploma-template
     * is present on the DOM
     */
    private mutationObserver() {
        const observer = new MutationObserver((mutations: MutationRecord[]) => {
            mutations.forEach((mutation: MutationRecord) => {
                const element = mutation.addedNodes[0] as HTMLElement;
                if (element) {
                    if (element.id === 'diploma-template') {
                        this.captureScreen();
                    }
                }
            });
        });

        observer.observe(this.cert.nativeElement as Node, {
            attributes: true,
            childList: true,
            characterData: true,
        });
    }

    /**
     * Takes a "screenshot" of the certificate diploma (cert).
     * We need to scroll to (0, 0) so it does not get cropped
     */
    private captureScreen(): void {
        const cert = this.cert.nativeElement;
        window.scroll(0, 0);
        html2canvas(cert).then((canvas) => {
            sessionStorage.setItem('diplomaImg', canvas.toDataURL('image/png'));
        });
    }

    private getDiplomaFromShareView(lang: string, byNewLang: boolean): void {
        this.route.params
            .takeUntil(this.destroy$)
            .subscribe((params: Params) => {
                const shareLink: string = params['shareLink']
                    ? params['shareLink']
                    : sessionStorage.getItem('shareLink');
                this.apiService
                    .getSharedCredentialDiploma(
                        shareLink,
                        lang
                    )
                    .takeUntil(this.destroy$)
                    .subscribe(
                        (credential: EuropassDiplomaView) => {
                            this.resetVariables();
                            this.linkExpirationDate = credential.expirationDate;
                            sessionStorage.setItem(
                                'linkExpirationDate',
                                credential.expirationDate.toString()
                            );
                            sessionStorage.setItem('shareLink', shareLink);
                            if (!byNewLang) {
                                this.primaryLanguage = credential.primaryLanguage;
                                this.shareDataService.toolbarLanguage = credential.primaryLanguage;
                            }
                            this.availableLanguages =
                                credential.availableLanguages;
                            this.shareDataService.shareLink = shareLink;
                            this.html = credential.html;
                            this.isLoading = false;
                        },
                        (response: HttpErrorResponse) => {
                            this.errorResponse(response);
                        }
                    );
            });
    }

    private getDiplomaFromView(lang: string, byNewLang: boolean): void {
        this.route.params
            .takeUntil(this.destroy$)
            .subscribe((params: Params) => {
                const credentialId: string = params['id']
                    ? params['id']
                    : sessionStorage.getItem('credId');
                const userId: string = params['userId']
                    ? params['userId']
                    : sessionStorage.getItem('userId');
                this.apiService
                    .getWalletDiplomaHTML(userId, credentialId, lang)
                    .takeUntil(this.destroy$)
                    .subscribe(
                        (credential: EuropassDiplomaView) => {
                            this.resetVariables();
                            sessionStorage.setItem('credId', credentialId);
                            sessionStorage.setItem('userId', userId);
                            if (!byNewLang) {
                                this.primaryLanguage = credential.primaryLanguage;
                                this.shareDataService.toolbarLanguage = credential.primaryLanguage;
                            }
                            this.availableLanguages =
                                credential.availableLanguages;
                            this.shareDataService.credentialId = credentialId;
                            this.shareDataService.userId = userId;
                            this.html = credential.html;
                            this.isLoading = false;
                        },
                        (response: HttpErrorResponse) => {
                            this.errorResponse(response);
                        }
                    );
            });
    }

    private errorResponse(response: HttpErrorResponse) {
        if (response.status === 403) {
            this.displayErrorService.displayRequestErrorGrowl(response);
        }
        this.router.navigate(['home']);
    }

    private getDiploma(xml: string, lang: string, byNewLang: boolean = false, fromNewCredentialButton = false): void {
        this.isPreview = !!sessionStorage.getItem('isPreview');
        const xmlBlob: Blob = new Blob([xml], {
            type: 'text/xml',
        });
        this.apiService
            .getCredentialDiploma(xmlBlob, lang)
            .takeUntil(this.destroy$)
            .subscribe(
                (data: EuropassDiplomaView) => {
                    this.resetVariables();
                    if (this.isPreview) {
                        sessionStorage.setItem('isPreview', 'true');
                    }
                    if (!byNewLang) {
                        this.primaryLanguage = data.primaryLanguage;
                        this.shareDataService.toolbarLanguage = data.primaryLanguage;
                    }
                    this.availableLanguages = data.availableLanguages;
                    this.html = data.html;
                    this.isLoading = false;
                    if (this.uploadFromDiplomaView) {
                        this.resetVerificationSteps();
                    }
                    sessionStorage.setItem('diplomaXML', xml);
                },
                (response: HttpErrorResponse) => {
                    this.isLoading = false;
                    if (fromNewCredentialButton) {
                        this.router.navigate(['home']);
                    } else {
                        if (!this.uploadFromDiplomaView) {
                            this.location.back();
                        }
                    }
                }
            );
    }

    private resetVerificationSteps(): void {
        sessionStorage.setItem('verificationSteps', null);
        this.shareDataService.verificationSteps = null;
    }

    private isShareView(): boolean {
        return (
            this.router.url.includes('/shareview') ||
            !!sessionStorage.getItem('shareLink')
        );
    }

    private isView(): boolean {
        return (
            this.router.url.includes('/view') ||
            (!!sessionStorage.getItem('credId') &&
                !!sessionStorage.getItem('userId'))
        );
    }

    private getXML(): string {
        // Using sessionStorage item in case the service does not hold the file.
        // Prevents error on refresh.
        return this.shareDataService.uploadedXML
            ? this.shareDataService.uploadedXML
            : sessionStorage.getItem('diplomaXML');
    }

    private resetVariables(): void {
        sessionStorage.clear();
        this.shareDataService.verificationSteps = null;
    }
}
