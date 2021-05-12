import { Component, OnInit, Input } from '@angular/core';
import { IdentifierFieldView } from '../../swagger';

@Component({
    selector: 'edci-viewer-identifiers',
    templateUrl: './identifiers.component.html',
    styleUrls: ['./identifiers.component.scss'],
})
export class IdentifiersComponent implements OnInit {
    @Input() identifiers: IdentifierFieldView[];
    @Input() showLiteral: boolean = true;

    constructor() {}

    ngOnInit() {}
}
