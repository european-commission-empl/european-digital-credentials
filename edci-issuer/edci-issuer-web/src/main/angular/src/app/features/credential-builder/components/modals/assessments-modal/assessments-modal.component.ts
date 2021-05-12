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
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage, UxService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { DateFormatService } from '@services/date-format.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { MoreInformationComponent } from '@shared/components/more-information/more-information.component';
import { Constants, Entities } from '@shared/constants';
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
    V1Service,
} from '@shared/swagger';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-assessments-modal',
    templateUrl: './assessments-modal.component.html',
    styleUrls: ['./assessments-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AssessmentsModalComponent implements OnInit, OnDestroy {

    @Input() modalTitle: string;
    @Input() language: string;
    @Input() modalId: string = 'assessmentModal';
    @Input() editAssessmentOid?: number;
    @Output() onCloseModal: EventEmitter<{isEdit: boolean, oid: number, title: string}> = new EventEmitter();
    @ViewChild('additionalNoteSpecification')
    additionalNoteSpecification: MoreInformationComponent;
    @ViewChild('additionalNote')
    additionalNote: MoreInformationComponent;

    defaultLanguage: string;
    editAssessment: AssessmentSpecView;
    assessmentLanguage: CodeDTView[] = [];
    selectedLanguages: UxLanguage[] = [];
    availableLanguages: string[] = [];
    destroy$: Subject<boolean> = new Subject<boolean>();
    assessmentBody: AssessmentSpecView;
    isSaveDisabled: boolean = false;
    isLoading: boolean = true;
    additionalNoteView: NoteDTView[];
    additionalNoteSpecView: NoteDTView[];
    selectedAssessedBy: OrganizationSpecLiteView;
    selectedSubAssessments: PagedResourcesAssessmentSpecLiteView;
    subAssessmentsOidList: number[] = [];
    indexToNextTab: number;
    openEntityModal: { [key: string]: { modalId: string, isOpen: boolean, oid?: number } } = {};
    entityWillBeOpened: Entities;
    isNewEntityDisabled: boolean;

    formGroup = new FormGroup({
        defaultTitle: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.required,
            noSpaceValidator,
        ]),
        // Assessment
        title: new FormGroup({}),
        description: new FormGroup({}),
        assessmentDate: new FormControl(null),
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

    get defaultTitle() {
        return this.formGroup.get('defaultTitle') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
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

    get gradeSchemeIdentifier() {
        return this.formGroup.get('gradeSchemeIdentifier') as FormControl;
    }

    get gradeDescription() {
        return this.formGroup.get('gradeDescription') as FormGroup;
    }

    get gradeOtherDocument() {
        return this.formGroup.get('gradeOtherDocument') as FormArray;
    }

    constructor(
        public uxService: UxService,
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private dateFormatService: DateFormatService
    ) {}

    ngOnInit() {
        if (this.editAssessmentOid) {
            this.modalTitle = this.translateService.instant('credential-builder.assessment-tab.editAssessment');
            this.getAssessmentDetails();
        } else {
            this.modalTitle = this.translateService.instant('"credential-builder.assessment-tab.createAssessment');
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

    onSave(): void {
        if (this.isFormInvalid()) {
            this.uxService.markControlsTouched(this.formGroup);
            this.isLoading = false;
            this.uxService.openMessageBox('messageBoxFormError');
        } else {
            this.isSaveDisabled = true;
            this.setAssessmentBody();
            if (this.editAssessment) {
                this.updateAssessment();
            } else {
                this.createAssessment();
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
        this.moreInformationAddLanguage(language);
    }

    languageRemoved(language: string): void {
        if (this.language === language) {
            this.language = this.selectedLanguages[0].code.toLowerCase();
        }
        this.title.removeControl(language);
        this.description.removeControl(language);
        this.gradeDescription.removeControl(language);
        this.assessmentSpecificationDescription.removeControl(language);
        this.gradeTitle.removeControl(language);
        this.moreInformationRemoveLanguage(language);
    }

    assessmentLanguageSelectionChange(assessmentLanguage: CodeDTView[]): void {
        this.assessmentLanguage = assessmentLanguage;
    }

    onSubAssessmentSelectionChange(oids: number[]): void {
        this.subAssessmentsOidList = oids;
    }

    onAssessedBySelectionChange(oid: number): void {
        this.formGroup.patchValue({ assessedBy: oid });
    }

    newEntityClicked(value: Entities, event = undefined): void {
        if (event === undefined) {
            this.entityWillBeOpened = value;
            this.uxService.openMessageBox('messageBoxNewEntityWarning');
        } else {
            if (event) {
                this.gotoEntity();
            }
        }
    }

    closeNewEntityModal(closeInfo: {isEdit: boolean, oid?: number, title?: string}) {
        this.openEntityModal[this.entityWillBeOpened].isOpen = false;
        this.uxService.closeModal(this.credentialBuilderService.getIdFromLastModalAndRemove());
        this.uxService.openModal(this.modalId);
        if (closeInfo.oid) {
            let item: any = {
                oid: closeInfo.oid,
                defaultTitle: closeInfo.title,
                defaultLanguage: this.defaultLanguage
            };
            switch (this.entityWillBeOpened) {
            case 'organization':
                this.selectedAssessedBy = item;
                break;
            case 'assessment':
                this.selectedSubAssessments =
                    this.credentialBuilderService.fillMultipleInput(this.selectedSubAssessments, this.subAssessmentsOidList, item);
                break;
            }
        }
    }

    editEntityClicked(event: { oid: number, type: Entities }) {
        if (event) {
            this.entityWillBeOpened = event.type;
            this.gotoEntity(event.oid);
        }
    }

    private gotoEntity(oid: number = null) {
        this.uxService.closeMessageBox('messageBoxNewEntityWarning');
        const newEntityModalId = this.credentialBuilderService.generateNewIdModal();
        this.openEntityModal[this.entityWillBeOpened] = {
            isOpen: true,
            modalId: newEntityModalId,
            oid
        };
        this.uxService.closeModal(this.modalId);
        this.uxService.openModal(newEntityModalId);
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
            ).pipe(takeUntil(this.destroy$))
            .subscribe(
                (assessment: AssessmentSpecView) => {
                    this.editAssessment = assessment;
                    this.availableLanguages = this.editAssessment.additionalInfo.languages;
                    this.language = this.editAssessment.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages = this.multilingualService.setUsedLanguages(
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
                (err) => this.closeModal(false)
            );
    }

    private getSubAssessments(): void {
        this.api
            .listHasAssPart(
                this.editAssessment.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (hasPart: PagedResourcesAssessmentSpecLiteView) => {
                    this.selectedSubAssessments = hasPart;
                },
                (err) => {}
            );
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
                },
                (err) => {}
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
            .subscribe(
                (assessment: AssessmentSpecLiteView) => {
                    this.isLoading = false;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.closeModal(true, assessment.oid, assessment.defaultTitle);
                },
                (err) => {
                    this.isLoading = false;
                    this.closeModal(false);
                }
            );
    }

    private createAssessment(): void {
        this.api
            .createAssessment(
                this.assessmentBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (assessment: AssessmentSpecLiteView) => {
                    this.isLoading = false;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.create'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.closeModal(true, assessment.oid, assessment.defaultTitle);
                },
                (err) => {
                    this.isLoading = false;
                    this.closeModal(false);
                }
            );
    }

    private setAssessmentBody(): void {
        this.assessmentBody = {
            defaultTitle: this.defaultTitle.value,
            defaultLanguage: this.defaultLanguage,
            title: this.credentialBuilderService.getDTView(this.title),
            description: this.credentialBuilderService.getDTView(
                this.description
            ),
            issuedDate: this.dateFormatService.dateToStringDateTime(
                this.assessmentDate.value
            ),
            additionalNote: this.additionalNote.getAdditionalNotes(),
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
        let specifiedBy: AssessmSpecificationDCView;
        specifiedBy = {
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
            assessmentType: this.credentialBuilderService.getArrayFromSingleItem(
                this.assessmentType.value
            ),
            additionalNote: this.additionalNoteSpecification.getAdditionalNotes(),
            supplementaryDocument: this.credentialBuilderService.getOtherDocument(
                this.specificationOtherDocument,
                this.defaultLanguage
            ),
            gradingSchemes: this.getGradingScheme(),
        };
        return this.credentialBuilderService.getObjectIfContent(specifiedBy);
    }

    private getGradingScheme(): ScoringSchemeDTView {
        let gradeScheme: ScoringSchemeDTView;
        gradeScheme = {
            title: this.credentialBuilderService.getDTView(this.gradeTitle),
            description: this.credentialBuilderService.getDTView(
                this.gradeDescription
            ),
            identifier: this.credentialBuilderService.getIdentifier(
                this.gradeSchemeIdentifier
            ),
            supplementaryDocument: this.credentialBuilderService.getOtherDocument(
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
                oid: [Array.isArray(this.assessedBy.value) ? this.assessedBy.value[0] : this.assessedBy.value],
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
            this.additionalNote.isFormInvalid() ||
            this.additionalNoteSpecification.isFormInvalid()
        );
    }

    private moreInformationRemoveLanguage(language: string): void {
        this.additionalNote.languageRemoved(language);
        this.additionalNoteSpecification.languageRemoved(language);
    }

    private moreInformationAddLanguage(language: string): void {
        this.additionalNote.languageAdded(language);
        this.additionalNoteSpecification.languageAdded(language);
    }
}
