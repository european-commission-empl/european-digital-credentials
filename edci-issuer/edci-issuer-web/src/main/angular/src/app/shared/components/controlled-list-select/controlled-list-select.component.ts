import {
    Component,
    EventEmitter,
    forwardRef,
    Input,
    OnDestroy,
    Output,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { UxLanguage } from '@eui/core';
import { ExtractLabelPipe } from '@shared/pipes/multilingual.pipe';
import { CodeDTView, Page, V1Service } from '@shared/swagger';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-controlled-list-select',
    templateUrl: './controlled-list-select.component.html',
    styleUrls: ['./controlled-list-select.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => ControlledListSelectComponent),
            multi: true,
        },
    ],
})
export class ControlledListSelectComponent
implements ControlValueAccessor, OnDestroy {
    private _language: string;
    private _selectedEntity: CodeDTView;
    private _isDisabled: boolean;
    private _parent: string = null;
    private _selectedLanguages: UxLanguage[];

    @Input() entityType: string;
    @Input() label: string;
    @Input() tooltip: string;
    @Input() required = false;
    @Input() set isDisabled(disabled: boolean) {
        this._isDisabled = disabled;
        if (!disabled && this.entityList.length === 0) {
            this.getEntityList();
        }
    }
    @Input() set activeLanguage(language: string) {
        this._language = language;
        this.sortDropdownElements();
    }

    @Input() set parent(parent: string) {
        this._parent = parent;
        this.getEntityList();
    }

    @Input() set selectedLanguages(selectedLanguages: UxLanguage[]) {
        const languages: string[] = [];
        this._selectedLanguages = selectedLanguages;
        selectedLanguages.forEach((language: UxLanguage) => {
            languages.push(language.code);
        });
        if (languages.length > 0) {
            this.requestedLanguages = languages.join();
            this.getEntityList();
        }
    }

    @Output() onError: EventEmitter<any> = new EventEmitter();

    entityList: CodeDTView[] = [];
    touched = false;
    requestedLanguages: string;
    destroy$: Subject<boolean> = new Subject<boolean>();
    loading: boolean;

    CREDENTIAL_GENERIC: String = 'http://data.europa.eu/snb/credential/e34929035b';
    CREDENTIAL_DIPLOMA: String = 'http://data.europa.eu/snb/credential/6dff8a0f87';

    get selectedEntity() {
        return this._selectedEntity;
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

    set selectedEntity(selection: CodeDTView) {
        this._selectedEntity = selection || undefined;
        this.onChange(this._selectedEntity);
    }

    constructor(
        private api: V1Service,
        private extractLabel: ExtractLabelPipe
    ) {}

    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onChange: any = (_) => {};
    onTouched: any = () => {};

    writeValue(entity: CodeDTView) {
        this.selectedEntity = entity;
    }

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onTouched = (arg: any) => {
            this.touched = true;
            fn(arg);
        };
    }

    checkEntity(entity: CodeDTView, selectedEntity: CodeDTView): boolean {
        let result = false;
        if (entity && selectedEntity) {
            result = entity.uri === selectedEntity.uri;
        } else if (!entity && !selectedEntity) {
            result = true;
        }
        return result;
    }

    private sortDropdownElements() {
        this.entityList.sort((a, b) =>
            this.extractLabel
                .transform(a.targetName.contents, this.language, true)
                .localeCompare(
                    this.extractLabel.transform(
                        b.targetName.contents,
                        this.language,
                        true
                    )
                )
        );
    }

    private getEntityList(): void {
        const copyList = this.entityList;
        this.loading = true;
        this.entityList = [];
        this.api
            .listEntities(
                this.entityType,
                0,
                1000,
                null,
                null,
                '',
                this.parent,
                this.requestedLanguages,
                this.language
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (entity: Page) => {
                    this.entityList = [];
                    entity.content.forEach((element: CodeDTView) => {
                        // Credential Type only display generic & diploma supplement & accreditation qualified award
                        if (this.entityType === 'credential') {
                            if (
                                element.uri ===
                                    this.CREDENTIAL_GENERIC ||
                                element.uri ===
                                    this.CREDENTIAL_DIPLOMA
                            ) {
                                this.entityList.push(element);
                            }
                        } else {
                            this.entityList.push(element);
                        }
                    });
                    this.entityList =
                        entity.content.length > 0 ? this.entityList : copyList;
                    this.sortDropdownElements();
                    this.loading = false;
                },
                error: () => {
                    this.loading = false;
                    this.onError.emit();
                },
            });
    }
}
