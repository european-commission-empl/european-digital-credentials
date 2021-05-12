import { Component, Input } from '@angular/core';
import { UxLink } from '@eui/core';
import { IssuerService } from '@services/issuer.service';

@Component({
    selector: 'edci-breadcrumb',
    templateUrl: './breadcrumb.component.html',
    styleUrls: ['./breadcrumb.component.scss']
})
export class BreadCrumbComponent {
    @Input() parts: UxLink[] = [];
    @Input() activePage: string = '';

    get canOverview(): boolean {
        return this.issuerService.getCredentials().length > 0;
    }

    constructor(private issuerService: IssuerService) { }

}
