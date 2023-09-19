import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import {
    FormArray,
    FormControl,
    FormGroup,
    Validators,
    FormBuilder,
} from '@angular/forms';
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
    CodeDTView,
    LearningActivitySpecLiteView,
    LearningActivitySpecView,
    LearningActSpecificationDCView,
    LocationDCView,
    NoteDTView,
    OrganizationSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ResourceLearningActivitySpecView,
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
    selector: 'edci-activities-form',
    templateUrl: './activities-form.component.html',
    styleUrls: ['./activities-form.component.scss'],
    providers: [{ provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT }],
})
export class ActivitiesFormComponent implements OnInit, OnDestroy {
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
    get startDate() {
        return this.formGroup.get('startDate');
    }

    get endDate() {
        return this.formGroup.get('endDate');
    }

    get workload() {
        return this.formGroup.get('workload');
    }

    get learningVolume() {
        return this.formGroup.get('learningVolume');
    }

    get completionLevel() {
        return this.formGroup.get('completionLevel');
    }

    get awardingBody() {
        return this.formGroup.get('awardingBody') as FormControl;
    }

    get directedBy() {
        return this.formGroup.get('directedBy');
    }

    get specificationDescription() {
        return this.formGroup.get('specificationDescription') as FormGroup;
    }

    get specificationDescriptionControl() {
        return this.specificationDescription?.controls[
            this.language
        ] as FormControl;
    }

    get learningActivityType() {
        return this.formGroup.get('learningActivityType');
    }

    get modeOfLearning() {
        return this.formGroup.get('modeOfLearning');
    }

    get specificationWorkload() {
        return this.formGroup.get('specificationWorkload');
    }

    get homePage() {
        return this.formGroup.get('homePage');
    }

    get supplementaryDocument() {
        return this.formGroup.get('supplementaryDocument') as FormArray;
    }

    get contactHours() {
        return this.formGroup.get('contactHours');
    }

    /* alternative name controls */
    get specificationTitle() {
        return this.formGroup.get('specificationTitle') as FormGroup;
    }

    get specificationTitleControl() {
        return this.specificationTitle.controls[this.language] as FormControl;
    }

    get locations() {
        return this.formGroup.get('locations') as FormArray;
    }

    parts: UxLink[] = [];

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
    @ViewChild('messageBoxFormError')
        messageBoxFormError: EuiMessageBoxComponent;

    newEntity: LearningActivitySpecView = null;

