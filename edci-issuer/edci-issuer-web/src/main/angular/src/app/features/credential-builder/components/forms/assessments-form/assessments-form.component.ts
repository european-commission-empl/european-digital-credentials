import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { UxLanguage, UxLink } from '@eui/core';
import { FormsService } from '@features/credential-builder/services/forms.service';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { DateFormatService } from '@services/date-format.service';
import { NotificationService } from '@services/error.service';
import { ModalsService } from '@services/modals.service';
import { MultilingualService } from '@services/multilingual.service';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';
import { Constants, Entities, TIME_FORMAT } from '@shared/constants';
import {
    AddressDCView,
    AssessmentSpecLiteView,
    AssessmentSpecView,
    AssessmSpecificationDCView,
    CodeDTView,
    LocationDCView,
    NoteDTView,
    OrganizationSpecLiteView,
    PagedResourcesAssessmentSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ScoringSchemeDTView,
    SubresourcesOids,
    V1Service,
} from '@shared/swagger';
import { dateValidator } from '@shared/validators/date-validator';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { requiredListValidator } from '@shared/validators/required-list-validator';
import { get as _get } from 'lodash';
import { Observable, Subject } from 'rxjs';
import { takeUntil, take } from 'rxjs/operators';

@Component({
    selector: 'edci-assessments-form',
    templateUrl: './assessments-form.component.html',
    styleUrls: ['./assessments-form.component.scss'],
    providers: [{ provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT }],
})
export class AssessmentsFormComponent implements OnInit, OnDestroy {

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

    get awardingBody() {
        return this.formGroup.get('awardingBody') as FormControl;
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

    get mode() {
        return this.formGroup.get('mode') as FormArray;
    }

    get dcType() {
        return this.formGroup.get('dcType') as FormArray;
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

    /* alternative name controls */
    get specificationTitle() {
        return this.formGroup.get('specificationTitle') as FormGroup;
    }

    get specificationTitleControl() {
        return this.specificationTitle.controls[this.language] as FormControl;
    }

    /* adress controls */
    get location() {
        return this.formGroup.get('location') as FormArray;
    }
    parts: UxLink[] = [];

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
    @ViewChild('messageBoxFormError')
    messageBoxFormError: EuiMessageBoxComponent;
    @Input() modalTitle: string;
    @Input() language: string;
    @Input() modalId = 'assessmentModal';
    @Input() editAssessmentOid?: number;
    @Input() isModal: boolean;
    @Input() modalData: any;
    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    newEntity: AssessmentSpecLiteView = null;

    organizationsListContent: [];
    isPrimaryLanguage = true;
    defaultLanguage: string;
    editAssessment: AssessmentSpecView;
    assessmentLanguage: CodeDTView[] = [];
    selectedLanguages: UxLanguage[] = [];
    languages: string[] = [];
    destroy$: Subject<boolean> = new Subject<boolean>();
    assessmentBody: AssessmentSpecView;
    isLoading = true;
    additionalNoteView: NoteDTView[];
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
    unsavedSubAssessments: AssessmentSpecLiteView[] = [];
    additionalNote: NoteDTView[];
    removedLanguage: string;
    addedLanguage: string;
    isAdditionalNoteValid: boolean;

    addressInterface = {
        description: new FormGroup({}),
        address: new FormGroup({}),
        country: [null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]],
        area: [null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]],
    };

