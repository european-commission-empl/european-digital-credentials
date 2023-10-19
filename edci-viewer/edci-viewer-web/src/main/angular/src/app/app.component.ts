import { ShareDataService } from 'src/app/core/services/share-data.service';
import {
    AfterViewInit,
    Component,
    OnDestroy,
    OnInit,
    Renderer2,
    ViewEncapsulation,
} from '@angular/core';
import { NavigationEnd, NavigationStart, Router, Scroll } from '@angular/router';
import { I18nState } from '@eui/base/lib/eui-models/eui-store/state/i18n.state';
import { I18nService } from '@eui/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { UserDetailsView, V1Service } from '@shared/swagger';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { ViewerService } from './core/services/viewer.service';

@Component({
    selector: 'edci-viewer-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AppComponent implements OnInit, OnDestroy, AfterViewInit {
    logoEuropass: Node = this.renderer.createElement('img');
    showExtraInfo = false;
    europassRoot: string = environment.europassRoot;
    issuerUrl: string = environment.issuerBaseUrl;
    hasBranding: boolean = environment.hasBranding;
    homeMainTitle: string = environment.homeMainTitle;
    homeMainDescription: string = environment.homeMainDescription;
    selectedLanguage: string = this.translateService.currentLang;
    destroy$: Subject<boolean> = new Subject<boolean>();
    userInfo: UserDetailsView;

    private basePath: string = environment.apiBaseUrl;
    constructor(
        private translateService: TranslateService,
        private viewerService: ViewerService,
        private router: Router,
        private renderer: Renderer2,
        private apiService: V1Service,
        private i18nService: I18nService,
        private shareDataService: ShareDataService
    ) {
        if (this.selectedLanguage === undefined) {
            const lang: I18nState = {
                activeLang: 'en',
            };
            this.i18nService.updateState(lang);
            this.translateService.setDefaultLang('en');
            this.i18nService.getState().subscribe((langReceived: any) => {
                this.translateService.use(langReceived['activeLang']);
            });
        }
        /**
         * Set the attribute lang so the cookie consent kit (CCK) can
         * display the language selected at the start of the navigation
         */
        this.renderer.setAttribute(
            document.getElementsByTagName('html')[0],
            'lang',
            this.translateService.currentLang
        );
        if (environment.hasBranding) {
            this.addJsToElement(
                'https://ec.europa.eu/wel/cookie-consent/consent.js'
            );
            this.addJsToElement(
                'https://europa.eu/webtools/load.js?globan=1110'
            );
        }
        this.apiService
            .getUserDetails(this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe((userDetails: UserDetailsView) => {
                this.userInfo = userDetails;
                this.viewerService.userInfo = userDetails;
            });
        this.addAttributesToCustomImgTag();
        this.router.events.subscribe((val) => {
            if (val instanceof NavigationEnd) {
                this.showExtraInfo = val.urlAfterRedirects === '/home';
            }
        });
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.selectedLanguage = event.lang;
                this.setLogoDisplay();
            });
    }

    ngOnInit() {
        /* this.viewerService.userDetails.pipe(takeUntil(this.destroy$)).subscribe(
            (userDetails: UserDetailsView) => {
                this.userInfo = userDetails;
            },
            err => {}
        );
        this.translateService.reloadLang('es'); */
    }

    ngAfterViewInit() {
        this.removeSearchBar();
        this.headerLogoSwap();
        this.getPreviewDiploma();
        if (this.translateService.currentLang === 'sr') {
            this.changeSerbianLabel();
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    addJsToElement(src: string): HTMLScriptElement {
        const script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = src;
        this.renderer.appendChild(document.body, script);
        return script;
    }

    onLanguageChanged(event: any) {
        this.translateService.use(event.language.code);
        if (this.translateService.currentLang === 'sr') {
            this.changeSerbianLabel();
        }
    }

    navigateToIssuer(): void {
        window.location.href = this.issuerUrl;
    }

    /**
     * Log-in / Log-out methods
     */
    onLogin(): void {
        window.location.href = `${environment.viewerBaseUrl}${environment.loginUrl
            }?redirectURI=${encodeURIComponent(window.location.href)}`;
    }

    onLogout(): void {
        this.viewerService
            .doPost(environment.viewerBaseUrl + environment.logoutUrl, null)
            .pipe(takeUntil(this.destroy$))
            .subscribe((res) => (window.location.href = res.redirectUrl));
    }

    /**
     * Change the header logo depending on the language provided
     */
    private setLogoDisplay(): void {
        if (this.selectedLanguage) {
            const language = this.selectedLanguage.toUpperCase();
            let imgSrc = `${environment.headerImagePath}${language}.svg`;
            if (!environment.hasBranding) {
                imgSrc = environment.headerImagePath;
            }
            this.renderer.setAttribute(this.logoEuropass, 'src', imgSrc);
        }
    }

    /**
     * Coming from /viewer/preview (issuer). Credential XML on body, get the XML and
     * remove credential from DOM finally redirect to diploma
     */
    private getPreviewDiploma(): void {
        this.router.events.pipe(takeUntil(this.destroy$)).subscribe((event) => {
            if (event instanceof NavigationStart) {
                if (event.url === '/') {
                    const element = document.getElementById('cred-wrapper');
                    if (element.children[0].id === 'cred') {
                        this.shareDataService.doStorageFullClear();
                        this.shareDataService.openSpinnerDialog();
                        const content = element.children[0] as HTMLElement;
                        this.shareDataService.diplomaJSON = content.textContent;
                        this.renderer.removeChild(document.body, element);
                        this.shareDataService.isPreview = true;
                        this.shareDataService.getUploadDetails('').subscribe({
                            complete: () => {
                                let jsonFile = new Blob([this.shareDataService.diplomaJSON], {
                                    type: 'application/ld+json',
                                });

                                this.shareDataService.setPresentationViewObs(
                                    (lang: string) => this.apiService.getCredentialDetail(jsonFile, lang)
                                );

                                this.shareDataService.setDiplomaViewObs(
                                    (lang: string) => this.apiService.getCredentialDiploma(jsonFile, lang)
                                );
                                this.router.navigate(['diploma-details']);
                            }
                        });

                    }
                }
            }
        });
    }

    /**
     * Swap the default img tag from eUI/ECL header to our custom one
     */
    private headerLogoSwap(): void {
        this.renderer.removeChild(
            document.getElementsByClassName('eui-header-logo-wrapper')[0],
            document.getElementsByClassName('eui-header-logo')[0]
        );
        this.renderer.insertBefore(
            document.getElementsByClassName('eui-header-logo-wrapper')[0],
            this.logoEuropass,
            document.getElementsByClassName('eui-header-logo')[0]
        );
    }

    private removeSearchBar(): void {
        if (
            document.getElementsByClassName(
                'ecl-site-header-core__search-container'
            )[0] !== undefined
        ) {
            this.renderer.removeChild(
                document.getElementsByClassName(
                    'ecl-site-header-core__action'
                )[0],
                document.getElementsByClassName(
                    'ecl-site-header-core__search-container'
                )[0]
            );
        }
    }

    /**
     * Added attributes, classes and event listeners needed on the img tag for the logo
     */
    private addAttributesToCustomImgTag(): void {
        this.setLogoDisplay();
        this.renderer.addClass(
            this.logoEuropass,
            'ecl-site-header-core__logo-image'
        );
        this.renderer.listen(this.logoEuropass, 'click', (event) => {
            window.location.href = `${this.europassRoot}/${this.selectedLanguage}`;
        });
    }

    private changeSerbianLabel(): void {
        const languageSelectorLabel = document.getElementsByClassName(
            'ecl-site-header__selector-link'
        )[0];
        if (languageSelectorLabel) {
            setTimeout(
                () => (languageSelectorLabel.childNodes[0]['data'] = 'srpski'),
                0
            );
        }
    }
}
