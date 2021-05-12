import { Component, OnInit, Input } from '@angular/core';
import { Constants } from '@shared/constants';
import { Progress } from '@core/models/DTO/progress.model';

@Component({
    selector: 'edci-progress-bar',
    templateUrl: './progress-bar.component.html'
})
export class ProgressbarComponent implements OnInit {

    @Input() actualExport: Progress;
    @Input() titleProgress: string = '';
    constants = Constants;

    constructor() {}

    ngOnInit() {}

}
