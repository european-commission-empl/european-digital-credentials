import { Injectable } from '@angular/core';
import {
    Resolve,
    ActivatedRouteSnapshot
} from '@angular/router';
import { PagedResourcesLearningActivitySpecLiteView, V1Service } from '@shared/swagger';
import { Observable, of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { take, catchError } from 'rxjs/operators';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';

@Injectable({
    providedIn: 'root'
})
export class ActivitiesFormSubActivitiesResolver implements Resolve<PagedResourcesLearningActivitySpecLiteView> {
    constructor(
    private api: V1Service,
    private translateService: TranslateService,
    private readonly pageLoadingSpinnerService: PageLoadingSpinnerService
    ) {}
    resolve(route: ActivatedRouteSnapshot): Observable<PagedResourcesLearningActivitySpecLiteView> {
        const routeID = route.paramMap.get('id') ? parseInt(route.paramMap.get('id')) : null;
        const currentLanguage = this.translateService.currentLang ?? null;

        if (routeID && currentLanguage) {
            return this.api.listHasActPart(routeID, currentLanguage).pipe(
                take(1),
                catchError(() => {
                    this.pageLoadingSpinnerService.redirectToCredentialsTab(3);
                    return of(null);
                })
            );
        }
    }
}
