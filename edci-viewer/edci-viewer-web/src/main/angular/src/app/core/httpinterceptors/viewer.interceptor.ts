import { Injectable } from '@angular/core';
import {
    HttpInterceptor,
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';
import { DisplayErrorService } from '../services/response-error-growl.service';
import { environment } from 'src/environments/environment';
// import { RegistryResponseView } from '@shared/swagger';

const apiBaseUrl = '/api/';

@Injectable()
export class ViewerInterceptor implements HttpInterceptor {
    constructor(
        private displayErrorService: DisplayErrorService,
        private translateService: TranslateService
    ) {}

    intercept(
        request: HttpRequest<any>,
        next: HttpHandler
    ): Observable<HttpEvent<any>> {
        // Required for CSRF Spring protection
        if (environment.csrfEnabled) {
            if (
                request.method !== 'GET' &&
                request.method !== 'OPTIONS' &&
                (request.url.startsWith(environment.apiBaseUrl) || request.url.includes(environment.loginUrl))
            ) {
                request = request.clone({
                    setHeaders: {
                        'X-XSRF-TOKEN': this.getCookieValue('XSRF-TOKEN'),
                    },
                });
            }
        }
        // Required for debug in localhost:4200
        if (environment.isMockUser && request.method !== 'OPTIONS') {
            const token = 'mockedToken';
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${token}`,
                },
                withCredentials: true,
            });
        }

        return next.handle(request).pipe(
            catchError((error: HttpErrorResponse) => {
                if (this.shouldBeIntercepted(request)) {
                    if (error.status === 401) {
                        this.displayErrorService.showNotificationText(
                            `${this.translateService.instant('common.login')}`,
                            `${this.translateService.instant(
                                'common.login.text'
                            )}`,
                            true
                        );
                    } else if (error.status === 403) {
                        if (error.error.code === 'SE-0001') {
                            this.displayErrorService.showNotificationText(
                                `${this.translateService.instant(
                                    'sessionExpired.title'
                                )}`,
                                `${this.translateService.instant(
                                    'sessionExpired.error'
                                )}`,
                                true
                            );
                        }
                    } else {
                        this.displayErrorService.displayRequestErrorGrowl(
                            error
                        );
                    }
                }
                return throwError(error);
            })
        );
    }

    private shouldBeIntercepted(request: HttpRequest<any>): boolean {
        return request.url.includes(apiBaseUrl);
    }

    private getCookieValue(name) {
        const value = document.cookie.match(
            '(^|;)\\s*' + name + '\\s*=\\s*([^;]+)'
        );
        return value ? value.pop() : '';
    }
}
