import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'edci-cb-tab-header',
    templateUrl: './cb-tab-header.component.html',
    styleUrls: ['./cb-tab-header.component.scss']
})
export class CbTabHeaderComponent {
    @Input() title: string;
    @Input() buttonLabel: string;
    @Input() isDisabled = false;
    @Output() onClick: EventEmitter<MouseEvent> = new EventEmitter();

    constructor() {}

}
