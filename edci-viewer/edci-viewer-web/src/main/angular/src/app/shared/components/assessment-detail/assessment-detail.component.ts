import { Component, Input } from '@angular/core';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { AssessmentTabView } from '../../swagger';

@Component({
    selector: 'edci-viewer-assessment-detail',
    templateUrl: './assessment-detail.component.html',
    styleUrls: ['./assessment-detail.component.scss'],
})
export class AssessmentDetailComponent {
    private _activeAssessment: AssessmentTabView;

    @Input() set activeAssessment(value: AssessmentTabView) {
        this._activeAssessment = value;
    }
    get activeAssessment(): AssessmentTabView {
        return this._activeAssessment;
    }

    constructor(private entityLinkService: EntityLinkService) {}

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }

    // openSubAssessmentModal(assessment: AssessmentTabView): void {
    //     const info: EntityModalInformation = {
    //         id: assessment.id,
    //         entityName: 'assessment',
    //         entity: assessment,
    //         modalTitle: this.translateService.instant(
    //             'details.assessments-tab.subAssessment'
    //         ),
    //     };
    //     this.entityLinkService.sendEntityModalInformation(info);
    // }

    // onOpenOrganizationModal(organization: OrganizationTabView): void {
    //     organization['isModal'] = true;
    //     const info: EntityModalInformation = {
    //         id: new Date().toISOString(),
    //         entityName: 'organization',
    //         entity: organization,
    //         modalTitle: this.translateService.instant(
    //             'details.assessments-tab.assessmentConductedBy'
    //         ),
    //     };
    //     this.entityLinkService.sendEntityModalInformation(info);
    // }
}
