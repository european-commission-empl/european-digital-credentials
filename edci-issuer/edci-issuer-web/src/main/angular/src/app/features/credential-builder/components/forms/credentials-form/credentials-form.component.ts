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
    FormArray,
    FormControl,
    FormGroup,
    Validators,
    FormBuilder,
    ValidationErrors,
    AbstractControl
} from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { UxLanguage, UxLink } from '@eui/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { DateFormatService } from '@services/date-format.service';
import { NotificationService } from '@services/error.service';
import { ModalsService } from '@services/modals.service';
import { MultilingualService } from '@services/multilingual.service';
import { Constants, Entities, TIME_FORMAT } from '@shared/constants';
import {
    ContentDTView,
    DiplomaSpecLiteView,
    EntitlementSpecLiteView,
    EuropassCredentialSpecView,
    LearningAchievementSpecLiteView,
    LearningActivitySpecLiteView,
    NoteDTView,
    PagedResourcesAssessmentSpecLiteView,
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesLearningAchievementSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    ResourceOrganizationSpecView,
    SubresourcesOids,
    TextDTView,
    V1Service,
} from '@shared/swagger';
import { dateValidator } from '@shared/validators/date-validator';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';
import { multipleFieldsBoundValidator } from '@shared/validators/multiple-fields-bound-validator';

