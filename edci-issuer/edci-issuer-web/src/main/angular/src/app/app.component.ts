import { Component, Renderer2 } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { environment } from '@environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { IssuerService } from '@services/issuer.service';
import cssVars from 'css-vars-ponyfill';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
})
export class AppComponent {
    showExtraInfo = false;
    constructor(
        private issuerService: IssuerService,
        private renderer: Renderer2,
        private translateService: TranslateService,
        private router: Router
    ) {
        cssVars();
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
        this.router.events.subscribe((val) => {
            if (val instanceof NavigationEnd) {
                this.issuerService.breadcrumbSubject.next(
                    val.urlAfterRedirects
                );
                this.showExtraInfo = val.urlAfterRedirects === '/home';
            }
        });
    }

    addJsToElement(src: string): HTMLScriptElement {
        const script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = src;
        this.renderer.appendChild(document.body, script);
        return script;
    }
}
