import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import {
    ActivatedRoute,
    Router,
    Params,
    NavigationStart,
    NavigationEnd,
} from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import {
    EuropassCredentialPresentationView,
    EuropassDiplomaView,
    V1Service,
} from 'src/app/shared/swagger';

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
    isDetailLoaded: boolean = false;
    reloadDetailsLanguage: boolean = false;
    language: string = this.translateService.currentLang;
    destroy$: Subject<boolean> = new Subject<boolean>();
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
        private translateService: TranslateService
    ) {}

    ngOnInit() {
        this.isDetailLoaded = false;
        this.handleNavigationScroll();
        this.checkSession();
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
            this.checkSession();
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

    private getCredentialDetailsXML(xml: Blob): void {
        this.apiService
            .getCredentialDetail(xml, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(
                (data: EuropassCredentialPresentationView) => {
                    // data = getEuropassCredential();
                    this.addDetailsToSession(data);
                    this.language = data.credentialMetadata.primaryLanguage;
                    this.availableLanguages =
                        data.credentialMetadata.availableLanguages;
                },
                () => {
                    this.router.navigate(['home']);
                }
            );
    }

    private getCredentialDetailsUUID() {
        this.apiService
            .getCredentialDetail_1(
                this.userId,
                this.credId,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (data: EuropassCredentialPresentationView) => {
                    this.addDetailsToSession(data);
                    this.language = data.credentialMetadata.primaryLanguage;
                    this.availableLanguages =
                        data.credentialMetadata.availableLanguages;
                },
                () => {
                    this.router.navigate(['home']);
                }
            );
    }

    private getCredentialDetailShare() {
        this.apiService
            .getSharedCredentialDetails(
                this.shareHash,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (data: EuropassCredentialPresentationView) => {
                    this.addDetailsToSession(data);
                    this.language = data.credentialMetadata.primaryLanguage;
                    this.availableLanguages =
                        data.credentialMetadata.availableLanguages;
                },
                () => {
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

    private checkSession(): void {
        // Wallet
        this.isPreview = !!sessionStorage.getItem('isPreview');
        if (this.isView()) {
            this.getDataFromView();
            // shareLink
        } else if (this.isShareView()) {
            this.getDataFromShareLink();
            // upload || preview
        } else {
            this.XMLFile = new Blob([sessionStorage.getItem('diplomaXML')], {
                type: 'text/xml',
            });
            this.getDataFromXML();
        }
    }

    private getDataFromXML(): void {
        this.getCredentialDetailsXML(this.XMLFile);
        this.getDiplomaFromXML(this.XMLFile);
    }

    private getDiplomaFromXML(XMLFile: Blob): void {
        this.apiService
            .getCredentialDiploma(XMLFile, this.translateService.currentLang)
            .takeUntil(this.destroy$)
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
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
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
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
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

    private getDataFromView(): void {
        this.route.params
            .takeUntil(this.destroy$)
            .subscribe((params: Params) => {
                if (
                    !!sessionStorage.getItem('credId') &&
                    !!sessionStorage.getItem('userId')
                ) {
                    this.credId = sessionStorage.getItem('credId');
                    this.userId = sessionStorage.getItem('userId');
                } else {
                    this.credId = params['id'];
                    this.userId = params['userId'];
                    sessionStorage.setItem('credId', params['id']);
                    sessionStorage.setItem('userId', params['userId']);
                }
                this.getCredentialDetailsUUID();
                this.getDiplomaFromUUID();
            });
    }

    private getDataFromShareLink(): void {
        this.route.params
            .takeUntil(this.destroy$)
            .subscribe((params: Params) => {
                if (!!sessionStorage.getItem('shareLink')) {
                    this.shareHash = sessionStorage.getItem('shareLink');
                } else {
                    this.shareHash = params['shareLink'];
                    sessionStorage.setItem('shareLink', params['shareLink']);
                }
                this.getCredentialDetailShare();
                this.getDiplomaFromShareView();
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
            this.router.url.includes('/shareview') ||
            !!sessionStorage.getItem('shareLink')
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
            .filter(
                (event) =>
                    event instanceof NavigationStart ||
                    event instanceof NavigationEnd
            )
            .takeUntil(this.destroy$)
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
