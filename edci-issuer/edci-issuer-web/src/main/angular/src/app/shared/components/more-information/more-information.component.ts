import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage } from '@eui/core';
import { MultilingualService } from '@services/multilingual.service';
import { Constants } from '@shared/constants';
import { NoteDTView } from '@shared/swagger';
import { additionalNoteValidator } from '@shared/validators/additional-note-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';

const MAIN_ADDITIONAL_NOTE_TOPIC = 'More information';

@Component({
    selector: 'edci-more-information',
    templateUrl: './more-information.component.html',
    styleUrls: ['./more-information.component.scss'],
})
export class MoreInformationComponent implements OnInit {
    private _additionalNoteToEdit: NoteDTView[] = [];
    private _activeLanguage: string;
    destroy$: Subject<boolean> = new Subject<boolean>();

    @Input() headerLabel: string = '';
    @Input() tooltip: string;
    @Input() selectedLanguages: UxLanguage[] = [];
    @Input() set isDisabled(disabled: boolean) {
        this.isFormDisabled = disabled;
        disabled ? this.formGroup.disable() : this.formGroup.enable();
        this.additionalNotes.controls.forEach((additionalNote: FormGroup) => {
            disabled ? additionalNote.disable() : additionalNote.enable();
        });
    }
    @Input() set activeLanguage(language: string) {
        this._activeLanguage = language;
    }
    @Input() set additionalNoteToEdit(additionalNote: NoteDTView[]) {
        this._additionalNoteToEdit = additionalNote;
        if (additionalNote && additionalNote.length > 0) {
            this.formGroup.reset();
            this.addMoreInformationFromView();
            this.getAdditionalNoteFromView();
        }
    }
    @Output() onValueChange: EventEmitter<FormGroup> = new EventEmitter();

    get activeLanguage(): string {
        return this._activeLanguage;
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

    isExpanded: boolean = true;
    isFormDisabled: boolean = false;
    formFilled: boolean = false;
    formGroup = new FormGroup({
        moreInformation: new FormGroup({}),
        additionalNotes: new FormArray([]),
    });

    constructor(private multilingualService: MultilingualService) {}

    ngOnInit() {
        if (
            !this.additionalNoteToEdit ||
            this.additionalNoteToEdit.length === 0
        ) {
            this.addAdditionalNoteRow();
            this.addMoreInformationControls(this.activeLanguage);
            this.detectValueChange();
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
        this.selectedLanguages.forEach((language: UxLanguage) => {
            this.addContentControl(
                this.additionalNotes.controls[
                    this.additionalNotes.controls.length - 1
                ]['controls'].content as FormGroup,
                language.code
            );
        });
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

    languageAdded(language: string) {
        this.addMoreInformationControls(language);
        this.additionalNotes.controls.forEach((additionalNote: FormGroup) => {
            this.addContentControl(
                additionalNote.get('content') as FormGroup,
                language
            );
        });
    }

    languageRemoved(language: string): void {
        this.moreInformation.removeControl(language);
        this.additionalNotes.controls.forEach((additionalNote: FormGroup) => {
            additionalNote.removeControl(language);
        });
    }

    getAdditionalNotes(): NoteDTView[] {
        let additionalNoteView: NoteDTView[] = [];
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
        this.formGroup.valueChanges.takeUntil(this.destroy$).subscribe(() => {
            this.onValueChange.emit(this.formGroup);
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
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
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