    /* awardedBy controls */
    selectedAwardedBy: PagedResourcesOrganizationSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };

    awardedByOidList: number[] = [];

    /* form  */
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
        mode: new FormArray([]),
        dcType: new FormArray([]),
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
        //
        //
        awardingBody: new FormControl(null, [requiredListValidator]),
        specificationTitle: new FormGroup({}),
        location: new FormArray([])
    });

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private dateFormatService: DateFormatService,
        private route: ActivatedRoute,
        private router: Router,
        private modalsService: ModalsService,
        private fb: FormBuilder,
        private readonly pageLoadingSpinnerService: PageLoadingSpinnerService,
        private formsService: FormsService
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
                if (this.modalData?.assessmentDetails) {
                    this.setDetailsData(this.modalData);
                } else {
                    this.setNewAssessmentsData();
                }
                return;
            }

            if (data.assessmentDetails) {
                this.setDetailsData(data);
            } else {
                this.setNewAssessmentsData();
            }
        });

        this.isPrimaryLanguage = true;
        this.titleValueChangeAutocomplete();
        this.loadBreadcrumb();
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
            this.formGroup.markAllAsTouched();
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
        this.specificationTitle.removeControl(language);
        this.removeLocationControls(language);
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

    onAssessedBySelectionChange(oids): void {
        this.formGroup.patchValue({ assessedBy: oids });
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

    onAwardedBySelectionChange(oids): void {
        this.awardedByOidList = oids;
        this.awardingBody.markAsTouched();
        this.awardingBody.patchValue(oids);
    }

    titleValueChangeAutocomplete(): void {
        this.title.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
            if (this.specificationTitle.controls[this.language] && this.specificationTitle.controls[this.language].pristine) {
                this.specificationTitle.controls[this.language].setValue(value[this.language]);
            }
        });
    }
    /* LOCATION */
    getLocationById(index) {
        return this.location.at(index) as FormGroup;
    }

    addLocationRow(language: string) {
        /* TODO bugfix - addressInterface description and address are sharing control */
        this.location.push(this.createLocationFormGroup(language));
    }

    addLocationsLanguageControls(language: string) {
        this.location.controls.forEach(location => {
            this.addLocationGroupLanguageControls(language, location as FormGroup);
        });
    }

    addLocationGroupLanguageControls(language: string, locationGroup: FormGroup) {
        const descCtrl = locationGroup.get('description') as FormGroup;
        descCtrl.addControl(
            language,
            new FormControl(null, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );

        const addCtrl = locationGroup.get('address') as FormGroup;
        addCtrl.addControl(
            language,
            new FormControl(null, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    createLocationFormGroup(language: string): FormGroup {
        const locationGroup = this.generateBaseLocationGroup();
        this.addLocationGroupLanguageControls(language, locationGroup);
        return locationGroup;
    }

    deleteLocationRow(i) {
        this.location.removeAt(i);
    }

    locationCountrySelectionChange(country: CodeDTView, index): void {
        const address = this.getLocationById(index);
        address.get('country').patchValue(country);
    }

    locationAreaSelectionChange(area: CodeDTView[], index): void {
        const address = this.getLocationById(index);
        address.get('area').patchValue(area);
    }

    removeLocationControls(language) {
        this.removeLocationAddressControl(language);
        this.removeLocationDescriptionControl(language);
    }

    removeLocationAddressControl(language) {
        const location = this.location.controls;
        location.forEach(element => {

            const address = element.get('address') as FormGroup;
            address.removeControl(language);
        });
    }

    removeLocationDescriptionControl(language) {
        const location = this.location.controls;
        location.forEach(element => {
            const address = element.get('description') as FormGroup;
            address.removeControl(language);
        });
    }

    onAddLocationRow() {
        this.addLocationRow(this.language);
    }

    onDeleteLocationRow(index) {
        this.deleteLocationRow(index);
    }

    setAdditionalNote() {
        if (this.additionalNote && this.additionalNote.length > 0 && this.additionalNote[0].contents.length > 0) {
            return this.additionalNote;
        } else {
            return null;
        }
    }

    onControlledListChange(codes: CodeDTView[], formArrayName: string) {
        const formArray = this.formGroup.get(formArrayName) as FormArray;
        formArray.clear();
        codes.forEach(code => formArray.push(new FormControl(code)));
    }

    setEditAssessmentsData(data) {
        this.modalTitle = this.translateService.instant(
            'credential-builder.assessment-tab.editAssessment'
        );
        this.editAssessment = data;
        this.editAssessmentOid = data.oid;
        this.languages = this.editAssessment.additionalInfo.languages;
        this.language = this.editAssessment.defaultLanguage;
        this.defaultLanguage = this.language;
        this.selectedLanguages =
            this.multilingualService.setUsedLanguages(
                this.editAssessment.additionalInfo.languages,
                this.defaultLanguage
            );
        this.setForm();
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
                'specifiedBy.gradingScheme.supplementaryDocument',
                []
            ),
            this.gradeOtherDocument
        );
    }

    setNewAssessmentsData() {
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
        this.addLocationRow(this.language);
        this.addNewLanguageControl(this.language);
        this.defaultLanguage = this.language;
        this.selectedLanguages.push({
            code: this.language,
            label: this.language,
        });
    }

    setDetailsData(data) {
        this.setEditAssessmentsData(data.assessmentDetails);
        if (data?.assessmentSubAssessments) {
            this.selectedSubAssessments = data?.assessmentSubAssessments;
            const oids = data?.assessmentSubAssessments?.content?.map(item => item.oid);
            this.subAssessmentsOidList = oids;
        }

        if (data?.assessmentAssessments && data?.assessmentAssessments?.content?.length > 0) {
            this.selectedAssessedBy = data?.assessmentAssessments?.content[0];
            this.assessedBy.patchValue(data?.assessmentAssessments?.content[0]);
        }

        if (data?.assessmentAwardedBy) {
            this.selectedAwardedBy = data?.assessmentAwardedBy;
            const oids = data?.assessmentAwardedBy?.content?.map(item => item.oid);
            this.awardedByOidList = oids;
            this.awardingBody.patchValue(oids);
        }
    }

    private generateBaseLocationGroup(): FormGroup {
        return this.fb.group({
            description: new FormGroup({}),
            address: new FormGroup({}),
            country: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
            area: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
        });
    }

    private addLocationControls(location: LocationDCView): void {
        if (location) {
            const locationGroup = this.generateBaseLocationGroup();
            const descCtrl = locationGroup.get('description') as FormGroup;
            const addrCtrl = locationGroup.get('address') as FormGroup;

            const fullAddressContents = location.address[0]?.fullAddress?.contents;
            const descriptionContents = location.description?.contents;

            this.selectedLanguages.forEach(lang => {
                const langCode = lang.code.toLowerCase();
                const fullAddressLangContent = fullAddressContents ?
                    this.multilingualService.getContentFromLanguage(langCode, fullAddressContents) : null;
                const descriptionLangContent = descriptionContents ?
                    this.multilingualService.getContentFromLanguage(langCode, descriptionContents) : null;
                addrCtrl.addControl(
                    langCode,
                    new FormControl(fullAddressLangContent, [Validators.maxLength(Constants.MAX_LENGTH_LONG)])
                );
                descCtrl.addControl(
                    langCode,
                    new FormControl(descriptionLangContent, [Validators.maxLength(Constants.MAX_LENGTH_LONG)])
                );
            });

            locationGroup.get('area').patchValue(location.spatialCode[0]);
            locationGroup.get('country').patchValue(location.address[0]?.countryCode);

            this.location.push(locationGroup);
        } else {
            this.addLocationRow(this.language);
        }
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
        this.addLocationsLanguageControls(language);
        this.addSpecificationTitleControls(language);
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
        this.isLoading = true;
        this.assessmentBody.oid = this.editAssessment.oid;
        this.api
            .updateAssessment(
                this.assessmentBody,
                this.translateService.currentLang
            )
            .pipe(take(1))
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
            this.credentialBuilderService.setOcbTabSelected(4);
            this.router.navigateByUrl('credential-builder');
        }
    }

    private createAssessment(): void {
        this.isLoading = true;
        this.api
            .createAssessment(
                this.assessmentBody,
                this.translateService.currentLang
            )
            .pipe(take(1))
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

    private setAssessmentBody(): void {
        this.assessmentBody = {
            label: this.label.value,
            defaultLanguage: this.defaultLanguage,
            title: this.credentialBuilderService.getDTView(this.title),
            description: this.credentialBuilderService.getDTView(
                this.description
            ),
            dateIssued: this.dateFormatService.dateToStringDateTime(
                this.assessmentDate.value
            ),
            idVerification: this.methodOfAssessment.value,
            specifiedBy: this.getAssessmentSpecification(),
            relAssessedBy: this.setAssessedBy(),
            relHasPart: this.setHasPart(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
            relAwardingBody: this.setAwardedBy(),
            location: this.formsService.getLocationDCView(this.location)
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
            mode: this.mode.getRawValue().filter(item => Object.values(item).some(x => x !== null && x !== '')),
            dcType: this.dcType.getRawValue().filter(item => Object.values(item).some(x => x !== null && x !== '')),
            assessmentType:
                this.credentialBuilderService.getArrayFromSingleItem(
                    this.assessmentType.value
                ),
            additionalNote: this.setAdditionalNote(),
            supplementaryDocument:
                this.credentialBuilderService.getOtherDocument(
                    this.specificationOtherDocument,
                    this.defaultLanguage
                ),
            gradingScheme: this.getGradingScheme(),
            title: this.credentialBuilderService.getDTView(
                this.specificationTitle
            ),
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
            assessmentDate: _get(this.editAssessment, 'dateIssued', null)
                ? new Date(_get(this.editAssessment, 'dateIssued', null))
                : null,
            assessmentSpecificationIdentifier: _get(
                this.editAssessment,
                'specifiedBy.identifier[0].content',
                null
            ),
            homePage: _get(
                this.editAssessment,
                'specifiedBy.homePage[0].contentUrl',
                null
            ),
            gradeSchemeIdentifier: _get(
                this.editAssessment,
                'specifiedBy.gradingScheme.identifier[0].notation',
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
        });
        _get(this.editAssessment, 'specifiedBy.mode', []).forEach(mode => {
            this.mode.push(new FormControl(mode));
        });
        _get(this.editAssessment, 'specifiedBy.dcType', [{}]).forEach(type => {
            this.dcType.push(new FormControl(type));
        });
        this.addLocationControls(
            _get(this.editAssessment, 'location', null)
        );
        this.isLoading = false;
    }

    private addControlsFromView(): void {
        this.languages.forEach((language: string) => {
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
                        'specifiedBy.gradingScheme.description.contents',
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
                        'specifiedBy.gradingScheme.title.contents',
                        []
                    )
                )
            );

            this.addSpecificationTitleControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(this.editAssessment, 'specifiedBy.title.contents', [])
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
            this.formGroup.invalid
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

    private setAwardedBy(): SubresourcesOids {
        let relAwardedBy: SubresourcesOids = null;
        if (this.awardedByOidList.length > 0) {
            relAwardedBy = {
                oid: this.awardedByOidList,
            };
        }
        return relAwardedBy;
    }

    private addSpecificationTitleControls(language: string, value: string = null): void {
        this.specificationTitle.addControl(
            language,
            new FormControl(value, [Validators.maxLength(Constants.MAX_LENGTH_LONG), Validators.required, noSpaceValidator])
        );
    }
}
