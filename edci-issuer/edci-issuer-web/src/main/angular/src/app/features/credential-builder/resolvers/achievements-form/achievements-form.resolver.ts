import { Injectable } from '@angular/core';
import {
    Resolve,
    ActivatedRouteSnapshot
} from '@angular/router';
import { LearningAchievementSpecView, V1Service } from '@shared/swagger';
import { Observable, of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { take, catchError } from 'rxjs/operators';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';

@Injectable({
    providedIn: 'root'
})
export class AchievementsFormResolver implements Resolve<LearningAchievementSpecView> {
    constructor(
    private api: V1Service,
    private translateService: TranslateService,
    private readonly pageLoadingSpinnerService: PageLoadingSpinnerService
    ) {}
    resolve(route: ActivatedRouteSnapshot): Observable<LearningAchievementSpecView> {
        const routeID = route.paramMap.get('id') ? parseInt(route.paramMap.get('id')) : null;
        const currentLanguage = this.translateService.currentLang ?? null;

        if (routeID && currentLanguage) {
            this.pageLoadingSpinnerService.startPageLoader();
            return this.api.getLearningAchievement(routeID, currentLanguage).pipe(
                take(1),
                catchError(() => {
                    this.pageLoadingSpinnerService.redirectToCredentialsTab(1);
                    return of(null);
                })
            );
        }
    }
}
