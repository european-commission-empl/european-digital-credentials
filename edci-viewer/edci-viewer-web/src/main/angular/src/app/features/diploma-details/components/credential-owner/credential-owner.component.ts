import { Component, ViewEncapsulation } from '@angular/core';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { CredentialSubjectTabView } from 'src/app/shared/swagger/model/credentialSubjectTabView';

@Component({
    selector: 'edci-viewer-credential-owner',
    templateUrl: './credential-owner.component.html',
    styleUrls: ['./credential-owner.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class CredentialOwnerComponent {
    credentialOwner: CredentialSubjectTabView = this.shareDataService
        .credentialSubject;

    constructor(private shareDataService: ShareDataService) {
    }

    existContactInfo(): boolean {
        for (let i = 0; i < this.credentialOwner.contactPoint.length; i++) {
            if (this.credentialOwner.contactPoint[i].address || this.credentialOwner.contactPoint[i].phone ||
                this.credentialOwner.contactPoint[i].email) {
                return true;
            }
        }
        return false;
    }
}
