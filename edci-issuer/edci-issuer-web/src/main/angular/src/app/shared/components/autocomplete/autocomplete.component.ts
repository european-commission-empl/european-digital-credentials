import {
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewChild,
    AfterViewInit, ElementRef,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Constants } from '@shared/constants';
import { ItemSpecLiteView } from '@shared/models/item-spec-lite-view.model';
import { SelectedItemList } from '@shared/models/selected-item-list.modal';
import { SelectedTagItemList } from '@shared/models/selected-tag-item-list.model';
import {
    AssessmentSpecLiteView,
    EntitlementSpecLiteView,
    EntitlementSpecView,
    LearningAchievementSpecLiteView,
    LearningActivitySpecLiteView,
    LearningOutcomeSpecLiteView,
    OrganizationSpecLiteView,
    PagedResourcesAssessmentSpecLiteView,
    PagedResourcesDiplomaSpecLiteView,
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesLearningAchievementSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    PagedResourcesLearningOutcomeSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    V1Service,
} from '@shared/swagger';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';
import { UxAutoCompleteComponent, UxAutoCompleteTagComponent } from '@eui/core';
import { CredentialBuilderService } from '@services/credential-builder.service';

@Component({
    selector: 'edci-autocomplete',
    templateUrl: './autocomplete.component.html',
    styleUrls: ['./autocomplete.component.scss'],
})
export class AutocompleteComponent implements OnInit, AfterViewInit {
    @ViewChild('autocompleteTag') autocompleteTag: UxAutoCompleteTagComponent;
    @ViewChild('divMultipleInput') myDivElementRef: ElementRef;
    @ViewChild('autocomplete') autocomplete: UxAutoCompleteComponent;

    @Output() onEntityClicked: EventEmitter<{ type: string, oid: number}>
        = new EventEmitter<{ type: string, oid: number}>();

    private _isDisabled: boolean = false;

    @Input() set preSelectedItems(
        items:
            | PagedResourcesAssessmentSpecLiteView
            | PagedResourcesEntitlementSpecLiteView
            | PagedResourcesLearningAchievementSpecLiteView
            | PagedResourcesLearningActivitySpecLiteView
            | PagedResourcesLearningOutcomeSpecLiteView
            | PagedResourcesOrganizationSpecLiteView
            | AssessmentSpecLiteView
            | EntitlementSpecLiteView
            | LearningAchievementSpecLiteView
            | LearningActivitySpecLiteView
            | LearningOutcomeSpecLiteView
            | OrganizationSpecLiteView
    ) {
        if (this.isSingleSelection) {
            this.setSingleItem(
                items as
                    | AssessmentSpecLiteView
                    | EntitlementSpecLiteView
                    | LearningAchievementSpecLiteView
                    | LearningActivitySpecLiteView
                    | LearningOutcomeSpecLiteView
                    | OrganizationSpecLiteView
            );
        } else {
            this.setMultipleItems(
                items as
                    | PagedResourcesAssessmentSpecLiteView
                    | PagedResourcesEntitlementSpecLiteView
                    | PagedResourcesLearningAchievementSpecLiteView
                    | PagedResourcesLearningActivitySpecLiteView
                    | PagedResourcesLearningOutcomeSpecLiteView
                    | PagedResourcesOrganizationSpecLiteView
            );
        }
    }
    @Input() set isDisabled(disabled: boolean) {
        this._isDisabled = disabled;
        this.updateEnable();
    }
    @Input() entityType:
        | 'achievement'
        | 'assessment'
        | 'organization'
        | 'learningOutcome'
        | 'entitlement'
        | 'activity'
        | 'htmlTemplate';
    @Input() mainItemOid: number;
    @Input() isSingleSelection: boolean = false;
    @Input() defaultLanguage: string;

    @Output() selectionChange: EventEmitter<
        number | number[]
    > = new EventEmitter<number | number[]>();

    get isDisabled(): boolean {
        return this._isDisabled;
    }

    get item() {
        return this.autocompleteForm.get('item') as FormControl;
    }

