import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
    AchievementTabView,
    ActivityTabView,
    AssessmentTabView,
    CredentialSubjectTabView,
    EntitlementTabView,
    OrganizationTabView,
} from 'src/app/shared/swagger';

@Component({
    selector: 'edci-menu-list',
    templateUrl: './menu-list.component.html',
    styleUrls: ['./menu-list.component.scss'],
})
export class MenuListComponent implements OnInit {
    private _items:
        | CredentialSubjectTabView
        | OrganizationTabView
        | AchievementTabView
        | ActivityTabView
        | EntitlementTabView
        | AssessmentTabView;
    private _parentTree: string[] = [];
    private _depthLevel = 1;

    @Input() set items(
        value:
            | CredentialSubjectTabView
            | OrganizationTabView
            | AchievementTabView
            | ActivityTabView
            | EntitlementTabView
            | AssessmentTabView
    ) {
        this._items = value;
    }
    get items() {
        return this._items;
    }

    @Input() set parentTree(value: string[]) {
        this._parentTree = value;
    }
    get parentTree(): string[] {
        return this._parentTree;
    }

    @Input() set depthLevel(value: number) {
        // After depth 7, further depth behaves the same as 7
        this._depthLevel = value > 7 ? 7 : value;
    }
    get depthLevel(): number {
        return this._depthLevel;
    }

    @Output() selected: EventEmitter<number | string> = new EventEmitter<
        number | string
    >();

    awardingBodyExpanded: boolean;
    directedByExpanded: boolean;
    conductedByExpanded: boolean;
    parentOrganizationExpanded: boolean;
    subAchievementExpanded: boolean[] = [];
    influencedByExpanded: boolean[] = [];
    provenByExpanded: boolean[] = [];
    entitledOwnerToExpanded: boolean[] = [];
    influencedExpanded: boolean[] = [];
    subActivitiesExpanded: boolean[] = [];
    subAssessmentsExpanded: boolean[] = [];
    subEntitlementsExpanded: boolean[] = [];

    constructor() {}

    ngOnInit() {}

    identify(
        index: number,
        item:
            | OrganizationTabView
            | AchievementTabView
            | ActivityTabView
            | EntitlementTabView
            | AssessmentTabView
    ) {
        return item.id;
    }

    onSingleExpand(expanded: boolean, entity: string): void {
        this[`${entity}Expanded`] = expanded;
    }

    onArrayExpand(expanded: boolean, index: number, entity: string): void {
        this[`${entity}Expanded`][index] = expanded;
    }

    onItemSelected(): void {
        this.selected.emit(this.depthLevel);
    }

    onChildSelected(): void {
        this.selected.emit(this.depthLevel);
    }

    parentList(id: string): string[] {
        return [...this.parentTree, id];
    }
}
