import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import {
    Router,
    NavigationStart,
    NavigationEnd
} from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import {
    EuropassCredentialPresentationView,
    EuropassDiplomaView
} from 'src/app/shared/swagger';
import { takeUntil, filter } from 'rxjs/operators';

@Component({
    selector: 'edci-viewer-diploma-details',
    templateUrl: './diploma-details.component.html',
    styleUrls: ['./diploma-details.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class DiplomaDetailsComponent implements OnInit, OnDestroy {
    diplomaImg: string[] = [];
    linkExpirationDate: string;
    issuerLogo: string;
    items: any[];
    header: any = {};
    isDetailLoaded = false;
    reloadDetailsLanguage = false;
    language: string = this.translateService.currentLang;
    toolbarLanguage: string = this.shareDataService.toolbarLanguage;
    destroy$: Subject<boolean> = new Subject<boolean>();
    availableLanguages: string[];
    credential: EuropassCredentialPresentationView;
    isPreview: boolean;
    scrollPosition: number[] = [0, 0];

    constructor(
        private router: Router,
        private shareDataService: ShareDataService,
        private translateService: TranslateService,
    ) { }

    ngOnInit() {
        this.getCredentialData(this.shareDataService.europassPresentationView, this.shareDataService.europassDiplomaView);
        this.handleNavigationScroll();

        this.shareDataService.closeSpinnerDialog();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onLanguageChange(languageCode: string): void {
        if (this.language !== languageCode) {
            this.reloadDetailsLanguage = true;
            this.shareDataService.toolbarLanguage = languageCode;
            this.shareDataService.changeToolbarLanguage(languageCode);
            this.shareDataService.doRefreshEuropassPresentationView().pipe(takeUntil(this.destroy$)).subscribe({
                next: (data) => {
                    let credDetails = data[0];
                    let diplomaDetails = data[1];
                    this.shareDataService.europassPresentationView = credDetails;
                    this.shareDataService.europassDiplomaView = diplomaDetails;
                    this.getCredentialData(credDetails, diplomaDetails, languageCode);
                }
            });
        }
    }

    onUploadNewCredential(): void {
        this.isDetailLoaded = false;
        this.shareDataService.doRefreshEuropassPresentationView().pipe(takeUntil(this.destroy$)).subscribe({
            next: (data) => {
                let credDetails = data[0];
                let diplomaDetails = data[1];
                this.shareDataService.toolbarLanguage = credDetails.credentialMetadata.primaryLanguage;
                this.shareDataService.changeToolbarLanguage(credDetails.credentialMetadata.primaryLanguage);
                this.shareDataService.europassPresentationView = credDetails;
                this.shareDataService.europassDiplomaView = diplomaDetails;
                this.getCredentialData(credDetails, diplomaDetails);
            }
        });
    }

    private getHeader(credential: EuropassCredentialPresentationView): void {

        this.header.title = credential.credentialMetadata.title;
        this.header.credentialType = credential.credentialMetadata.type;
        this.header.validFrom = credential.credentialMetadata.validFrom;
        this.header.expiryDate = credential.credentialMetadata.expirationDate;
        this.header.uuid = credential.credentialMetadata.uuid;
        this.header.issued = credential.credentialMetadata.issued;
        // logo is not present in design
        // this.issuerLogo = credential.issuerCredential.logo.content;
    }

    private getCredentialData(data?: EuropassCredentialPresentationView, diploma?: EuropassDiplomaView, language?: string): void {
        this.getHeader(data);
        this.credential = data;
        this.language = this.shareDataService.toolbarLanguage;
        this.availableLanguages = data.credentialMetadata.availableLanguages;
        this.diplomaImg = diploma.base64DiplomaImages;

        this.shareDataService.issuerCredential = data.issuerCredential;
        this.shareDataService.issuerPresentation = data.issuerPresentation;

        if (diploma.expirationDate) {
            this.linkExpirationDate = diploma.expirationDate.toString();
        }

        this.reloadDetailsLanguage = false;
        this.isDetailLoaded = true;
    }

    private handleNavigationScroll(): void {
        this.router.events
            .pipe(
                filter(
                    (event) =>
                        event instanceof NavigationStart ||
                        event instanceof NavigationEnd
                )).pipe(takeUntil(this.destroy$))
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