    autocompleteForm = new FormGroup({
        item: new FormControl(null),
    });
    selectedItems: SelectedTagItemList[] = [];
    itemsOidList: number[] = [];
    searchList: SelectedTagItemList[] | SelectedItemList[] = [];
    isLoading: boolean = false;
    destroy$: Subject<boolean> = new Subject<boolean>();
    textToSearch: string;
    listAddedItemsFromAllResults: { [key: string]: SelectedTagItemList | SelectedItemList } = {};

    constructor(
        private api: V1Service,
        private translateService: TranslateService,
        private credentialBuilderService: CredentialBuilderService
    ) {}

    ngOnInit() {
        this.onItemValueChange();
    }

    ngAfterViewInit() {
        // prevent "ExpressionChangedAfterItHasBeenCheckedError"
        Promise.resolve(null).then(() => this.updateEnable());
    }

    onItemValueChange(): void {
        this.item.valueChanges.takeUntil(this.destroy$).subscribe((item) => {
            if (
                item &&
                item.length &&
                item[0].id === Constants.EMPTY_RESULT_ID
            ) {
                this.item.setValue(null);
            } else {
                if (this.hasItem()) {
                    this.selectionChange.emit(this.getItemOid());
                } else {
                    this.selectionChange.emit(null);
                }
            }
        });
    }

    onSearchItem(searchText: string): void {
        if (!!searchText && searchText.length >= Constants.MIN_CHAR_SEARCH) {
            this.listItems(this.searchString(searchText));
        } else {
            this.searchList = [];
        }
    }

    onClick() {
        if (!this.textToSearch && this.entityType) {
            this.searchAllItems();
        }
    }

    searchAllItems(): void {
        this.listItems(this.searchString(''));
    }

    onItemAdded(oid: number): void {
        if (this.isSingleSelection) {
            for (const iterator of this.searchList) {
                if (iterator.id === String(oid)) {
                    let selectedItem = {
                        id: String(iterator),
                        label: iterator[this.entityType].defaultTitle,
                        typeClass: null,
                        iconClass: null,
                        isDeletable: true
                    };
                    selectedItem[this.entityType] = iterator[this.entityType];
                    this.selectedItems = [selectedItem];
                    this.selectionChange.emit([iterator.id]);
                    break;
                }
            }
            this.addClickEventToTags();
        } else {
            this.selectedItems = this.selectedItems.filter(
                (item: SelectedTagItemList) => {
                    return item.id !== Constants.EMPTY_RESULT_ID;
                }
            );
            this.addClickEventToTags();
            if (oid) {
                this.itemsOidList.push(oid);
                this.selectionChange.emit(this.itemsOidList);
            }
        }
    }

    onItemRemoved(oid: number): void {
        if (oid) {
            const index = this.itemsOidList.indexOf(oid);
            if (index !== -1) {
                this.itemsOidList.splice(index, 1);
            }
            this.selectionChange.emit(this.itemsOidList);
        }
    }

    private addClickEventToTags() {
        // TODO: NEXT SPRINT
        /*const classOfLabels = '.ux-chips-list__chip-label';
        const isDisabled = this.credentialBuilderService.isNewEntityDisabled;
        setTimeout(() => {
            const children = this.myDivElementRef.nativeElement.querySelectorAll(classOfLabels);
            Array.from(children).forEach((e, i) => {
                if (!isDisabled) {
                    children[i].style.cursor = 'pointer';
                    if (this.entityType) {
                        children[i].id = this.selectedItems[i][this.entityType].oid;
                        children[i].addEventListener('click', (event) => {
                            if (event && event.currentTarget) {
                                this.onEntityClicked.emit({
                                    oid: event.currentTarget.id,
                                    type: this.entityType
                                });
                            }
                        });
                    }
                }
            });
        }, 100);*/
    }

