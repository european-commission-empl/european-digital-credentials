import { Component, Input, OnInit } from '@angular/core';
import { OrganizationTabView, VerificationCheckView } from '../../swagger';
import { EntityModalInformation } from '../../model/entityModalInformation';
import { TranslateService } from '@ngx-translate/core';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { ShareDataService } from 'src/app/core/services/share-data.service';

@Component({
    selector: 'edci-viewer-organization-detail',
    templateUrl: './organization-detail.component.html',
    styleUrls: ['./organization-detail.component.scss'],
})
export class OrganizationDetailComponent implements OnInit {
    @Input() organization: OrganizationTabView;
    @Input() titleSection: string;
    @Input() styleClass: string;
    @Input() subTitleSection: string;
    @Input() showVP: boolean;
    logo: string = null;
    sealLongDescription: string;
    constructor(
        private translateService: TranslateService,
        private entityLinkService: EntityLinkService,
        private shareDataService: ShareDataService
    ) {}

    ngOnInit() {
        this.logo = this.organization.logo
            ? `data:${this.organization.logo.mimeType};base64,${this.organization.logo.base64Content}`
            : null;
        this.sealLongDescription = this.getSealLongDescription();
    }

    openParentOrganizationModal(organization: OrganizationTabView): void {
        organization['isModal'] = true;
        const info: EntityModalInformation = {
            id: new Date().toISOString(),
            entityName: 'organization',
            entity: organization,
            modalTitle: this.translateService.instant(
                'details.organization-tab.parentOrganization'
            ),
        };
        this.entityLinkService.sendEntityModalInformation(info);
    }

    private getSealLongDescription(): string {
        let longDescription: string = null;
        const verificationSteps: VerificationCheckView[] = this.shareDataService
            .verificationSteps
            ? this.shareDataService.verificationSteps
            : JSON.parse(sessionStorage.getItem('verificationSteps'));
        if (verificationSteps) {
            longDescription = verificationSteps[1].longDescription;
        }
        return longDescription;
    }
}
