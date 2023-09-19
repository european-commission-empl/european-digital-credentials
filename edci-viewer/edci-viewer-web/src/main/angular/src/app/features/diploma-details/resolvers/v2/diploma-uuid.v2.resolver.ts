import { TranslateService } from '@ngx-translate/core';
import { Injectable } from '@angular/core';
import { EuropassDiplomaView, V1Service } from '@shared/swagger';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { Observable, of, tap } from 'rxjs';
import { ShareDataService } from '@services/share-data.service';
import { take, catchError } from 'rxjs/operators';
import { DisplayErrorService } from '@services/response-error-growl.service';

@Injectable({
    providedIn: 'root'
})
export class DiplomaUUIDResolverV2 implements Resolve<EuropassDiplomaView> {

    constructor(private api: V1Service,
        private shareDataService: ShareDataService,
        private router: Router) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<EuropassDiplomaView> {
        const credId = route.paramMap.get('id');
        const walletAddress = route.queryParamMap.get('walletAddress');
        const toolBarlanguage = this.shareDataService.toolbarLanguage;

        if (credId && walletAddress) {
            return this.api.getWalletDiplomaHTML(credId, walletAddress, toolBarlanguage).pipe(
                take(1),
                tap(diploma => {
                    this.shareDataService.europassDiplomaView = diploma;
                }),
                catchError(() => {
                    this.shareDataService.closeSpinnerDialog();
                    this.router.navigate(['home']);
                    return of(null);
                })
            );
        }
    }

}
