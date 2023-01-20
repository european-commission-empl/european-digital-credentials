import { Component, Input } from '@angular/core';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { ActivityTabView } from '../../swagger';

@Component({
    selector: 'edci-viewer-activity-detail',
    templateUrl: './activity-detail.component.html',
    styleUrls: ['./activity-detail.component.scss'],
})
export class ActivityDetailComponent {
    private _activeActivity: ActivityTabView;
    @Input() set activeActivity(value: ActivityTabView) {
        this.isSubActivitiesExpanded = true;
        this.isInfluencedExpanded = true;
        this._activeActivity = value;
    }
    get activeActivity(): ActivityTabView {
        return this._activeActivity;
    }

    isSubActivitiesExpanded = true;
    isInfluencedExpanded = true;

    constructor(private entityLinkService: EntityLinkService) {}

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }
}
