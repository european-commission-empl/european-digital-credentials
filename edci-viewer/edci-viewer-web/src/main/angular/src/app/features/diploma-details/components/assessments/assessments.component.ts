import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { AssessmentTabView } from './../../../../shared/swagger/model/assessmentTabView';

@Component({
    selector: 'edci-viewer-assessments',
    templateUrl: './assessments.component.html',
    styleUrls: ['./assessments.component.scss'],
})
export class AssessmentsComponent implements OnInit, OnDestroy {
    activeAssessment: AssessmentTabView = this.shareDataService.activeEntity;
    activeId: string;

    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private shareDataService: ShareDataService) {}

    ngOnInit() {
        this.shareDataService
            .changeEntitySelection()
            .takeUntil(this.destroy$)
            .subscribe((assessment) => {
                this.activeAssessment = assessment;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true), this.destroy$.unsubscribe();
    }
}
