import { Component, Input } from '@angular/core';
import { AccreditationFieldView } from '../../swagger';

@Component({
    selector: 'edci-viewer-accreditation',
    templateUrl: './accreditation-detail.component.html',
    styleUrls: ['./accreditation-detail.component.scss'],
})
export class AccreditationComponent {
    @Input() accreditation: AccreditationFieldView;
    @Input() isMainPanel = false;
    @Input() isTabExpanded = false;
}
