import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage } from '@eui/core';
import { MultilingualService } from '@services/multilingual.service';
import { Constants } from '@shared/constants';
import { NoteDTView } from '@shared/swagger';
import { additionalNoteValidator } from '@shared/validators/additional-note-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

const MAIN_ADDITIONAL_NOTE_TOPIC = 'More information';

@Component({
    selector: 'edci-more-information',
    templateUrl: './more-information.component.html',
    styleUrls: ['./more-information.component.scss']
})
export class MoreInformationComponent implements OnInit {
    private _additionalNoteToEdit: NoteDTView[] = [];
    private _activeLanguage: string;
    private _removedLanguage: string;
    private _addedLanguage: string;
    private _selectedLanguages: UxLanguage[];

    destroy$: Subject<boolean> = new Subject<boolean>();

    @Input() headerLabel = '';
    @Input() tooltip: string;
    @Input() defaultLanguage: string;
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
        if (additionalNote && additionalNote.length > 0) {
            if (this.additionalNotes.length !== 0) {
                this.additionalNotes.removeAt(0);
            }
            this.getAdditionalNoteFromView();
            this.addMoreInformationFromView();
        }
    }
    get additionalNoteToEdit(): NoteDTView[] {
        return this._additionalNoteToEdit;
    }
    @Output() onValueChange: EventEmitter<NoteDTView[] | null> =
        new EventEmitter();
    @Output() isValid: EventEmitter<boolean> = new EventEmitter();

    isExpanded = true;
    isFormDisabled = false;
    formFilled = false;
    isPrimaryLanguage: boolean;
    formGroup = new FormGroup({
        moreInformation: new FormGroup({}),
        additionalNotes: new FormArray([]),
    });

    get moreInformation(): FormGroup {
        return this.formGroup.get('moreInformation') as FormGroup;
    }

    get additionalNotes(): FormArray {
        return this.formGroup.get('additionalNotes') as FormArray;
    }

    get moreInformationControls(): FormControl {
        return this.moreInformation?.controls[
            this.activeLanguage
        ] as FormControl;
    }

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
            this.addMoreInformationControls(this.activeLanguage);
        }
    }

    changePanelVisibility(): void {
        this.isExpanded = !this.isExpanded;
    }

    addAdditionalNoteRow() {
        this.additionalNotes.push(
            new FormGroup(
                {
                    topic: new FormControl(null, [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    ]),
                    content: new FormGroup({}),
                },
                additionalNoteValidator
            )
        );

        if (this.selectedLanguages.length > 0) {
            this.selectedLanguages.forEach((language: UxLanguage) => {
                this.addContentControl(
                    this.additionalNotes.controls[
                        this.additionalNotes.controls.length - 1
                    ]['controls'].content as FormGroup,
                    language.code
                );
            });
        } else {
            this.addContentControl(
                this.additionalNotes.controls[
                    this.additionalNotes.controls.length - 1
                ]['controls'].content as FormGroup,
                this.activeLanguage
            );
        }

        this.isFormDisabled
            ? this.formGroup.disable()
            : this.formGroup.enable();
        this.additionalNotes.controls.forEach((additionalNote: FormGroup) => {
            this.isFormDisabled
                ? additionalNote.disable()
                : additionalNote.enable();
        });
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
        const contents = this.multilingualService.formToView(
            this.moreInformation.value
        );
        if (contents.length > 0) {
            additionalNoteView.push({
                topic: MAIN_ADDITIONAL_NOTE_TOPIC,
                contents: contents,
            });
        }
        this.additionalNotes.controls.forEach((additionalNote: FormGroup) => {
            if (additionalNote.get('topic').value) {
                additionalNoteView.push(this.getViewFromForm(additionalNote));
            }
        });
        return additionalNoteView.length > 0 ? additionalNoteView : null;
    }

    isFormInvalid(): boolean {
        return this.formGroup.invalid;
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
        this.additionalNoteToEdit.forEach((note: NoteDTView) => {
            if (note.topic !== MAIN_ADDITIONAL_NOTE_TOPIC) {
                this.additionalNotes.push(
                    new FormGroup(
                        {
                            topic: new FormControl(note.topic, [
                                Validators.maxLength(
                                    Constants.MAX_LENGTH_DEFAULT
                                ),
                            ]),
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
                });
            }
        });
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
        let noteView: NoteDTView;
        noteView = {
            topic: additionalNote.get('topic').value,
            contents: this.multilingualService.formToView(
                additionalNote.get('content').value
            ),
        };
        return noteView;
    }

    private getMoreInformationContent(): NoteDTView {
        let moreInformationContent: NoteDTView = null;
        this.additionalNoteToEdit.forEach((note: NoteDTView) => {
            if (note.topic === MAIN_ADDITIONAL_NOTE_TOPIC) {
                moreInformationContent = note;
            }
        });
        return moreInformationContent;
    }
}
