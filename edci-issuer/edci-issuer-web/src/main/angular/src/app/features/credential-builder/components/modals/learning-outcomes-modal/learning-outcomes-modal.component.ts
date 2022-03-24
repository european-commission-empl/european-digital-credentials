import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage, UxService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { Constants } from '@shared/constants';
import { ExtractLabelPipe } from '@shared/pipes/multilingual.pipe';
import {
    CodeDTView,
    ContentDTView,
    LearningOutcomeSpecView,
    NoteDTView,
    ResourceLearningOutcomeSpecView,
    TextDTView,
    V1Service,
} from '@shared/swagger';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-learning-outcomes-modal',
    templateUrl: './learning-outcomes-modal.component.html',
    styleUrls: ['./learning-outcomes-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class LearningOutcomesModalComponent implements OnInit, OnDestroy {
    @Input() modalTitle: string;
    @Input() modalId: string = 'learningOutcomeModal';
    @Input() language: string;
    @Input() editLearningOutcomeOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        title: string;
    }> = new EventEmitter();

    isLoading: boolean = true;
    editLearningOutcome: LearningOutcomeSpecView;
    defaultLanguage: string;
    availableLanguages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    destroy$: Subject<boolean> = new Subject<boolean>();
    learningOutcomeBody: LearningOutcomeSpecView;
    isSaveDisabled: boolean = false;
    selectedEscoSkills: CodeDTView[] = [];
    formGroup = new FormGroup({
        defaultTitle: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.required,
            noSpaceValidator,
        ]),
        title: new FormGroup({}),
        learningOutcomeType: new FormControl(null),
        reusabilityLevel: new FormControl(null),
        description: new FormGroup({}),
    });
    modalTitleBreadcrumb: string[];

    get defaultTitle() {
        return this.formGroup.get('defaultTitle') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get learningOutcomeType() {
        return this.formGroup.get('learningOutcomeType') as FormControl;
    }

    get reusabilityLevel() {
        return this.formGroup.get('reusabilityLevel') as FormControl;
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
    }

    constructor(
        public uxService: UxService,
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private extractLabel: ExtractLabelPipe
    ) {}

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editLearningOutcomeOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.learning-outcomes-tab.editLearningOutcome'
            );
            this.getLearningOutcomeDetail();
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.learning-outcomes-tab.createLearningOutcome'
            );
            this.language = this.language || this.translateService.currentLang;
            this.defaultLanguage = this.language;
            this.selectedLanguages.push({
                code: this.language,
                label: this.language,
            });
            this.addNewLanguageControl(this.language);
            this.isLoading = false;
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSave(): void {
        if (this.formGroup.invalid) {
            this.uxService.markControlsTouched(this.formGroup);
            this.isLoading = false;
            this.uxService.openMessageBox('messageBoxFormError');
        } else {
            this.isSaveDisabled = true;
            this.setLearningOutcomeBody();
            if (this.editLearningOutcome) {
                this.updateLearningOutcome();
            } else {
                this.createLearningOutcome();
            }
        }
    }

    closeModal(isEdit: boolean, oid?: number, title?: string): void {
        this.onCloseModal.emit({ isEdit, oid, title });
        this.formGroup.reset();
        this.isSaveDisabled = false;
    }

    languageTabSelected(language: string) {
        if (this.language !== language) {
            this.language = language.toLowerCase();
        }
    }

    languageAdded(language: string) {
        this.addNewLanguageControl(language);
    }

    languageRemoved(language: string): void {
        if (this.language === language) {
            this.language = this.selectedLanguages[0].code.toLowerCase();
        }
        this.title.removeControl(language);
        this.description.removeControl(language);
    }

    occupationSelectionChange(skillsList: CodeDTView[]): void {
        this.selectedEscoSkills = skillsList;
    }

    private getLearningOutcomeDetail(): void {
        this.api
            .getLearningOutcome(
                this.editLearningOutcomeOid,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (learningOutcome: LearningOutcomeSpecView) => {
                    this.editLearningOutcome = learningOutcome;
                    this.availableLanguages =
                        this.editLearningOutcome.additionalInfo.languages;
                    this.language = this.editLearningOutcome.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages =
                        this.multilingualService.setUsedLanguages(
                            this.editLearningOutcome.additionalInfo.languages,
                            this.defaultLanguage
                        );
                    this.setForm();
                },
                (err) => this.closeModal(false)
            );
    }

    private addNewLanguageControl(language: string): void {
        this.addTitleControls(language);
        this.addDescriptionControls(language);
    }

    private addTitleControls(language: string, value: string = null): void {
        this.title.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
                Validators.required,
                noSpaceValidator,
            ])
        );
    }

    private updateLearningOutcome(): void {
        this.learningOutcomeBody.oid = this.editLearningOutcome.oid;
        this.api
            .updateLearningOutcome(
                this.learningOutcomeBody,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (learningOutcome: ResourceLearningOutcomeSpecView) => {
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.closeModal(
                        true,
                        learningOutcome.oid,
                        learningOutcome.defaultTitle
                    );
                },
                (err) => {
                    this.closeModal(false);
                    this.isLoading = false;
                }
            );
    }

    private createLearningOutcome(): void {
        this.api
            .createLearningOutcome(
                this.learningOutcomeBody,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (learningOutcome: ResourceLearningOutcomeSpecView) => {
                    this.isLoading = false;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.create'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.closeModal(
                        true,
                        learningOutcome.oid,
                        learningOutcome.defaultTitle
                    );
                },
                (err) => {
                    this.isLoading = false;
                    this.closeModal(false);
                }
            );
    }

    private addDescriptionControls(
        language: string,
        value: string = null
    ): void {
        this.description.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private setLearningOutcomeBody(): void {
        this.learningOutcomeBody = {
            defaultTitle: this.defaultTitle.value,
            defaultLanguage: this.defaultLanguage,
            title: this.getTitle(),
            description: this.getDescription(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
            relatedESCOSkill:
                this.selectedEscoSkills.length > 0
                    ? this.selectedEscoSkills
                    : null,
            reusabilityLevel: this.reusabilityLevel.value,
            learningOutcomeType: this.learningOutcomeType.value,
        };
    }

    private getTitle(): TextDTView {
        let title: TextDTView;
        title = {
            contents: this.multilingualService.formToView(this.title.value),
        };
        return title;
    }

    private getDescription(): NoteDTView {
        let description: NoteDTView = null;
        const descriptionValue: ContentDTView[] =
            this.multilingualService.formToView(this.description.value);

        if (descriptionValue.length > 0) {
            description = {
                contents: descriptionValue,
            };
        }

        return description;
    }

    private setForm(): void {
        this.addControlsFromView();
        this.selectedEscoSkills = _get(
            this.editLearningOutcome,
            'relatedESCOSkill',
            null
        );
        this.formGroup.patchValue({
            defaultTitle: this.editLearningOutcome.defaultTitle,
            reusabilityLevel: _get(
                this.editLearningOutcome,
                'reusabilityLevel',
                null
            ),
            learningOutcomeType: _get(
                this.editLearningOutcome,
                'learningOutcomeType',
                null
            ),
        });
        this.isLoading = false;
    }

    private addControlsFromView(): void {
        this.availableLanguages.forEach((language: string) => {
            this.addTitleControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    this.editLearningOutcome.title.contents
                )
            );

            this.addDescriptionControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(this.editLearningOutcome, 'description.contents', [])
                )
            );
        });
    }
}
