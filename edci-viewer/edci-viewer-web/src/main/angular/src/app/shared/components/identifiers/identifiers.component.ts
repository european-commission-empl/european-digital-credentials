import { Component, OnInit, Input } from '@angular/core';
import { Identifier, IdentifierFieldView } from '../../swagger';

@Component({
    selector: 'edci-viewer-identifiers',
    templateUrl: './identifiers.component.html',
    styleUrls: ['./identifiers.component.scss'],
})
export class IdentifiersComponent implements OnInit {
    @Input() identifiers: IdentifierFieldView[];
    @Input() nationalID: IdentifierFieldView;
    @Input() taxIdentifiers: Array<IdentifierFieldView>;
    @Input() vatIdentifiers: Array<IdentifierFieldView>;
    @Input() eidasIdentifier: IdentifierFieldView;
    @Input() registration: IdentifierFieldView;
    @Input() showLiteral = true;

    constructor() {}

    ngOnInit() {}

    public getIdentifierTitleLabel(identifier: IdentifierFieldView): string {
        let label = identifier.schemeName;
        if (!label) {
            if (identifier.type === 'Identifier') {
                label = 'Other Identifier';
            } else if (identifier.type === 'LegalIdentifier') {
                label = 'Other Legal Identifier';
            }
        }
        return label;
    }
}
