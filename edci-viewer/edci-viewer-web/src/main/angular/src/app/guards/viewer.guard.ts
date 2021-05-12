import { CanActivate } from '@angular/router';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, take, tap } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';
import { UserDetailsView, V1Service } from '../shared/swagger';
import { environment } from 'src/environments/environment';
import { ViewerService } from '../core/services/viewer.service';
import { Constants } from '../shared/constants';

@Injectable({
    providedIn: 'root'
})
export class ViewerGuard implements CanActivate {
    private basePath = environment.apiBaseUrl;

    constructor(
        private api: V1Service,
        private viewerService: ViewerService,
        private translateService: TranslateService
    ) {}

    canActivate(): Observable<boolean> {
        return this.api.getUserDetails(this.translateService.currentLang).pipe(
            take(1),
            map((userDetails: UserDetailsView) => {
                this.viewerService.userInfo = userDetails;
                return userDetails.authenticated;
            }),
            tap((authenticated: boolean) => {
                if (!authenticated) {
                    window.location.href = `${this.basePath}${
                        environment.loginUrl
                    }?${Constants.PARAMETER_REDIRECTURI}=${encodeURIComponent(window.location.href)}`;
                }
            })
        );
    }
}
