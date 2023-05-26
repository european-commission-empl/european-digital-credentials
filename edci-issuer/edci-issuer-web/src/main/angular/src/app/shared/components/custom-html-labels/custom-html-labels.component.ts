import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage } from '@eui/core';
import { Constants } from '@shared/constants';
import { ContentDTView, LabelDTView } from '@shared/swagger';
import { customLabelValidator } from '@shared/validators/custom-label-validator';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-custom-html-labels',
    templateUrl: './custom-html-labels.component.html',
    styleUrls: ['./custom-html-labels.component.scss'],
})
export class CustomHtmlLabelsComponent implements OnInit {
    private _activeLanguage: string;
    private _removedLanguage: string;
    private _addedLanguage: string;
    private _customLabelsEdit: LabelDTView[];
    private _selectedLanguages: UxLanguage[];

    isPrimaryLanguage: boolean;
    @Input() defaultLanguage: string;

    @Input() tooltip: string;
    @Input() headerLabel: string;

    @Input() set selectedLanguages(languages: UxLanguage[]) {
        if (languages) {
            this._selectedLanguages = languages;
        }
    }
    get selectedLanguages(): UxLanguage[] {
        return this._selectedLanguages;
    }

    @Input() set activeLanguage(language: string) {
        if (language) {
            this._activeLanguage = language;
            this.checkLanguage();
        }
    }
    get activeLanguage(): string {
        return this._activeLanguage;
    }

    @Input() set addedLanguage(language: string) {
        this._addedLanguage = language;
        this.languageAdded(language);
    }
    get addedLanguage(): string {
        return this._addedLanguage;
    }

    @Input() set removedLanguage(language: string) {
        this._removedLanguage = language;
        this.languageRemoved(language);
    }
    get removedLanguage(): string {
        return this._removedLanguage;
    }

    @Input() set customLabelsEdit(value: LabelDTView[]) {
        this._customLabelsEdit = value;
        if (value && value.length > 0) {
            if (this.customLabels.length !== 0) {
                this.customLabels.removeAt(0);
            }
            this.viewToForm();
        }
    }
    get customLabelsEdit(): LabelDTView[] {
        return this._customLabelsEdit;
    }

    @Output() onValueChange: EventEmitter<LabelDTView[]> = new EventEmitter();
    @Output() isValid: EventEmitter<boolean> = new EventEmitter();

    formGroup = new FormGroup({
        customLabels: new FormArray([]),
    });

    get customLabels(): FormArray {
        return this.formGroup.get('customLabels') as FormArray;
    }

    private destroy$ = new Subject();

    constructor() {
        this.customLabels.valueChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe((value) => {
                this.onValueChange.emit(this.formToView(value));
            });

        this.customLabels.statusChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe((value) => {
                this.isValid.emit(value === 'VALID');
            });
    }

    ngOnInit(): void {
        this.checkLanguage();
        if (!(this.customLabelsEdit && this.customLabelsEdit.length > 0)) {
            this.addAdditionalRow();
        }
    }

    addAdditionalRow(): void {
        this.customLabels.push(
            new FormGroup(
                {
                    keyLabel: new FormControl(null, [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    ]),
                    content: new FormGroup({}),
                },
                customLabelValidator
            )
        );
        this.selectedLanguages.forEach((language: UxLanguage) => {
            this.addContentControl(
                this.customLabels.controls[
                    this.customLabels.controls.length - 1
                ]['controls'].content as FormGroup,
                language.code
            );
        });
    }

    removeAdditionalNoteRow(index: number): void {
        this.customLabels.removeAt(index);
    }

    private addContentControl(
        contentControl: FormGroup,
        language: string,
        value: string = null
    ): void {
        contentControl.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private checkLanguage() {
        this.isPrimaryLanguage = this.activeLanguage === this.defaultLanguage;
    }

    private languageAdded(language: string): void {
        this.customLabels.controls.forEach((customLabel: FormGroup) => {
            this.addContentControl(
                customLabel.get('content') as FormGroup,
                language
            );
        });
        this.formGroup.updateValueAndValidity();
        this.checkLanguage();
    }

    private languageRemoved(language: string): void {
        this.customLabels.controls.forEach((customLabel: FormGroup) => {
            (customLabel.controls.content as FormGroup).removeControl(language);
        });
        this.formGroup.updateValueAndValidity();
        this.checkLanguage();
    }

    private formToView(
        labels: {
            keyLabel: string;
            content: any;
        }[]
    ): LabelDTView[] {
        const labelsView: LabelDTView[] = [];
        labels.forEach((label) => {
            const labelView: LabelDTView = this.getLabelView(label);
            if (labelView) {
                labelsView.push(labelView);
            }
        });
        return labelsView.length > 0 ? labelsView : null;
    }

    private getLabelView(label): LabelDTView {
        let labelView: LabelDTView;
        const contentsLabel = this.getContentView(label.content);
        if (contentsLabel && label.keyLabel) {
            labelView = {
                key: label.keyLabel,
                contents: contentsLabel,
            };
        }
        return labelView;
    }

    private getContentView(label): ContentDTView[] {
        const content: ContentDTView[] = [];
        for (const key in label) {
            if (Object.prototype.hasOwnProperty.call(label, key)) {
                if (label[key]) {
                    content.push({ content: label[key], language: key });
                }
            }
        }
        return content;
    }

    private viewToForm(): void {
        this.customLabelsEdit.forEach(
            (labelView: LabelDTView, index: number) => {
                this.customLabels.push(
                    new FormGroup(
                        {
                            keyLabel: new FormControl(labelView.key, [
                                Validators.maxLength(
                                    Constants.MAX_LENGTH_DEFAULT
                                ),
                            ]),
                            content: new FormGroup({}),
                        },
                        customLabelValidator
                    )
                );
                this.setContentsForm(labelView.contents, index);
            }
        );
    }

    private setContentsForm(contents: ContentDTView[], index: number): void {
        contents.forEach((content: ContentDTView) => {
            this.addContentControl(
                this.customLabels.controls[index]['controls']
                    .content as FormGroup,
                content.language,
                content.content
            );
        });
    }
}
