import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { NavigationEnd, NavigationStart, Router } from '@angular/router';
import { UxDynamicModalConfig, UxDynamicModalService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { EntityModalComponent } from 'src/app/shared/components/entity-modal/entity-modal.component';
import { EntityModalConfiguration } from 'src/app/shared/components/entity-modal/entity-modal.configuration';
import { EntityModalInformation } from 'src/app/shared/model/entityModalInformation';
import {
    EuropassCredentialPresentationView,
    V1Service,
} from 'src/app/shared/swagger';

@Component({
    selector: 'edci-viewer-diploma-details',
    templateUrl: './diploma-details.component.html',
    styleUrls: ['./diploma-details.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class DiplomaDetailsComponent implements OnInit, OnDestroy {
    diplomaImg: string = sessionStorage.getItem('diplomaImg');
    linkExpirationDate: string = sessionStorage.getItem('linkExpirationDate');
    credId: string = sessionStorage.getItem('credId') || null;
    userId: string = sessionStorage.getItem('userId') || null;
    shareHash: string = sessionStorage.getItem('shareLink') || null;
    issuerLogo: string;
    XMLFile: Blob;
    items: any[];
    header: any = {};
    isDiplomaDisplayed: boolean = true;
    isDetailLoaded: boolean = false;
    reloadDetailsLanguage: boolean = false;
    language: string = this.translateService.currentLang;
    destroy$: Subject<boolean> = new Subject<boolean>();
    primaryLanguage: string;
    availableLanguages: string[];
    type: string;
    credential: EuropassCredentialPresentationView;
    scrollPosition: number[] = [0, 0];

    constructor(
        private apiService: V1Service,
        private translateService: TranslateService,
        private router: Router,
        private uxDynamicModalService: UxDynamicModalService,
        private entityLinkService: EntityLinkService,
        private shareDataService: ShareDataService
    ) {}

    ngOnInit() {
        this.handleNavigationScroll();
        if (this.shareDataService.toolbarLanguage) {
            this.language = this.shareDataService.toolbarLanguage;
            this.checkSession(true);
        } else {
            this.language = this.translateService.currentLang;
            this.checkSession();
        }
        this.type = sessionStorage.getItem('diplomaType');
        // this.openEntityModal();
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
            this.checkSession(true);
        }
    }

    private getHeader(credential: EuropassCredentialPresentationView): void {
        this.header.title = credential.credentialMetadata.title;
        this.header.credentialType = credential.credentialMetadata.type;
        this.header.issuanceDate = credential.credentialMetadata.issuanceDate;
        this.header.expiryDate = credential.credentialMetadata.expirationDate;
        this.issuerLogo = credential.issuerCredential.logo
            ? `data:${credential.issuerCredential.logo.mimeType};base64,${credential.issuerCredential.logo.base64Content}`
            : null;
    }

    private setCredentialDetailsXML(
        xml: Blob,
        language: string,
        byNewLang: boolean = false
    ): void {
        this.apiService
            .getCredentialDetail(xml, language)
            .takeUntil(this.destroy$)
            .subscribe(
                (data: EuropassCredentialPresentationView) => {
                    this.addDetailsToSession(data);
                    if (!byNewLang) {
                        this.primaryLanguage =
                            data.credentialMetadata.primaryLanguage;
                        this.language = this.primaryLanguage;
                        this.shareDataService.toolbarLanguage = this.language;
                    }
                    this.availableLanguages =
                        data.credentialMetadata.availableLanguages;
                },
                (response: HttpErrorResponse) => {
                    this.router.navigate(['diploma']);
                }
            );
    }

    private setCredentialDetailsUUID() {
        this.apiService
            .getCredentialDetail_1(this.userId, this.credId, this.language)
            .takeUntil(this.destroy$)
            .subscribe(
                (data: EuropassCredentialPresentationView) => {
                    this.addDetailsToSession(data);
                    this.primaryLanguage =
                        data.credentialMetadata.primaryLanguage;
                    this.availableLanguages =
                        data.credentialMetadata.availableLanguages;
                },
                (response: HttpErrorResponse) => {
                    this.router.navigate(['diploma']);
                }
            );
    }

    private setCredentialDetailShare() {
        this.apiService
            .getSharedCredentialDetails(this.shareHash, this.language)
            .takeUntil(this.destroy$)
            .subscribe(
                (data: EuropassCredentialPresentationView) => {
                    this.addDetailsToSession(data);
                    this.primaryLanguage =
                        data.credentialMetadata.primaryLanguage;
                    this.availableLanguages =
                        data.credentialMetadata.availableLanguages;
                },
                (response: HttpErrorResponse) => {
                    this.router.navigate(['diploma']);
                }
            );
    }

    private addDetailsToSession(
        credential: EuropassCredentialPresentationView
    ) {
        this.setLabels(credential);
        this.credential = credential;
        this.shareDataService.achievements = credential.achievements;
        this.shareDataService.activities = credential.activities;
        this.shareDataService.credentialSubject = credential.credentialSubject;
        this.shareDataService.entitlements = credential.entitlements;
        this.shareDataService.issuerCredential = credential.issuerCredential;
        this.shareDataService.issuerPresentation =
            credential.issuerPresentation;
        this.shareDataService.subCredentials = credential.subCredentials;
        this.getHeader(credential);
        this.isDetailLoaded = true;
        this.reloadDetailsLanguage = false;
    }

    private setLabels(credential: EuropassCredentialPresentationView): void {
        this.items = [
            {
                label: 'details.organisations',
                url: 'issuing-organization',
                isDisplayed: this.showOrganizationTab(credential),
            },
            {
                label: 'details.verification',
                url: 'verification',
                isDisplayed: false,
            },
            {
                label: 'details.credential-owner',
                url: 'credential-owner',
                isDisplayed: this.showCredentialOwnerTab(credential),
            },
            {
                label: 'details.achievements',
                url: 'achievements',
                isDisplayed: this.showAchievementsTab(credential),
            },
            {
                label: 'details.activities',
                url: 'activities',
                isDisplayed: this.showActivitiesTab(credential),
            },
            {
                label: 'details.entitlements',
                url: 'entitlements',
                isDisplayed: this.showEntitlementsTab(credential),
            },
            {
                label: 'details.sub-credentials',
                url: 'sub-credentials',
                isDisplayed: this.showSubCredentialsTab(credential),
            },
        ];
    }

    private checkSession(byNewLang: boolean = false): void {
        // Wallet
        if (this.credId && this.userId) {
            this.setCredentialDetailsUUID();
            // shareLink
        } else if (this.shareHash) {
            this.setCredentialDetailShare();
            // upload || preview
        } else {
            this.XMLFile = new Blob([sessionStorage.getItem('diplomaXML')], {
                type: 'text/xml',
            });
            this.setCredentialDetailsXML(
                this.XMLFile,
                this.language,
                byNewLang
            );
        }
    }

    private showOrganizationTab(
        credential: EuropassCredentialPresentationView
    ): boolean {
        return !!credential.issuerCredential;
    }

    private showCredentialOwnerTab(
        credential: EuropassCredentialPresentationView
    ): boolean {
        return !!credential.credentialSubject;
    }

    private showAchievementsTab(
        credential: EuropassCredentialPresentationView
    ): boolean {
        return this.get(credential, 'achievements') > 0;
    }

    private showActivitiesTab(
        credential: EuropassCredentialPresentationView
    ): boolean {
        return this.get(credential, 'activities') > 0;
    }

    private showEntitlementsTab(
        credential: EuropassCredentialPresentationView
    ): boolean {
        return this.get(credential, 'entitlements') > 0;
    }

    private get(credential: any, entity: string): number {
        return credential && credential[entity] && credential[entity].length
            ? credential[entity].length
            : 0;
    }

    private showSubCredentialsTab(
        credential: EuropassCredentialPresentationView
    ): boolean {
        // TODO: Anthony review
        // return _get(credential, 'subCredentials.length', 0) > 0 ? true : false;
        return false;
    }

    private openEntityModal(): void {
        this.entityLinkService.newModalInformation$
            .takeUntil(this.destroy$)
            .subscribe((modalInformation: EntityModalInformation) => {
                this.shareDataService.modalsOpen++;
                const config = new UxDynamicModalConfig({
                    id: modalInformation.id,
                    titleLabel: modalInformation.modalTitle,
                    isSizeSmall: true,
                    isFooterVisible: false,
                    styleClass: 'modal-close-focus',
                    bodyInjectedComponent: {
                        component: EntityModalComponent,
                        config: new EntityModalConfiguration({
                            modalInformation: modalInformation,
                        }),
                    },
                    onClose: (portalHostRef, portalRef) => {
                        this.shareDataService.modalsOpen--;
                        this.uxDynamicModalService.closeModal(
                            portalHostRef,
                            portalRef
                        );
                    },
                });
                this.uxDynamicModalService.openModal(config);
            });
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
