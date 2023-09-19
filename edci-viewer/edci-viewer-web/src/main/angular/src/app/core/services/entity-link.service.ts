import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { EntityModalInformation } from 'src/app/shared/model/entityModalInformation';
import {
    AchievementTabView,
    ActivityTabView,
    AssessmentTabView,
    EntitlementTabView,
} from 'src/app/shared/swagger';
import { ShareDataService } from './share-data.service';

@Injectable({
    providedIn: 'root',
})
export class EntityLinkService {
    constructor(private shareDataService: ShareDataService) {}

    changeSelection(id: string): void {
        this.shareDataService.emitHierarchyTree([
            ...this.shareDataService.hierarchyTree,
            id,
        ]);
    }
}
