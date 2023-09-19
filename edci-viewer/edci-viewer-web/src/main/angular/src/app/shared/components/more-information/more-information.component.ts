import { Component, OnInit, Input } from '@angular/core';
import { NoteFieldView } from '../../swagger';

@Component({
    selector: 'edci-viewer-more-information',
    templateUrl: './more-information.component.html',
    styleUrls: ['./more-information.component.scss'],
})
export class MoreInformationComponent implements OnInit {
    @Input() moreInformation: NoteFieldView[];

    constructor() {}

    ngOnInit() {}
}
