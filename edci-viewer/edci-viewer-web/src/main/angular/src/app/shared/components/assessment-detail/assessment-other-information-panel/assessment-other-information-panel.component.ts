import { Component, Input } from '@angular/core';
import { AssessmentTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-assessment-other-information-panel',
    templateUrl: './assessment-other-information-panel.component.html',
    styleUrls: ['./assessment-other-information-panel.component.scss']
})
export class AssessmentOtherInformationPanelComponent {

    private _activeAssessment: AssessmentTabView;
    @Input()
    set activeAssessment(value: AssessmentTabView) {
        this.isPanelExpanded = false;
        this._activeAssessment = value;
    }
    get activeAssessment(): AssessmentTabView {
        return this._activeAssessment;
    }

    isPanelExpanded = false;
    constructor() {}
}
