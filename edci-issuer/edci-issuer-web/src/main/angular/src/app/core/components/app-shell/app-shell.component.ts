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
import { UxAppShellService, UxEuLanguages, UxLink, UxService } from '@eui/core';
import { EclAppShellLanguageChangeEvent } from '@eui/ecl-core';
import { EclLanguage } from '@eui/ecl-core/lib/model/ecl-language.model';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { ApiService } from '@services/api.service';
import { IssuerService } from '@services/issuer.service';
import { UserDetailsView, V1Service } from '@shared/swagger';
import { fromEvent } from 'rxjs/observable/fromEvent';
import { debounceTime, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs/Subject';
import { Subscription } from 'rxjs/Subscription';
import { FooterMenuLink } from './footer/footer-link.model';

@Component({
    selector: 'edci-app-shell',
    templateUrl: './app-shell.component.html',
    styleUrls: ['./app-shell.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AppShellComponent implements OnInit, AfterViewInit, OnDestroy {
    languages: Array<EclLanguage> = this.issuerService.addMissingLanguages(
        UxEuLanguages.getLanguages()
    );

    @Input() showExtraInfo: boolean = false;
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
        public uxService: UxService,
        public uxAppShellService: UxAppShellService,
        public translateService: TranslateService,
        private issuerService: IssuerService,
        private renderer: Renderer2,
        private api: V1Service,
        private apiService: ApiService
    ) {
        this.addAttributesToCustomImgTag();
        this.api
            .getUserDetails(this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe((userDetails: UserDetailsView) => {
                this.userInfo = userDetails;
                this.issuerService.userInfo = userDetails;
            });
        this.translateService.onLangChange
            .takeUntil(this.destroy$)
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
        if (environment.hasLabelsOnly) {
            this.languages.push({
                code: 'lo',
                label: 'Labels Only',
            });
        }
        this.windowResizedSubscription = fromEvent(window, 'resize')
            .pipe(debounceTime(200), takeUntil(this.destroy$))
            .subscribe((event: Event) => {
                this.uxAppShellService.setState({
                    ...this.uxAppShellService.state,
                    windowHeight: window.innerHeight,
                    windowWidth: window.innerWidth,
                });
            });
        this.issuerService.userDetails.takeUntil(this.destroy$).subscribe(
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

    onLanguageChanged(event: EclAppShellLanguageChangeEvent) {
        this.translateService.use(event.language.code);
        if (event.language.code === 'sr') {
            this.changeSerbianLabel();
        }
    }

    onLogin(): void {
        window.location.href = `${this.basePath}${
            environment.loginUrl
        }?redirectURI=${encodeURIComponent(window.location.href)}`;
    }

    onLogout(): void {
        this.apiService
            .doPost(environment.apiBaseUrl + environment.logoutUrl, null)
            .takeUntil(this.destroy$)
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
     * Change the header logo depending on the language provided
     */
    private setLogoDisplay(): void {
        if (this.selectedLanguage) {
            const language = this.selectedLanguage.toUpperCase();
            this.renderer.setAttribute(
                this.logoEuropass,
                'src',
                `${environment.headerImagePath}${language}.svg`
            );
        }
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