    @Input() modalTitle: string;
    @Input() language: string;
    @Input() modalId = 'activityModal';
    @Input() editActivityOid?: number;
    @Input() isModal: boolean;
    @Input() modalData: any;
    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    isPrimaryLanguage = true;
    defaultLanguage: string;
    editActivity: LearningActivitySpecView;
    activityLanguage: CodeDTView[] = [];
    locationNUTS: CodeDTView[] = [];
    languages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    destroy$: Subject<boolean> = new Subject<boolean>();
    activityBody: LearningActivitySpecView;
    isLoading = true;
    additionalNoteSpecView: NoteDTView[];
    selectedSubActivities: PagedResourcesLearningActivitySpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    selectedDirectedBy: PagedResourcesOrganizationSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    selectedAwardedBy: PagedResourcesOrganizationSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    subActivitiesOidList: number[] = [];
    directedByOidList: number[] = [];
    awardedByOidList: number[] = [];
    indexToNextTab: number;
    openEntityModal: {
        [key: string]: { modalId: string; isOpen: boolean; oid?: number };
    } = {};
    entityWillBeOpened: Entities | string;
    addressInterface = {
        description: new FormGroup({}),
        address: new FormGroup({}),
        country: [null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]],
        area: [null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]],
    };

    formGroup = new FormGroup({
        // Activity
        label: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_LABELS),
            noSpaceValidator,
        ]),
        title: new FormGroup({}),
        description: new FormGroup({}),
        startDate: new FormControl(null, [dateValidator]),
        endDate: new FormControl(null, [dateValidator]),
        workload: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
            Validators.pattern(Constants.INTEGER_REGULAR_EXPRESSION),
        ]),
        learningVolume: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
            Validators.pattern(Constants.INTEGER_REGULAR_EXPRESSION),
        ]),
        completionLevel: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
            Validators.pattern(Constants.INTEGER_REGULAR_EXPRESSION),
        ]),
        contactHours: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
        ]),
        directedBy: new FormControl(null),
        // Activity Specification
        specificationDescription: new FormGroup({}),
        learningActivityType: new FormControl(null),
        modeOfLearning: new FormControl(null),
        specificationWorkload: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
            Validators.pattern(Constants.INTEGER_REGULAR_EXPRESSION),
        ]),
        homePage: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
        ]),
        supplementaryDocument: new FormArray([]),
        awardingBody: new FormControl(null, [requiredListValidator]),
        specificationTitle: new FormGroup({}),
        locations: new FormArray([]),
    });
    startDateValueInvalid: boolean;
    endDateValueInvalid: boolean;
    unsavedActivities: LearningActivitySpecLiteView[] = [];
    unsavedDirectedBy: OrganizationSpecLiteView[] = [];
    additionalNoteSpecification: NoteDTView[];
    removedLanguage: string;
    addedLanguage: string;
    isAdditionalNoteSpecificationValid: boolean;

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private multilingualService: MultilingualService,
        private notificationService: NotificationService,
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

                if (this.modalData?.activityDetails) {
                    this.setDetailsData(this.modalData);
                } else {
                    this.setNewActivitiesData();
                }
                return;
            }

            if (data.activityDetails) {
                this.setDetailsData(data);
            } else {
                this.setNewActivitiesData();
            }
        });

        this.isPrimaryLanguage = true;
        this.titleValueChangeAutocomplete();
        this.loadBreadcrumb();
        this.formGroup.get('startDate').valueChanges.subscribe(() => {
            this.checkValidDate();
        });
        this.formGroup.get('endDate').valueChanges.subscribe(() => {
            this.checkValidDate();
        });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSave(): void {
        this.validateFormDatesValues();
        if (
            this.formGroup.invalid ||
            this.startDateValueInvalid ||
            this.endDateValueInvalid
        ) {
            this.formGroup.markAllAsTouched();
            this.isLoading = false;
            this.messageBoxFormError.openMessageBox();
        } else {
            this.setActivityBody();
            if (this.editActivity) {
                this.updateActivity();
            } else {
                this.createActivity();
            }
        }
    }

    checkValidDate(): void {
        this.validateFormDatesValues();
        if (
            !this.dateFormatService.validateActivityDates(
                this.startDate.value,
                this.endDate.value
            )
        ) {
            this.startDate.setErrors({ invalidDateError: true });
            this.endDate.setErrors({ invalidDateError: true });
        } else {
            this.startDate.setErrors(null);
            this.endDate.setErrors(null);
        }
    }

    closeForm(): void {
        this.credentialBuilderService.setOcbTabSelected(3);
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
        this.specificationDescription.removeControl(language);
        this.specificationTitle.removeControl(language);
        this.isPrimaryLanguage = this.defaultLanguage === this.language;

        this.removeLocationControls(language);
    }

    createActivity(): void {
        this.isLoading = true;
        this.api
            .createLearningActivity(
                this.activityBody,
                this.translateService.currentLang
            )
            .pipe(take(1))
            .subscribe({
                next: (activity: LearningActivitySpecView) => {
                    this.newEntity = activity;
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

    activityLanguageSelectionChange(activityLanguage: CodeDTView[]): void {
        console.log(activityLanguage);
        this.activityLanguage = activityLanguage;
    }

    locationNUTSSelectionChange(locationNUTS: CodeDTView[]): void {
        this.locationNUTS = locationNUTS;
    }

    onSubActivitiesSelectionChange(oids): void {
        this.subActivitiesOidList = oids;
    }

    onDirectedBySelectionChange(oids): void {
        this.directedByOidList = oids;
    }

    onAwardedBySelectionChange(oids): void {
        this.awardedByOidList = oids;
        this.awardingBody.markAsTouched();
        this.awardingBody.patchValue(oids);
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
            const item = {
                oid: closeInfo.oid,
                displayName: closeInfo.displayName,
                defaultLanguage: this.defaultLanguage,
            };
            switch (this.entityWillBeOpened) {
            case 'organization':
                this.selectedDirectedBy =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedDirectedBy,
                            this.directedByOidList,
                            item
                        );
                break;
            case 'activity':
                this.selectedSubActivities =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedSubActivities,
                            this.subActivitiesOidList,
                            item
                        );
                break;
            }
        }
    }

    titleValueChangeAutocomplete(): void {
        this.title.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
            if (this.specificationTitle.controls[this.language] && this.specificationTitle.controls[this.language].pristine) {
                this.specificationTitle.controls[this.language].setValue(value[this.language]);
            }
        });
    }

    editEntityClicked(event: { oid: number; type: string }) {
        if (event) {
            this.entityWillBeOpened = event.type;
            this.gotoEntity(event.oid);
        }
    }

    additionalNoteSpecificationValueChange(
        additionalNoteSpecification: NoteDTView[]
    ): void {
        this.additionalNoteSpecification = additionalNoteSpecification;
    }

    setCompletionLevelPayload() {
        let completion: number | null = null;
        if (this.completionLevel?.value) {
            completion = parseInt(this.completionLevel?.value);
        } else {
            completion = null;
        }
        return completion;
    }

    setContactHoursPayload() {
        const contactHours: string[] = [];
        const value = this.contactHours?.value;
        if (value) {
            contactHours.push(value);
        }
        return contactHours;
    }

    /* LOCATION */
    getLocationById(index) {
        return this.locations.at(index) as FormGroup;
    }

    addLocationRow(language: string) {
        /* TODO bugfix - addressInterface description and address are sharing control */
        this.locations.push(this.createLocationFormGroup(language));
    }

    addLocationsLanguageControls(language: string) {
        this.locations.controls.forEach(location => {
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
        this.locations.removeAt(i);
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
        const location = this.locations.controls;
        location.forEach(element => {
            const address = element.get('address') as FormGroup;
            address.removeControl(language);
        });
    }

    removeLocationDescriptionControl(language) {
        const location = this.locations.controls;
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
        if (this.additionalNoteSpecification && this.additionalNoteSpecification.length > 0 && this.additionalNoteSpecification[0].contents.length > 0) {
            return this.additionalNoteSpecification;
        } else {
            return null;
        }
    }

    additionalNoteSpecificationValidityChange(isValid: boolean) {
        this.isAdditionalNoteSpecificationValid = isValid;
    }

    setEditActivitiesData(data) {
        this.modalTitle = this.translateService.instant(
            'credential-builder.activities-tab.editActivity'
        );
        this.editActivity = data;
        this.editActivityOid = data.oid;
        this.languages = this.editActivity.additionalInfo.languages;
        this.language = this.editActivity.defaultLanguage;
        this.defaultLanguage = this.language;
        this.selectedLanguages =
            this.multilingualService.setUsedLanguages(
                this.editActivity.additionalInfo.languages,
                this.defaultLanguage
            );
        this.credentialBuilderService.extractWebDocuments(
            _get(
                this.editActivity,
                'specifiedBy.supplementaryDocument',
                []
            ),
            this.supplementaryDocument
        );
        this.setForm();
    }

    setNewActivitiesData() {
        this.modalTitle = this.translateService.instant(
            'credential-builder.activities-tab.createActivity'
        );
        this.language = this.language || this.translateService.currentLang;
        this.credentialBuilderService.addOtherDocumentRow(
            this.supplementaryDocument
        );
        this.defaultLanguage = this.language;
        this.selectedLanguages.push({
            code: this.language,
            label: this.language,
        });
        this.addLocationRow(this.language);
        this.addNewLanguageControl(this.language);
    }

    setDetailsData(data) {
        this.setEditActivitiesData(data.activityDetails);

        if (data?.activityDirectedBy) {
            this.selectedDirectedBy = data?.activityDirectedBy;
            const oids = data?.activityDirectedBy?.content?.map(item => item.oid);
            this.directedByOidList = oids;
            this.directedBy.patchValue(oids);
        }

        if (data?.activityAwardedBy) {
            this.selectedAwardedBy = data?.activityAwardedBy;
            const oids = data?.activityAwardedBy?.content?.map(item => item.oid);
            this.awardedByOidList = oids;
            this.awardingBody.patchValue(oids);
        }

        if (data?.activitySubActivities) {
            this.selectedSubActivities = data?.activitySubActivities;
            const oids = data?.activitySubActivities?.content?.map(item => item.oid);
            this.subActivitiesOidList = oids;
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

    private addLocationControls(locations: LocationDCView[]): void {
        if (locations?.length > 0) {
            locations.forEach(location => {
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
                        new FormControl(fullAddressLangContent, [Validators.maxLength(Constants.MAX_LENGTH_LONG)] )
                    );
                    descCtrl.addControl(
                        langCode,
                        new FormControl(descriptionLangContent, [Validators.maxLength(Constants.MAX_LENGTH_LONG)])
                    );
                });

                locationGroup.get('area').patchValue(location.spatialCode[0]);
                locationGroup.get('country').patchValue(location.address[0]?.countryCode);

                this.locations.push(locationGroup);
            });
        } else {
            this.addLocationRow(this.language);
        }
    }

    private gotoEntity(oid: number = null) {
        this.openEntityModal[this.entityWillBeOpened] =
            this.modalsService.openModal(this.modalTitle, oid);
        this.setActivityBody();
    }

    private addNewLanguageControl(language: string): void {
        this.addTitleControls(language);
        this.addDescriptionControls(language);
        this.addSpecificationDescriptionControls(language);
        this.addSpecificationTitleControls(language);
        this.addLocationsLanguageControls(language);
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

    private addSpecificationDescriptionControls(
        language: string,
        value: string = null
    ): void {
        this.specificationDescription.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private addSpecificationTitleControls(language: string, value: string = null): void {
        this.specificationTitle.addControl(
            language,
            new FormControl(value, [Validators.maxLength(Constants.MAX_LENGTH_LONG), Validators.required, noSpaceValidator])
        );
    }

    private updateActivity(): void {
        this.isLoading = true;
        this.activityBody.oid = this.editActivity.oid;
        this.api
            .updateLearningActivity(
                this.activityBody,
                this.translateService.currentLang
            )
            .pipe(take(1))
            .subscribe({
                next: (activity: ResourceLearningActivitySpecView) => {
                    this.newEntity = activity;
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
            this.isLoading = false;
            this.credentialBuilderService.setOcbTabSelected(3);
            this.router.navigateByUrl('credential-builder');
        }
    }
    private setActivityBody(): void {
        this.activityBody = {
            label:
                this.label.value !== null && this.label.value.length > 0
                    ? this.label.value
                    : null,
            defaultLanguage: this.defaultLanguage,
            title: this.credentialBuilderService.getDTView(this.title),
            description: this.credentialBuilderService.getDTView(
                this.description
            ),
            startedAtTime: this.dateFormatService.dateToStringDateTime(
                this.startDate.value
            ),
            endedAtTime: this.dateFormatService.dateToStringDateTime(
                this.endDate.value
            ),
            workload:
                this.workload.value !== null && this.workload.value.length > 0
                    ? this.workload.value
                    : null,
            location: this.formsService.getLocationDCViews(this.locations),
            additionalNote: this.setAdditionalNote(),
            specifiedBy: this.getActivitiesSpecification(),
            relDirectedBy: this.setDirectedBy(),
            relAwardingBody: this.setAwardedBy(),
            relHasPart: this.setSubActivities(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
            levelOfCompletion: this.setCompletionLevelPayload(),
        };
    }

    private getActivitiesSpecification(): LearningActSpecificationDCView {
        const specifiedBy = {
            description: this.credentialBuilderService.getDTView(
                this.specificationDescription
            ),
            workload:
                this.specificationWorkload.value !== null &&
                    this.specificationWorkload.value.length > 0
                    ? this.specificationWorkload.value
                    : null,
            supplementaryDocument:
                this.credentialBuilderService.getOtherDocument(
                    this.supplementaryDocument,
                    this.defaultLanguage
                ),
            language:
                _get(this.activityLanguage, 'length', 0) > 0
                    ? this.activityLanguage
                    : null,

            learningActivityType:
                this.credentialBuilderService.getArrayFromSingleItem(
                    this.learningActivityType.value
                ),
            mode: this.modeOfLearning.value
                ? [this.modeOfLearning.value]
                : null,
            homePage: this.credentialBuilderService.getHomePage(
                this.homePage.value
            ),
            additionalNote: this.additionalNoteSpecification,
            title: this.credentialBuilderService.getDTView(
                this.specificationTitle
            ),
            volumeOfLearning: this.learningVolume.value,
            contactHours: this.setContactHoursPayload(),
        };
        return this.credentialBuilderService.getObjectIfContent(specifiedBy);
    }

    private setSubActivities(): SubresourcesOids {
        let relSubActivities: SubresourcesOids = null;
        if (this.subActivitiesOidList.length > 0) {
            relSubActivities = {
                oid: this.subActivitiesOidList,
            };
        }
        return relSubActivities;
    }

    private setDirectedBy(): SubresourcesOids {
        let relDirectedBy: SubresourcesOids = null;
        if (this.directedByOidList.length > 0) {
            relDirectedBy = {
                oid: this.directedByOidList,
            };
        }
        return relDirectedBy;
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

    private setForm(): void {
        /* get additional note data */
        this.additionalNoteSpecView = _get(
            this.editActivity,
            'specifiedBy.additionalNote',
            []
        );
        this.activityLanguage = _get(
            this.editActivity,
            'specifiedBy.language',
            null
        );
        /* useless var? */
        this.locationNUTS = _get(
            this.editActivity,
            'location[0].spatialCode',
            []
        );
        this.addControlsFromView();
        this.formGroup.patchValue({
            label: this.editActivity.label,
            startDate: this.getStartDate(),
            endDate: this.getEndDate(),
            workload: _get(this.editActivity, 'workload', null),
            learningVolume: _get(
                this.editActivity,
                'specifiedBy.volumeOfLearning',
                null
            ),
            learningActivityType: _get(
                this.editActivity,
                'specifiedBy.learningActivityType[0]',
                null
            ),
            modeOfLearning: _get(
                this.editActivity,
                'specifiedBy.mode[0]',
                null
            ),
            specificationWorkload: _get(
                this.editActivity,
                'specifiedBy.workload',
                null
            ),
            homePage: _get(
                this.editActivity,
                'specifiedBy.homePage[0].contentUrl',
                null
            ),
            completionLevel: _get(this.editActivity, 'levelOfCompletion', null),
            contactHours: _get(
                this.editActivity,
                'specifiedBy.contactHours[0]',
                null
            ),
        });
        this.addLocationControls(
            _get(this.editActivity, 'location', [])
        );
        this.isLoading = false;
    }

    private getStartDate(): Date | null {
        const date = _get(this.editActivity, 'startedAtTime', null);
        return date ? new Date(date) : null;
    }

    private getEndDate(): Date | null {
        const date = _get(this.editActivity, 'endedAtTime', null);
        return date ? new Date(date) : null;
    }

    private addControlsFromView(): void {
        this.languages.forEach((language: string) => {
            this.addTitleControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    this.editActivity.title.contents
                )
            );
            this.addDescriptionControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(this.editActivity, 'description.contents', [])
                )
            );
            this.addSpecificationDescriptionControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editActivity,
                        'specifiedBy.description.contents',
                        []
                    )
                )
            );
            this.addSpecificationTitleControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(this.editActivity, 'specifiedBy.title.contents', [])
                )
            );
        });
    }

    private validateFormDatesValues() {
        if (
            this.startDate.value &&
            !this.dateFormatService.validateDate(this.startDate.value)
        ) {
            this.startDateValueInvalid = true;
            this.startDate.reset();
        } else {
            this.startDateValueInvalid = false;
        }

        if (
            this.endDate.value &&
            !this.dateFormatService.validateDate(this.endDate.value)
        ) {
            this.endDateValueInvalid = true;
            this.endDate.reset();
        } else {
            this.endDateValueInvalid = false;
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
