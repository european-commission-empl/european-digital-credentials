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
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
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
    AssessmentSpecLiteView,
    AssessmentSpecView,
    AssessmSpecificationDCView,
    CodeDTView,
    NoteDTView,
    OrganizationSpecLiteView,
    PagedResourcesAssessmentSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ScoringSchemeDTView,
    SubresourcesOids,
    V1Service
} from '@shared/swagger';
import { dateValidator } from '@shared/validators/date-validator';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-assessments-form',
    templateUrl: './assessments-form.component.html',
    styleUrls: ['./assessments-form.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [
        { provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT },
    ],
})
export class AssessmentsFormComponent implements OnInit, OnDestroy {
    parts: UxLink[] = [];

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
    @ViewChild('messageBoxFormError')
        messageBoxFormError: EuiMessageBoxComponent;
    @Input() modalTitle: string;
    @Input() language: string;
    @Input() modalId = 'assessmentModal';
    @Input() editAssessmentOid?: number;
    @Input() isModal: boolean;

    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    newEntity: AssessmentSpecLiteView = null;

    organizationsListContent: [];
    isPrimaryLanguage = true;
    defaultLanguage: string;
    editAssessment: AssessmentSpecView;
    assessmentLanguage: CodeDTView[] = [];
    selectedLanguages: UxLanguage[] = [];
    availableLanguages: string[] = [];
    destroy$: Subject<boolean> = new Subject<boolean>();
    assessmentBody: AssessmentSpecView;
    isLoading = true;
    additionalNoteView: NoteDTView[];
    additionalNoteSpecView: NoteDTView[];
    selectedAssessedBy: OrganizationSpecLiteView;
    selectedSubAssessments: PagedResourcesAssessmentSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    subAssessmentsOidList: number[] = [];
    indexToNextTab: number;
    openEntityModal: {
        [key: string]: { modalId: string; isOpen: boolean; oid?: number };
    } = {};
    entityWillBeOpened: Entities | string;
    isNewEntityDisabled: boolean;

