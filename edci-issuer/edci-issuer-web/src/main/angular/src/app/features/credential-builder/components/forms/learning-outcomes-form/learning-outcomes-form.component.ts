import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
import {
    AbstractControl,
    FormControl,
    FormGroup,
    Validators
} from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { UxLanguage, UxLink } from '@eui/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { Constants, TIME_FORMAT } from '@shared/constants';
import {
    CodeDTView,
    ContentDTView,
    LearningOutcomeSpecView,
    NoteDTView,
    ResourceLearningOutcomeSpecView,
    TextDTView,
    V1Service
} from '@shared/swagger';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-learning-outcomes-form',
    templateUrl: './learning-outcomes-form.component.html',
    styleUrls: ['./learning-outcomes-form.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [
        { provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT },
    ],
})
export class LearningOutcomesFormComponent implements OnInit, OnDestroy {
    parts: UxLink[] = [];

    @ViewChild('messageBoxFormError')
        messageBoxFormError: EuiMessageBoxComponent;
    @Input() modalTitle: string;
    @Input() modalId = 'learningOutcomeModal';
    @Input() language: string;
    @Input() editLearningOutcomeOid?: number;
    @Input() isModal: boolean;

    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    newEntity: ResourceLearningOutcomeSpecView = null;

    isLoading = true;
    editLearningOutcome: LearningOutcomeSpecView;
    defaultLanguage: string;
    availableLanguages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    isPrimaryLanguage = true;
    destroy$: Subject<boolean> = new Subject<boolean>();
    learningOutcomeBody: LearningOutcomeSpecView;
    selectedEscoSkills: CodeDTView[] = [];
    formGroup = new FormGroup(
        {
            label: new FormControl(null, [
                Validators.maxLength(Constants.MAX_LENGTH_LABELS),
                noSpaceValidator,
            ]),
            title: new FormGroup({}),
            learningOutcomeType: new FormControl(null),
            reusabilityLevel: new FormControl(null),
            description: new FormGroup({}),
            relatedSkill: new FormGroup({
                frameworkUri: new FormControl(null, [
                    Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
                ]),
                uri: new FormControl(null, [
                    Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
                ]),
            }),
            name: new FormGroup({}),
        },
        {
        // use binding for language
            validators: [
                this.relatedSkillValidation.bind(this),
                this.relatedSkillNameValidation.bind(this),
            ],
        }
    );

    filledRelatedSkill = false;

    get label() {
        return this.formGroup.get('label') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get titleControl() {
        return this.title.controls[this.language] as FormControl;
    }

    get learningOutcomeType() {
        return this.formGroup.get('learningOutcomeType') as FormControl;
    }

    get reusabilityLevel() {
        return this.formGroup.get('reusabilityLevel') as FormControl;
    }

    get relatedSkill() {
        return this.formGroup.get('relatedSkill') as FormGroup;
    }

    get frameworkUri() {
        return this.relatedSkill.get('frameworkUri') as FormControl;
    }

    get uri() {
        return this.relatedSkill.get('uri') as FormControl;
    }

    get name() {
        return this.formGroup.get('name') as FormGroup;
    }

    get nameControls() {
        return this.name.controls[this.language] as FormControl;
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
    }

    get descriptionControl() {
        return this.description.controls[this.language] as FormControl;
    }

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private route: ActivatedRoute,
        private router: Router
    ) {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.loadBreadcrumb();
            });
    }

    ngOnInit() {
        this.isPrimaryLanguage = true;
        if (this.isModal) {
            this.eventSave.pipe(takeUntil(this.destroy$)).subscribe((res) => {
                this.onSave();
            });
        } else {
            this.route.params.subscribe((params) => {
                if (params['id'] !== null || params['id'] !== undefined) {
                    this.editLearningOutcomeOid = params['id'];
                }
            });
        }
        this.loadBreadcrumb();
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
            this.formGroup.markAsUntouched();
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
        this.name.removeControl(language);
        this.isPrimaryLanguage = this.defaultLanguage === this.language;
    }

    occupationSelectionChange(skillsList: CodeDTView[]): void {
        this.selectedEscoSkills = skillsList;
    }

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

    private getLearningOutcomeDetail(): void {
        this.api
            .getLearningOutcome(
                this.editLearningOutcomeOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
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
                    this.setRelatedSkill(learningOutcome);
                    this.setForm();
                },
                (err) => this.closeForm()
            );
    }

    private setRelatedSkill(learningOutcome: LearningOutcomeSpecView) {
        if (learningOutcome.relatedSkill.length > 0) {
            this.formGroup.controls['relatedSkill']
                .get('frameworkUri')
                .setValue(learningOutcome.relatedSkill[0].targetFrameworkURI);
            this.formGroup.controls['relatedSkill']
                .get('uri')
                .setValue(learningOutcome.relatedSkill[0].uri);
            learningOutcome.relatedSkill[0].targetName.contents.forEach(
                (name) => {
                    this.addNameControls(name.language, name.content);
                }
            );
        } else {
            this.formGroup.controls['relatedSkill']
                .get('frameworkUri')
                .setValue(null);
            this.formGroup.controls['relatedSkill'].get('uri').setValue(null);
            this.addNameControls(learningOutcome.defaultLanguage, null);
        }
    }

    private addNewLanguageControl(language: string): void {
        this.addTitleControls(language);
        this.addDescriptionControls(language);
        this.addNameControls(language);
    }

    private addNameControls(language: string, value: string = null): void {
        this.name.addControl(language, new FormControl(value));
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
            .pipe(takeUntil(this.destroy$))
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
                }
            }).add(() => this.handleSaveNavigation());
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
        this.api
            .createLearningOutcome(
                this.learningOutcomeBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
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
                }
            }).add(() => this.handleSaveNavigation());
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
        const relatedSkill = this.getRelatedSkill();
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
            relatedSkill: relatedSkill === null ? null : [relatedSkill],
        };
    }

    private getRelatedSkill() {
        let relatedSkill: CodeDTView = {};
        const frameworkUri =
            this.formGroup.get('relatedSkill')['controls']['frameworkUri']
                .value;
        const uri = this.formGroup.get('relatedSkill')['controls']['uri'].value;
        const name = this.getName();
        relatedSkill.targetFrameworkURI =
            frameworkUri === '' ? null : frameworkUri;
        relatedSkill.uri = uri === '' ? null : uri;
        relatedSkill.targetName = name === '' ? null : name;
        relatedSkill.targetDescription = null;
        relatedSkill.targetFramework = null;
        if (
            relatedSkill.targetFrameworkURI === null &&
            relatedSkill.uri === null &&
            (relatedSkill.targetName === null ||
                relatedSkill.targetName.contents.length === 0)
        ) {
            relatedSkill = null;
        }
        return relatedSkill;
    }

    private getTitle(): TextDTView {
        const title = {
            contents: this.multilingualService.formToView(this.title.value),
        };
        return title;
    }

    private getName(): TextDTView {
        let name: TextDTView;
        let nameValue = null;
        nameValue = this.name.value;
        name = {
            contents: this.multilingualService.formToView(nameValue),
        };
        this.selectedLanguages.forEach((lang) => {
            if (nameValue[lang.code] === null) {
                name = null;
            }
        });
        return name;
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
            relatedSkill: _get(this.editLearningOutcome, 'relatedSkill', null),
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

            this.addNameControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(this.editLearningOutcome, 'name', [])
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
