import { Component, OnInit, Input } from '@angular/core';
import { ShareDataService } from '@services/share-data.service';

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
    language: string;

    constructor(private shareDataService: ShareDataService) {}

    ngOnInit() {
        this.shareDataService.toolbarLanguageObservable.subscribe(language => {
            this.language = language;
        });
    }

    goToLink(): void {
        if (this.link) {
            window.open(this.link, '_blank').focus();
        }
    }
}
