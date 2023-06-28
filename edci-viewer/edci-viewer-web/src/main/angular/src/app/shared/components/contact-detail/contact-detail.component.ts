import { Component, OnInit, Input } from '@angular/core';
import { ContactPointFieldView, LocationFieldView } from '@shared/swagger';

@Component({
    selector: 'edci-contact-detail',
    templateUrl: './contact-detail.component.html',
    styleUrls: ['./contact-detail.component.scss']
})
export class ContactDetailComponent implements OnInit {

    @Input() contact: ContactPointFieldView;

    constructor() { }

    ngOnInit(): void {
    }

}
