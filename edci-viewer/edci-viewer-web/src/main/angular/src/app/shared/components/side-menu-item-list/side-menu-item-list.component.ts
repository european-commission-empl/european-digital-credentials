import { Component, Input, OnInit } from '@angular/core';
import {
    AchievementTabView,
    ActivityTabView,
    AssessmentTabView,
    EntitlementTabView,
    EuropassCredentialPresentationView,
    OrganizationTabView,
    CredentialSubjectTabView,
} from '../../swagger';

@Component({
    selector: 'edci-side-menu-item-list',
    templateUrl: './side-menu-item-list.component.html',
    styleUrls: ['./side-menu-item-list.component.scss'],
})
export class SideMenuItemListComponent implements OnInit {
    private _items: EuropassCredentialPresentationView;
    isSelected: boolean;
    isChildSelected: boolean;
    styleClass: string;
    arrayItems: [
        CredentialSubjectTabView,
        OrganizationTabView,
        Array<AchievementTabView>,
        Array<ActivityTabView>,
        Array<EntitlementTabView>
    ];

    @Input() set items(value: EuropassCredentialPresentationView) {
        this._items = value;
        // We use array since we want this order for the two first items
        this.arrayItems = [
            value.credentialSubject,
            value.issuerCredential,
            value.achievements,
            value.activities,
            value.entitlements,
        ];
    }
    get items() {
        return this._items;
    }

    subjectExpanded: boolean;
    issuerExpanded: boolean;
    achievementExpanded: boolean[] = [];
    activityExpanded: boolean[] = [];
    entitlementExpanded: boolean[] = [];

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

    onSubjectExpand(expanded: boolean): void {
        this.subjectExpanded = expanded;
    }

    onIssuerExpand(expanded: boolean): void {
        this.issuerExpanded = expanded;
    }

    onAchievementExpand(expanded: boolean, index: number): void {
        this.achievementExpanded[index] = expanded;
    }

    onActivityExpand(expanded: boolean, index: number): void {
        this.activityExpanded[index] = expanded;
    }

    onEntitlementExpand(expanded: boolean, index: number): void {
        this.entitlementExpanded[index] = expanded;
    }

    getName() {
        if (this.arrayItems[0].fullName === null) {
            return this.arrayItems[0].givenNames + ' ' + this.arrayItems[0].familyName;
        } else {
            return this.arrayItems[0].fullName;
        }
    }
}
