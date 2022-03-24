import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { ActivityTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-viewer-activities',
    templateUrl: './activities.component.html',
    styleUrls: ['./activities.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class ActivitiesComponent implements OnInit, OnDestroy {
    activities: ActivityTabView[] = this.shareDataService.activities;
    activeId: string;
    activeActivity: ActivityTabView = this.shareDataService.activeEntity;
    destroy$: Subject<boolean> = new Subject<boolean>();
    constructor(
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private shareDataService: ShareDataService
    ) {
        // this.activatedRoute.queryParams
        //     .takeUntil(this.destroy$)
        //     .subscribe((params) => {
        //         this.activeId = params.id;
        //         if (this.activities) {
        //             if (!this.activeId) {
        //                 this.setInitialActiveActivity();
        //             } else {
        //                 this.setActivityById();
        //             }
        //         }
        //     });
    }

    ngOnInit() {
        this.shareDataService
            .changeEntitySelection()
            .takeUntil(this.destroy$)
            .subscribe((activity) => {
                this.activeActivity = activity;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    // onSelect(event) {
    //     this.activities.forEach((activity: ActivityTabView) => {
    //         activity['active'] = false;
    //         if (activity.subActivities) {
    //             activity.subActivities.forEach((part: ActivityTabView) => {
    //                 part['active'] = false;
    //             });
    //         }
    //     });
    //     event['active'] = true;
    //     this.activeActivity = event;
    //     this.setQueryParams();
    // }

    // private setInitialActiveActivity() {
    //     this.activities.forEach((activity: ActivityTabView, i: number) => {
    //         if (i === 0) {
    //             activity['active'] = true;
    //         } else {
    //             activity['active'] = false;
    //         }
    //         if (activity.subActivities) {
    //             activity.subActivities.forEach((part: ActivityTabView) => {
    //                 part['active'] = false;
    //             });
    //         }
    //     });
    //     this.activeActivity = this.activities[0];
    //     this.setQueryParams();
    // }

    // private setActivityById() {
    //     this.activities.forEach((activity: ActivityTabView) => {
    //         if (activity.id === this.activeId) {
    //             activity['active'] = true;
    //             this.activeActivity = activity;
    //         } else {
    //             activity['active'] = false;
    //         }
    //         if (activity.subActivities) {
    //             activity.subActivities.forEach((part: ActivityTabView) => {
    //                 if (part.id) {
    //                     if (part.id === this.activeId) {
    //                         part['active'] = true;
    //                         this.activeActivity = part;
    //                     }
    //                 } else {
    //                     part['active'] = false;
    //                 }
    //             });
    //         }
    //     });
    // }

    // private setQueryParams() {
    //     this.router.navigate([], {
    //         relativeTo: this.activatedRoute,
    //         queryParams: { id: this.activeActivity.id },
    //         queryParamsHandling: 'merge',
    //     });
    // }
}
