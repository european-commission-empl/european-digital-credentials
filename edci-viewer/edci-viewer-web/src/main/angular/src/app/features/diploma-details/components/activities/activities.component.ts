import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { ActivityTabView } from 'src/app/shared/swagger';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-viewer-activities',
    templateUrl: './activities.component.html',
    styleUrls: ['./activities.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class ActivitiesComponent implements OnInit, OnDestroy {
    activeId: string;
    activeActivity: ActivityTabView = this.shareDataService.activeEntity;
    destroy$: Subject<boolean> = new Subject<boolean>();
    constructor(private shareDataService: ShareDataService) {}

    ngOnInit() {
        this.shareDataService
            .changeEntitySelection()
            .pipe(takeUntil(this.destroy$))
            .subscribe((activity) => {
                this.activeActivity = activity;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }
}
