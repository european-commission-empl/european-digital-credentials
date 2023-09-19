import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { UxLanguage, UxLink } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { DateFormatService } from '@services/date-format.service';
import { NotificationService } from '@services/error.service';
import { ModalsService } from '@services/modals.service';
import { MultilingualService } from '@services/multilingual.service';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';
import { Constants, Entities, TIME_FORMAT } from '@shared/constants';
import {
    AwardingProcessDCView,
    CodeDTView,
    LearningAchievementSpecLiteView,
    LearningAchievementSpecView,
    LearningActivitySpecLiteView,
    LearningOutcomeSpecLiteView,
    LearningSpecificationDCView,
    NoteDTView,
    OrganizationSpecLiteView,
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesLearningAchievementSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    PagedResourcesLearningOutcomeSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ResourceLearningAchievementSpecView,
    SubresourcesOids,
    V1Service,
    PagedResourcesAssessmentSpecLiteView,
    CreditPointView,
} from '@shared/swagger';
import { dateValidator } from '@shared/validators/date-validator';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { requiredListValidator } from '@shared/validators/required-list-validator';
import { get as _get } from 'lodash';
import { Observable, Subject } from 'rxjs';
import { takeUntil, take } from 'rxjs/operators';

@Component({
    selector: 'edci-achievements-form',
    templateUrl: './achievements-form.component.html',
    styleUrls: ['./achievements-form.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [{ provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT }],
})
export class AchievementsFormComponent implements OnInit, OnDestroy {
    get label() {
        return this.formGroup.get('label') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get titleControl() {
        return this.title.controls[this.language] as FormControl;
    }

    get awardingBody() {
        return this.formGroup.get('awardingBody') as FormControl;
    }

    get awardingDate() {
        return this.formGroup.get('awardingDate');
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
    }

    get descriptionControl() {
        return this.description.controls[this.language] as FormControl;
    }

    get provenBy() {
        return this.formGroup.get('provenBy');
    }

    get entitledTo() {
        return this.formGroup.get('entitledTo');
    }

    get specificationTitle() {
        return this.formGroup.get('specificationTitle') as FormGroup;
    }

    get specificationTitleControl() {
        return this.specificationTitle.controls[this.language] as FormControl;
    }

    get learningOutcomeSummary() {
        return this.formGroup.get('learningOutcomeSummary') as FormGroup;
    }

    get learningOutcomeSummaryControl() {
        return this.learningOutcomeSummary.controls[this.language] as FormControl;
    }

    get volumeOfLearning() {
        return this.formGroup.get('volumeOfLearning');
    }

    get maxDuration() {
        return this.formGroup.get('maxDuration');
    }

    get learningSetting() {
        return this.formGroup.get('learningSetting');
    }

    get entryRequirements() {
        return this.formGroup.get('entryRequirements') as FormGroup;
    }

    get entryRequirementsControl() {
        return this.entryRequirements.controls[this.language] as FormControl;
    }

    get homePage() {
        return this.formGroup.get('homePage');
    }

    get otherWebDocuments() {
        return this.formGroup.get('otherWebDocuments') as FormArray;
    }

    get partialQualification() {
        return this.formGroup.get('partialQualification');
    }

    get eqfLevel() {
        return this.formGroup.get('eqfLevel');
    }

    get dcTypes() {
        return this.formGroup.get('dcType') as FormArray;
    }

    get targetGroup() {
        return this.formGroup.get('targetGroup') as FormArray;
    }

    get educationLevel() {
        return this.formGroup.get('educationLevel') as FormArray;
    }

    get educationSubject() {
        return this.formGroup.get('educationSubject') as FormArray;
    }

    get educationalSystemNote() {
        return this.formGroup.get('educationalSystemNote') as FormGroup;
    }

    get educationalSystemNoteControl() {
        return this.educationalSystemNote.controls[this.language] as FormControl;
    }

    get creditPointsArray(): FormArray {
        return this.formGroup.get('creditPoints') as FormArray;
    }

    get creditPoints(): FormGroup[] {
        const formArray = this.formGroup.get('creditPoints') as FormArray;
        return formArray.controls as FormGroup[];
    }

    parts: UxLink[] = [];

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
    @ViewChild('messageBoxFormError')
        messageBoxFormError: EuiMessageBoxComponent;

    newEntity: LearningAchievementSpecView = null;

    @Input() modalTitle: string;
    @Input() language: string;
    @Input() modalId = 'achievementModal';
    @Input() editAchievementOid?: number;
    @Input() isModal: boolean;
    @Input() modalData: any;
    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    editAchievement: LearningAchievementSpecView;
    defaultLanguage: string;
    languages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    achievementLanguage: CodeDTView[] = [];
    modeOfLearning: CodeDTView[] = [];
    thematicArea: CodeDTView[] = [];
    nqfLevel: CodeDTView = null;
    isAdditionalNoteValid: boolean;
    nqfLevelParent: CodeDTView = null;
    destroy$: Subject<boolean> = new Subject<boolean>();
    achievementBody: LearningAchievementSpecView;
    isLoading = true;
    selectedSubAchievements: PagedResourcesLearningAchievementSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    subAchievementOidList: number[] = [];
    selectedLearningOutcomes: PagedResourcesLearningOutcomeSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    selectedAwardingBodies: PagedResourcesOrganizationSpecLiteView;
    awardingBodyOidList: number[] = [];
    learningOutcomesOidList: number[] = [];
    influencedByOidList: number[] = [];
    selectedProvenBy: PagedResourcesAssessmentSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    provenByOidList: number[] = [];
    selectedInfluencedBy: PagedResourcesLearningActivitySpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    selectedEntitledTo: PagedResourcesEntitlementSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    entitledOidList: number[] = [];
    indexToNextTab: number;
    openEntityModal: {
        [key: string]: { modalId: string; isOpen: boolean; oid?: number };
    } = {};
    entityWillBeOpened: Entities | string;
    additionalNoteView: NoteDTView[];
    formGroup = new FormGroup(
        {
            label: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_LABELS), noSpaceValidator]),
            // Achievement
            title: new FormGroup({}),
            awardingBody: new FormControl(null, [requiredListValidator]),
            awardingDate: new FormControl(
                {
                    value: '',
                }
            ),
            description: new FormGroup({}),
            provenBy: new FormControl(null),
            entitledTo: new FormControl(null),
            // Specification
            specificationTitle: new FormGroup({}),
            learningOutcomeSummary: new FormGroup({}),
            volumeOfLearning: new FormControl(null, [
                Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
                Validators.pattern(Constants.INTEGER_REGULAR_EXPRESSION),
            ]),
            maxDuration: new FormControl(null, [
                Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
                Validators.pattern(Constants.INTEGER_REGULAR_EXPRESSION),
            ]),
            learningSetting: new FormControl(null),
            entryRequirements: new FormGroup({}),
            homePage: new FormControl('', [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT), Validators.pattern(Constants.URL_REGULAR_EXPRESSION)]),
            otherWebDocuments: new FormArray([]),
            dcType : new FormArray([]),
            targetGroup : new FormArray([]),
            educationLevel : new FormArray([]),
            educationSubject: new FormArray([]),
            educationalSystemNote : new FormGroup({}),
            creditPoints : new FormArray([
                new FormGroup({
                    framework : new FormControl({}),
                    point : new FormControl(null),
                })
            ]),
            // Qualification
            eqfLevel: new FormControl(null),
            partialQualification: new FormControl(null),
        }
    );
    isPrimaryLanguage = true;
    unsavedActivities: LearningActivitySpecLiteView[] = [];
    unsavedAchievements: LearningAchievementSpecLiteView[] = [];
    unsavedLearning: LearningOutcomeSpecLiteView[] = [];
    unsavedAwardingBody: OrganizationSpecLiteView[] = [];
    additionalNote: NoteDTView[];
    removedLanguage: string;
    addedLanguage: string;
    saveClicked: boolean;

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
        private readonly pageLoadingSpinnerService: PageLoadingSpinnerService
    ) {
        this.translateService.onLangChange.pipe(takeUntil(this.destroy$)).subscribe(() => {
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
                if (this.modalData?.achievementDetails) {
                    this.setDetailsData(this.modalData);
                } else {
                    this.setNewAchievementData();
                }
                return;
            }

            if (data.achievementDetails) {
                this.setDetailsData(data);
                return;
            } else {
                this.setNewAchievementData();
            }
        });

        this.isPrimaryLanguage = true;
        this.loadBreadcrumb();
        this.awardingDateValueChange();
        this.titleValueChangeAutocomplete();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSave(): void {
        this.formGroup.markAllAsTouched();
        this.isLoading = true;
        this.saveClicked = true;
        this.saveForm();
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
        this.specificationTitle.removeControl(language);
        this.learningOutcomeSummary.removeControl(language);
        this.entryRequirements.removeControl(language);
        this.isPrimaryLanguage = this.defaultLanguage === this.language;
    }

    titleValueChangeAutocomplete(): void {
        this.title.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
            if (this.specificationTitle.controls[this.language] && this.specificationTitle.controls[this.language].pristine) {
                this.specificationTitle.controls[this.language].setValue(value[this.language]);
            }
        });
    }

    awardingDateValueChange(): void {
        this.awardingDate.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
            if (this.awardingDate.value && !this.dateFormatService.validateDate(this.awardingDate.value)) {
                this.awardingDate.reset(null, {
                    emitEvent: false,
                    onlySelf: true,
                });
            }
        });
    }

    onSubAchievementSelectionChange(oids): void {
        this.subAchievementOidList = oids;
    }

    onLearningOutcomeSelectionChange(oids): void {
        this.learningOutcomesOidList = oids;
    }

    onAwardingBodySelectionChange(oids): void {
        this.awardingBodyOidList = oids;
        this.awardingBody.markAsTouched();
        this.awardingBody.patchValue(oids);
        if (oids.length === 0) {
            this.awardingDate.patchValue(null);
            Object.keys(this.educationalSystemNote.controls).forEach(control => {
                this.educationalSystemNote.controls[control].patchValue(null);
            });
        }
    }

    onProvenBySelectionChange(oids): void {
        this.provenByOidList = oids;
    }

    onInfluencedBySelectionChange(oids): void {
        this.influencedByOidList = oids;
    }

    onEntitledToSelectionChange(oids): void {
        this.entitledOidList = oids;
    }

    achievementLanguageSelectionChange(achievementLanguage: CodeDTView[]): void {
        this.achievementLanguage = achievementLanguage;
    }

    thematicAreaSelectionChange(thematicArea: CodeDTView[]): void {
        this.thematicArea = thematicArea;
    }

    modeOfLearningSelectionChange(modeOfLearning: CodeDTView[]): void {
        this.modeOfLearning = modeOfLearning;
    }

    nqfLevelParentSelectionChange(nqfLevelParent: CodeDTView): void {
        if (_get(this.nqfLevelParent, 'uri', null) !== _get(nqfLevelParent, 'uri', null)) {
            this.nqfLevelParent = nqfLevelParent;
            this.nqfLevel = null;
        }
    }

    nqfLevelSelectionChange(nqfLevel: CodeDTView): void {
        this.nqfLevel = nqfLevel;
    }

    newEntityClicked(value: Entities | string, event = undefined, isMultiSelect = false): void {
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

    closeForm() {
        this.credentialBuilderService.setOcbTabSelected(1);
        this.router.navigateByUrl('credential-builder');
    }

    closeNewEntityModal(closeInfo: { isEdit: boolean; oid?: number; displayName?: string }) {
        this.openEntityModal[this.entityWillBeOpened].isOpen = this.modalsService.closeModal();
        if (closeInfo.oid) {
            const item: any = {
                oid: closeInfo.oid,
                displayName: closeInfo.displayName,
                defaultLanguage: this.defaultLanguage,
            };
            switch (this.entityWillBeOpened) {
            case 'organization':
                this.selectedAwardingBodies = this.credentialBuilderService.fillMultipleInput(this.selectedAwardingBodies, this.awardingBodyOidList, item);
                break;
            case 'assessment':
                this.selectedProvenBy = this.credentialBuilderService.fillMultipleInput(this.selectedProvenBy, this.provenByOidList, item);
                break;
            case 'activity':
                this.selectedInfluencedBy = this.credentialBuilderService.fillMultipleInput(this.selectedInfluencedBy, this.influencedByOidList, item);
                break;
            case 'entitlement':
                this.selectedEntitledTo = this.credentialBuilderService.fillMultipleInput(this.selectedEntitledTo, this.entitledOidList, item);
                break;
            case 'achievement':
                this.selectedSubAchievements = this.credentialBuilderService.fillMultipleInput(
                    this.selectedSubAchievements,
                    this.subAchievementOidList,
                    item
                );
                break;
            case 'learningOutcome':
                this.selectedLearningOutcomes = this.credentialBuilderService.fillMultipleInput(
                    this.selectedLearningOutcomes,
                    this.learningOutcomesOidList,
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

    public isEducationalSystemNoteDisabled(): boolean {
        return this.formGroup ? this.awardingBody?.value?.length === 0 : true;
    }

    onFreeControlledListChange(codes: CodeDTView[], formArrayName: string) {
        const formArray = this.formGroup.get(formArrayName) as FormArray;
        formArray.clear();
        codes.forEach(code => formArray.push(new FormControl(code)));
    }

    onCreditSystemSelection(code: CodeDTView, formGroup: FormGroup) {
        formGroup.get('framework').patchValue(code);
        this.doCheckRequiredValidatorsForCreditPoint(formGroup);
    }

    onPointChange(event: any, formGroup: FormGroup) {
        const point = event.target.value;
        formGroup.get('point').patchValue(point ? point : null);
        this.doCheckRequiredValidatorsForCreditPoint(formGroup);
    }

    removeFromFormArray(formArray: FormArray, index: number) {
        formArray.removeAt(index);
    }

    addCreditPointsFormGroup(formArray: FormArray) {
        formArray.push(new FormGroup({
            framework : new FormControl({}),
            point : new FormControl(null)
        }));
    }

    setEditAchievementData(data) {
        this.modalTitle = this.translateService.instant('credential-builder.achievements-tab.editAchievement');
        this.editAchievement = data;
        this.editAchievementOid = data.oid;
        this.languages = this.editAchievement.additionalInfo.languages;
        this.language = this.editAchievement.defaultLanguage;
        this.defaultLanguage = this.language;
        this.selectedLanguages = this.multilingualService.setUsedLanguages(this.editAchievement.additionalInfo.languages, this.defaultLanguage);
        if (this.editAchievement?.specifiedBy?.supplementaryDocument && this.editAchievement?.specifiedBy?.supplementaryDocument.length > 0) {
            this.credentialBuilderService.extractWebDocuments(
                _get(this.editAchievement, 'specifiedBy.supplementaryDocument', []),
                this.otherWebDocuments
            );
        } else {
            this.credentialBuilderService.addOtherDocumentRow(this.otherWebDocuments);
        }
        this.setForm();
        this.markSpecTitleAsDirty();
    }
    setNewAchievementData() {
        this.modalTitle = this.translateService.instant('credential-builder.achievements-tab.createAchievement');
        this.language = this.language || this.translateService.currentLang;
        this.credentialBuilderService.addOtherDocumentRow(this.otherWebDocuments);
        this.defaultLanguage = this.language;
        this.selectedLanguages.push({
            code: this.language,
            label: this.language,
        });
        this.addNewLanguageControl(this.language);
        this.isLoading = false;
    }

    setDetailsData(data) {
        this.setEditAchievementData(data.achievementDetails);

        if (data?.achievementSubAchievements) {
            this.selectedSubAchievements = data?.achievementSubAchievements;
            const oids = data?.achievementSubAchievements?.content?.map(item => item.oid);
            this.subAchievementOidList = oids;
        }

        if (data?.achievementLearningOutcome) {
            this.selectedLearningOutcomes = data?.achievementLearningOutcome;
            const oids = data?.achievementLearningOutcome?.content?.map(item => item.oid);
            this.learningOutcomesOidList = oids;
        }

        if (data?.achievementAwardingBodies) {
            this.selectedAwardingBodies = data?.achievementAwardingBodies;
            const awardingBodyIds = data?.achievementAwardingBodies?.content?.map(item => item.oid);
            this.awardingBody.patchValue(awardingBodyIds);
            this.awardingBodyOidList = awardingBodyIds;
        }

        if (data?.achievementEntitledTo && data?.achievementEntitledTo?.content?.length > 0) {
            this.selectedEntitledTo = data?.achievementEntitledTo;
            const oids = data?.achievementEntitledTo?.content?.map(item => item.oid);
            this.entitledOidList = oids;
            this.entitledTo.patchValue(oids);
        }

        if (data?.achievementInfluencedBy && data?.achievementInfluencedBy?.content?.length > 0) {
            this.selectedInfluencedBy = data?.achievementInfluencedBy;
            const oids = data?.achievementInfluencedBy?.content?.map(item => item.oid);
            this.influencedByOidList = oids;
        }

        if (data?.achievementProvenBy && data?.achievementProvenBy?.content?.length > 0) {
            this.selectedProvenBy = data?.achievementProvenBy;
            const oids = data?.achievementProvenBy?.content?.map(item => item.oid);
            this.provenByOidList = oids;
        }
    }

    private doCheckRequiredValidatorsForCreditPoint(formGroup: FormGroup) {
        const point = formGroup.get('point').value;
        const framework = formGroup.get('framework').value;
        if (point || framework) {
            formGroup.get('framework').addValidators([Validators.required]);
            formGroup.get('point').addValidators([Validators.required]);
        } else {
            formGroup.get('framework').removeValidators([Validators.required]);
            formGroup.get('point').removeValidators([Validators.required]);
        }
        formGroup.get('framework').updateValueAndValidity();
        formGroup.get('point').updateValueAndValidity();

    }

    private saveForm(): void {
        if (!this.formGroup.valid || this.nqfLevelInvalid() || !this.isAdditionalNoteValid) {
            this.isLoading = false;
            this.messageBoxFormError.openMessageBox();
        } else {
            this.setAchievementBody();
            if (this.editAchievementOid) {
                this.updateAchievement();
            } else {
                this.createAchievement();
            }
        }
    }

    private gotoEntity(oid: number = null) {
        this.openEntityModal[this.entityWillBeOpened] = this.modalsService.openModal(this.modalTitle, oid);
        this.setAchievementBody();
    }

    private addNewLanguageControl(language: string): void {
        this.addTitleControls(language);
        this.addEducationSystemNoteControls(language);
        this.addDescriptionControls(language);
        this.addLearningOutcomeSummaryControls(language);
        this.addEntryRequirementsControls(language);
        this.addSpecificationTitleControls(language);
    }

    private addTitleControls(language: string, value: string = null): void {
        this.title.addControl(language, new FormControl(value, [Validators.maxLength(Constants.MAX_LENGTH_LONG), Validators.required, noSpaceValidator]));
    }

    private addDescriptionControls(language: string, value: string = null): void {
        this.description.addControl(language, new FormControl(value, [Validators.maxLength(Constants.MAX_LENGTH_LONG), noSpaceValidator]));
    }

    private addEntryRequirementsControls(language: string, value: string = null): void {
        this.entryRequirements.addControl(language, new FormControl(value, [Validators.maxLength(Constants.MAX_LENGTH_LONG), noSpaceValidator]));
    }

    private addSpecificationTitleControls(language: string, value: string = null): void {
        this.specificationTitle.addControl(
            language,
            new FormControl(value, [Validators.maxLength(Constants.MAX_LENGTH_LONG), Validators.required, noSpaceValidator])
        );
    }

    private addEducationSystemNoteControls(language: string, value: string = null): void {
        this.educationalSystemNote.addControl(
            language,
            new FormControl(value, [Validators.maxLength(Constants.MAX_LENGTH_LONG), noSpaceValidator])
        );
    }

    private addLearningOutcomeSummaryControls(language: string, value: string = null): void {
        this.learningOutcomeSummary.addControl(language, new FormControl(value, [Validators.maxLength(Constants.MAX_LENGTH_LONG), noSpaceValidator]));
    }

    private updateAchievement(): void {
        this.achievementBody.oid = this.editAchievement.oid;
        this.isLoading = true;
        this.api
            .updateLearningAchievement(this.achievementBody, this.translateService.currentLang)
            .pipe(take(1))
            .subscribe({
                next: (achievement: ResourceLearningAchievementSpecView) => {
                    this.newEntity = achievement;
                    this.handleSaveNavigation();
                    this.showNotification();
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

    private setSubAchievements(): SubresourcesOids {
        let subAchievements: SubresourcesOids = null;
        if (this.subAchievementOidList.length > 0) {
            subAchievements = {
                oid: this.subAchievementOidList,
            };
        }
        return subAchievements;
    }

    private setAwardingBody(): SubresourcesOids {
        let awardingBodies: SubresourcesOids = null;
        if (this.awardingBodyOidList.length > 0) {
            awardingBodies = {
                oid: this.awardingBodyOidList,
            };
        }
        return awardingBodies;
    }

    private setLearningOutcomes(): SubresourcesOids {
        let learningOutcomes: SubresourcesOids = null;
        if (this.learningOutcomesOidList.length > 0) {
            learningOutcomes = {
                oid: this.learningOutcomesOidList,
            };
        }
        return learningOutcomes;
    }

    private setProvenBy(): SubresourcesOids {
        let relProvenBy: SubresourcesOids = null;
        if (this.provenByOidList.length > 0) {
            relProvenBy = {
                oid: this.provenByOidList,
            };
        }
        return relProvenBy;
    }

    private setInfluencedBy(): SubresourcesOids {
        let relInfluencedBy: SubresourcesOids = null;
        if (this.influencedByOidList) {
            relInfluencedBy = {
                oid: this.influencedByOidList,
            };
        }
        return relInfluencedBy;
    }

    private setEntitledTo(): SubresourcesOids {
        let relEntitledTo: SubresourcesOids = null;
        if (this.entitledOidList.length > 0) {
            relEntitledTo = {
                oid: this.entitledOidList,
            };
        }
        return relEntitledTo;
    }

    /* private setAwardingBody(): SubresourcesOids {
          let relAwardingBody: SubresourcesOids = null;
          if (this.awardingBody.value) {
              relAwardingBody = {
                  oid: [
                      Array.isArray(this.awardingBody.value)
                          ? this.awardingBody.value[0]
                          : this.awardingBody.value,
                  ],
              };
          }
          return relAwardingBody;
      } */

    private createAchievement(): void {
        this.isLoading = true;
        this.api
            .createLearningAchievement(this.achievementBody, this.translateService.currentLang)
            .pipe(take(1))
            .subscribe({
                next: (achievement: ResourceLearningAchievementSpecView) => {
                    this.newEntity = achievement;
                    this.handleSaveNavigation();
                    this.showNotification();
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
            this.credentialBuilderService.setOcbTabSelected(1);
            this.router.navigateByUrl('credential-builder');
        }
    }

    private setAchievementBody(): void {
        this.achievementBody = {
            label: this.label.value,
            defaultLanguage: this.defaultLanguage,
            title: this.credentialBuilderService.getDTView(this.title),
            description: this.credentialBuilderService.getDTView(this.description),
            awardedBy: this.getAwardedBy(),
            specifiedBy: this.getSpecification(),
            relAwardingBody: this.setAwardingBody(),
            relProvenBy: this.setProvenBy(),
            relInfluencedBy: this.setInfluencedBy(),
            relEntitlesTo: this.setEntitledTo(),
            relSubAchievements: this.setSubAchievements(),
            relLearningOutcomes: this.setLearningOutcomes(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(this.selectedLanguages),
            },
        };
    }

    private getAwardedBy(): AwardingProcessDCView {
        const awardingProcess: AwardingProcessDCView = {
            awardingDate: this.dateFormatService.dateToStringDateTime(this.awardingDate.value),
            educationalSystemNote : {
                targetName :  this.credentialBuilderService.getDTView(this.educationalSystemNote)
            }
        };

        if (awardingProcess?.educationalSystemNote?.targetName === null) {
            awardingProcess.educationalSystemNote = null;
        }

        return awardingProcess;
    }

    private getSpecification(): LearningSpecificationDCView {
        const specifiedBy: LearningSpecificationDCView = {
            title: this.credentialBuilderService.getDTView(this.specificationTitle),
            learningOutcomeSummary: this.credentialBuilderService.getDTView(this.learningOutcomeSummary),
            maximumDuration: this.maxDuration.value,
            volumeOfLearning: this.volumeOfLearning.value,
            homePage: this.credentialBuilderService.getHomePage(this.homePage.value),
            supplementaryDocument: this.credentialBuilderService.getOtherDocument(this.otherWebDocuments, this.defaultLanguage),
            thematicArea: this.thematicArea,
            mode: this.modeOfLearning,
            learningSetting: this.learningSetting.value,
            eqfLevel: this.eqfLevel.value,
            creditPoints: this.getFilteredCreditPonits(),
            // Robert says: 'null better than []'
            nqfLevel: this.nqfLevel ? [this.nqfLevel] : null,
            nqfLevelParent: this.nqfLevelParent,
            language: this.achievementLanguage,
            dcType : this.dcTypes.getRawValue() as CodeDTView[],
            targetGroup : this.targetGroup.getRawValue() as CodeDTView[],
            educationLevel : this.educationLevel.getRawValue() as CodeDTView[],
            educationSubject : this.educationSubject.getRawValue() as CodeDTView[],
            additionalNote: this.additionalNote,
            entryRequirement: this.credentialBuilderService.getDTView(this.entryRequirements),
            partialQualification: this.partialQualification.value,
        };
        return specifiedBy;
    }

    private getFilteredCreditPonits(): CreditPointView[] {
        return this.creditPointsArray.getRawValue().filter(creditPoint =>
            (Object.values(creditPoint.framework).some(x => x !== null && x !== '') &&
            (creditPoint.point !== null && creditPoint.point !== '')
            ));
    }
    private setForm(): void {
        this.additionalNoteView = _get(this.editAchievement, 'specifiedBy.additionalNote', []);
        this.achievementLanguage = _get(this.editAchievement, 'specifiedBy.language', null);
        this.modeOfLearning = _get(this.editAchievement, 'specifiedBy.mode', null);
        this.nqfLevelParent = _get(this.editAchievement, 'specifiedBy.nqfLevelParent', null);
        this.nqfLevel = _get(this.editAchievement, 'specifiedBy.nqfLevel[0]', null);
        this.thematicArea = _get(this.editAchievement, 'specifiedBy.thematicArea', null);
        _get(this.editAchievement, 'specifiedBy.dcType', []).forEach(dcType => {
            this.dcTypes.push(new FormControl(dcType));
        });
        _get(this.editAchievement, 'specifiedBy.targetGroup', []).forEach(targetGroup => {
            this.targetGroup.push(new FormControl(targetGroup));
        });
        _get(this.editAchievement, 'specifiedBy.educationLevel', []).forEach(educationLevel => {
            this.educationLevel.push(new FormControl(educationLevel));
        });
        _get(this.editAchievement, 'specifiedBy.educationSubject', []).forEach(educationSubject => {
            this.educationSubject.push(new FormControl(educationSubject));
        });
        _get(this.editAchievement, 'specifiedBy.creditPoints', []).forEach((creditPoint, index) => {
            if (index === 0) {
                this.creditPointsArray.clear();
            }
            this.creditPointsArray.push(new FormGroup({
                framework : new FormControl(creditPoint.framework),
                point : new FormControl(creditPoint.point)
            }));
        });
        this.addControlsFromView();
        this.formGroup.patchValue({
            label: this.editAchievement.label,
            awardingBody: this.setAwardingBody(),
            awardingDate: this.setAwardingDate(),
            volumeOfLearning: _get(this.editAchievement, 'specifiedBy.volumeOfLearning', null),
            maxDuration: _get(this.editAchievement, 'specifiedBy.maximumDuration', null),
            learningSetting: _get(this.editAchievement, 'specifiedBy.learningSetting', null),
            homePage: _get(this.editAchievement, 'specifiedBy.homePage[0].contentUrl', null),
            eqfLevel: _get(this.editAchievement, 'specifiedBy.eqfLevel', null)
        });
        this.setPartialQualification();
        this.isLoading = false;
    }

    private addControlsFromView(): void {
        this.languages.forEach((language: string) => {
            this.addTitleControls(language, this.multilingualService.getContentFromLanguage(language, this.editAchievement.title.contents));
            this.addDescriptionControls(
                language,
                this.multilingualService.getContentFromLanguage(language, _get(this.editAchievement, 'description.contents', []))
            );
            this.addEducationSystemNoteControls(
                language,
                this.multilingualService.getContentFromLanguage(language, _get(this.editAchievement, 'awardedBy.educationalSystemNote.targetName.contents', []))
            );
            this.addSpecificationTitleControls(
                language,
                this.multilingualService.getContentFromLanguage(language, _get(this.editAchievement, 'specifiedBy.title.contents', []))
            );
            this.addLearningOutcomeSummaryControls(
                language,
                this.multilingualService.getContentFromLanguage(language, _get(this.editAchievement, 'specifiedBy.learningOutcomeSummary.contents', []))
            );
            this.addEntryRequirementsControls(
                language,
                this.multilingualService.getContentFromLanguage(language, _get(this.editAchievement, 'specifiedBy.entryRequirement.contents', []))
            );
        });
    }

    private setAwardingDate(): Date | null {
        const date = _get(this.editAchievement, 'awardedBy.awardingDate', null);
        const result = date ? new Date(date) : null;
        return result;
    }

    private showNotification() {
        if (this.editAchievement) {
            this.notificationService.showNotification({
                severity: 'success',
                summary: this.translateService.instant('common.edit'),
                detail: this.translateService.instant('credential-builder.operationSuccessful'),
            });
        } else {
            this.notificationService.showNotification({
                severity: 'success',
                summary: this.translateService.instant('common.create'),
                detail: this.translateService.instant('credential-builder.operationSuccessful'),
            });
        }
    }

    private setPartialQualification() {
        const partialQualification = _get(this.editAchievement, 'specifiedBy.partialQualification', 'noContent');
        this.partialQualification.patchValue(partialQualification);
    }

    private nqfLevelInvalid(): boolean {
        return !!this.nqfLevelParent && !this.nqfLevel;
    }

    private markSpecTitleAsDirty(): void {
        this.editAchievement.additionalInfo.languages.forEach((language: string) => {
            this.specificationTitle.controls[language].markAsDirty();
        });
    }

    private loadBreadcrumb() {
        this.parts = [
            new UxLink({
                label: this.translateService.instant('breadcrumb.digitallySealedCredentials'),
                url: '/home',
            }),
            new UxLink({
                label: this.translateService.instant('breadcrumb.credentialBuilder'),
                url: '/credential-builder',
            }),
        ];
    }
}
