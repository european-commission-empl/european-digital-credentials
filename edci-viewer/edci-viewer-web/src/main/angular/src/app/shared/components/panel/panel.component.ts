import { Component, Input, Output, EventEmitter } from '@angular/core';
import { openCloseAnimation } from '@shared/animations/openClose';

@Component({
    selector: 'edci-panel',
    templateUrl: './panel.component.html',
    styleUrls: ['./panel.component.scss'],
    animations: [
        openCloseAnimation
    ]
})
export class PanelComponent {
    @Input() title: string;
    @Input() isMainPanel = false;
    @Input() isExpanded = false;

    constructor() {}

    toggleExpanded(): void {
        if (!this.isMainPanel) {
            this.isExpanded = !this.isExpanded;
        }
    }
}
