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
export class LanguageSharelinkResolverV1 implements Resolve<any> {

    constructor(private api: V1Service, private shareDataService: ShareDataService,
        private router: Router) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
        const shareHash = route.paramMap.get('shareLink');
        const toolBarlanguage = this.shareDataService.toolbarLanguage;

        if (shareHash) {
            return this.api.getSharedCredentialDetails(shareHash, toolBarlanguage).pipe(
                take(1),
                tap(details => {
                    this.shareDataService.toolbarLanguage = details.credentialMetadata.primaryLanguage;
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
