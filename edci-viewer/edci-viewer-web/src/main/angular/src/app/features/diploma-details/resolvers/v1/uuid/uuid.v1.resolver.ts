import { Injectable } from '@angular/core';
import { EuropassCredentialPresentationView, EuropassDiplomaView, V1Service, VerificationCheckView } from '@shared/swagger';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot, Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { ShareDataService } from '@services/share-data.service';
import { DetailsUUIDResolverV1 } from './details-uuid.v1.resolver';
import { DiplomaUUIDResolverV1 } from './diploma-uuid.v1.resolver';
import { LanguageResolverV1 } from './language-uuid.v1resolver';
import { environment } from '@environments/environment';

@Injectable({
    providedIn: 'root'
})
export class UUIDResolverV1 implements Resolve<any> {

    constructor(private shareDataService: ShareDataService,
        private detailsResolver: DetailsUUIDResolverV1,
        private diplomaResolver: DiplomaUUIDResolverV1,
        private languageResolver: LanguageResolverV1,
        private api: V1Service) { }

    async resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<any> {
        const userId = route.paramMap.get('userId');
        const credId = route.paramMap.get('id');
        const walletAddress = environment.walletAddress.concat(userId);

        if (credId && walletAddress) {
            this.shareDataService.openSpinnerDialog();
            this.shareDataService.doStorageFullClear();

            return await lastValueFrom<any>(this.languageResolver.resolve(route, state)).then(() => {
                this.shareDataService.setVerificationStepsObs(
                   (lang: string) => this.api.getCredentialVerificationByWalletAddressID(walletAddress, credId, lang)
                );

                this.shareDataService.setPresentationViewObs(
                    (lang: string) => this.api.getWalletCredentialDetail(walletAddress, credId, lang)
                );

                this.shareDataService.setDiplomaViewObs(
                    (lang: string) => this.api.getWalletDiplomaHTML(credId, walletAddress, lang)
                );

                return lastValueFrom<EuropassCredentialPresentationView>(this.detailsResolver.resolve(route, state)),
                lastValueFrom<EuropassDiplomaView>(this.diplomaResolver.resolve(route, state));
            });
        }
    }

}
