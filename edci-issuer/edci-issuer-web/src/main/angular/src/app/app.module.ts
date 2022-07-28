import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ApiInterceptor } from '@core/httpinterceptors/response.interceptor';
import { environment } from '@environments/environment';
import { MultilingualService } from '@services/multilingual.service';
import { BASE_PATH } from '@shared/swagger';
import { MessageService } from 'primeng/api';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';

@NgModule({
    declarations: [AppComponent],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        CoreModule,
        AppRoutingModule,
    ],
    providers: [
        MessageService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ApiInterceptor,
            multi: true,
        },
        {
            provide: BASE_PATH,
            useValue: environment.apiBaseUrl,
        },
        MultilingualService,
    ],
    bootstrap: [AppComponent],
})
export class AppModule {}
