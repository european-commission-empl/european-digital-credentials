import { ClipboardModule } from '@angular/cdk/clipboard';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import {
    CachePreventionInterceptor,
    CoreModule as EuiCoreModule,
    CoreModuleEffects,
    CorsSecurityInterceptor,
    CsrfPreventionInterceptor,
    EUI_CONFIG_TOKEN,
    translateConfig
} from '@eui/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import {
    MissingTranslationHandler,
    MissingTranslationHandlerParams,
    TranslateModule
} from '@ngx-translate/core';
import { appConfig } from '../../config/index';
import { environment } from '../../environments/environment';
import { SharedModule } from '../shared/shared.module';
import { V1Service } from '../shared/swagger';
import * as enJSON from './../../assets/i18n/en.json';
import { AppShellComponent } from './components/app-shell/app-shell.component';
import { FooterComponent } from './components/app-shell/footer/footer.component';
import { getReducers, metaReducers, REDUCER_TOKEN } from './reducers/index';

export class MyMissingTranslationHandler implements MissingTranslationHandler {
    constructor() {}

    handle(params: MissingTranslationHandlerParams) {
        return enJSON['default'][params.key];
    }
}

translateConfig['missingTranslationHandler'] = {
    provide: MissingTranslationHandler,
    useClass: MyMissingTranslationHandler,
};

@NgModule({
    imports: [
        SharedModule,
        EuiCoreModule.forRoot(),
        EffectsModule.forRoot([...CoreModuleEffects]),
        TranslateModule.forRoot(translateConfig),
        StoreModule.forRoot(REDUCER_TOKEN, { metaReducers }),
        ClipboardModule,
    ],
    declarations: [FooterComponent, AppShellComponent],
    exports: [SharedModule, AppShellComponent],
    providers: [
        {
            provide: REDUCER_TOKEN,
            deps: [],
            useFactory: getReducers,
        },
        {
            provide: EUI_CONFIG_TOKEN,
            useValue: { appConfig: appConfig, environment: environment },
        },
        {
            // Sets the withCredentials on Ajax Request to send the JSESSIONID cookie to another domain.
            // This is necessary when a request is being made to another domain that is protected by EU Login.
            provide: HTTP_INTERCEPTORS,
            useClass: CorsSecurityInterceptor,
            multi: true,
        },
        {
            // Adds HTTP header to each Ajax request that ensures the request is set by a piece of JavaScript code in the application.
            // This prevents dynamically-loaded content from forging a request in the name of the currently logged-in user.
            // Be aware that this assumes that cross-site scripting (XSS) is already put in place, (default setting in Angular).
            provide: HTTP_INTERCEPTORS,
            useClass: CsrfPreventionInterceptor,
            multi: true,
        },
        {
            // Asks the intermediate proxies not to return a cache copy of the resource.
            // In matter of fact forces each server in the chain to validate the freshness of the resource.
            provide: HTTP_INTERCEPTORS,
            useClass: CachePreventionInterceptor,
            multi: true,
        },
        V1Service,
    ],
})
export class CoreModule {}
