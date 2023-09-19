import { LanguageResolverV2 } from './language.v2.resolver';
import { DiplomaUUIDResolverV2 } from './diploma-uuid.v2.resolver';
import { Injectable } from '@angular/core';
import { EuropassCredentialPresentationView, EuropassDiplomaView, V1Service, VerificationCheckView } from '@shared/swagger';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot, Router } from '@angular/router';
import { lastValueFrom, concatMap } from 'rxjs';
import { ShareDataService } from '@services/share-data.service';
import { DetailsUUIDResolverV2 } from './details-uuid.v2.resolver';

@Injectable({
    providedIn: 'root'
})
export class UUIDResolverV2 implements Resolve<any> {

    constructor(private shareDataService: ShareDataService,
        private detailsResolver: DetailsUUIDResolverV2,
        private diplomaResolver: DiplomaUUIDResolverV2,
        private languageResolver: LanguageResolverV2,
        private api: V1Service) { }

    async resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<any> {
        const credId = route.paramMap.get('id');
        const walletAddress = route.queryParamMap.get('walletAddress');

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
