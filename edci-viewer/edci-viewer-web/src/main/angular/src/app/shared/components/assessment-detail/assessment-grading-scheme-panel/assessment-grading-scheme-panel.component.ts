import { Component, Input } from '@angular/core';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { AssessmentTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-assessment-grading-scheme-panel',
    templateUrl: './assessment-grading-scheme-panel.component.html',
    styleUrls: ['./assessment-grading-scheme-panel.component.scss']
})
export class AssessmentGradingSchemePanelComponent {

    private _activeAssessment: AssessmentTabView;

    @Input() set activeAssessment(value: AssessmentTabView) {
        this.isPanelExpanded = true;
        this._activeAssessment = value;
    }
    get activeAssessment(): AssessmentTabView {
        return this._activeAssessment;
    }

    isPanelExpanded: boolean = true;

    constructor(private entityLinkService: EntityLinkService) {}
}
