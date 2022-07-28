import {
    AfterViewInit,
    Component,
    Input,
    OnDestroy,
    OnInit,
    Renderer2,
    ViewEncapsulation,
} from '@angular/core';
import { environment } from '@environments/environment';
import { I18nState } from '@eui/base/lib/eui-models/eui-store/state/i18n.state';
import { I18nService, UxAppShellService, UxLink } from '@eui/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { ApiService } from '@services/api.service';
import { IssuerService } from '@services/issuer.service';
import { UserDetailsView, V1Service } from '@shared/swagger';
import { fromEvent, Subject, Subscription } from 'rxjs';
import { debounceTime, takeUntil } from 'rxjs/operators';
import { FooterMenuLink } from './footer/footer-link.model';

@Component({
    selector: 'edci-app-shell',
    templateUrl: './app-shell.component.html',
    styleUrls: ['./app-shell.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AppShellComponent implements OnInit, AfterViewInit, OnDestroy {
    @Input() showExtraInfo: boolean;

    logoEuropass: Node = this.renderer.createElement('img');
    europassRoot: string = environment.europassRoot;
    selectedLanguage: string = this.translateService.currentLang;
    footerMainLinks: FooterMenuLink[] = [];
    footerSecondaryLinks: FooterMenuLink[] = [];
    footerOtherLinks: UxLink[] = [];
    userInfo: UserDetailsView;
    logoutUrl: string = environment.apiBaseUrl + environment.logoutUrl;
    hasBranding: boolean = environment.hasBranding;
    homeMainTitle: string = environment.homeMainTitle;
    homeMainDescription: string = environment.homeMainDescription;

    private basePath: string = environment.apiBaseUrl;
    private viewerUrl: string = environment.viewerBaseUrl;
    private windowResizedSubscription: Subscription;
    private destroy$: Subject<boolean> = new Subject<boolean>();
    constructor(
        public uxAppShellService: UxAppShellService,
        public translateService: TranslateService,
        private issuerService: IssuerService,
        private renderer: Renderer2,
        private api: V1Service,
        private apiService: ApiService,
        private i18nService: I18nService
    ) {
        let lang: I18nState = {
            activeLang: 'en',
        };
        this.i18nService.updateState(lang);
        this.translateService.setDefaultLang('en');
        this.i18nService.getState().subscribe((langReceived: any) => {
            this.translateService.use(langReceived['activeLang']);
        });
        this.addAttributesToCustomImgTag();
        this.api
            .getUserDetails(this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe((userDetails: UserDetailsView) => {
                this.userInfo = userDetails;
                this.issuerService.userInfo = userDetails;
            });

        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.selectedLanguage = event.lang;
                this.setLogoDisplay();
            });
    }

    ngAfterViewInit() {
        this.headerLogoSwap();
        if (this.translateService.currentLang === 'sr') {
            this.changeSerbianLabel();
        }
    }

    ngOnInit() {
        this.windowResizedSubscription = fromEvent(window, 'resize')
            .pipe(debounceTime(200), takeUntil(this.destroy$))
            .subscribe((event: Event) => {
                this.uxAppShellService.setState({
                    ...this.uxAppShellService.state,
                    windowHeight: window.innerHeight,
                    windowWidth: window.innerWidth,
                });
            });
        this.issuerService.userDetails.pipe(takeUntil(this.destroy$)).subscribe(
            (userDetails: UserDetailsView) => {
                this.userInfo = userDetails;
            },
            (err) => {}
        );
    }

    ngOnDestroy() {
        if (this.windowResizedSubscription) {
            this.windowResizedSubscription.unsubscribe();
        }
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onLanguageChanged(event: any /*EclAppShellLanguageChangeEvent*/) {
        this.translateService.use(event.language.code);
        if (event.language.code === 'sr') {
            this.changeSerbianLabel();
        }
    }

    onLogin(): void {
        window.location.href = `${environment.issuerBaseUrl}${
            environment.loginUrl
        }?redirectURI=${encodeURIComponent(window.location.href)}`;
    }

    onLogout(): void {
        this.apiService
            .doPost(environment.issuerBaseUrl + environment.logoutUrl, null)
            .pipe(takeUntil(this.destroy$))
            .subscribe((res) => (window.location.href = res.redirectUrl));
    }

    navigateToViewer(): void {
        window.location.href = this.viewerUrl;
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

    /**
     * Change the header logo depending on the language provided
     */
    private setLogoDisplay(): void {
        this.selectedLanguage = 'en';
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
