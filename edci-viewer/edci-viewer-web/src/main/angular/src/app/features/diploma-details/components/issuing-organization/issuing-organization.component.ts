import { Component, ViewEncapsulation } from '@angular/core';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { OrganizationTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-viewer-issuing-organization',
    templateUrl: './issuing-organization.component.html',
    styleUrls: ['./issuing-organization.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class IssuingOrganizationComponent {
    organizationPresentation: OrganizationTabView = this.shareDataService.issuerPresentation;
    organizationCredential: OrganizationTabView = this.shareDataService.issuerCredential;
    issuerLogo: string = this.organizationCredential.logo
        ? `data:${this.organizationCredential.logo.mimeType};base64,${this.organizationCredential.logo.base64Content}`
        : null;
    constructor(private shareDataService: ShareDataService) {}
}
