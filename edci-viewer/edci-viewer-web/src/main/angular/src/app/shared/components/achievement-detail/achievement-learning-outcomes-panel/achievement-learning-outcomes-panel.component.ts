import { Component, Input, OnInit } from '@angular/core';
import { AchievementTabView, LinkFieldView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-achievement-learning-outcomes-panel',
    templateUrl: './achievement-learning-outcomes-panel.component.html',
    styleUrls: ['./achievement-learning-outcomes-panel.component.scss'],
})
export class AchievementLearningOutcomesPanelComponent implements OnInit {
    private _activeAchievement: AchievementTabView;
    @Input()
    set activeAchievement(value: AchievementTabView) {
        // Resets learning outcome expand state.
        this.isLearningOutcomeExpanded = [];
        this.isPanelExpanded = true;
        this._activeAchievement = value;
    }
    get activeAchievement(): AchievementTabView {
        return this._activeAchievement;
    }
    isLearningOutcomeExpanded: boolean[] = [];
    isPanelExpanded: boolean = true;
    sortedRelatedSkills: {
        [key: string]: LinkFieldView[];
    }[] = [];
    constructor() {}

    ngOnInit(): void {
        if (
            this.activeAchievement &&
            this.activeAchievement.specifiedBy &&
            this.activeAchievement.specifiedBy.learningOutcome
        ) {
            this.activeAchievement.specifiedBy.learningOutcome.forEach(
                (learningOutcome) => {
                    this.sortedRelatedSkills.push(
                        this.sortRelatedSkillsByTargetFramework(
                            learningOutcome.relatedSkill
                        )
                    );
                }
            );
        }
    }

    toggleLearningOutcome(index: number): void {
        this.isLearningOutcomeExpanded[index] =
            !this.isLearningOutcomeExpanded[index];
    }

    private sortRelatedSkillsByTargetFramework(
        relatedSkills: LinkFieldView[]
    ): {
        [key: string]: LinkFieldView[];
    } {
        if (relatedSkills) {
            let sortedRelatedSkills: {
                [key: string]: LinkFieldView[];
            } = {};
            relatedSkills.forEach((skill: LinkFieldView) => {
                if (!sortedRelatedSkills[skill.targetFramework]) {
                    sortedRelatedSkills[skill.targetFramework] = [];
                }
                sortedRelatedSkills[skill.targetFramework].push(skill);
            });
            return sortedRelatedSkills;
        }
    }
}
