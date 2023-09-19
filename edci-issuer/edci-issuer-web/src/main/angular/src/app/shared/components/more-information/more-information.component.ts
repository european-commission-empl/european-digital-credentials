import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage } from '@eui/core';
import { MultilingualService } from '@services/multilingual.service';
import { Constants } from '@shared/constants';
import { ContentDTView, NoteDTView, TextDTView } from '@shared/swagger';
import { additionalNoteValidator } from '@shared/validators/additional-note-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';

const MAIN_ADDITIONAL_NOTE_TOPIC = 'fieldLabel.moreInformation';

@Component({
    selector: 'edci-more-information',
    templateUrl: './more-information.component.html',
    styleUrls: ['./more-information.component.scss'],
})
export class MoreInformationComponent implements OnInit {
    @Input() set isDisabled(disabled: boolean) {
        this.isFormDisabled = disabled;
        disabled ? this.formGroup.disable() : this.formGroup.enable();
        this.additionalNotes.controls.forEach((additionalNote: FormGroup) => {
            disabled ? additionalNote.disable() : additionalNote.enable();
        });
    }
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
            this.checkPrimaryLanguage();
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
    @Input() set additionalNoteToEdit(additionalNote: NoteDTView[]) {
        this._additionalNoteToEdit = additionalNote;
        this.addMoreInformationFromView();
        this.getAdditionalNoteFromView();
    }
    get additionalNoteToEdit(): NoteDTView[] {
        return this._additionalNoteToEdit;
    }

    get moreInformation(): FormGroup {
        return this.formGroup.get('moreInformation') as FormGroup;
    }

    get additionalNotes(): FormArray {
        return this.formGroup.get('additionalNotes') as FormArray;
    }

    get additionalNoteControls(): FormGroup[] {
        return this.additionalNotes.controls as FormGroup[];
    }

    get moreInformationControls(): FormControl {
        return this.moreInformation?.controls[
            this.activeLanguage
        ] as FormControl;
    }
    @Input() headerLabel = '';
    @Input() tooltip: string;
    @Input() additionalFieldsTooltip: string;
    @Input() defaultLanguage: string;

    @Output() onValueChange: EventEmitter<NoteDTView[] | null> =
        new EventEmitter();
    @Output() isValid: EventEmitter<boolean> = new EventEmitter();

    destroy$: Subject<boolean> = new Subject<boolean>();
    isFormDisabled = false;
    formFilled = false;
    isPrimaryLanguage: boolean;
    formGroup = new FormGroup({
        moreInformation: new FormGroup({}),
        additionalNotes: new FormArray([]),
    });

    private _additionalNoteToEdit: NoteDTView[] = [];
    private _activeLanguage: string;
    private _removedLanguage: string;
    private _addedLanguage: string;
    private _selectedLanguages: UxLanguage[];

    constructor(private multilingualService: MultilingualService) {
        this.detectValueChange();
    }

    ngOnInit() {
        this.isPrimaryLanguage = this.defaultLanguage === this.activeLanguage;
        if (
            !this.additionalNoteToEdit ||
            this.additionalNoteToEdit.length === 0
        ) {
            this.addAdditionalNoteRow();
            this.addMoreInformationLanguageControls(this.multilingualService.getUsedLanguages(this.selectedLanguages));
        }
    }

    addAdditionalNoteRow() {
        const topicFormGroup = new FormGroup({});
        const contentGormGroup = new FormGroup({});

        if (this.selectedLanguages.length > 0) {
            this.selectedLanguages.forEach((language: UxLanguage) => {
                topicFormGroup.addControl(language.code, new FormControl(null));
                contentGormGroup.addControl(language.code, new FormControl(null));
            });
        } else {
            topicFormGroup.addControl(this.activeLanguage, new FormControl(null));
            contentGormGroup.addControl(this.activeLanguage, new FormControl(null));
        }
        this.additionalNotes.push(
            new FormGroup(
                {
                    topic: topicFormGroup,
                    content: contentGormGroup,
                },
                additionalNoteValidator
            )
        );
    }

    removeAdditionalNoteRow(index: number) {
        this.additionalNotes.removeAt(index);
    }

    checkPrimaryLanguage() {
        this.isPrimaryLanguage = this.defaultLanguage === this.activeLanguage;
    }

    languageAdded(language: string) {
        this.addMoreInformationControls(language);
        this.additionalNotes.controls.forEach((additionalNote: FormGroup) => {
            this.addContentControl(
                additionalNote.get('content') as FormGroup,
                language
            );

            this.addTopicControl(
                additionalNote.get('topic') as FormGroup,
                language
            );
        });
        this.checkPrimaryLanguage();
    }

    languageRemoved(language: string): void {
        this.moreInformation.removeControl(language);
        this.additionalNotes.controls.forEach((additionalNote: FormGroup) => {
            additionalNote.removeControl(language);
        });
        this.checkPrimaryLanguage();
    }

