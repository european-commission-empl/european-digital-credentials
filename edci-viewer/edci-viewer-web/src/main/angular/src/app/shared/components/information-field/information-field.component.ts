import { Component, OnInit, Input } from '@angular/core';

@Component({
    selector: 'edci-information-field',
    templateUrl: './information-field.component.html',
    styleUrls: ['./information-field.component.scss'],
})
export class InformationFieldComponent implements OnInit {
    @Input() icon: string;
    @Input() title: string;
    @Input() text: string;
    @Input() link: string;
    @Input() tooltip: string;

    constructor() {}

    ngOnInit() {}

    goToLink(): void {
        if (this.link) {
            window.open(this.link, '_blank').focus();
        }
    }
}