    private setSingleItem(
        item:
            | AssessmentSpecLiteView
            | EntitlementSpecLiteView
            | LearningAchievementSpecLiteView
            | LearningActivitySpecLiteView
            | LearningOutcomeSpecLiteView
            | OrganizationSpecLiteView
    ): void {
        if (item) {
            let selectedItem = {
                id: String(item.oid),
                label: item.defaultTitle,
                typeClass: null,
                iconClass: null,
                isDeletable: true
            };
            selectedItem[this.entityType] = item;
            this.selectedItems = [selectedItem];
            this.addClickEventToTags();
            this.selectionChange.emit([item.oid]);
            this.autocompleteForm.patchValue({ item: selectedItem });
        }
    }

    private setMultipleItems(
        items:
            | PagedResourcesAssessmentSpecLiteView
            | PagedResourcesEntitlementSpecLiteView
            | PagedResourcesLearningAchievementSpecLiteView
            | PagedResourcesLearningActivitySpecLiteView
            | PagedResourcesLearningOutcomeSpecLiteView
            | PagedResourcesOrganizationSpecLiteView
    ): void {
        if (items && items.content) {
            if (this.selectedItems.length) {
                const validItems = this.preserveSelection(items);
                this.selectedItems = this.getItems(validItems.content);
                this.addClickEventToTags();
                this.setOids(validItems.content);
            } else {
                this.selectedItems = this.getItems(items.content);
                this.selectedItems.forEach(s => {
                    this.listAddedItemsFromAllResults[this.returnExistentElement(s).oid] = s;
                });
                this.addClickEventToTags();
                this.setOids(items.content);
            }
        }
    }

    private listItems(searchText: string): void {
        this.searchList = [];
        this.isLoading = true;
        this[`${this.entityType}Search`](searchText);
    }

    private achievementSearch(search: string): void {
        this.api
            .listLearningAchievement(
                'defaultTitle',
                'ASC',
                0,
                20,
                search,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (results: PagedResourcesLearningAchievementSpecLiteView) => {
                    if (results.content.length) {
                        this.searchList = this.getItems(results.content);
                    } else {
                        this.searchList = this.createEmptyResultsMessage();
                    }
                    this.textToSearch = '';
                }
            );
    }

