import { Component, Input } from '@angular/core';
import { AssessmentTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-assessment-information-panel',
    templateUrl: './assessment-information-panel.component.html',
    styleUrls: ['./assessment-information-panel.component.scss']
})
export class AssessmentInformationPanelComponent {

    private _activeAssessment: AssessmentTabView;

    @Input()
    set activeAssessment(value: AssessmentTabView) {
        this.isPanelExpanded = true;
        this._activeAssessment = value;
    }

    get activeAssessment(): AssessmentTabView {
        return this._activeAssessment;
    }

    isPanelExpanded = true;

    constructor() { }
}
