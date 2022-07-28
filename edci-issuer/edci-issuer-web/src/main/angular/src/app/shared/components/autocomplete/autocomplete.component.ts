import {
    AfterViewInit,
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnInit,
    Output,
    Renderer2,
    ViewChild,
    OnDestroy,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { EuiAutocompleteComponent } from '@eui/components/eui-autocomplete';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { Constants, Entities } from '@shared/constants';
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
import { debounceTime, pairwise, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-autocomplete',
    templateUrl: './autocomplete.component.html',
    styleUrls: ['./autocomplete.component.scss'],
})
export class AutocompleteComponent implements OnInit, AfterViewInit, OnDestroy {
    @ViewChild('divMultipleInput') myDivElementRef: ElementRef;
    @ViewChild('autocomplete') autocomplete: EuiAutocompleteComponent;
    @ViewChild('autocompleteTag') autocompleteTag: EuiAutocompleteComponent;

    @Output() onEntityClicked: EventEmitter<{ type: string; oid: number }> =
        new EventEmitter<{ type: string; oid: number }>();

    private _isDisabled: boolean = false;
    @Input() isSingleSelection: boolean = false;
    @Input() entityType: Entities;

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
    }

    @Input() mainItemOid: number;
    @Input() defaultLanguage: string;
    @Input() isPrimaryLanguage: boolean = true;

    @Output() selectionChange: EventEmitter<number | number[]> =
        new EventEmitter<number | number[]>();

    get isDisabled(): boolean {
        return this._isDisabled;
    }

    get item() {
        return this.autocompleteForm.get('item') as FormControl;
    }

    get tagItem() {
        return this.autocompleteForm.get('tagItem') as FormControl;
    }

    autocompleteForm = new FormGroup({
        item: new FormControl(null),
        tagItem: new FormControl(null),
    });

    itemsOidList: number[] = [];
    searchList: SelectedTagItemList[] | SelectedItemList[] = [];
    isLoading: boolean = false;
    destroy$: Subject<boolean> = new Subject<boolean>();
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
        this.onSearchItem('');
        if (this.isSingleSelection) {
            this.itemValueChange();
        } else {
            this.tagItemValueChange();
        }
    }

    ngAfterViewInit() {
        if (!this.isSingleSelection) {
            this.checkChipsAdded();
            this.autocompleteTag.inputChange
                /**
                 * Getting previous values to prevent triggering same search multiple times.
                 * [pairwise()]
                 * (https://rxjs.dev/api/index/function/pairwise)
                 * Waiting 250ms before triggering the search.
                 * [debounceTime(250)]
                 * (https://rxjs.dev/api/operators/debounceTime)
                 */
                .pipe(pairwise(), debounceTime(250), takeUntil(this.destroy$))
                .subscribe(([prev, next]) => {
                    if (prev !== next) {
                        // If the value does not have "label" we will use an empty string.
                        this.onSearchItem(next || '');
                    }
                });
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    /**
     * Since the chips are added asynchronously we need
     * the MutationObserver to tell us when the chips are
     * on the DOM to add the click event on them.
     */
    private checkChipsAdded(): void {
        // Should only be one mat-chip-list-wrapper class for component
        const chipList = this.myDivElementRef.nativeElement.querySelector(
            '.mat-chip-list-wrapper'
        );
        // MutationObserver that will trigger on observe

        const changes: MutationObserver = new MutationObserver(
            (mutations: MutationRecord[]) => {
                mutations.forEach((mutation: MutationRecord) => {
                    // We only want the DOM changes that add Nodes to 'mat-chip-list-wrapper'
                    // We only want to see when chips are added.
                    if (mutation?.addedNodes?.length > 0) {
                        mutation.addedNodes.forEach((node: Node) => {
                            // We check if the node has children to sort out the comments
                            if (node?.childNodes?.length > 0) {
                                this.addClickEventToTag(node as HTMLElement);
                            }
                        });
                    }
                });
            }
        );
        // Observe the chipList for DOM changes
        changes.observe(chipList, {
            attributes: true,
            childList: true,
            characterData: true,
        });
    }

    private itemValueChange(): void {
        this.item.valueChanges
            /**
             * Getting previous values to prevent triggering same search multiple times.
             * [pairwise()]
             * (https://rxjs.dev/api/index/function/pairwise)
             * Waiting 250ms before triggering the search.
             * [debounceTime(250)]
             * (https://rxjs.dev/api/operators/debounceTime)
             */
            .pipe(pairwise(), debounceTime(250), takeUntil(this.destroy$))
            .subscribe(([prev, next]) => {
                /**
                 * If empty result is selected, remove it and empty search
                 */
                if (next?.id === Constants.EMPTY_RESULT_ID) {
                    this.item.setValue(null);
                    this.onSearchItem('');
                } else {
                    if (this.hasItem()) {
                        this.selectionChange.emit(this.getItemOid());
                        // Display the div with event when element is selected
                        this.setEventDiv();
                    } else {
                        // Remove the div with event when no element is selected
                        this.removeDiv();
                        this.selectionChange.emit(null);
                    }
                }
                if (this.needsSearch(prev, next)) {
                    // If the value does not have "label" we will use an empty string.
                    this.onSearchItem(next?.label || '');
                }
            });
    }

    private setEventDiv(): void {
        const elementEvent = this.myDivElementRef?.nativeElement;
        if (elementEvent) {
            // We need to overwrite the "display: none" from remove
            this.renderer.setStyle(elementEvent, 'display', 'initial');
            this.renderer.setStyle(elementEvent, 'width', `fit-content`);
            // Setting oid as attribute to use on event emitter (see: openEntityModal)
            elementEvent.setAttribute('oid', this.item.value.id);
            this.setEventElement(elementEvent);
        }
    }

    private removeDiv(): void {
        const elementEvent = this.myDivElementRef?.nativeElement;
        if (elementEvent) {
            // Remove styles and 'click' listener
            this.renderer.setStyle(elementEvent, 'display', 'none');
            this.renderer.setStyle(elementEvent, 'cursor', 'default');
            this.renderer.listen(elementEvent, 'click', () => false);
            // Event no longer here, set attribute to false
            this.renderer.setAttribute(
                elementEvent,
                Constants.OPEN_ENTITY_MODAL_CLICK_EVENT,
                'false'
            );
        }
    }

    private addClickEventToTag(chipElement: HTMLElement): void {
        const classOfLabels = '.eui-chip__content-container';
        const isDisabled = this.credentialBuilderService.isNewEntityDisabled;
        const elementEvent = chipElement.querySelector(classOfLabels);
        // Get euiInternalId of DOM element
        const euiInternalId = (
            chipElement.firstChild as HTMLElement
        ).getAttribute(Constants.EUI_INTERNAL_ID_ATTRIBUTE);

        if (euiInternalId !== undefined && euiInternalId !== null) {
            if (!isDisabled) {
                // Setting oid as attribute to use on event emitter (see: openEntityModal)
                elementEvent.setAttribute('oid', this.getEntityOid(euiInternalId));
                this.setEventElement(elementEvent);
            }
        }
    }

    private getEntityOid(euiInternalId: string): string {
        // Matching the DOM euiInternalId with the elements on tagItem to find the oid
        // entity oid is stored on id property
        return this.tagItem.value.find(
            (chip: SelectedTagItemList) => chip.euiInternalId === euiInternalId
        ).id;
    }

    // Sets common properties for single & multi autocomplete events
    private setEventElement(elementEvent): void {
        const hasEvent: 'true' | 'false' | null = elementEvent.getAttribute(
            Constants.OPEN_ENTITY_MODAL_CLICK_EVENT
        );
        // Adding cursor style
        this.renderer.setStyle(elementEvent, 'cursor', 'pointer');
        // Prevent multiple listeners in same tag (failsafe).
        if (!hasEvent || hasEvent === 'false') {
            // Adding 'click' listener
            this.renderer.listen(elementEvent, 'click', (event) =>
                this.openEntityModal(event)
            );
        }
        // Attribute to keep track of the events to prevent multiple events on same tag
        this.renderer.setAttribute(
            elementEvent,
            Constants.OPEN_ENTITY_MODAL_CLICK_EVENT,
            'true'
        );
    }

    private openEntityModal(event): void {
        if (event && event.currentTarget) {
            this.onEntityClicked.emit({
                oid: event.currentTarget.getAttribute('oid'),
                type: this.entityType,
            });
        }
    }

    private tagItemValueChange(): void {
        this.tagItem.valueChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe((value) => {
                /**
                 * Check if empty value has been added.
                 * If added remove it.
                 */
                const filteredItems = value?.filter(
                    (item: SelectedTagItemList) => {
                        return item.id !== Constants.EMPTY_RESULT_ID;
                    }
                );

                if (filteredItems?.length < value?.length) {
                    this.autocompleteForm.patchValue({
                        tagItem: filteredItems,
                    });
                }
                this.setOids(this.tagItem.value, false);
            });
    }

    private onSearchItem(searchText: string): void {
        if (typeof searchText === 'string') {
            if (
                !!searchText &&
                searchText.length >= Constants.MIN_CHAR_SEARCH
            ) {
                this.listItems(this.searchString(searchText));
            } else if (searchText === '') {
                this.listItems('');
            }
        }
    }

    private needsSearch(prev, next): boolean {
        /**
         * Using prev & next to prevent duplicated requests.
         * prev & next can be string or objects, we will compare the vars
         * for both cases. In obj case we compare the "label" key.
         * If value does not have id it is not a selected object but text.
         */
        const notEqual: boolean =
            typeof next === 'string'
                ? prev !== next
                : prev?.label !== next?.label;

        return notEqual && (!next || !next?.id);
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
            let selectedItem: SelectedItemList = {
                id: String(item.oid),
                euiInternalId: String(item.oid),
                label: item.defaultTitle,
                typeClass: null,
                sizeClass: 'euiSizeS',
                iconClass: null,
                isRemovable: true,
                isOutline: false,
                isRounded: false,
                dragAndDropSource: null,
            };
            selectedItem[this.entityType] = item;
            // this.selectedItems = [selectedItem];
            // if (this.selectedItems.length) {
            //     this.hideSingleInput();
            // }
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
            this.setOids(items.content);
            this.autocompleteForm.patchValue({
                tagItem: this.getItems(items.content),
            });
        }
    }

    private listItems(searchText: string): void {
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
            .pipe(takeUntil(this.destroy$))
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
                    this.isLoading = false;
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
            .pipe(takeUntil(this.destroy$))
            .subscribe((results: PagedResourcesAssessmentSpecLiteView) => {
                if (results.content.length) {
                    this.searchList = this.getItems(results.content);
                    if (this.searchList.length === 0) {
                        this.searchList = this.createEmptyResultsMessage();
                    }
                } else {
                    this.searchList = this.createEmptyResultsMessage();
                }
                this.isLoading = false;
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
            .pipe(takeUntil(this.destroy$))
            .subscribe((results: PagedResourcesOrganizationSpecLiteView) => {
                if (results.content.length) {
                    this.searchList = this.getItems(results.content);
                    if (this.searchList.length === 0) {
                        this.searchList = this.createEmptyResultsMessage();
                    }
                } else {
                    this.searchList = this.createEmptyResultsMessage();
                }
                this.isLoading = false;
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
            .pipe(takeUntil(this.destroy$))
            .subscribe((results: PagedResourcesLearningOutcomeSpecLiteView) => {
                if (results.content.length) {
                    this.searchList = this.getItems(results.content);
                    if (this.searchList.length === 0) {
                        this.searchList = this.createEmptyResultsMessage();
                    }
                } else {
                    this.searchList = this.createEmptyResultsMessage();
                }
                this.isLoading = false;
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
            .pipe(takeUntil(this.destroy$))
            .subscribe((results: PagedResourcesEntitlementSpecLiteView) => {
                if (results.content.length) {
                    this.searchList = this.getItems(results.content);
                    if (this.searchList.length === 0) {
                        this.searchList = this.createEmptyResultsMessage();
                    }
                } else {
                    this.searchList = this.createEmptyResultsMessage();
                }
                this.isLoading = false;
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
            .pipe(takeUntil(this.destroy$))
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
                    this.isLoading = false;
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
            .pipe(takeUntil(this.destroy$))
            .subscribe((results: PagedResourcesDiplomaSpecLiteView) => {
                if (results.content.length) {
                    this.searchList = this.getItems(results.content);
                    if (this.searchList.length === 0) {
                        this.searchList = this.createEmptyResultsMessage();
                    }
                } else {
                    this.searchList = this.createEmptyResultsMessage();
                }
                this.isLoading = false;
            });
    }

    private searchString(searchText: string): string {
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
                // if (!item.defaultTitle) {
                //     item = this.addMissingLabel(item);
                // }
                item['isNew'] = false;
                let pushItem = this.getTagItemList(item);
                pushItem[this.entityType] = item;
                tagList.push(pushItem);
            }
        });

        return tagList;
    }

    // private addMissingLabel(itemNoLabel: ItemSpecLiteView): ItemSpecLiteView {
    //     const noLabelOid: number = itemNoLabel.oid;
    //     let itemWithLabel: ItemSpecLiteView;
    //     this.selectedItems.forEach((item) => {
    //         if (this.returnExistentElement(item).oid === noLabelOid) {
    //             itemWithLabel = this.returnExistentElement(item);
    //         }
    //     });
    //     return itemWithLabel;
    // }

    private createEmptyResultsMessage(): SelectedTagItemList[] {
        return [
            {
                id: Constants.EMPTY_RESULT_ID,
                euiInternalId: Constants.EMPTY_RESULT_ID,
                label: this.translateService.instant(
                    'credential-builder.emptyResults'
                ),
                isRemovable: true,
                typeClass: null,
                sizeClass: null,
                iconClass: null,
                isOutline: false,
                isRounded: false,
                dragAndDropSource: null,
            },
        ];
    }

    private getTagItemList(item: ItemSpecLiteView): SelectedTagItemList {
        return {
            id: item.oid.toString(),
            euiInternalId: item.oid.toString(),
            label: item.defaultTitle,
            isRemovable: true,
            typeClass: 'primary',
            sizeClass: 'euiSizeM',
            iconClass: null,
            isOutline: false,
            isRounded: false,
            dragAndDropSource: null,
        };
    }

    private setOids(
        items: ItemSpecLiteView[],
        fromInput: boolean = true
    ): void {
        this.itemsOidList = [];
        if (items) {
            items.forEach((item) => {
                if (fromInput) {
                    this.itemsOidList.push(item.oid);
                } else {
                    this.itemsOidList.push(item[this.entityType].oid);
                }
            });
        }

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