    formGroup = new FormGroup({
        label: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_LABELS),
            noSpaceValidator,
        ]),
        // Assessment
        title: new FormGroup({}),
        description: new FormGroup({}),
        assessmentDate: new FormControl(null, [dateValidator]),
        methodOfAssessment: new FormControl(null),
        assessedBy: new FormControl(null),
        // Assessment Specification
        assessmentSpecificationIdentifier: new FormControl(
            '',
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
        ),
        assessmentSpecificationDescription: new FormGroup({}),
        assessmentType: new FormControl(null),
        modeOfAssessment: new FormControl(null),
        homePage: new FormControl('', [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
        ]),
        specificationOtherDocument: new FormArray([]),
        // Grade
        gradeTitle: new FormGroup({}),
        gradeSchemeIdentifier: new FormControl(
            '',
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
        ),
        gradeDescription: new FormGroup({}),
        gradeOtherDocument: new FormArray([]),
    });
    unsavedSubAssessments: AssessmentSpecLiteView[] = [];
    additionalNoteSpecification: NoteDTView[];
    additionalNote: NoteDTView[];
    removedLanguage: string;
    addedLanguage: string;
    isAdditionalNoteValid: boolean;
    isAdditionalNoteSpecificationValid: boolean;

    get label() {
        return this.formGroup.get('label') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get titleControl() {
        return this.title?.controls[this.language] as FormControl;
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
    }

    get descriptionControl() {
        return this.description.controls[this.language] as FormControl;
    }

    get assessmentDate() {
        return this.formGroup.get('assessmentDate') as FormControl;
    }

    get methodOfAssessment() {
        return this.formGroup.get('methodOfAssessment') as FormControl;
    }

    get assessedBy() {
        return this.formGroup.get('assessedBy') as FormControl;
    }

    get assessmentSpecificationIdentifier() {
        return this.formGroup.get(
            'assessmentSpecificationIdentifier'
        ) as FormControl;
    }

    get assessmentSpecificationDescription() {
        return this.formGroup.get(
            'assessmentSpecificationDescription'
        ) as FormGroup;
    }

    get assessmentSpecificationDescriptionControl() {
        return this.assessmentSpecificationDescription.controls[
            this.language
        ] as FormControl;
    }
    get assessmentType() {
        return this.formGroup.get('assessmentType') as FormControl;
    }

    get modeOfAssessment() {
        return this.formGroup.get('modeOfAssessment') as FormControl;
    }

    get homePage() {
        return this.formGroup.get('homePage') as FormControl;
    }

    get specificationOtherDocument() {
        return this.formGroup.get('specificationOtherDocument') as FormArray;
    }

    get gradeTitle() {
        return this.formGroup.get('gradeTitle') as FormGroup;
    }

    get gradeTitleControl() {
        return this.gradeTitle.controls[this.language] as FormControl;
    }

    get gradeSchemeIdentifier() {
        return this.formGroup.get('gradeSchemeIdentifier') as FormControl;
    }

    get gradeDescription() {
        return this.formGroup.get('gradeDescription') as FormGroup;
    }

    get gradeDescriptionControl() {
        return this.gradeDescription.controls[this.language] as FormControl;
    }

    get gradeOtherDocument() {
        return this.formGroup.get('gradeOtherDocument') as FormArray;
    }

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private dateFormatService: DateFormatService,
        private route: ActivatedRoute,
        private router: Router,
        private modalsService: ModalsService
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
                    this.editAssessmentOid = params['id'];
                }
            });
        }
        this.loadBreadcrumb();
        if (this.editAssessmentOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.assessment-tab.editAssessment'
            );
            this.getAssessmentDetails();
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.assessment-tab.createAssessment'
            );
            this.language = this.language || this.translateService.currentLang;
            this.credentialBuilderService.addOtherDocumentRow(
                this.gradeOtherDocument
            );
            this.credentialBuilderService.addOtherDocumentRow(
                this.specificationOtherDocument
            );
            this.addNewLanguageControl(this.language);
            this.defaultLanguage = this.language;
            this.selectedLanguages.push({
                code: this.language,
                label: this.language,
            });
            this.isLoading = false;
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    checkValidDate() {
        this.validateFormDatesValues();
    }

    onSave(): void {
        this.validateFormDatesValues();
        if (this.isFormInvalid()) {
            this.formGroup.markAsUntouched();
            this.isLoading = false;
            this.messageBoxFormError.openMessageBox();
        } else {
            this.setAssessmentBody();
            if (this.editAssessment) {
                this.updateAssessment();
            } else {
                this.createAssessment();
            }
        }
    }

    closeForm(): void {
        this.credentialBuilderService.setOcbTabSelected(4);
        this.router.navigateByUrl('credential-builder');
    }

    languageTabSelected(language: string) {
        if (this.language !== language) {
            this.language = language.toLowerCase();
        }
        this.isPrimaryLanguage = this.defaultLanguage === language;
    }

    languageAdded(language: string) {
        this.addedLanguage = language;
        this.addNewLanguageControl(language);
    }

    languageRemoved(language: string): void {
        this.removedLanguage = language;
        if (this.language === language) {
            this.language = this.selectedLanguages[0].code.toLowerCase();
        }
        this.title.removeControl(language);
        this.description.removeControl(language);
        this.gradeDescription.removeControl(language);
        this.assessmentSpecificationDescription.removeControl(language);
        this.gradeTitle.removeControl(language);
        this.isPrimaryLanguage = this.defaultLanguage === this.language;
    }

    assessmentLanguageSelectionChange(assessmentLanguage: CodeDTView[]): void {
        this.assessmentLanguage = assessmentLanguage;
    }

    deleteEntityFromList(list: any, oid: number) {
        if (list !== undefined) {
            let i = 0;
            let index = -1;
            list.content.forEach((element) => {
                if (element.oid === oid) {
                    index = i;
                }
                i++;
            });
            if (index !== -1) {
                list.content.splice(index, 1);
            }
        }
    }

    onOrganizationCharge($event) {
        this.organizationsListContent = $event.content;
    }

    onSubAssessmentSelectionChange(oids): void {
        this.subAssessmentsOidList = oids;
    }

    onAssessedBySelectionChange(oid): void {
        this.formGroup.patchValue({ assessedBy: oid });
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
            case 'organization':
                this.selectedAssessedBy = item;
                break;
            case 'assessment':
                this.selectedSubAssessments =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedSubAssessments,
                            this.subAssessmentsOidList,
                            item
                        );
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

    additionalNoteValueChange(additionalNote: NoteDTView[]): void {
        this.additionalNote = additionalNote;
    }

    additionalNoteValidityChange(isValid: boolean) {
        this.isAdditionalNoteValid = isValid;
    }

    additionalNoteSpecificationValueChange(
        additionalNoteSpecification: NoteDTView[]
    ): void {
        this.additionalNoteSpecification = additionalNoteSpecification;
    }

    additionalNoteSpecificationValidityChange(isValid: boolean) {
        this.isAdditionalNoteSpecificationValid = isValid;
    }

    private gotoEntity(oid: number = null) {
        this.openEntityModal[this.entityWillBeOpened] =
            this.modalsService.openModal(this.modalTitle, oid);
        this.setAssessmentBody();
    }

    private addNewLanguageControl(language: string): void {
        this.addTitleControls(language);
        this.addDescriptionControls(language);
        this.addGradeDescriptionControls(language);
        this.addSpecificationDescriptorControls(language);
        this.addGradeTitleControls(language);
    }

    private getAssessmentDetails(): void {
        this.api
            .getAssessment(
                this.editAssessmentOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (assessment: AssessmentSpecView) => {
                    this.editAssessment = assessment;
                    this.availableLanguages =
                        this.editAssessment.additionalInfo.languages;
                    this.language = this.editAssessment.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages =
                        this.multilingualService.setUsedLanguages(
                            this.editAssessment.additionalInfo.languages,
                            this.defaultLanguage
                        );
                    this.setForm();
                    this.getSubAssessments();
                    this.getAssessmentBy();
                    this.credentialBuilderService.extractWebDocuments(
                        _get(
                            this.editAssessment,
                            'specifiedBy.supplementaryDocument',
                            []
                        ),
                        this.specificationOtherDocument
                    );
                    this.credentialBuilderService.extractWebDocuments(
                        _get(
                            this.editAssessment,
                            'specifiedBy.gradingSchemes.supplementaryDocument',
                            []
                        ),
                        this.gradeOtherDocument
                    );
                },
                (err) => this.closeForm()
            );
    }

    private getSubAssessments(): void {
        this.api
            .listHasAssPart(
                this.editAssessment.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((hasPart: PagedResourcesAssessmentSpecLiteView) => {
                this.selectedSubAssessments = hasPart;
            });
    }

    private getAssessmentBy(): void {
        this.api
            .listAssessedBy(
                this.editAssessment.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (organizations: PagedResourcesOrganizationSpecLiteView) => {
                    if (organizations.content.length > 0) {
                        this.selectedAssessedBy = organizations.content[0];
                    }
                }
            );
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

    private addGradeDescriptionControls(
        language: string,
        value: string = null
    ): void {
        this.gradeDescription.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private addSpecificationDescriptorControls(
        language: string,
        value: string = null
    ): void {
        this.assessmentSpecificationDescription.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private addGradeTitleControls(
        language: string,
        value: string = null
    ): void {
        this.gradeTitle.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private updateAssessment(): void {
        this.assessmentBody.oid = this.editAssessment.oid;
        this.api
            .updateAssessment(
                this.assessmentBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (assessment: AssessmentSpecLiteView) => {
                    this.newEntity = assessment;
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
            this.credentialBuilderService.setOcbTabSelected(4);
            this.router.navigateByUrl('credential-builder');
        }
    }

    private createAssessment(): void {
        this.api
            .createAssessment(
                this.assessmentBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (assessment: AssessmentSpecLiteView) => {
                    this.newEntity = assessment;
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

    private setAssessmentBody(): void {
        this.assessmentBody = {
            label: this.label.value,
            defaultLanguage: this.defaultLanguage,
            title: this.credentialBuilderService.getDTView(this.title),
            description: this.credentialBuilderService.getDTView(
                this.description
            ),
            issuedDate: this.dateFormatService.dateToStringDateTime(
                this.assessmentDate.value
            ),
            additionalNote: this.additionalNote,
            idVerification: this.methodOfAssessment.value,
            specifiedBy: this.getAssessmentSpecification(),
            relAssessedBy: this.setAssessedBy(),
            relHasPart: this.setHasPart(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
        };
    }

    private getAssessmentSpecification(): AssessmSpecificationDCView {
        const specifiedBy = {
            identifier: this.credentialBuilderService.getIdentifier(
                this.assessmentSpecificationIdentifier
            ),
            description: this.credentialBuilderService.getDTView(
                this.assessmentSpecificationDescription
            ),
            homePage: this.credentialBuilderService.getHomePage(
                this.homePage.value
            ),
            language:
                _get(this.assessmentLanguage, 'length', 0) > 0
                    ? this.assessmentLanguage
                    : null,
            mode: this.modeOfAssessment.value,
            assessmentType:
                this.credentialBuilderService.getArrayFromSingleItem(
                    this.assessmentType.value
                ),
            additionalNote: this.additionalNoteSpecification,
            supplementaryDocument:
                this.credentialBuilderService.getOtherDocument(
                    this.specificationOtherDocument,
                    this.defaultLanguage
                ),
            gradingSchemes: this.getGradingScheme(),
        };
        return this.credentialBuilderService.getObjectIfContent(specifiedBy);
    }

    private getGradingScheme(): ScoringSchemeDTView {
        const gradeScheme = {
            title: this.credentialBuilderService.getDTView(this.gradeTitle),
            description: this.credentialBuilderService.getDTView(
                this.gradeDescription
            ),
            identifier: this.credentialBuilderService.getIdentifier(
                this.gradeSchemeIdentifier
            ),
            supplementaryDocument:
                this.credentialBuilderService.getOtherDocument(
                    this.gradeOtherDocument,
                    this.defaultLanguage
                ),
        };
        return this.credentialBuilderService.getObjectIfContent(gradeScheme);
    }

    private setForm(): void {
        this.additionalNoteView = _get(
            this.editAssessment,
            'additionalNote',
            []
        );
        this.additionalNoteSpecView = _get(
            this.editAssessment,
            'specifiedBy.additionalNote',
            []
        );
        this.assessmentLanguage = _get(
            this.editAssessment,
            'specifiedBy.language',
            null
        );

        this.addControlsFromView();
        this.formGroup.patchValue({
            label: _get(this.editAssessment, 'label', null),
            defaultTitle: _get(this.editAssessment, 'defaultTitle', null),
            assessmentDate: _get(this.editAssessment, 'issuedDate', null)
                ? new Date(_get(this.editAssessment, 'issuedDate', null))
                : null,
            assessmentSpecificationIdentifier: _get(
                this.editAssessment,
                'specifiedBy.identifier[0].content',
                null
            ),
            homePage: _get(
                this.editAssessment,
                'specifiedBy.homePage[0].content',
                null
            ),
            gradeSchemeIdentifier: _get(
                this.editAssessment,
                'specifiedBy.gradingSchemes.identifier[0].content',
                null
            ),
            methodOfAssessment: _get(
                this.editAssessment,
                'idVerification',
                null
            ),
            assessmentType: _get(
                this.editAssessment,
                'specifiedBy.assessmentType[0]',
                null
            ),
            modeOfAssessment: _get(
                this.editAssessment,
                'specifiedBy.mode',
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
                    _get(this.editAssessment, 'title.contents', [])
                )
            );
            this.addDescriptionControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(this.editAssessment, 'description.contents', [])
                )
            );
            this.addGradeDescriptionControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editAssessment,
                        'specifiedBy.gradingSchemes.description.contents',
                        []
                    )
                )
            );
            this.addSpecificationDescriptorControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editAssessment,
                        'specifiedBy.description.contents',
                        []
                    )
                )
            );
            this.addGradeTitleControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editAssessment,
                        'specifiedBy.gradingSchemes.title.contents',
                        []
                    )
                )
            );
        });
    }

    private setAssessedBy(): SubresourcesOids {
        let relAssessedBy: SubresourcesOids = null;
        if (this.assessedBy.value) {
            relAssessedBy = {
                oid: [
                    Array.isArray(this.assessedBy.value)
                        ? this.assessedBy.value[0]
                        : this.assessedBy.value,
                ],
            };
        }
        return relAssessedBy;
    }

    private setHasPart(): SubresourcesOids {
        let relHasPart: SubresourcesOids = null;
        if (this.subAssessmentsOidList.length > 0) {
            relHasPart = {
                oid: this.subAssessmentsOidList,
            };
        }
        return relHasPart;
    }

    private isFormInvalid(): boolean {
        return (
            this.formGroup.invalid ||
            !this.isAdditionalNoteValid ||
            !this.isAdditionalNoteSpecificationValid
        );
    }

    private validateFormDatesValues() {
        if (
            this.assessmentDate.value &&
            !this.dateFormatService.validateDate(this.assessmentDate.value)
        ) {
            this.assessmentDate.reset();
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
}
