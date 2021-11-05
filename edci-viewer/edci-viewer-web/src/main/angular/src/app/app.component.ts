import {
    AfterViewInit,
    Component,
    OnDestroy,
    OnInit,
    Renderer2,
    ViewEncapsulation,
} from '@angular/core';
import { NavigationEnd, NavigationStart, Router } from '@angular/router';
import { EclAppShellLanguageChangeEvent } from '@eui/ecl-core';
import { EclLanguage } from '@eui/ecl-core/lib/model/ecl-language.model';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ViewerService } from './core/services/viewer.service';
import { UserDetailsView, V1Service } from './shared/swagger';

@Component({
    selector: 'edci-viewer-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AppComponent implements AfterViewInit, OnDestroy, OnInit {
    // languages: Array<EclLanguage> = this.viewerService.addMissingLanguages(
    //     UxEuLanguages.getLanguages()
    // );
    languages: Array<EclLanguage> = [{ code: 'en', label: 'English' }];

    logoEuropass: Node = this.renderer.createElement('img');
    showExtraInfo: boolean = false;
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
        private api: V1Service
    ) {
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
        if (environment.hasLabelsOnly) {
            this.languages.push({
                code: 'lo',
                label: 'Labels Only',
            });
        }
        this.api
            .getUserDetails(this.translateService.currentLang)
            .takeUntil(this.destroy$)
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
            .takeUntil(this.destroy$)
            .subscribe((event: LangChangeEvent) => {
                this.selectedLanguage = event.lang;
                this.setLogoDisplay();
            });
    }

    ngOnInit() {
        /*this.viewerService.userDetails.takeUntil(this.destroy$).subscribe(
            (userDetails: UserDetailsView) => {
                this.userInfo = userDetails;
            },
            err => {}
        );*/
    }

    ngAfterViewInit() {
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

    onLanguageChanged(event: EclAppShellLanguageChangeEvent) {
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
        window.location.href = `${environment.viewerBaseUrl}${
            environment.loginUrl
        }?redirectURI=${encodeURIComponent(window.location.href)}`;
    }

    onLogout(): void {
        this.viewerService
            .doPost(environment.viewerBaseUrl + environment.logoutUrl, null)
            .takeUntil(this.destroy$)
            .subscribe((res) => (window.location.href = res.redirectUrl));
    }

    /**
     * Change the header logo depending on the language provided
     */
    private setLogoDisplay(): void {
        const language = this.selectedLanguage.toUpperCase();
        let imgSrc = `${environment.headerImagePath}${language}.svg`;
        if (!this.hasBranding) {
            imgSrc = environment.headerImagePath;
        }
        this.renderer.setAttribute(this.logoEuropass, 'src', imgSrc);
    }

    /**
     * Coming from /viewer/preview (issuer). Credential XML on body, get the XML and
     * remove credential from DOM finally redirect to diploma
     */
    private getPreviewDiploma(): void {
        this.router.events.takeUntil(this.destroy$).subscribe((event) => {
            if (event instanceof NavigationStart) {
                if (event.url === '/') {
                    const element = document.getElementById('cred-wrapper');
                    if (element.children[0].id === 'cred') {
                        sessionStorage.clear();
                        const content = element.children[0] as HTMLElement;
                        sessionStorage.setItem(
                            'diplomaXML',
                            content.textContent
                        );
                        this.renderer.removeChild(document.body, element);
                        sessionStorage.setItem('isPreview', 'true');
                        this.router.navigate(['diploma']);
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
            document.getElementsByClassName('ecl-site-header__banner')[0],
            document.getElementsByClassName('ecl-link--standalone')[0]
        );
        this.renderer.insertBefore(
            document.getElementsByClassName('ecl-site-header__banner')[0],
            this.logoEuropass,
            document.getElementsByClassName('ecl-site-header__selector')[0]
        );
    }

    /**
     * Added attributes, classes and event listeners needed on the img tag for the logo
     */
    private addAttributesToCustomImgTag(): void {
        this.setLogoDisplay();
        this.renderer.addClass(this.logoEuropass, 'edci-custom-europass__logo');
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
