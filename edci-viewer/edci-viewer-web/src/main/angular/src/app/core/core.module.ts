import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';

import { HTTP_INTERCEPTORS } from '@angular/common/http';
import {
    CachePreventionInterceptor,
    CorsSecurityInterceptor,
    CsrfPreventionInterceptor,
    EuLoginSessionTimeoutHandlingInterceptor,
    CoreModule as UxCoreModule,
    translateConfig,
} from '@eui/core';

import './operators';

import { appConfig } from '../../config';
import { environment } from '../../environments/environment';

import { REDUCER_TOKEN, getReducers, metaReducers } from './reducers';

import { SharedModule } from '../shared/shared.module';

import { DisplayErrorService } from './services/response-error-growl.service';
import { V1Service } from '../shared/swagger';
import { EntityLinkService } from './services/entity-link.service';
import { ShareDataService } from './services/share-data.service';
import { ViewerService } from './services/viewer.service';

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
    V1Service,
    DisplayErrorService,
    EntityLinkService,
    ShareDataService,
    ViewerService,
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
    V1Service,
    DisplayErrorService,
    EntityLinkService,
    ShareDataService,
    ViewerService,
];

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
    declarations: [],
    exports: [SharedModule],
    providers: environment.production ? productionProviders : commonProviders,
})
export class CoreModule {}
