import { Component, OnInit, Input } from '@angular/core';
import { LinkFieldView } from '../../swagger';

@Component({
    selector: 'edci-viewer-other-documents',
    templateUrl: './other-documents.component.html',
    styleUrls: ['./other-documents.component.scss'],
})
export class OtherDocumentsComponent implements OnInit {
    @Input() otherDocuments: LinkFieldView[];
    constructor() {}

    ngOnInit() {}
}
