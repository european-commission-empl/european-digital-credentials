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
        this.isPanelExpanded = true;
        this._activeAssessment = value;
    }
    get activeAssessment(): AssessmentTabView {
        return this._activeAssessment;
    }

    isPanelExpanded = true;

    constructor(private entityLinkService: EntityLinkService) {}

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }
}
