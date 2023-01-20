import { Component, OnDestroy, OnInit, ViewEncapsulation, AfterViewInit } from '@angular/core';
import { ActivatedRoute,
    Router,
    Params,
    NavigationStart,
    NavigationEnd } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { EuropassCredentialPresentationView,
    EuropassDiplomaView,
    V1Service } from 'src/app/shared/swagger';
import { takeUntil, filter } from 'rxjs/operators';
import { DisplayErrorService } from '@services/response-error-growl.service';

@Component({
    selector: 'edci-viewer-diploma-details',
    templateUrl: './diploma-details.component.html',
    styleUrls: ['./diploma-details.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class DiplomaDetailsComponent implements OnInit, OnDestroy {
    diplomaImg: string[] = [];
    linkExpirationDate: string;
    credId: string;
    userId: string;
    shareHash: string = sessionStorage.getItem('shareLink') || null;
    issuerLogo: string;
    XMLFile: Blob;
    items: any[];
    header: any = {};
    isDetailLoaded = false;
    reloadDetailsLanguage = false;
    language: string = this.translateService.currentLang;
    toolbarLanguage: string = this.shareDataService.toolbarLanguage;
    destroy$: Subject < boolean> = new Subject<boolean>();
    availableLanguages: string[];
    formatType: string;
    credential: EuropassCredentialPresentationView;
    isPreview: boolean;
    scrollPosition: number[] = [0, 0];

    constructor(
        private route: ActivatedRoute,
        private apiService: V1Service,
        private router: Router,
        private shareDataService: ShareDataService,
        private translateService: TranslateService,
        private displayErrorService: DisplayErrorService
    ) {}

    ngOnInit() {
        console.log('inside ng on init');
        this.isDetailLoaded = false;
        this.handleNavigationScroll();
        this.checkSession('');
        this.shareDataService.toolbarLanguageObservable.pipe(takeUntil(this.destroy$)).subscribe(language => {
            this.toolbarLanguage = language;
        });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onLanguageChange(languageCode: string): void {
        if (this.language !== languageCode) {
            this.reloadDetailsLanguage = true;
            this.language = languageCode;
            this.shareDataService.toolbarLanguage = languageCode;
            this.shareDataService.changeToolbarLanguage(languageCode);
            this.shareDataService.setToolbarLanguage(languageCode);
            this.checkSession(languageCode);
        }
    }

    onUploadNewCredential(xml: string): void {
        this.isDetailLoaded = false;
        this.XMLFile = new Blob([xml], { type: 'text/xml' });
        this.getDataFromXML();
    }

    private getHeader(credential: EuropassCredentialPresentationView): void {
        this.header.title = credential.credentialMetadata.title;
        this.header.credentialType = credential.credentialMetadata.type;
        this.header.credentialVPType = credential.credentialMetadata.vpType;
        this.header.issuanceDate = credential.credentialMetadata.issuanceDate;
        this.header.expiryDate = credential.credentialMetadata.expirationDate;
        this.issuerLogo = credential.issuerCredential.logo
            ? `data:${credential.issuerCredential.logo.mimeType};base64,${credential.issuerCredential.logo.base64Content}`
            : null;
    }

    private changeLanguage(language: string, primaryLanguage: string) {
        if (language === null || language === undefined) {
            this.language = primaryLanguage;
            this.shareDataService.setToolbarLanguage(this.language);
            this.checkSession(this.language);
        }
    }

    private getCredentialDetailsXML(xml: Blob, language?: string): void {
        if (this.toolbarLanguage === undefined) {
            this.toolbarLanguage = '';
        }
        this.apiService
            .getCredentialDetail(xml, this.toolbarLanguage)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (data: EuropassCredentialPresentationView) => {
                    // data = getEuropassCredential();
                    this.addDetailsToSession(data);
                    this.changeLanguage(language, data.credentialMetadata.primaryLanguage);
                    this.availableLanguages =
                        data.credentialMetadata.availableLanguages;
                },
                (err) => {
                    this.displayErrorService.showNotificationText(
                        `${this.translateService.instant(err.error.message)}`,
                        `${this.translateService.instant(err.message)}`,
                        true
                    );
                    this.router.navigate(['home']);
                }
            );
    }

    private getCredentialDetailsUUID(language?: string) {
        this.apiService
            .getCredentialDetail_1(
                this.userId,
                this.credId,
                this.toolbarLanguage
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (data: EuropassCredentialPresentationView) => {
                    this.addDetailsToSession(data);
                    this.changeLanguage(language, data.credentialMetadata.primaryLanguage);
                    this.availableLanguages =
                        data.credentialMetadata.availableLanguages;
                },
                (err) => {
                    this.displayErrorService.showNotificationText(
                        `${this.translateService.instant(err.error.message)}`,
                        `${this.translateService.instant(err.message)}`,
                        true
                    );
                    this.router.navigate(['home']);
                }
            );
    }

    private getCredentialDetailShare(language?: string) {
        this.apiService
            .getSharedCredentialDetails(
                this.shareHash,
                this.toolbarLanguage
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (data: EuropassCredentialPresentationView) => {
                    this.addDetailsToSession(data);
                    this.changeLanguage(language, data.credentialMetadata.primaryLanguage);
                    this.availableLanguages =
                        data.credentialMetadata.availableLanguages;
                },
                (err) => {
                    this.displayErrorService.showNotificationText(
                        `${this.translateService.instant(err.error.message)}`,
                        `${this.translateService.instant(err.message)}`,
                        true
                    );
                    this.router.navigate(['home']);
                }
            );
    }

    private addDetailsToSession(
        credential: EuropassCredentialPresentationView
    ) {
        this.getHeader(credential);
        this.credential = credential;
        this.shareDataService.issuerCredential = credential.issuerCredential;
        this.shareDataService.issuerPresentation =
            credential.issuerPresentation;
        this.shareDataService.toolbarLanguage =
            credential.credentialMetadata.primaryLanguage;
        this.isDetailLoaded = true;
        this.reloadDetailsLanguage = false;
    }

    private getPrimaryLanguage(credentialType: string) {
        switch (credentialType) {
        case 'uuid':
            this.apiService
                .getWalletDiplomaHTML(
                    this.userId,
                    this.credId,
                    ''
                )
                .pipe(takeUntil(this.destroy$))
                .subscribe(
                    (data: EuropassDiplomaView) => {
                        this.diplomaImg = this.formatBase64String(data.html);
                        this.shareDataService.emitDiplomaImage(this.diplomaImg);
                        sessionStorage.setItem(
                            'diplomaImg',
                            JSON.stringify(this.diplomaImg)
                        );
                    },
                    () => {
                        this.shareDataService.emitDiplomaImage(null);
                        this.diplomaImg = null;
                        sessionStorage.removeItem('diplomaImg');
                    }
                );
            break;

        case 'sharedCredential':

            this.apiService
                .getSharedCredentialDiploma(
                    this.shareHash,
                    'en'
                )
                .pipe(takeUntil(this.destroy$))
                .subscribe(
                    (data: EuropassDiplomaView) => {
                        this.diplomaImg = this.formatBase64String(data.html);
                        this.shareDataService.emitDiplomaImage(this.diplomaImg);
                        sessionStorage.setItem(
                            'diplomaImg',
                            JSON.stringify(this.diplomaImg)
                        );
                        this.linkExpirationDate = data.expirationDate.toString();
                    },
                    () => {
                        this.shareDataService.emitDiplomaImage(null);
                        this.diplomaImg = null;
                        sessionStorage.removeItem('diplomaImg');
                    }
                );
            break;

        case 'XMLFile':
            this.apiService
                .getCredentialDetail(this.XMLFile, '')
                .pipe(takeUntil(this.destroy$))
                .subscribe(
                    (data: EuropassCredentialPresentationView) => {
                        // data = getEuropassCredential();
                        this.addDetailsToSession(data);
                        this.changeLanguage(null, data.credentialMetadata.primaryLanguage);
                        this.availableLanguages =
                            data.credentialMetadata.availableLanguages;
                    },
                    (err) => {
                        this.displayErrorService.showNotificationText(
                            `${this.translateService.instant(err.error.message)}`,
                            `${this.translateService.instant(err.message)}`,
                            true
                        );
                        this.router.navigate(['home']);
                    }
                );
            break;

        default:
            break;
        }
    }

    private checkSession(language?: string): void {
        this.isPreview = !!sessionStorage.getItem('isPreview');
        if (this.isShareView()) {
            // shareLink
            this.getDataFromShareLink(language);
        } else if (this.isView()) {
            // Wallet
            this.getDataFromView(language);
        } else {
            // upload || preview
            this.XMLFile = new Blob([sessionStorage.getItem('diplomaXML')], {
                type: 'text/xml',
            });
            if (language === '') {
                this.getPrimaryLanguage('XMLFile');
            } else {
                this.getDataFromXML(language);
            }
        }
    }

    private getDataFromXML(language?: string): void {
        this.getCredentialDetailsXML(this.XMLFile, language);
        if (this.toolbarLanguage !== '') {
            this.getDiplomaFromXML(this.XMLFile);
        }
    }

    private getDiplomaFromXML(XMLFile: Blob): void {
        this.apiService
            .getCredentialDiploma(XMLFile, this.toolbarLanguage)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (data: EuropassDiplomaView) => {
                    this.diplomaImg = this.formatBase64String(data.html);
                    this.shareDataService.emitDiplomaImage(this.diplomaImg);
                    sessionStorage.setItem(
                        'diplomaImg',
                        JSON.stringify(this.diplomaImg)
                    );
                },
                () => {
                    this.shareDataService.emitDiplomaImage(null);
                    this.diplomaImg = null;
                    sessionStorage.removeItem('diplomaImg');
                }
            );
    }

    private getDiplomaFromShareView(): void {
        this.apiService
            .getSharedCredentialDiploma(
                this.shareHash,
                this.toolbarLanguage
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (data: EuropassDiplomaView) => {
                    this.diplomaImg = this.formatBase64String(data.html);
                    this.shareDataService.emitDiplomaImage(this.diplomaImg);
                    sessionStorage.setItem(
                        'diplomaImg',
                        JSON.stringify(this.diplomaImg)
                    );
                    this.linkExpirationDate = data.expirationDate.toString();
                },
                () => {
                    this.shareDataService.emitDiplomaImage(null);
                    this.diplomaImg = null;
                    sessionStorage.removeItem('diplomaImg');
                }
            );
    }

    private getDiplomaFromUUID(): void {
        this.apiService
            .getWalletDiplomaHTML(
                this.userId,
                this.credId,
                this.toolbarLanguage
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (data: EuropassDiplomaView) => {
                    this.diplomaImg = this.formatBase64String(data.html);
                    this.shareDataService.emitDiplomaImage(this.diplomaImg);
                    sessionStorage.setItem(
                        'diplomaImg',
                        JSON.stringify(this.diplomaImg)
                    );
                },
                () => {
                    this.shareDataService.emitDiplomaImage(null);
                    this.diplomaImg = null;
                    sessionStorage.removeItem('diplomaImg');
                }
            );
    }

    private getDataFromView(language?: string): void {
        this.route.params
            .pipe(takeUntil(this.destroy$))
            .subscribe((params: Params) => {
                if (
                    (sessionStorage.getItem('credId') &&
                    sessionStorage.getItem('userId')) && (params['id'] === sessionStorage.getItem('userId') || !params['id'])
                ) {
                    this.credId = sessionStorage.getItem('credId');
                    this.userId = sessionStorage.getItem('userId');
                } else if (params['id'] && params['userId']) {
                    this.credId = params['id'];
                    this.userId = params['userId'];
                    sessionStorage.setItem('credId', params['id']);
                    sessionStorage.setItem('userId', params['userId']);
                }
                this.getCredentialDetailsUUID(language);
                if (language !== '') {
                    this.getDiplomaFromUUID();
                } else {
                    this.getPrimaryLanguage('uuid');
                }
            });
    }

    private getDataFromShareLink(language?: string): void {
        this.route.params
            .pipe(takeUntil(this.destroy$))
            .subscribe((params: Params) => {
                if (sessionStorage.getItem('shareLink')) {
                    this.shareHash = sessionStorage.getItem('shareLink');
                } else {
                    this.shareHash = params['shareLink'];
                    sessionStorage.setItem('shareLink', params['shareLink']);
                }
                this.getCredentialDetailShare(language);
                if (language !== '') {
                    this.getDiplomaFromShareView();
                } else {
                    this.getPrimaryLanguage('sharedCredential');
                }
            });
    }

    private isView(): boolean {
        return (
            this.router.url.includes('/view') ||
            (!!sessionStorage.getItem('credId') &&
                !!sessionStorage.getItem('userId'))
        );
    }

    private isShareView(): boolean {
        return (
            this.router.url.includes('/shareview')
             || !!sessionStorage.getItem('shareLink')
        );
    }

    private formatBase64String(html: string[]): string[] {
        const base64Formatted: string[] = [];
        html.forEach((base64String) => {
            base64Formatted.push(`data:image/jpg;base64,${base64String}`);
        });
        return base64Formatted;
    }

    private handleNavigationScroll(): void {
        this.router.events
            .pipe(
                filter(
                    (event) =>
                        event instanceof NavigationStart ||
                        event instanceof NavigationEnd
                )
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((event) => {
                event instanceof NavigationStart
                    ? (this.scrollPosition = [window.scrollX, window.scrollY])
                    : window.scrollTo(
                        this.scrollPosition[0],
                        this.scrollPosition[1]
                    );
            });
    }
}
