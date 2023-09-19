import { Injectable } from '@angular/core';
import { EuropassCredentialPresentationView, V1Service } from '@shared/swagger';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of, tap } from 'rxjs';
import { ShareDataService } from '@services/share-data.service';
import { take, catchError } from 'rxjs/operators';
import { environment } from '@environments/environment';

@Injectable({
    providedIn: 'root'
})
export class LanguageResolverV1 implements Resolve<any> {

    constructor(private api: V1Service, private shareDataService: ShareDataService,
        private router: Router) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
        const userId = route.paramMap.get('userId');
        const credId = route.paramMap.get('id');
        const toolBarlanguage = this.shareDataService.toolbarLanguage;
        const walletAddress = environment.walletAddress.concat(userId);

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