    getAdditionalNotes(): NoteDTView[] {
        const additionalNoteView: NoteDTView[] = [];
        // Add More Information Note
        const moreInformationTargetName: TextDTView = {
            contents : []
        };
        const moreInformationNote: NoteDTView = {
            contents : [],
            subject : {},
            moreInformation: true,
        };
        Object.keys(this.moreInformation.controls).forEach(lang => {
            if (this.moreInformation.controls[lang].value) {
                moreInformationNote.contents.push({
                    content : this.moreInformation.controls[lang].value,
                    language : lang
                });

                moreInformationTargetName.contents.push({
                    content : MAIN_ADDITIONAL_NOTE_TOPIC,
                    language : lang
                });
            }
        });
        moreInformationNote.subject.targetName = moreInformationTargetName;
        if (moreInformationNote.contents.length > 0 || moreInformationTargetName.contents.length > 0) {
            additionalNoteView.push(moreInformationNote);
        }
        // Add extra notes
        this.additionalNotes.controls.forEach((additionalNote: FormGroup) => {
            const note: NoteDTView = {
                contents : [],
                subject : {}
            };

            const contentFormGroup = additionalNote.get('content') as FormGroup;
            Object.keys(contentFormGroup.controls).forEach(lang => {
                if (contentFormGroup.controls[lang].value) {
                    note.contents.push({
                        content: contentFormGroup.controls[lang].value,
                        language : lang
                    });
                }
            });

            const topicFormGroup = additionalNote.get('topic') as FormGroup;
            const targetName: TextDTView = {
                contents : []
            };
            Object.keys(topicFormGroup.controls).forEach(lang => {
                if (topicFormGroup.controls[lang].value) {
                    targetName.contents.push({
                        content : topicFormGroup.controls[lang].value,
                        language : lang
                    });
                }
            });
            note.subject.targetName = targetName;

            if (note.contents.length > 0 || targetName.contents.length > 0) {
                additionalNoteView.push(note);
            }
        });
        return additionalNoteView.length > 0 ? additionalNoteView : null;
    }

    getAdditionalNoteTopicControl(formGroup: FormGroup, language: string): FormControl {
        const topicGroup = formGroup.get('topic') as FormGroup;
        return topicGroup.controls[language] as FormControl;
    }

    getAdditionalNoteContentControl(formGroup: FormGroup, language: string): FormControl {
        const contentGroup = formGroup.get('content') as FormGroup;
        return contentGroup.controls[language] as FormControl;
    }

    isFormInvalid(): boolean {
        return this.formGroup.invalid;
    }

    notesCtrl(i) {
        return this.getNoteById(i).get('topic') as FormGroup;
    }

    getNoteById(index) {
        return this.additionalNotes.at(index) as FormGroup;
    }

    private detectValueChange() {
        this.formGroup.valueChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.onValueChange.emit(this.getAdditionalNotes());
            });

        this.formGroup.statusChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe((value) => {
                this.isValid.emit(value === 'VALID' || value === 'DISABLED');
            });
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

    private addTopicControl(
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

    private addMoreInformationLanguageControls(languages: string[], value: string = null) {
        languages.forEach(language => this.addMoreInformationControls(language));
    }

    private addMoreInformationControls(
        language: string,
        value: string = null
    ): void {
        this.moreInformation.addControl(
            language,
            new FormControl('', [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
        const objValue = {};
        objValue[language] = value;
        this.moreInformation.patchValue(objValue);
        this.isFormDisabled
            ? this.moreInformation.disable()
            : this.moreInformation.enable();
    }

    private getAdditionalNoteFromView(): void {
        this.additionalNoteToEdit?.forEach((note: NoteDTView) => {
            if (!note?.moreInformation) {
                this.additionalNotes.push(
                    new FormGroup(
                        {
                            topic: new FormGroup({}),
                            content: new FormGroup({}),
                        },
                        additionalNoteValidator
                    )
                );
                this.selectedLanguages.forEach((language: UxLanguage) => {
                    this.addContentControl(
                        this.additionalNotes.controls[
                            this.additionalNotes.controls.length - 1
                        ]['controls'].content as FormGroup,
                        language.code,
                        this.multilingualService.getContentFromLanguage(
                            language.code,
                            note.contents
                        )
                    );

                    this.addTopicControl(
                        this.additionalNotes.controls[
                            this.additionalNotes.controls.length - 1
                        ]['controls'].topic as FormGroup,
                        language.code,
                        this.multilingualService.getContentFromLanguage(
                            language.code,
                            note?.subject?.targetName?.contents
                        )
                    );
                });
            }
        });
        if (this.additionalNotes.length === 0) {
            this.addAdditionalNoteRow();
        }
    }

    private addMoreInformationFromView(): void {
        const moreInfoContent: NoteDTView = this.getMoreInformationContent();
        this.selectedLanguages.forEach((language: UxLanguage) => {
            this.addMoreInformationControls(
                language.code,
                this.multilingualService.getContentFromLanguage(
                    language.code,
                    _get(moreInfoContent, 'contents', [])
                )
            );
        });
    }

    private getViewFromForm(additionalNote: FormGroup): NoteDTView {
        return {
            subject: {
                targetName: {
                    contents: [{
                        content: additionalNote.get('topic').value[this.activeLanguage],
                        language: this.activeLanguage,
                        format: 'string'
                    }]
                }
            },
            contents: this.multilingualService.formToView(
                additionalNote.get('content').value
            ),
        };
    }

    private getMoreInformationContent(): NoteDTView {
        let moreInformationContent: NoteDTView = null;
        this.additionalNoteToEdit?.forEach((note: NoteDTView) => {

            if (note?.moreInformation) {
                moreInformationContent = note;
            }
        });
        return moreInformationContent;
    }

}
