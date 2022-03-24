import {
    AfterViewInit,
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewChild,
    Renderer2,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { UxAutoCompleteComponent, UxAutoCompleteTagComponent } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
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

@Component({
    selector: 'edci-autocomplete',
    templateUrl: './autocomplete.component.html',
    styleUrls: ['./autocomplete.component.scss'],
})
export class AutocompleteComponent implements OnInit, AfterViewInit {
    @ViewChild('autocompleteTag') autocompleteTag: UxAutoCompleteTagComponent;
    @ViewChild('divMultipleInput') myDivElementRef: ElementRef;
    @ViewChild('autocomplete') autocomplete: UxAutoCompleteComponent;

    @Output() onEntityClicked: EventEmitter<{ type: string; oid: number }> =
        new EventEmitter<{ type: string; oid: number }>();

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

    @Output() selectedItemsChange: EventEmitter<SelectedTagItemList[]> =
        new EventEmitter<SelectedTagItemList[]>();
    @Output() selectionChange: EventEmitter<number | number[]> =
        new EventEmitter<number | number[]>();

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
    listAddedItemsFromAllResults: {
        [key: string]: SelectedTagItemList | SelectedItemList;
    } = {};

    constructor(
        private api: V1Service,
        private renderer: Renderer2,
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
        if (oid) {
            this.itemsOidList.push(oid);
            this.selectionChange.emit(this.itemsOidList);
            this.selectedItemsChange.emit(this.selectedItems);
        }
    }

    onItemRemoved(oid: number): void {
        if (oid) {
            const index = this.itemsOidList.indexOf(oid);
            if (index !== -1) {
                this.itemsOidList.splice(index, 1);
            }
            if (this.isSingleSelection) {
                this.showSingleInput();
            }
            this.selectionChange.emit(
                this.itemsOidList.length === 0 && this.isSingleSelection
                    ? null
                    : this.itemsOidList
            );
        }
    }

    onSelectionChange(selection: SelectedTagItemList[]) {
        this.selectedItems = selection;
        this.selectedItems = this.selectedItems.filter(
            (item: SelectedTagItemList) => {
                return item.id !== Constants.EMPTY_RESULT_ID;
            }
        );
        if (this.isSingleSelection) {
            this.hideSingleInput();
        }
        if (this.selectedItems.length > 0) {
            this.addClickEventToTags();
        }
    }

    private addClickEventToTags() {
        const classOfLabels = '.ux-chips-list__chip-label';
        const isDisabled = this.credentialBuilderService.isNewEntityDisabled;
        const elementEventHandler = (event) => {
            if (event && event.currentTarget) {
                this.onEntityClicked.emit({
                    oid: event.currentTarget.id,
                    type: this.entityType,
                });
            }
        };
        setTimeout(() => {
            const children =
                this.myDivElementRef.nativeElement.querySelectorAll(
                    classOfLabels
                );

            Array.from(children).forEach((e, i) => {
                if (!isDisabled) {
                    children[i].style.cursor = 'pointer';
                    if (this.entityType) {
                        // Prevent adding multiple listeners to same tag
                        if (!children[i].id) {
                            children[i].id =
                                this.selectedItems[i][this.entityType].oid;
                            this.renderer.listen(
                                children[i],
                                'click',
                                elementEventHandler
                            );
                        }
                    }
                }
            });
        }, 100);
    }

    private hideSingleInput() {
        const classOfLabel = '.mat-chip-list';
        const classOfAutocomplete = '.ux-autocomplete-tag__field-wrapper';
        setTimeout(() => {
            const matchip =
                this.myDivElementRef.nativeElement.querySelectorAll('mat-chip');
            const autocomplete =
                this.myDivElementRef.nativeElement.querySelectorAll(
                    classOfAutocomplete
                )[0];
            const parentLabel =
                this.myDivElementRef.nativeElement.querySelectorAll(
                    classOfLabel
                )[0];
            if (parentLabel && autocomplete && matchip && matchip.length > 0) {
                matchip[0].style.justifyContent = 'space-between';
                autocomplete.style.display = 'none';
                parentLabel.style.width = '100%';
                parentLabel.children[0].style.display = 'block';
            }
        }, 1);
    }

    private showSingleInput() {
        const classOfAutocomplete = '.ux-autocomplete-tag__field-wrapper';
        const autocomplete =
            this.myDivElementRef.nativeElement.querySelectorAll(
                classOfAutocomplete
            )[0];
        if (autocomplete) {
            autocomplete.style.display = '';
        }
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
                isDeletable: true,
            };
            selectedItem[this.entityType] = item;
            this.selectedItems = [selectedItem];
            this.addClickEventToTags();
            if (this.selectedItems.length) {
                this.hideSingleInput();
            }
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
                this.selectedItems = this.getItems(items.content);
                this.addClickEventToTags();
                this.setOids(items.content);
            } else {
                this.selectedItems = this.getItems(items.content);
                this.selectedItems.forEach((s) => {
                    this.listAddedItemsFromAllResults[
                        this.returnExistentElement(s).oid
                    ] = s;
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
                        if (this.searchList.length === 0) {
                            this.searchList = this.createEmptyResultsMessage();
                        }
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
                    if (this.searchList.length === 0) {
                        this.searchList = this.createEmptyResultsMessage();
                    }
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
                    if (this.searchList.length === 0) {
                        this.searchList = this.createEmptyResultsMessage();
                    }
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
                    if (this.searchList.length === 0) {
                        this.searchList = this.createEmptyResultsMessage();
                    }
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
                    if (this.searchList.length === 0) {
                        this.searchList = this.createEmptyResultsMessage();
                    }
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
                        if (this.searchList.length === 0) {
                            this.searchList = this.createEmptyResultsMessage();
                        }
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
            .subscribe((results: PagedResourcesDiplomaSpecLiteView) => {
                if (results.content.length) {
                    this.searchList = this.getItems(results.content);
                    if (this.searchList.length === 0) {
                        this.searchList = this.createEmptyResultsMessage();
                    }
                } else {
                    this.searchList = this.createEmptyResultsMessage();
                }
                this.textToSearch = '';
            });
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
                if (!item.defaultTitle) {
                    item = this.addMissingLabel(item);
                }
                item['isNew'] = false;
                let pushItem = this.getTagItemList(item);
                pushItem[this.entityType] = item;
                tagList.push(pushItem);
            }
        });
        this.isLoading = false;

        return tagList;
    }

    private addMissingLabel(itemNoLabel: ItemSpecLiteView): ItemSpecLiteView {
        const noLabelOid: number = itemNoLabel.oid;
        let itemWithLabel: ItemSpecLiteView;
        this.selectedItems.forEach((item) => {
            if (this.returnExistentElement(item).oid === noLabelOid) {
                itemWithLabel = this.returnExistentElement(item);
            }
        });
        return itemWithLabel;
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

    private returnExistentElement(
        e: any
    ):
        | LearningAchievementSpecLiteView
        | LearningOutcomeSpecLiteView
        | LearningActivitySpecLiteView
        | AssessmentSpecLiteView
        | OrganizationSpecLiteView
        | EntitlementSpecView {
        return (
            e['achievement'] ||
            e['learningOutcome'] ||
            e['activity'] ||
            e['assessment'] ||
            e['organization'] ||
            e['entitlement'] ||
            null
        );
    }
}
