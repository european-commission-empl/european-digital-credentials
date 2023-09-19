import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { ShareDataService } from '@services/share-data.service';
import { EuropassCredentialPresentationView, V1Service } from '@shared/swagger';
import { take, catchError } from 'rxjs/operators';
import { Observable, of, tap } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn : 'root'
})
export class DetailsUUIDResolverV1 implements Resolve<EuropassCredentialPresentationView> {
    constructor(
        private api: V1Service,
        private shareDataService: ShareDataService,
        private router: Router
    ) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<EuropassCredentialPresentationView> {
        const userId = route.paramMap.get('userId');
        const credId = route.paramMap.get('id');
        const toolBarlanguage = this.shareDataService.toolbarLanguage;
        const walletAddress = environment.walletAddress.concat(userId);

        if (userId && credId) {
            return this.api.getWalletCredentialDetail(walletAddress, credId, toolBarlanguage).pipe(
                take(1),
                tap(details => {
                    this.shareDataService.europassPresentationView = details;
                    this.shareDataService.issuerCredential = details.issuerCredential;
                    this.shareDataService.issuerPresentation = details.issuerPresentation;
                    this.shareDataService.toolbarLanguage = details.credentialMetadata.primaryLanguage;
                    this.shareDataService.credUUID = credId;
                    this.shareDataService.walletAddress = walletAddress;
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
