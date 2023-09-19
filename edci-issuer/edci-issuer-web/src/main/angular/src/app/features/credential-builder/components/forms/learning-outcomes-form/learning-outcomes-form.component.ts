import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import {
    AbstractControl,
    FormControl,
    FormGroup,
    Validators,
    FormArray,
    FormBuilder
} from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { UxLanguage, UxLink } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';
import { Constants, TIME_FORMAT } from '@shared/constants';
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
import { Observable, Subject } from 'rxjs';
import { takeUntil, take } from 'rxjs/operators';

@Component({
    selector: 'edci-learning-outcomes-form',
    templateUrl: './learning-outcomes-form.component.html',
    styleUrls: ['./learning-outcomes-form.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [{ provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT }],
})
export class LearningOutcomesFormComponent implements OnInit, OnDestroy {

    get label() {
        return this.learningOutcomesForm.get('label') as FormControl;
    }

    get title() {
        return this.learningOutcomesForm.get('title') as FormGroup;
    }

    get titleControl() {
        return this.title.controls[this.language] as FormControl;
    }

    get learningOutcomeType() {
        return this.learningOutcomesForm.get('learningOutcomeType') as FormControl;
    }

    get reusabilityLevel() {
        return this.learningOutcomesForm.get('reusabilityLevel') as FormControl;
    }

    get relatedSkills() {
        return this.learningOutcomesForm.controls['relatedSkills'] as FormArray;
    }

    get relatedSkillsFormGroups() {
        return this.relatedSkills.controls as FormGroup[];
    }

    get relSkills() {
        return (<FormArray>this.learningOutcomesForm.get('relatedSkills')).controls;
    }

    get description() {
        return this.learningOutcomesForm.get('description') as FormGroup;
    }

    get descriptionControl() {
        return this.description.controls[this.language] as FormControl;
    }
    parts: UxLink[] = [];

    @ViewChild('messageBoxFormError')
        messageBoxFormError: EuiMessageBoxComponent;
    @Input() modalTitle: string;
    @Input() modalId = 'learningOutcomeModal';
    @Input() language: string;
    @Input() editLearningOutcomeOid?: number;
    @Input() isModal: boolean;
    @Input() modalData: any;
    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    newEntity: ResourceLearningOutcomeSpecView = null;

    isLoading = true;
    editLearningOutcome: LearningOutcomeSpecView;
    defaultLanguage: string;
    languages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    isPrimaryLanguage = true;
    destroy$: Subject<boolean> = new Subject<boolean>();
    learningOutcomeBody: LearningOutcomeSpecView;
    selectedEscoSkills: CodeDTView[] = [];
    filledRelatedSkill = false;

    learningOutcomesForm = this.fb.group(
        {
            label: this.fb.control(null, [
                Validators.maxLength(Constants.MAX_LENGTH_LABELS),
                noSpaceValidator,
            ]),
            title: this.fb.group({}),
            learningOutcomeType: this.fb.control(null),
            reusabilityLevel: this.fb.control(null),
            description: this.fb.group({}),
            relatedSkills: this.fb.array([
            ])
        },
        {
            // use binding for language
            validators: [
                /* this.relatedSkillValidation.bind(this), */
                /* this.relatedSkillNameValidation.bind(this), */
            ],
        }
    );

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private route: ActivatedRoute,
        private router: Router,
        private fb: FormBuilder,
        private readonly pageLoadingSpinnerService: PageLoadingSpinnerService
    ) {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.loadBreadcrumb();
            });
    }

    ngOnInit() {
        this.route.data.pipe(takeUntil(this.destroy$)).subscribe((data) => {
            this.pageLoadingSpinnerService.stopPageLoader();
            this.isLoading = false;

            if (this.isModal) {
                this.eventSave.pipe(takeUntil(this.destroy$)).subscribe(() => {
                    this.onSave();
                });
                if (this.modalData) {
                    this.setEditLearningOutcomesData(this.modalData);
                } else {
                    this.setNewLearningOutcomesData();
                }
                return;
            }

            if (data.learningOutcomeDetails) {
                this.setEditLearningOutcomesData(data.learningOutcomeDetails);
            } else {
                this.setNewLearningOutcomesData();
            }

        });

        this.isPrimaryLanguage = true;

        this.loadBreadcrumb();
    }

    getRelatedSkillNameControl(language: string, formGroup: FormGroup): FormControl {
        return (formGroup.get('name') as FormGroup).get(language) as FormControl;
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSave(): void {
        if (this.learningOutcomesForm.invalid) {
            this.learningOutcomesForm.markAllAsTouched();
            this.isLoading = false;
            this.messageBoxFormError.openMessageBox();
        } else {
            this.setLearningOutcomeBody();
            if (this.editLearningOutcome) {
                this.updateLearningOutcome();
            } else {
                this.createLearningOutcome();
            }
        }
    }

    closeForm(): void {
        this.credentialBuilderService.setOcbTabSelected(2);
        this.router.navigateByUrl('credential-builder');
    }

    languageTabSelected(language: string) {
        if (this.language !== language) {
            this.language = language.toLowerCase();
        }
        this.isPrimaryLanguage = this.defaultLanguage === language;
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
        this.removeRelatedSkillLanguage(language);
        this.isPrimaryLanguage = this.defaultLanguage === this.language;
    }

    occupationSelectionChange(skillsList: CodeDTView[]): void {
        this.selectedEscoSkills = skillsList;
    }

    addRelatedSkill(code: CodeDTView = null) {
        const frameworkUri = code?.targetFrameworkURI ? code.targetFrameworkURI : null;
        const uri = code?.uri ? code.uri : null;
        const skill = this.fb.group({
            frameworkUri: new FormControl(frameworkUri, [
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
            ]),
            uri: new FormControl(uri, [
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
            ]),
            name: new FormGroup({})
        });
        this.selectedLanguages.forEach(language => {
            const value = code?.targetName?.contents?.length > 0 ?
                this.multilingualService.getContentFromLanguage(language.code, code.targetName.contents) : null;
            (skill.get('name') as FormGroup)
                .addControl(language.code, new FormControl(value, [Validators.maxLength(Constants.MAX_LENGTH_LONG), noSpaceValidator]));
        });
        this.relatedSkills.push(skill);
    }

    removeRelatedSkillLanguage(language: string) {
        this.relatedSkills.controls.forEach((group: FormGroup) => {
            (group.get('name') as FormGroup).removeControl(language);
        });
    }

    addRelatedSkillLanguage(language: string) {
        this.relatedSkills.controls.forEach((group: FormGroup) => {
            (group.get('name') as FormGroup).addControl(language, new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_LONG), noSpaceValidator]));
        });
    }

    deleteRelatedSkill(index) {
        this.relatedSkills.removeAt(index);
    }

    getSkill(index) {
        return this.relatedSkills.at(index) as FormGroup;
    }

    trackByFn(index: any, item: any) {
        return index;
    }

    setEditLearningOutcomesData(data) {
        this.modalTitle = this.translateService.instant(
            'credential-builder.learning-outcomes-tab.editLearningOutcome'
        );

        this.editLearningOutcome = data;
        this.editLearningOutcomeOid = data.oid;
        this.languages = this.editLearningOutcome.additionalInfo.languages;
        this.language = this.editLearningOutcome.defaultLanguage;
        this.defaultLanguage = this.language;
        this.selectedLanguages =
                        this.multilingualService.setUsedLanguages(
                            this.editLearningOutcome.additionalInfo.languages,
                            this.defaultLanguage
                        );

        this.setRelatedSkill(data);
        this.setForm();
    }

    setNewLearningOutcomesData() {
        this.modalTitle = this.translateService.instant(
            'credential-builder.learning-outcomes-tab.createLearningOutcome'
        );
        this.language = this.language || this.translateService.currentLang;
        this.defaultLanguage = this.language;
        this.selectedLanguages.push({
            code: this.language,
            label: this.language,
        });
        this.addRelatedSkill();
        this.addNewLanguageControl(this.language);
    }

    /* custom validations for related Skill */
    private relatedSkillValidation(
        control: AbstractControl
    ): { [key: string]: boolean } | null {
        // Get all controls
        const frameworkUriControl = control
            .get('relatedSkill')
            .get('frameworkUri');
        const uriControl = control.get('relatedSkill').get('uri');
        const nameControl = control.get('name').get(this.language);
        // language and name control are obtained dynamically, and could not exist
        if (this.language && nameControl) {
            if (frameworkUriControl.value || uriControl.value) {
                this.filledRelatedSkill = true;
            } else {
                this.filledRelatedSkill = false;
            }
            // Name empty
            if (
                (frameworkUriControl.value || uriControl.value) &&
                !nameControl.value
            ) {
                return { nameEmpty: true };
                // URI empty
            } else if (
                (frameworkUriControl.value || nameControl.value) &&
                !uriControl.value
            ) {
                return { uriEmpty: true };
                // FrameworkUri empty
            } else if (
                (uriControl.value || nameControl.value) &&
                !frameworkUriControl.value
            ) {
                return { frameworkUriEmpty: true };
            }
        }
        return null;
    }

    private relatedSkillNameValidation(
        control: AbstractControl
    ): { [key: string]: boolean } | null {
        // Get all controls
        const frameworkUriControl = control
            .get('relatedSkill')
            .get('frameworkUri');
        const uriControl = control.get('relatedSkill').get('uri');
        const nameControl = control.get('name') as FormGroup;
        let isFullFilled = true;
        if (nameControl && (frameworkUriControl.value || uriControl.value)) {
            Object.keys(nameControl.controls).forEach((controlKey) => {
                if (!nameControl.get(controlKey).value) {
                    isFullFilled = false;
                }
            });
        }
        return isFullFilled ? null : { missingRelatedSkillLanguageName: true };
    }

    private setRelatedSkill(learningOutcome: LearningOutcomeSpecView) {
        const relatedSkills = learningOutcome.relatedSkill;
        if (relatedSkills.length > 0) {
            relatedSkills.forEach((relatedSkill) => {
                this.addRelatedSkill(relatedSkill);
            });
        } else {
            this.addRelatedSkill();
        }
    }

    private addNewLanguageControl(language: string): void {
        this.addTitleControls(language);
        this.addDescriptionControls(language);
        this.addRelatedSkillLanguage(language);
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
        this.isLoading = true;
        this.api
            .updateLearningOutcome(
                this.learningOutcomeBody,
                this.translateService.currentLang
            )
            .pipe(take(1))
            .subscribe({
                next: (learningOutcome: ResourceLearningOutcomeSpecView) => {
                    this.newEntity = learningOutcome;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.handleSaveNavigation();
                    this.isLoading = false;
                },
                error: () => {
                    if (this.isModal) {
                        this.onSaveEvent.emit(null);
                    } else {
                        this.closeForm();
                    }
                    this.isLoading = false;
                }
            });
    }

    private handleSaveNavigation() {
        if (this.isModal) {
            // If it is a modal, just emit event when saving is done
            this.onSaveEvent.emit(this.newEntity);
        } else {
            this.credentialBuilderService.setOcbTabSelected(2);
            this.router.navigateByUrl('credential-builder');
        }
    }

    private createLearningOutcome(): void {
        this.isLoading = true;
        this.api
            .createLearningOutcome(
                this.learningOutcomeBody,
                this.translateService.currentLang
            )
            .pipe(take(1))
            .subscribe({
                next: (learningOutcome: ResourceLearningOutcomeSpecView) => {
                    this.newEntity = learningOutcome;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.create'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.handleSaveNavigation();
                    this.isLoading = false;
                },
                error: () => {
                    if (this.isModal) {
                        this.onSaveEvent.emit(null);
                    } else {
                        this.closeForm();
                    }
                    this.isLoading = false;
                }
            });
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

    /* only on new learning outcome */
    private setLearningOutcomeBody(): void {
        const relatedSkills = this.getRelatedSkill();

        this.learningOutcomeBody = {
            label: this.label.value,
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
            relatedSkill: relatedSkills.length > 0 ? relatedSkills : null,
        };
    }

    private getRelatedSkill(): CodeDTView[] {
        const relatedSkills: CodeDTView[] = [];

        this.relatedSkills.controls.forEach(control => {
            const frameworkUri = control.get('frameworkUri').value;
            const uri = control.get('uri').value;
            const targetName: TextDTView = {
                contents : []
            };
            const nameFormGroup = control.get('name') as FormGroup;
            Object.keys(nameFormGroup.controls).forEach(language => {
                if (nameFormGroup.get(language).value) {
                    targetName.contents.push({
                        content : nameFormGroup.get(language).value,
                        language : language
                    });
                }
            });

            if (frameworkUri || uri || targetName.contents.length > 0) {
                relatedSkills.push({
                    targetName : targetName,
                    targetFrameworkURI : frameworkUri,
                    uri : uri
                });
            }
        });
        return relatedSkills;
    }

    private getTitle(): TextDTView {
        const title = {
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

        const learningOutcome = this.learningOutcomesForm.patchValue({
            label: this.editLearningOutcome.label,
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
        this.languages.forEach((language: string) => {
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

    private loadBreadcrumb() {
        this.parts = [
            new UxLink({
                label: this.translateService.instant(
                    'breadcrumb.digitallySealedCredentials'
                ),
                url: '/home',
            }),
            new UxLink({
                label: this.translateService.instant(
                    'breadcrumb.credentialBuilder'
                ),
                url: '/credential-builder',
            }),
        ];
    }
}
