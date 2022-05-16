import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'edci-panel',
    templateUrl: './panel.component.html',
    styleUrls: ['./panel.component.scss'],
})
export class PanelComponent {
    @Input() title: string;
    @Input() isMainPanel: boolean = false;
    @Input() isExpanded: boolean = true;
    @Output() isExpandedChange = new EventEmitter<boolean>();

    constructor() {}

    toggleExpanded(): void {
        this.isExpanded = !this.isExpanded;
        this.isExpandedChange.emit(this.isExpanded);
    }
}
