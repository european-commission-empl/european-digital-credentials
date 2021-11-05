import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';

@Component({
    selector: 'edci-cb-modal-footer',
    templateUrl: './cb-modal-footer.component.html',
    styleUrls: ['./cb-modal-footer.component.scss']
})
export class CbModalFooterComponent implements OnInit {
    @Input() isDisabled: boolean = false;
    @Output() onClose: EventEmitter<string> = new EventEmitter();
    @Output() onSave: EventEmitter<string> = new EventEmitter();

    constructor() {}

    ngOnInit() {}
}
