import { Component, Input } from '@angular/core';
import { AchievementTabView, AgentView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-achievement-main-panel',
    templateUrl: './achievement-main-panel.component.html',
    styleUrls: ['./achievement-main-panel.component.scss'],
})
export class AchievementMainPanelComponent {
    private _activeAchievement: AchievementTabView;
    @Input()
    set activeAchievement(value: AchievementTabView) {
        this.isAwardedByExpanded = true;
        this._activeAchievement = value;
    }
    get activeAchievement(): AchievementTabView {
        return this._activeAchievement;
    }
    isAwardedByExpanded = true;
    isPanelExpanded = true;
    constructor() {}

    toggleAwardedBy(): void {
        this.isAwardedByExpanded = !this.isAwardedByExpanded;
    }

    getAwardingBodyName(awardingBody: AgentView): string {
        const agentLabels = awardingBody.legalName ? awardingBody.legalName : awardingBody.fullName;
        return agentLabels ? agentLabels : 'Awarding Body';
    }
}
