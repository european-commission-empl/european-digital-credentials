import { Injectable } from '@angular/core';
import { EuropassCredentialPresentationView, V1Service } from '@shared/swagger';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of, tap } from 'rxjs';
import { ShareDataService } from '@services/share-data.service';
import { take, catchError } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class LanguageResolverV2 implements Resolve<any> {

    constructor(private api: V1Service, private shareDataService: ShareDataService,
        private router: Router) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
        const credId = route.paramMap.get('id');
        const walletAddress = route.queryParamMap.get('walletAddress');
        const toolBarlanguage = this.shareDataService.toolbarLanguage;

        if (credId && walletAddress) {
            return this.api.getWalletCredentialDetail(walletAddress, credId, toolBarlanguage).pipe(
                take(1),
                tap(details => {
                    this.shareDataService.toolbarLanguage = details.credentialMetadata.primaryLanguage;
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
