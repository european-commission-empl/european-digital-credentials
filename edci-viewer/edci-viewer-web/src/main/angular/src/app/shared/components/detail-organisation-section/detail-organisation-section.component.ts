import { Component, Input } from '@angular/core';
import { OrganizationTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-detail-organisation-section',
    templateUrl: './detail-organisation-section.component.html',
    styleUrls: ['./detail-organisation-section.component.scss'],
})
export class DetailOrganisationSectionComponent {
    private _organisation: OrganizationTabView;

    @Input()
    set organisation(value: OrganizationTabView) {
        this.isAwardedByExpanded = true;
        this._organisation = value;
    }

    get organisation(): OrganizationTabView {
        return this._organisation;
    }

    isAwardedByExpanded: boolean = true;
    constructor() {}

    toggleAwardedBy(): void {
        this.isAwardedByExpanded = !this.isAwardedByExpanded;
    }
}
