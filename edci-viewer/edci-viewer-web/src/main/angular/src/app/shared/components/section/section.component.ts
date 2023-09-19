import { Component, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { openCloseAnimation } from '@shared/animations/openClose';

@Component({
    selector: 'edci-section',
    templateUrl: './section.component.html',
    styleUrls: ['./section.component.scss'],
    animations: [
        openCloseAnimation
    ]
})
export class SectionComponent {
    @Input() title: string;
    @Input() isMainTitle = false;
    @Input() isExpanded = false;
    @ViewChild('content') content: ElementRef;
    hasContent: boolean = true;

    constructor() { }

    ngAfterViewInit() {
        this.hasContent = this.content.nativeElement.children.length !== 0;
    }

    toggleExpanded(): void {
        this.isExpanded = !this.isExpanded;
    }

}
