import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, APP_INITIALIZER } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule } from '@core/core.module';
import { RouterModule } from '@angular/router';
import { HttpModule } from '@angular/http';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ApiInterceptor } from '@core/httpinterceptors/response.interceptor';
import { MultilingualService } from '@services/multilingual.service';
import { BASE_PATH } from './shared/swagger/variables';
import { environment } from '@environments/environment';

@NgModule({
    declarations: [AppComponent],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        CoreModule,
        AppRoutingModule,
        RouterModule,
        HttpModule,
    ],
    providers: [{
        provide: HTTP_INTERCEPTORS,
        useClass: ApiInterceptor,
        multi: true,
    },
    {
        provide: BASE_PATH,
        useValue: environment.apiBaseUrl
    },
        MultilingualService
    ],
    bootstrap: [AppComponent],
})

export class AppModule {}