    private assessmentSearch(search: string): void {
        this.api
            .listAssessment(
                'defaultTitle',
                'ASC',
                0,
                20,
                search,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe((results: PagedResourcesAssessmentSpecLiteView) => {
                if (results.content.length) {
                    this.searchList = this.getItems(results.content);
                } else {
                    this.searchList = this.createEmptyResultsMessage();
                }
                this.textToSearch = '';
            });
    }

    private organizationSearch(search: string): void {
        this.api
            .listOrganization(
                'defaultTitle',
                'ASC',
                0,
                20,
                search,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe((results: PagedResourcesOrganizationSpecLiteView) => {
                if (results.content.length) {
                    this.searchList = this.getItems(results.content);
                } else {
                    this.searchList = this.createEmptyResultsMessage();
                }
                this.textToSearch = '';
            });
    }

    private learningOutcomeSearch(search: string): void {
        this.api
            .listLearningOutcome(
                'defaultTitle',
                'ASC',
                0,
                20,
                search,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe((results: PagedResourcesLearningOutcomeSpecLiteView) => {
                if (results.content.length) {
                    this.searchList = this.getItems(results.content);
                } else {
                    this.searchList = this.createEmptyResultsMessage();
                }
                this.textToSearch = '';
            });
    }

    private entitlementSearch(search: string): void {
        this.api
            .listEntitlement(
                'defaultTitle',
                'ASC',
                0,
                20,
                search,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe((results: PagedResourcesEntitlementSpecLiteView) => {
                if (results.content.length) {
                    this.searchList = this.getItems(results.content);
                } else {
                    this.searchList = this.createEmptyResultsMessage();
                }
                this.textToSearch = '';
            });
    }

    private activitySearch(search: string): void {
        this.api
            .listLearningActivity(
                'defaultTitle',
                'ASC',
                0,
                20,
                search,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (results: PagedResourcesLearningActivitySpecLiteView) => {
                    if (results.content.length) {
                        this.searchList = this.getItems(results.content);
                    } else {
                        this.searchList = this.createEmptyResultsMessage();
                    }
                    this.textToSearch = '';
                }
            );
    }

    private htmlTemplateSearch(search: string): void {
        this.api
            .listDiploma(
                'defaultTitle',
                'ASC',
                0,
                20,
                search,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (results: PagedResourcesDiplomaSpecLiteView) => {
                    if (results.content.length) {
                        this.searchList = this.getItems(results.content);
                    } else {
                        this.searchList = this.createEmptyResultsMessage();
                    }
                    this.textToSearch = '';
                }
            );
    }

    private searchString(searchText: string): string {
        this.textToSearch = searchText;
        let oids: string = '';
        let oidList: number[] = [...this.itemsOidList];
        if (this.mainItemOid) {
            oidList.push(this.mainItemOid);
        }
        if (oidList.length > 0) {
            oids = ';oid!' + oidList.join(';oid!');
        }
        return `defaultTitle~${searchText}${oids}`;
    }

    private getItems(itemList: ItemSpecLiteView[]): SelectedTagItemList[] {
        let tagList: SelectedTagItemList[] = [];
        itemList.forEach((item) => {
            if (
                !this.defaultLanguage ||
                item.defaultLanguage === this.defaultLanguage
            ) {
                let pushItem = this.getTagItemList(item);
                pushItem[this.entityType] = item;
                tagList.push(pushItem);
            }
        });
        this.isLoading = false;
        return tagList;
    }

    private createEmptyResultsMessage(): SelectedTagItemList[] {
        this.isLoading = false;
        return [
            {
                id: Constants.EMPTY_RESULT_ID,
                label: this.translateService.instant(
                    'credential-builder.emptyResults'
                ),
                isDeletable: true,
                typeClass: null,
            },
        ];
    }

    private getTagItemList(item: ItemSpecLiteView): SelectedTagItemList {
        return {
            id: item.oid.toString(),
            label: item.defaultTitle,
            isDeletable: true,
            typeClass: null,
        };
    }

    private setOids(items: ItemSpecLiteView[]): void {
        this.itemsOidList = [];
        items.forEach((item) => {
            this.itemsOidList.push(item.oid);
        });
        this.selectionChange.emit(this.itemsOidList);
    }

    private hasItem(): boolean {
        return (
            (_get(this.item, 'value', false) &&
                _get(this.item, 'value[0].id', false)) ||
            _get(this.item, 'value.id', false)
        );
    }

    private getItemOid(): number {
        return this.item.value.length === 1
            ? this.item.value[0][this.entityType].oid
            : this.item.value[this.entityType].oid;
    }

    private updateEnable(): void {
        if (this.autocompleteTag) {
            this.autocompleteTag.setDisabledState(this.isDisabled);
        } else if (this.autocomplete) {
            this.autocomplete.setDisabledState(this.isDisabled);
        }
    }

    private preserveSelection(items): any {
        const itemsOfSelectedItems = this.selectedItems.map( c => {
            return this.returnExistentElement(c);
        });
        const newItems = {
            content: []
        };
        items.content.forEach((content) => {
            itemsOfSelectedItems.forEach((itemOfSelectedItem) => {
                if (content.oid === itemOfSelectedItem.oid) {
                    if (!content.defaultTitle) {
                        newItems.content.push(itemOfSelectedItem);
                    } else {
                        if (content.isNew) {
                            for (let k = 0; k < newItems.content.length; k++) {
                                if (content.oid === newItems.content[k].oid) {
                                    newItems.content[k] = content;
                                    break;
                                }
                            }
                        } else {
                            newItems.content.push(content);
                        }
                    }
                }
            });
            if (!itemsOfSelectedItems.map(it => it.oid ).includes(content.oid)) {
                if (content.isNew) {
                    newItems.content.push(content);
                }
            }
        });
        return newItems;
    }

    private returnExistentElement(e: any): LearningAchievementSpecLiteView | LearningOutcomeSpecLiteView | LearningActivitySpecLiteView
        | AssessmentSpecLiteView | OrganizationSpecLiteView | EntitlementSpecView {
        return e['achievement'] ||
        e['learningOutcome'] ||
        e['activity'] ||
        e['assessment'] ||
        e['organization'] ||
        e['entitlement'] || null;
    }
}
