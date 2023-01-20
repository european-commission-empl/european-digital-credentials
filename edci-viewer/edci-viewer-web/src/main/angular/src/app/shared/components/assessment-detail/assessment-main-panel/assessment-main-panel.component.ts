import { Component, Input } from '@angular/core';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { AssessmentTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-assessment-main-panel',
    templateUrl: './assessment-main-panel.component.html',
    styleUrls: ['./assessment-main-panel.component.scss']
})
export class AssessmentMainPanelComponent {

    @Input()
    set activeAssessment(value: AssessmentTabView) {
        // this.isPanelExpanded = true;
        this._activeAssessment = value;
    }

    get activeAssessment(): AssessmentTabView {
        return this._activeAssessment;
    }

    private _activeAssessment: AssessmentTabView;
    constructor(private entityLinkService: EntityLinkService) { }

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }

}
