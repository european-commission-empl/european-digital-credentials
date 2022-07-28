import { Component, OnDestroy, ViewEncapsulation } from '@angular/core';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { AchievementTabView } from 'src/app/shared/swagger';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-viewer-achievements',
    templateUrl: './achievements.component.html',
    styleUrls: ['./achievements.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AchievementsComponent implements OnDestroy {
    activeId: string;
    activeAchievement: AchievementTabView;
    isLearningOutcomeExpanded: boolean = false;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private shareDataService: ShareDataService) {
        this.shareDataService
            .changeEntitySelection()
            .pipe(takeUntil(this.destroy$))
            .subscribe((achievement) => {
                this.activeAchievement = achievement;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }
}
