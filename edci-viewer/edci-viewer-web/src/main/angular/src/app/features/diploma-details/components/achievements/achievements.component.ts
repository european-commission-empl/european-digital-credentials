import { Component, OnDestroy, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { AchievementTabView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-viewer-achievements',
    templateUrl: './achievements.component.html',
    styleUrls: ['./achievements.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AchievementsComponent implements OnDestroy {
    achievements: AchievementTabView[] = this.shareDataService.achievements;
    activeId: string;
    activeAchievement: AchievementTabView;
    isLearningOutcomeExpanded: boolean = false;
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
        //         if (this.achievements) {
        //             if (!this.activeId) {
        //                 this.setInitialActiveAchievement();
        //             } else {
        //                 this.setAchievementById();
        //             }
        //         }
        //     });
        this.shareDataService
            .changeEntitySelection()
            .takeUntil(this.destroy$)
            .subscribe((achievement) => {
                this.activeAchievement = achievement;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    // onSelect(event) {
    //     this.achievements.forEach((achievement: AchievementTabView) => {
    //         achievement['active'] = false;
    //         if (achievement.subAchievements) {
    //             achievement.subAchievements.forEach(
    //                 (part: AchievementTabView) => {
    //                     part['active'] = false;
    //                 }
    //             );
    //         }
    //     });
    //     event.active = true;
    //     this.activeAchievement = event;
    //     this.setQueryParams();
    // }

    // goToLinkedDestination(id: string, destination: string) {
    //     this.router.navigate([`/diploma-details/${destination}`], {
    //         queryParams: { id: id },
    //     });
    // }

    // private setInitialActiveAchievement() {
    //     this.achievements.forEach(
    //         (achievement: AchievementTabView, i: number) => {
    //             if (i === 0) {
    //                 achievement['active'] = true;
    //             } else {
    //                 achievement['active'] = false;
    //             }
    //             if (achievement.subAchievements) {
    //                 achievement.subAchievements.forEach(
    //                     (part: AchievementTabView) => {
    //                         part['active'] = false;
    //                     }
    //                 );
    //             }
    //         }
    //     );
    //     this.activeAchievement = this.achievements[0];
    //     this.setQueryParams();
    // }

    // private setAchievementById() {
    //     this.achievements.forEach((achievement: AchievementTabView) => {
    //         if (achievement.id === this.activeId) {
    //             achievement['active'] = true;
    //             this.activeAchievement = achievement;
    //         } else {
    //             achievement['active'] = false;
    //         }
    //         if (achievement.subAchievements) {
    //             achievement.subAchievements.forEach(
    //                 (part: AchievementTabView) => {
    //                     if (part.id === this.activeId) {
    //                         part['active'] = true;
    //                         this.activeAchievement = part;
    //                     } else {
    //                         part['active'] = false;
    //                     }
    //                 }
    //             );
    //         }
    //     });
    // }

    // private setQueryParams() {
    //     this.router.navigate([], {
    //         relativeTo: this.activatedRoute,
    //         queryParams: { id: this.activeAchievement.id },
    //         queryParamsHandling: 'merge',
    //     });
    // }
}
