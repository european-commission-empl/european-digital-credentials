import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { OrganizationTabView, VerificationCheckView } from '../../swagger';
import { EntityModalInformation } from '../../model/entityModalInformation';
import { TranslateService } from '@ngx-translate/core';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-viewer-organization-detail',
    templateUrl: './organization-detail.component.html',
    styleUrls: ['./organization-detail.component.scss'],
})
export class OrganizationDetailComponent implements OnInit, OnDestroy {
    @Input() organization: OrganizationTabView;
    @Input() titleSection: string;
    @Input() styleClass: string;
    @Input() subTitleSection: string;
    @Input() showVP: boolean;
    logo: string = null;
    sealLongDescription: { [key: string]: string };
    language: string = this.shareDataService.toolbarLanguage;
    verificationSteps: VerificationCheckView[] = this.shareDataService
        .verificationSteps
        ? this.shareDataService.verificationSteps
        : JSON.parse(sessionStorage.getItem('verificationSteps'));
    destroy$: Subject<boolean> = new Subject<boolean>();
    constructor(
        private translateService: TranslateService,
        private entityLinkService: EntityLinkService,
        private shareDataService: ShareDataService
    ) {
        this.shareDataService.toolbarLanguageChange
            .takeUntil(this.destroy$)
            .subscribe((newLanguage) => {
                this.language = newLanguage;
            });

        this.shareDataService
            .getVerificationSteps()
            .takeUntil(this.destroy$)
            .subscribe((verificationSteps) => {
                this.verificationSteps = verificationSteps;
                this.sealLongDescription = this.getSealLongDescription();
            });
    }

    ngOnInit() {
        this.logo = this.organization.logo
            ? `data:${this.organization.logo.mimeType};base64,${this.organization.logo.base64Content}`
            : null;
        this.sealLongDescription = this.getSealLongDescription();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }

    // openParentOrganizationModal(organization: OrganizationTabView): void {
    //     organization['isModal'] = true;
    //     const info: EntityModalInformation = {
    //         id: new Date().toISOString(),
    //         entityName: 'organization',
    //         entity: organization,
    //         modalTitle: this.translateService.instant(
    //             'details.organization-tab.parentOrganization'
    //         ),
    //     };
    //     this.entityLinkService.sendEntityModalInformation(info);
    // }

    private getSealLongDescription(): { [key: string]: string } {
        let longDescription: { [key: string]: string } = null;
        if (this.verificationSteps) {
            this.verificationSteps.forEach((step) => {
                if (
                    step.type.link ===
                    'http://data.europa.eu/snb/verification/f9c2016fe9'
                ) {
                    longDescription = step.longDescrAvailableLangs;
                }
            });
        }
        return longDescription;
    }
}
