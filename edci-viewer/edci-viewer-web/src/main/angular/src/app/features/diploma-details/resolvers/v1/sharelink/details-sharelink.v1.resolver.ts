
import { Resolve, ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { Injectable } from '@angular/core';
import { ShareDataService } from '@services/share-data.service';
import { Observable, of, tap } from 'rxjs';
import { take, catchError } from 'rxjs/operators';
import { EuropassCredentialPresentationView, V1Service } from '@shared/swagger';

@Injectable({
    providedIn: 'root'
})
export class DetailsShareLinkResolverV1 implements Resolve<EuropassCredentialPresentationView> {
    constructor(
        private api: V1Service,
        private shareDataService: ShareDataService,
        private router: Router
    ) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<EuropassCredentialPresentationView> {
        const shareHash = route.paramMap.get('shareLink');
        const toolBarlanguage = this.shareDataService.toolbarLanguage;

        if (shareHash) {
            return this.api.getSharedCredentialDetails(shareHash, toolBarlanguage).pipe(
                take(1),
                tap(details => {
                    this.shareDataService.europassPresentationView = details;
                    this.shareDataService.issuerCredential = details.issuerCredential;
                    this.shareDataService.issuerPresentation = details.issuerPresentation;
                    this.shareDataService.toolbarLanguage = details.credentialMetadata.primaryLanguage;
                    this.shareDataService.shareLink = shareHash;
                }),
                catchError(() => {
                    this.shareDataService.closeSpinnerDialog();
                    this.router.navigate(['home']);
                    return of (null);
                })
            );
        }

    }

}
