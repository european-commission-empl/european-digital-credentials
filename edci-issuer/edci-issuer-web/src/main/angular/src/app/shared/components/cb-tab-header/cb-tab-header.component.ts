import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'edci-cb-tab-header',
    templateUrl: './cb-tab-header.component.html',
    styleUrls: ['./cb-tab-header.component.scss']
})
export class CbTabHeaderComponent implements OnInit {
    @Input() title: string;
    @Input() buttonLabel: string;
    @Input() isDisabled: boolean = false;
    @Output() onClick: EventEmitter<MouseEvent> = new EventEmitter();

    constructor() {}

    ngOnInit() {}
}