@Component({
    selector: 'edci-credentials-form',
    templateUrl: './credentials-form.component.html',
    styleUrls: ['./credentials-form.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [{ provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT }],
})
export class CredentialsFormComponent implements OnInit, OnDestroy {
    get label() {
        return this.formGroup.get('label') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get titleControls() {
        return this.title.controls[this.language] as FormControl;
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
    }

    get descriptionControls() {
        return this.description.controls[this.language] as FormControl;
    }

    get validFrom() {
        return this.formGroup.get('validFrom') as FormControl;
    }

    get expiryDate() {
        return this.formGroup.get('expiryDate') as FormControl;
    }

    get credentialType() {
        return this.formGroup.get('credentialType') as FormControl;
    }

    get issuingOrganization() {
        return this.formGroup.get('issuingOrganization');
    }

    get htmlTemplateSelected() {
        return this.formGroup.get('htmlTemplateSelected');
    }

    get identifier() {
        return this.formGroup.get('identifier') as FormArray;
    }

    get identifierGroups(): FormGroup[] {
        return this.identifier.controls as FormGroup[];
    }

    get identifierCtrl() {
        return (<FormArray>this.formGroup.get('identifier')).controls;
    }

    get accreditation() {
        return this.formGroup.get('accreditation') as FormControl;
    }

    get achievements() {
        return this.formGroup.get('achievements') as FormControl;
    }

    get assessments() {
        return this.formGroup.get('assessments') as FormControl;
    }

    get activities() {
        return this.formGroup.get('activities') as FormControl;
    }

    get entitlements() {
        return this.formGroup.get('entitlements') as FormControl;
    }

    parts: UxLink[] = [];

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
    @ViewChild('messageBoxFormError')
        messageBoxFormError: EuiMessageBoxComponent;

    newEntity: EuropassCredentialSpecView = null;

    @Input() modalId = 'credentialModal';
    @Input() modalTitle: string;
    @Input() editCredentialOid?: number;
    @Input() isModal: boolean;

    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    isPrimaryLanguage = true;
    editCredential: EuropassCredentialSpecView;
    credentialBody: EuropassCredentialSpecView;
    isLoading = true;
    language: string;
    maxDate: Date;
    minDate: Date;
    defaultLanguage: string;
    languages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    destroy$: Subject<boolean> = new Subject<boolean>();
    selectedAchievements: PagedResourcesLearningAchievementSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    selectedActivities: PagedResourcesLearningActivitySpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    selectedEntitlements: PagedResourcesEntitlementSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    selectedAssessments: PagedResourcesAssessmentSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    selectedHtmlTemplates: DiplomaSpecLiteView;
    selectedIssuingOrganization: ResourceOrganizationSpecView = null;
    openEntityModal: {
        [key: string]: { modalId: string; isOpen: boolean; oid?: number };
    } = {};
    entityWillBeOpened: Entities | string;
    validFromValueInvalid: boolean;
    expiryDateValueInvalid: boolean;
    formGroup: FormGroup;

    unsavedEntitlements: EntitlementSpecLiteView[] = [];
    unsavedActivities: LearningActivitySpecLiteView[] = [];
    unsavedAchievements: LearningAchievementSpecLiteView[] = [];

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private dateFormatService: DateFormatService,
        private router: Router,
        private route: ActivatedRoute,
        private modalsService: ModalsService,
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
        this.createForm();

        this.route.data.pipe(takeUntil(this.destroy$)).subscribe((data) => {
            this.pageLoadingSpinnerService.stopPageLoader();
            this.isLoading = false;

            if (data.credentialDetails) {
                this.setEditCredentialData(data.credentialDetails);

                if (data?.credentialIssuer) {
                    this.selectedIssuingOrganization = data.credentialIssuer;
                    this.issuingOrganization.patchValue(data.credentialIssuer.oid);
                }

                if (data?.credentialAchieved) {
                    this.selectedAchievements = data?.credentialAchieved;
                    this.achievements.patchValue(this.selectedAchievements.content?.map(item => item.oid));
                }

                if (data?.credentialEntitledTo) {
                    this.selectedEntitlements = data?.credentialEntitledTo;
                    this.entitlements.patchValue(this.selectedEntitlements.content?.map(item => item.oid));
                }

                if (data?.credentialPerformed) {
                    this.selectedActivities = data?.credentialPerformed;
                    this.activities.patchValue(this.selectedActivities.content?.map(item => item.oid));
                }

                if (data?.credentialHtmlTemplate) {
                    this.selectedHtmlTemplates = data?.credentialHtmlTemplate;
                    this.htmlTemplateSelected.patchValue(data?.credentialHtmlTemplate?.oid);
                }

                if (data?.credentialAssessed) {
                    this.selectedAssessments = data?.credentialAssessed;
                    this.assessments.patchValue(this.selectedAssessments.content?.map(item => item.oid));
                }
            } else {
                this.setNewCredentialData();
            }
        });

        if (this.isModal) {
            this.eventSave.pipe(takeUntil(this.destroy$)).subscribe(() => {
                this.onSave();
            });
        }

        this.isPrimaryLanguage = true;
        this.loadBreadcrumb();

        this.formGroup.get('validFrom').valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
            this.checkValidDate();
        });
        this.formGroup.get('expiryDate').valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
            this.checkValidDate();
        });

    }

    checkAccreditationValidity() {
        /* { id: this.accreditation.value } */
        const accreditation = 123;
        this.api
            .getAccreditation(accreditation, this.language, 'response')
            .subscribe(
                (resp) => console.log(resp),
                (err) => console.log(err)
            );
    }

    createForm() {
        this.formGroup = new FormGroup({
            label: new FormControl(null, [
                Validators.maxLength(Constants.MAX_LENGTH_LABELS),
                noSpaceValidator,
            ]),
            achievements : new FormControl(null),
            activities : new FormControl(null),
            assessments : new FormControl(null),
            entitlements : new FormControl(null),
            title: new FormGroup({}),
            description: new FormGroup({}),
            validFrom: new FormControl(null, [
                Validators.required,
                dateValidator,
            ]),
            identifier: this.fb.array([
                new FormGroup({
                    identifierContent: new FormControl(null, [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    ]),
                    identifierName: new FormControl(null, [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    ]),
                }, multipleFieldsBoundValidator(['identifierContent', 'identifierName'])),
            ]),
            expiryDate: new FormControl(null, [dateValidator]),
            issuingOrganization: new FormControl(null, [Validators.required]),
            htmlTemplateSelected: new FormControl(null),
            credentialType: new FormControl(null, [Validators.required]),
            accreditation: new FormControl(null, [
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            ]),
        }, [
            this.requiredClaimValidator
        ]);
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    closeForm() {
        this.credentialBuilderService.setOcbTabSelected(0);
        this.router.navigateByUrl('credential-builder');
    }

    onSave(): void {
        if (this.isModal) {
            this.onSaveEvent.emit([this.formGroup]);
        } else {
            this.isLoading = true;
            this.validateFormDatesValues();
            if (
                this.formGroup.invalid ||
                this.validFromValueInvalid ||
                this.expiryDateValueInvalid
            ) {
                this.formGroup.markAllAsTouched();
                this.isLoading = false;
                this.messageBoxFormError.openMessageBox();
            } else {
                this.setCredentialBody();
                if (this.editCredential) {
                    this.updateCredential();
                } else {
                    this.createCredential();
                }
            }
        }
    }

    checkValidDate(): void {
        this.validateFormDatesValues();

        if (
            !this.dateFormatService.validateDates(
                this.validFrom.value,
                this.expiryDate.value
            )
        ) {
            this.validFrom.setErrors({ invalidDateError: true });
            this.expiryDate.setErrors({ invalidDateError: true });
        } else {
            this.validFrom.setErrors(null);
            this.expiryDate.setErrors(null);
        }
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
        this.isPrimaryLanguage = this.defaultLanguage === this.language;
    }

    onIssuingOrganizationSelectionChange(oid): void {
        this.formGroup.patchValue({
            issuingOrganization: oid,
        });
    }

    onAchievementSelectionChange(oids): void {
        this.achievements.markAsTouched();
        this.achievements.patchValue(oids);
    }

    onActivitySelectionChange(oids): void {
        this.activities.markAsTouched();
        this.activities.patchValue(oids);
    }

    onEntitlementSelectionChange(oids): void {
        this.entitlements.markAsTouched();
        this.entitlements.patchValue(oids);
    }

    onAssessmentSelectionChange(oids): void {
        this.assessments.markAsTouched();
        this.assessments.patchValue(oids);
    }

    onHTMLTemplateSelectionChange(oid): void {
        this.formGroup.patchValue({
            htmlTemplateSelected: oid,
        });
    }

    newEntityClicked(
        value: Entities | string,
        event = undefined,
        isMultiSelect = false
    ): void {
        if (event === undefined && !isMultiSelect) {
            this.entityWillBeOpened = value;
            this.messageBox.openMessageBox();
        } else {
            if (isMultiSelect) {
                this.entityWillBeOpened = value;
                this.gotoEntity();
            } else if (event) {
                this.gotoEntity();
            }
        }
    }

    closeNewEntityModal(closeInfo: {
        isEdit: boolean;
        oid?: number;
        displayName?: string;
    }) {
        this.openEntityModal[this.entityWillBeOpened].isOpen =
            this.modalsService.closeModal();
        if (closeInfo.oid) {
            const item: any = {
                oid: closeInfo.oid,
                displayName: closeInfo.displayName,
                defaultLanguage: this.defaultLanguage,
            };
            switch (this.entityWillBeOpened) {
            case 'achievement':
                this.selectedAchievements =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedAchievements,
                            this.achievements.value,
                            item
                        );
                break;
            case 'organization':
                this.selectedIssuingOrganization = item;
                break;
            case 'activity':
                this.selectedActivities =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedActivities,
                            this.activities.value,
                            item
                        );
                break;
            case 'entitlement':
                this.selectedEntitlements =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedEntitlements,
                            this.entitlements.value,
                            item
                        );
                break;
            case 'assessment':
                this.selectedAssessments =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedEntitlements,
                            this.assessments.value,
                            item
                        );
                break;
            case 'htmlTemplate':
                this.selectedHtmlTemplates = item;
                break;
            }
        }
    }

    editEntityClicked(event: { oid: number; type: string }) {
        if (event) {
            this.entityWillBeOpened = event.type;
            this.gotoEntity(event.oid);
        }
    }

    trackByFn(index: number) {
        return index;
    }

    addIdentifierRow() {
        this.identifier.push(
            new FormGroup(
                {
                    identifierContent: new FormControl(
                        null,
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
                    ),
                    identifierName: new FormControl(
                        null,
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
                    ),
                },
                multipleFieldsBoundValidator(['identifierContent', 'identifierName'])
            )
        );
    }

    setNewCredentialData() {
        this.modalTitle = this.translateService.instant(
            'credential-builder.credentials-tab.createCredential'
        );
        this.language = this.translateService.currentLang;
        this.defaultLanguage = this.language;
        this.selectedLanguages.push({
            code: this.language,
            label: this.language,
        });
        this.addNewLanguageControl(this.language);
    }

    setEditCredentialData(data) {
        this.modalTitle = this.translateService.instant(
            'credential-builder.credentials-tab.editCredential'
        );

        this.editCredential = data;
        this.editCredentialOid = data.oid;
        this.languages = this.editCredential.additionalInfo.languages;
        this.language = this.editCredential.defaultLanguage;
        this.defaultLanguage = this.language;
        this.selectedLanguages =
                        this.multilingualService.setUsedLanguages(
                            this.editCredential.additionalInfo.languages,
                            this.defaultLanguage
                        );

        if (this.editCredential?.identifier && this.editCredential?.identifier.length > 0) {
            this.credentialBuilderService.extractCredentialIdentifierContent(
                _get(this.editCredential, 'identifier', []),
                this.identifier
            );
        }
        this.setForm();
    }

    private gotoEntity(oid: number = null) {
        this.openEntityModal[this.entityWillBeOpened] =
            this.modalsService.openModal(this.modalTitle, oid);
        this.setCredentialBody();
    }

    private createCredential(): void {
        this.isLoading = true;
        this.api
            .createCredential(
                this.credentialBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (credential: EuropassCredentialSpecView) => {
                    this.newEntity = credential;
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

    private updateCredential(): void {
        this.credentialBody.oid = this.editCredential.oid;
        this.isLoading = true;
        this.api
            .updateCredential(
                this.credentialBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (credential: EuropassCredentialSpecView) => {
                    this.newEntity = credential;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.handleSaveNavigation();
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
            this.credentialBuilderService.setOcbTabSelected(0);
            this.router.navigateByUrl('credential-builder');
        }
    }

    private setCredentialBody(): void {
        this.credentialBody = {
            label: this.label.value,
            defaultLanguage: this.defaultLanguage,
            title: this.getTitle(),
            description: this.getDescription(),
            validFrom: this.dateFormatService.dateToStringDateTime(
                this.validFrom.value
            ),
            validUntil: this.dateFormatService.dateToStringDateTime(
                this.expiryDate.value
            ),
            identifier:
                this.credentialBuilderService.getCredentialIdentifierAchievements(
                    this.identifier
                ),
            type: this.credentialType.value,
            relAchieved: this.achievements.value && this.achievements.value.length > 0 ? {
                oid : this.achievements.value
            } : null,
            relPerformed: this.activities.value && this.activities.value.length > 0 ? {
                oid: this.activities.value
            } : null,
            relEntitledTo: this.entitlements.value && this.entitlements.value.length > 0 ? {
                oid: this.entitlements.value
            } : null,
            relAssessed: this.assessments.value && this.assessments.value.length > 0 ? {
                oid: this.assessments.value
            } : null,
            relDiploma: this.setHTMLTemplateTo(),
            relIssuer: this.setIssuingOrganization(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
            hasAccreditation: this.accreditation.value,
        };
    }

    private getDescription(): NoteDTView {
        let description: NoteDTView = null;
        const descriptionContents: ContentDTView[] =
            this.multilingualService.formToView(this.description.value);
        if (descriptionContents.length > 0) {
            description = {
                contents: descriptionContents,
            };
        }
        return description;
    }

    private getTitle(): TextDTView {
        const title = {
            contents: this.multilingualService.formToView(this.title.value),
        };
        return title;
    }

    private addTitleControls(): void {
        this.languages.forEach((language: string) => {
            this.title.addControl(
                language,
                new FormControl(
                    this.multilingualService.getContentFromLanguage(
                        language,
                        this.editCredential.title.contents
                    ),
                    [
                        Validators.maxLength(Constants.MAX_LENGTH_LONG),
                        Validators.required,
                        noSpaceValidator,
                    ]
                )
            );
        });
    }

    private addDescriptionControls(): void {
        this.languages.forEach((language: string) => {
            this.description.addControl(
                language,
                new FormControl(
                    this.multilingualService.getContentFromLanguage(
                        language,
                        _get(this.editCredential, 'description.contents', [])
                    ),
                    [Validators.maxLength(Constants.MAX_LENGTH_LONG)]
                )
            );
        });
    }

    private addNewLanguageControl(language: string): void {
        this.title.addControl(
            language,
            new FormControl('', [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
                Validators.required,
                noSpaceValidator,
            ])
        );
        this.description.addControl(
            language,
            new FormControl('', [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private setForm(): void {
        this.addTitleControls();
        this.addDescriptionControls();
        this.formGroup.patchValue({
            label: this.editCredential.label,
            validFrom: new Date(this.editCredential.validFrom),
            expiryDate: this.editCredential.validUntil
                ? new Date(this.editCredential.validUntil)
                : null,
            credentialType: _get(this.editCredential, 'type', undefined),
            accreditation: _get(
                this.editCredential,
                'hasAccreditation',
                undefined
            ),
        });
    }

    private setHTMLTemplateTo(): SubresourcesOids {
        let relHTMLTemplateTo: SubresourcesOids = null;
        if (this.htmlTemplateSelected) {
            relHTMLTemplateTo = {
                oid: [this.htmlTemplateSelected.value],
            };
        }
        return relHTMLTemplateTo;
    }

    private setIssuingOrganization(): SubresourcesOids {
        let relIssuingOrganization: SubresourcesOids = null;
        if (this.issuingOrganization.value) {
            relIssuingOrganization = {
                oid: [this.issuingOrganization.value],
            };
        }
        return relIssuingOrganization;
    }

    private validateFormDatesValues() {
        if (
            this.validFrom.value &&
            !this.dateFormatService.validateDate(this.validFrom.value)
        ) {
            this.validFromValueInvalid = true;
            this.validFrom.reset();
        } else {
            this.validFromValueInvalid = false;
        }

        if (
            this.expiryDate.value &&
            !this.dateFormatService.validateDate(this.expiryDate.value)
        ) {
            this.expiryDateValueInvalid = true;
            this.expiryDate.reset();
        } else {
            this.expiryDateValueInvalid = false;
        }
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
    private requiredClaimValidator(
        group: FormGroup
    ): ValidationErrors | null {
        const achievements = group.get('achievements').value;
        const activities = group.get('activities').value;
        const assessments = group.get('assessments').value;
        const entitlements = group.get('entitlements').value;
        if (achievements?.length > 0 || activities?.length > 0 || assessments?.length > 0 || entitlements?.length > 0 ) {
            return null;
        } else {
            return {
                requiredClaim : true
            };
        }
    }
}
