import { Component, Input } from '@angular/core';
import { ActivityTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-assessment-other-information-panel',
    templateUrl: './assessment-other-information-panel.component.html',
    styleUrls: ['./assessment-other-information-panel.component.scss']
})
export class AssessmentOtherInformationPanelComponent {

    private _activeAssessment: ActivityTabView;
    @Input()
    set activeAssessment(value: ActivityTabView) {
        this.isPanelExpanded = true;
        this._activeAssessment = value;
    }
    get activeAssessment(): ActivityTabView {
        return this._activeAssessment;
    }

    isPanelExpanded: boolean = true;
    constructor() {}
}
