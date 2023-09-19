import { Injectable } from '@angular/core';
import { EuropassCredentialPresentationView, EuropassDiplomaView, V1Service, VerificationCheckView } from '@shared/swagger';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot, Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { ShareDataService } from '@services/share-data.service';
import { environment } from '@environments/environment';
import { DetailsShareLinkResolverV1 } from './details-sharelink.v1.resolver';
import { LanguageSharelinkResolverV1 } from './language-sharelink.v1resolver';
import { DiplomaShareLinkResolverV1 } from './diploma-sharelink.v1.resolver';

@Injectable({
    providedIn: 'root'
})
export class SharelinkResolver implements Resolve<any> {

    constructor(private shareDataService: ShareDataService,
        private detailsResolver: DetailsShareLinkResolverV1,
        private diplomaResolver: DiplomaShareLinkResolverV1,
        private languageResolver: LanguageSharelinkResolverV1,
        private api: V1Service) { }

    async resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<any> {
        const shareHash = route.paramMap.get('shareLink');

        if (shareHash) {
            this.shareDataService.openSpinnerDialog();
            this.shareDataService.doStorageFullClear();

            return await lastValueFrom<any>(this.languageResolver.resolve(route, state)).then(() => {
                this.shareDataService.setVerificationStepsObs(
                    (lang: string ) => this.api.getShareLinkCredentialVerification(shareHash, lang)
                );

                this.shareDataService.setPresentationViewObs(
                    (lang: string) => this.api.getSharedCredentialDetails(shareHash, lang)
                );

                this.shareDataService.setDiplomaViewObs(
                    (lang: string) => this.api.getSharedCredentialDiploma(shareHash, lang)
                );

                return lastValueFrom<EuropassCredentialPresentationView>(this.detailsResolver.resolve(route, state)),
                lastValueFrom<EuropassDiplomaView>(this.diplomaResolver.resolve(route, state));
            });
        }
    }

}
