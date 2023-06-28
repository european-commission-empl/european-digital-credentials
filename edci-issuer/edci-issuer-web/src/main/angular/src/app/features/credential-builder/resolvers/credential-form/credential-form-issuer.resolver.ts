import { Injectable } from '@angular/core';
import {
    Resolve,
    ActivatedRouteSnapshot
} from '@angular/router';
import { ResourceOrganizationSpecView, V1Service } from '@shared/swagger';
import { Observable, of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { take, catchError } from 'rxjs/operators';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';

@Injectable({
    providedIn: 'root'
})
export class CredentialFormIssuerResolver implements Resolve<ResourceOrganizationSpecView> {
    constructor(
    private api: V1Service,
    private translateService: TranslateService,
    private readonly pageLoadingSpinnerService: PageLoadingSpinnerService
    ) {}

    resolve(route: ActivatedRouteSnapshot): Observable<ResourceOrganizationSpecView> {
        const routeID = route.paramMap.get('id') ? parseInt(route.paramMap.get('id')) : null;
        const currentLanguage = this.translateService.currentLang ?? null;

        if (routeID && currentLanguage) {
            return this.api.getIssuer(routeID, currentLanguage).pipe(
                take(1),
                catchError(() => {
                    this.pageLoadingSpinnerService.redirectToCredentialsTab(0);
                    return of(null);
                })
            );
        }
    }
}
