import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { UxLanguage } from '@eui/core';
import { SelectedItemList } from '@shared/models/selected-item-list.modal';
import { SelectedTagItemList } from '@shared/models/selected-tag-item-list.model';
import { ExtractLabelPipe } from '@shared/pipes/multilingual.pipe';
import { CodeDTView, Page, V1Service } from '@shared/swagger';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';
import { Constants } from '@shared/constants';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'edci-controlled-list',
    templateUrl: './controlled-list.component.html',
    styleUrls: ['./controlled-list.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class ControlledListComponent implements OnInit, OnDestroy {
    private _language: string;
    private _parent: string = null;
    private _isDisabled: boolean = false;
    private _selectedLanguages: UxLanguage[];

    @Input() entityType: string;
    @Input() isSingleSelection: boolean = false;
    @Input() set isDisabled(disabled: boolean) {
        this._isDisabled = disabled;
        disabled ? this.singleEntity.disable() : this.singleEntity.enable();
        disabled ? this.multipleEntity.disable() : this.multipleEntity.enable();
        if (disabled && this.isDefaultLanguage) {
            this.itemsSelected = null;
            this.formGroup.patchValue({
                singleEntity: null,
            });
        }
    }
    @Input() set activeLanguage(language: string) {
        this._language = language;
        // If it is not default language component should be readOnly / disabled
        if (this.selectedLanguages && this.selectedLanguages[0]) {
            this.isDefaultLanguage =
                this.selectedLanguages[0].code === language;
            this.isDisabled = !this.isDefaultLanguage;
        }
        if (this.selectedItems.length > 0) {
            this.changeSelectedItemsLabel();
        } else if (this.singleEntity.value) {
            this.changeSingleItemLabel();
        }
    }
    @Input() set itemsSelected(item: CodeDTView[] | CodeDTView) {
        if (Array.isArray(item)) {
            this.selectedItems = this.getEntityTagItemList(item);
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
        this._parent = parent;
        this.placeholder = '';
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
            this.listEntitiesByUri();
        }
    }

    @Output()
    onItemSelectionChange: EventEmitter<CodeDTView[] | CodeDTView> =
        new EventEmitter();
    @Output() onError: EventEmitter<any> = new EventEmitter();

    searchList: SelectedTagItemList[] | SelectedItemList[] = [];
    requestedLanguages: string;
    formGroup: FormGroup = new FormGroup({
        singleEntity: new FormControl(null),
        multipleEntity: new FormControl(null),
    });
    isLoading: boolean = false;
    destroy$: Subject<boolean> = new Subject<boolean>();
    selectedItems: SelectedTagItemList[] = [];
    placeholder: string = '';
    textToSearch: string;
    allResults: SelectedTagItemList[] | SelectedItemList[] = [];
    isDefaultLanguage: boolean = true;

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

    constructor(
        private api: V1Service,
        private extractLabel: ExtractLabelPipe,
        private translateService: TranslateService
    ) {}

    ngOnInit() {
        if (this.isSingleSelection) {
            this.singleEntity.valueChanges
                .takeUntil(this.destroy$)
                .subscribe((item) => {
                    const value = this.getItem();
                    const selection = value ? value.entity : null;
                    if (selection) {
                        this.onItemSelectionChange.emit(selection);
                    } else {
                        this.onItemSelectionChange.emit(null);
                    }
                });
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSearchEntity(searchText: string): void {
        if ((!!searchText && searchText.length >= 3) || this.parent) {
            this.isLoading = true;
            const listSearchText = searchText ? searchText : '';
            this.listEntities(listSearchText);
        } else {
            this.searchList = [];
        }
    }

    onSelectionChange(items: SelectedTagItemList[]): void {
        let selection: CodeDTView[] = [];
        items.forEach((item: SelectedTagItemList) => {
            if (item.id !== Constants.EMPTY_RESULT_ID) {
                selection.push(item.entity);
            }
        });
        this.onItemSelectionChange.emit(selection);
    }

    onClick() {
        if (!this.textToSearch && this.entityType) {
            if (this.allResults.length && this.isSingleSelection) {
                this.searchList = this.allResults;
            } else {
                this.listEntities('');
            }
        }
    }

    private listEntities(searchText: string): void {
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
            .takeUntil(this.destroy$)
            .subscribe(
                (entities: Page) => {
                    if (!this.isSingleSelection) {
                        if (!entities.content.length) {
                            this.searchList = this.createEmptyResultsMessage();
                        } else {
                            this.searchList = this.getEntityTagItemList(
                                this.removeDuplicates(entities.content)
                            );
                            if (this.textToSearch === '') {
                                this.allResults = this.searchList;
                            }
                        }
                    } else {
                        this.searchList = this.getEntityItemList(
                            entities.content
                        );
                        if (this.textToSearch === '') {
                            this.allResults = this.searchList;
                        }
                    }
                    this.textToSearch = '';
                    this.isLoading = false;
                },
                (err) => this.onError.emit()
            );
    }

    private createEmptyResultsMessage(): SelectedTagItemList[] {
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

    private getEntityTagItemList(
        itemList: CodeDTView[]
    ): SelectedTagItemList[] {
        const entityTagList: SelectedTagItemList[] = [];
        this.sortEntities(itemList);
        itemList.forEach((item: CodeDTView) => {
            entityTagList.push({
                id: item.uri,
                label: this.extractLabel.transform(
                    item.targetName.contents,
                    this.language,
                    true
                ),
                isDeletable: true,
                typeClass: null,
                entity: item,
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
                .takeUntil(this.destroy$)
                .subscribe(
                    (entities: CodeDTView[]) => {
                        if (this.isSingleSelection) {
                            this.formGroup.patchValue({
                                singleEntity:
                                    this.getEntityItemList(entities)[0],
                            });
                        } else {
                            this.selectedItems =
                                this.getEntityTagItemList(entities);
                            this.onItemSelectionChange.emit(entities);
                        }
                    },
                    (err) => this.onError.emit()
                );
        }
    }

    private getEntityItemList(itemList: CodeDTView[]): SelectedItemList[] {
        const searchList: SelectedItemList[] = [];
        this.sortEntities(itemList);
        itemList.forEach((item: CodeDTView) => {
            searchList.push({
                id: item.uri,
                label: this.extractLabel.transform(
                    item.targetName.contents,
                    this.language,
                    true
                ),
                entity: item,
                iconClass: null,
                typeClass: null,
            });
        });
        return searchList;
    }

    private sortEntities(itemList: CodeDTView[]): void {
        // Getting element 0 since we only want the defaultLanguage for the sort
        // since any other would result in a readonly form
        itemList = itemList.sort((a, b) => {
            return a.targetName.contents[0].content.localeCompare(
                b.targetName.contents[0].content,
                this.selectedLanguages[0]
                    ? this.selectedLanguages[0].code
                    : this.language,
                { sensitivity: 'base' }
            );
        });
    }

    private changeSelectedItemsLabel(): void {
        this.searchList = [];
        this.selectedItems.forEach((item: SelectedTagItemList) => {
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

    private removeDuplicates(entityList: CodeDTView[]): CodeDTView[] {
        return entityList.filter(
            (entity) =>
                this.selectedItems.findIndex(
                    (item) => item.id === entity.uri
                ) === -1
        );
    }

    private getURIs(): string {
        let uris: string = '';
        this.selectedItems.forEach(
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
}
