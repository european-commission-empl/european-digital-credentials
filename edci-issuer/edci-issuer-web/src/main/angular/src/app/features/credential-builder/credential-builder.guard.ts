import { CanActivate } from '@angular/router';
import { Injectable } from '@angular/core';
import { V1Service, UserDetailsView } from '@shared/swagger';
import { Observable } from 'rxjs';
import { tap, map, take } from 'rxjs/operators';
import { environment } from '@environments/environment';
import { IssuerService } from '@services/issuer.service';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
    providedIn: 'root'
})
export class CredentialBuilderGuard implements CanActivate {
    private basePath = environment.apiBaseUrl;

    constructor(
        private api: V1Service,
        private issuerService: IssuerService,
        private translateService: TranslateService
    ) {}

    canActivate(): Observable<boolean> {
        return this.api.getUserDetails(this.translateService.currentLang).pipe(
            take(1),
            map((userDetails: UserDetailsView) => {
                this.issuerService.userInfo = userDetails;
                return userDetails.authenticated;
            }),
            tap((authenticated: boolean) => {
                if (!authenticated) {
                    window.location.href = `${this.basePath}${
                        environment.loginUrl
                    }`;
                }
            })
        );
    }
}
