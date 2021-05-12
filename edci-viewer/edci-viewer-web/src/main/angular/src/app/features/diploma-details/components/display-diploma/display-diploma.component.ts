import { Component } from '@angular/core';

@Component({
    selector: 'edci-viewer-display-diploma',
    templateUrl: './display-diploma.component.html',
    styleUrls: ['./display-diploma.component.scss']
})
export class DisplayDiplomaComponent {
    diplomaImg: string = sessionStorage.getItem('diplomaImg');

    constructor() {}
}
