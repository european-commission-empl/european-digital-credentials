import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { ShareDataService } from '@services/share-data.service';
import { EuropassDiplomaView, V1Service } from '@shared/swagger';
import { take, catchError } from 'rxjs/operators';
import { Observable, of , tap } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn : 'root'
})
export class DiplomaUUIDResolverV1 implements Resolve<EuropassDiplomaView> {
    constructor(
        private api: V1Service,
        private shareDataService: ShareDataService,
        private router: Router
    ) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<EuropassDiplomaView> {
        const userId = route.paramMap.get('userId');
        const credId = route.paramMap.get('id');
        const toolBarlanguage = this.shareDataService.toolbarLanguage;
        const walletAddress = environment.walletAddress.concat(userId);

        if (userId && credId) {
            return this.api.getWalletDiplomaHTML(credId, walletAddress, toolBarlanguage).pipe(
                take(1),
                tap(diploma => {
                    this.shareDataService.europassDiplomaView = diploma;
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
