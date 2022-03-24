import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import {
    CachePreventionInterceptor,
    CoreModule as UxCoreModule,
    CorsSecurityInterceptor,
    CsrfPreventionInterceptor,
    EuLoginSessionTimeoutHandlingInterceptor,
    translateConfig,
} from '@eui/core';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import {
    TranslateModule,
    MissingTranslationHandler,
    MissingTranslationHandlerParams,
} from '@ngx-translate/core';
import { V1Service } from '@shared/swagger';
import { appConfig } from '../../config';
import { environment } from '@environments/environment';
import { SharedModule } from '@shared/shared.module';
import { FooterComponent } from './components/app-shell/footer/footer.component';
import { getReducers, metaReducers, REDUCER_TOKEN } from './reducers/index';
import { AppShellComponent } from './components/app-shell/app-shell.component';
import * as enJSON from 'assets/i18n/en.json';
import { BASE_PATH } from './../shared/swagger/variables';

const commonProviders = [
    {
        provide: REDUCER_TOKEN,
        deps: [],
        useFactory: getReducers,
    },
    {
        provide: HTTP_INTERCEPTORS,
        useClass: EuLoginSessionTimeoutHandlingInterceptor,
        multi: true,
    },
    {
        provide: HTTP_INTERCEPTORS,
        useClass: CsrfPreventionInterceptor,
        multi: true,
    },
    {
        provide: BASE_PATH,
        useValue: environment.apiBaseUrl,
    },
    V1Service,
];

const productionProviders = [
    {
        provide: HTTP_INTERCEPTORS,
        useClass: CorsSecurityInterceptor,
        multi: true,
    },
    {
        provide: HTTP_INTERCEPTORS,
        useClass: CachePreventionInterceptor,
        multi: true,
    },
    {
        provide: REDUCER_TOKEN,
        deps: [],
        useFactory: getReducers,
    },
    {
        provide: HTTP_INTERCEPTORS,
        useClass: EuLoginSessionTimeoutHandlingInterceptor,
        multi: true,
    },
    {
        provide: HTTP_INTERCEPTORS,
        useClass: CsrfPreventionInterceptor,
        multi: true,
    },
    {
        provide: BASE_PATH,
        useValue: environment.apiBaseUrl,
    },
    V1Service,
];
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
        UxCoreModule.forRoot({
            appConfig: appConfig,
            environment: environment,
        }),
        TranslateModule.forRoot(translateConfig),
        StoreModule.forRoot(REDUCER_TOKEN, { metaReducers }),
        !environment.production
            ? StoreDevtoolsModule.instrument({ maxAge: 50 })
            : [],
    ],
    declarations: [FooterComponent, AppShellComponent],
    exports: [SharedModule, AppShellComponent],
    providers: [environment.production ? productionProviders : commonProviders],
})
export class CoreModule {}
