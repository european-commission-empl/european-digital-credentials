import { Subject } from 'rxjs';
import { Component, ViewEncapsulation, OnDestroy, OnInit } from '@angular/core';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import {
    OrganizationTabView,
    AccreditationFieldView,
} from 'src/app/shared/swagger';

@Component({
    selector: 'edci-viewer-issuing-organization',
    templateUrl: './issuing-organization.component.html',
    styleUrls: ['./issuing-organization.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class IssuingOrganizationComponent implements OnInit, OnDestroy {
    // REVIEW

    organizationPresentation: OrganizationTabView =
        this.shareDataService.issuerPresentation;
    organizationCredential: OrganizationTabView =
        this.shareDataService.issuerCredential;

    activeOrganisation: OrganizationTabView =
        this.shareDataService.activeEntity;

    issuerLogo: string = this.organizationCredential.logo
        ? `data:${this.organizationCredential.logo.mimeType};base64,${this.organizationCredential.logo.base64Content}`
        : null;

    destroy$: Subject<boolean> = new Subject<boolean>();
    accreditations: AccreditationFieldView[];
    constructor(private shareDataService: ShareDataService) {}

    ngOnInit() {
        this.shareDataService
            .changeEntitySelection()
            .takeUntil(this.destroy$)
            .subscribe((organisation) => {
                this.activeOrganisation = organisation;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }
}
