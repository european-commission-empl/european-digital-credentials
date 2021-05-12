import { Component, Input } from '@angular/core';
import { OrganizationTabView, AssessmentTabView } from '../../swagger';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { TranslateService } from '@ngx-translate/core';
import { EntityModalInformation } from '../../model/entityModalInformation';

@Component({
    selector: 'edci-viewer-assessment-detail',
    templateUrl: './assessment-detail.component.html',
    styleUrls: ['./assessment-detail.component.scss'],
})
export class AssessmentDetailComponent {
    @Input() activeAssessment: AssessmentTabView;

    constructor(
        private entityLinkService: EntityLinkService,
        private translateService: TranslateService
    ) {}

    openSubAssessmentModal(assessment: AssessmentTabView): void {
        const info: EntityModalInformation = {
            id: assessment.id,
            entityName: 'assessment',
            entity: assessment,
            modalTitle: this.translateService.instant(
                'details.assessments-tab.subAssessment'
            ),
        };
        this.entityLinkService.sendEntityModalInformation(info);
    }

    onOpenOrganizationModal(organization: OrganizationTabView): void {
        organization['isModal'] = true;
        const info: EntityModalInformation = {
            id: new Date().toISOString(),
            entityName: 'organization',
            entity: organization,
            modalTitle: this.translateService.instant(
                'details.assessments-tab.assessmentConductedBy'
            ),
        };
        this.entityLinkService.sendEntityModalInformation(info);
    }
}
