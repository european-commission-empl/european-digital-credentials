
import { Resolve, ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { Injectable } from '@angular/core';
import { ShareDataService } from '@services/share-data.service';
import { Observable, of, tap } from 'rxjs';
import { take, catchError } from 'rxjs/operators';
import { EuropassCredentialPresentationView, EuropassDiplomaView, V1Service } from '@shared/swagger';

@Injectable({
    providedIn: 'root'
})
export class DiplomaShareLinkResolverV1 implements Resolve<EuropassDiplomaView> {
    constructor(
        private api: V1Service,
        private shareDataService: ShareDataService,
        private router: Router
    ) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<EuropassDiplomaView> {
        const shareHash = route.paramMap.get('shareLink');
        const toolBarlanguage = this.shareDataService.toolbarLanguage;

        if (shareHash) {
            return this.api.getSharedCredentialDiploma(shareHash, toolBarlanguage).pipe(
                take(1),
                tap(details => {
                    this.shareDataService.europassDiplomaView = details;
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
