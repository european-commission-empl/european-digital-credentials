import { Component, Input } from '@angular/core';
import { AccreditationFieldView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-accreditation-panel',
    templateUrl: './accreditation-panel.component.html',
    styleUrls: ['./accreditation-panel.component.scss'],
})
export class AccreditationPanelComponent {
    private _accreditations: AccreditationFieldView[];

    @Input() set accreditations(value: AccreditationFieldView[]) {
        this.isAccreditationExpanded = [];
        this.isPanelExpanded = true;
        this._accreditations = value;
    }
    get accreditations(): AccreditationFieldView[] {
        return this._accreditations;
    }
    isPanelExpanded = true;
    isAccreditationExpanded: boolean[] = [];

    constructor() {}

    toggleAccreditation(index: number): void {
        this.isAccreditationExpanded[index] =
            !this.isAccreditationExpanded[index];
    }
}
