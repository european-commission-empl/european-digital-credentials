import {
    AfterViewInit,
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { EuiAutocompleteComponent } from '@eui/components/eui-autocomplete';
import { UxLanguage } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { Constants } from '@shared/constants';
import { SelectedItemList } from '@shared/models/selected-item-list.modal';
import { SelectedTagItemList } from '@shared/models/selected-tag-item-list.model';
import { ExtractLabelPipe } from '@shared/pipes/multilingual.pipe';
import { CodeDTView, Page, V1Service } from '@shared/swagger';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';
import { debounceTime, pairwise, takeUntil, take } from 'rxjs/operators';

@Component({
    selector: 'edci-controlled-list',
    templateUrl: './controlled-list.component.html',
    styleUrls: ['./controlled-list.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class ControlledListComponent
implements OnInit, AfterViewInit, OnDestroy {
    @Input() set isDisabled(disabled: boolean) {
        this._isDisabled = disabled;
    }
    @Input() set activeLanguage(language: string) {
        this._language = language;
        if (this.multipleEntity.value?.length > 0) {
            this.changeSelectedItemsLabel();
        } else if (this.singleEntity.value) {
            this.changeSingleItemLabel();
        }
    }
    @Input() set itemsSelected(item: CodeDTView[] | CodeDTView) {
        if (Array.isArray(item)) {
            this.formGroup.patchValue({
                multipleEntity: this.getEntityTagItemList(item),
            });
        } else {
            if (item) {
                const selectedItem = {
                    id: item.uri,
                    label: this.extractLabel.transform(
                        item.targetName.contents,
                        this.language,
                        true
                    ),
                    entity: item,
                    iconClass: null,
                    typeClass: null,
                };
                this.formGroup.patchValue({ singleEntity: selectedItem });
            }
        }
    }

    /**
     * NQF // QDR
     */
    @Input() set parent(parent: string) {
        if (parent !== this._parent) {
            this.formGroup.patchValue({
                singleEntity: null,
            });
        }
        this._parent = parent;
        this._placeHolder = '';
        this.onSearchEntity('');
    }

    @Input() set selectedLanguages(selectedLanguages: UxLanguage[]) {
        const languages: string[] = [];
        this._selectedLanguages = selectedLanguages;
        selectedLanguages.forEach((language: UxLanguage) => {
            languages.push(language.code);
        });
        if (languages.length > 0) {
            this.requestedLanguages = languages.join();

            /* TODO FIX - selectedLanguage is filled before language and calls a service that needs language */
            if (!this.language) {
                this._language = selectedLanguages[0].label;
            }
            this.listEntitiesByUri();
        }
    }

    @Input() set placeHolder(placeHolder: string) {
        this._placeHolder = placeHolder;
    }

    get placeHolder(): string {
        return this._placeHolder;
    }

    get parent(): string {
        return this._parent;
    }

    get language(): string {
        return this._language;
    }

    get selectedLanguages(): UxLanguage[] {
        return this._selectedLanguages;
    }

    get isDisabled(): boolean {
        return this._isDisabled;
    }

    get singleEntity(): FormControl {
        return this.formGroup.get('singleEntity') as FormControl;
    }

    get multipleEntity(): FormControl {
        return this.formGroup.get('multipleEntity') as FormControl;
    }
    private _language: string;
    private _parent: string = null;
    private _isDisabled = false;
    private _selectedLanguages: UxLanguage[];
    private _placeHolder: string = this.translateService.instant('common.start.typing');

    @ViewChild('autocomplete') autocomplete: EuiAutocompleteComponent;
    @ViewChild('autocompleteTag') autocompleteTag: EuiAutocompleteComponent;

    @Input() entityType: string;
    @Input() isPrimaryLanguage = true;
    @Input() isSingleSelection = false;

    @Output()
        onItemSelectionChange: EventEmitter<CodeDTView> = new EventEmitter();

    @Output()
        onItemsSelectionChange: EventEmitter<CodeDTView[]> = new EventEmitter();

    @Output() onError: EventEmitter<void> = new EventEmitter();

    searchList: SelectedTagItemList[] | SelectedItemList[] = [];
    requestedLanguages: string;
    formGroup: FormGroup = new FormGroup({
        singleEntity: new FormControl(null),
        multipleEntity: new FormControl(null),
    });
    isLoading = false;
    destroy$: Subject<boolean> = new Subject<boolean>();
    isDefaultLanguage = true;

    constructor(
        private api: V1Service,
        private extractLabel: ExtractLabelPipe,
        private translateService: TranslateService
    ) {}

    ngOnInit() {
        this.onSearchEntity('');
    }

    ngAfterViewInit() {
        if (this.isSingleSelection) {
            this.itemValueChange();
        } else {
            this.tagItemValueChange();
        }
        if (!this.isSingleSelection) {
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
                        this.onSearchEntity(next || '');
                    }
                });
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSelectionChange(items: SelectedTagItemList[]): void {
        const selection: CodeDTView[] = [];
        items.forEach((item: SelectedTagItemList) => {
            if (item.id !== Constants.EMPTY_RESULT_ID) {
                selection.push(item.entity);
            }
        });
        this.emitItems(selection);
    }

    public onSingleEntityFocusOut() {
        const selection = this.getItem();
        if (selection && !selection.entity) {
            this.formGroup.patchValue({
                singleEntity : null
            });
            this.onSearchEntity('');
        }

    }

    private itemValueChange(): void {
        this.singleEntity.valueChanges
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
                const value = this.getItem();
                const selection = value ? value.entity : null;
                // Quick fix for some fields not loading
                if (selection !== undefined) {
                    this.onItemSelectionChange.emit(selection);
                }
                if (this.needsSearch(prev, next)) {
                    // If the value does not have "label" we will use an empty string.
                    this.onSearchEntity(next?.label || '');
                }
            });
    }

    private tagItemValueChange(): void {
        this.multipleEntity.valueChanges
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
                    this.multipleEntity.patchValue({
                        tagItem: filteredItems,
                    });
                }
            });
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

    private onSearchEntity(searchText: string): void {
        if (typeof searchText === 'string') {
            if (
                !!searchText &&
                searchText.length >= Constants.MIN_CHAR_SEARCH
            ) {
                this.listEntities(searchText);
            } else if (searchText === '') {
                this.listEntities('');
            }
        }
    }

    private listEntities(searchText: string): void {
        this.isLoading = true;
        this.api
            .listEntities(
                this.entityType,
                0,
                50,
                null,
                null,
                searchText,
                this.parent,
                this.requestedLanguages,
                this.language
            )
            .pipe(take(1))
            .subscribe({
                next: (entities: Page) => {
                    if (!this.isSingleSelection) {
                        if (!entities.content.length) {
                            this.searchList = this.createEmptyResultsMessage();
                            this.clearAutocompleteOnEmptyMessage();
                        } else {
                            this.searchList = this.getEntityTagItemList(
                                entities.content
                            );
                        }
                    } else {
                        this.searchList = this.getEntityItemList(
                            entities.content
                        );
                    }
                    this.isLoading = false;
                },
                error: () => this.onError.emit(),
            });
    }

    /* creates empty result message inside autocomplete options */
    private createEmptyResultsMessage(): SelectedTagItemList[] {
        return [];
        /* return [
            {
                id: Constants.EMPTY_RESULT_ID,
                euiInternalId: Constants.EMPTY_RESULT_ID,
                label: this.translateService.instant(
                    'credential-builder.emptyResults'
                ),
                isRemovable: true,
                isRounded: false,
                isOutline: false,
                typeClass: null,
                sizeClass: null,
                iconClass: null,
                dragAndDropSource: '',
            },
        ]; */
    }

    private getEntityTagItemList(
        itemList: CodeDTView[]
    ): SelectedTagItemList[] {
        const entityTagList: SelectedTagItemList[] = [];
        itemList.forEach((item: CodeDTView) => {
            entityTagList.push({
                id: item.uri,
                euiInternalId: item.uri,
                label: this.extractLabel.transform(
                    item.targetName.contents,
                    this.language,
                    true
                ),
                typeClass: null,
                entity: item,
                isRemovable: true,
                isRounded: false,
                isOutline: false,
                sizeClass: null,
                iconClass: null,
                dragAndDropSource: '',
            });
        });
        return entityTagList;
    }

    private listEntitiesByUri(): void {
        const uris: string = this.isSingleSelection
            ? _get(this.singleEntity, 'value.entity.uri')
            : this.getURIs();
        if (uris) {
            this.api
                .listEntitiesByUri(
                    this.entityType,
                    uris,
                    this.requestedLanguages,
                    this.language
                )
                .pipe(take(1))
                .subscribe({
                    next: (entities: CodeDTView[]) => {
                        if (this.isSingleSelection) {
                            this.formGroup.patchValue({
                                singleEntity:
                                    this.getEntityItemList(entities)[0],
                            });
                        } else {
                            this.formGroup.patchValue({
                                multipleEntity:
                                    this.getEntityItemList(entities),
                            });
                            this.emitItems(entities);
                        }
                    },
                    error: () => this.onError.emit(),
                });
        }
    }

    private getEntityItemList(itemList: CodeDTView[]): SelectedItemList[] {
        const searchList: SelectedItemList[] = [];
        itemList.forEach((item: CodeDTView) => {
            searchList.push({
                id: item.uri,
                label: this.extractLabel.transform(
                    item.targetName.contents,
                    this.language,
                    true
                ),
                entity: item,
                euiInternalId: item.uri,
                typeClass: null,
                isRemovable: true,
                isRounded: false,
                isOutline: false,
                sizeClass: null,
                iconClass: null,
                dragAndDropSource: 'false',
            });
        });
        return searchList;
    }

    private changeSelectedItemsLabel(): void {
        this.searchList = [];
        this.multipleEntity.value.forEach((item: SelectedTagItemList) => {
            item.label = this.extractLabel.transform(
                item.entity.targetName.contents,
                this.language,
                true
            );
        });
    }

    private changeSingleItemLabel(): void {
        const item = this.getItem();
        if (item.entity) {
            const selectedItem = {
                id: item.entity.uri,
                label: this.extractLabel.transform(
                    item.entity.targetName.contents,
                    this.language,
                    true
                ),
                entity: item.entity,
                iconClass: null,
                typeClass: null,
            };
            this.formGroup.patchValue({ singleEntity: selectedItem });
        }
    }

    private getItem(): SelectedItemList {
        return this.singleEntity.value && this.singleEntity.value.length === 1
            ? this.singleEntity.value[0]
            : this.singleEntity.value;
    }

    private getURIs(): string {
        let uris = '';
        this.multipleEntity?.value?.forEach(
            (item: SelectedTagItemList, index: number) => {
                if (index === 0) {
                    uris = item.entity.uri;
                } else {
                    uris = uris.concat(',' + item.entity.uri);
                }
            }
        );
        return uris;
    }

    private emitItems(entities) {
        this.onItemsSelectionChange.emit(entities);
    }

    private clearAutocompleteOnEmptyMessage() {
        if (this.isSingleSelection) {
            this.autocomplete.autocompleteInput.nativeElement.addEventListener(
                'blur',
                () => {
                    if (this.searchList?.length === 0) {
                        this.autocomplete.autocompleteControl.reset();
                    }
                },
                { once: true }
            );
        } else {
            this.autocompleteTag.autocompleteInput.nativeElement.addEventListener(
                'blur',
                () => {
                    if (this.searchList?.length === 0) {
                        this.autocompleteTag.autocompleteControl.reset();
                    }
                },
                { once: true }
            );
        }
    }
}
