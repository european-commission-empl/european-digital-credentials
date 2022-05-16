import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation,
} from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage, UxLink, UxService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { DateFormatService } from '@services/date-format.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { Constants, Entities } from '@shared/constants';
import { SelectedTagItemList } from '@shared/models/selected-tag-item-list.model';
import {
    AwardingProcessDCView,
    CodeDTView,
    EntitlementSpecLiteView,
    LearningAchievementSpecLiteView,
    LearningAchievementSpecView,
    LearningActivitySpecLiteView,
    LearningOutcomeSpecLiteView,
    LearningSpecificationDCView,
    NoteDTView,
    OrganizationSpecLiteView,
    PagedResourcesAssessmentSpecLiteView,
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesLearningAchievementSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    PagedResourcesLearningOutcomeSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ResourceAssessmentSpecView,
    ResourceLearningAchievementSpecView,
    ScoreDTView,
    SubresourcesOids,
    V1Service,
} from '@shared/swagger';
import { dateValidator } from '@shared/validators/date-validator';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-achievements-modal',
    templateUrl: './achievements-modal.component.html',
    styleUrls: ['./achievements-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AchievementsModalComponent implements OnInit, OnDestroy {
    get defaultTitle() {
        return this.formGroup.get('defaultTitle') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get awardingBody() {
        return this.formGroup.get('awardingBody');
    }

    get awardingDate() {
        return this.formGroup.get('awardingDate');
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
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

    get specificationDescription() {
        return this.formGroup.get('specificationDescription') as FormGroup;
    }

    get workload() {
        return this.formGroup.get('workload');
    }

    get maxDuration() {
        return this.formGroup.get('maxDuration');
    }

    get ectsCredits() {
        return this.formGroup.get('ectsCredits');
    }

    get learningSetting() {
        return this.formGroup.get('learningSetting');
    }

    get modeOfLearning() {
        return this.formGroup.get('modeOfLearning');
    }

    get learningOpportunityType() {
        return this.formGroup.get('learningOpportunityType');
    }

    get entryRequirements() {
        return this.formGroup.get('entryRequirements') as FormGroup;
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

    get accreditation() {
        return this.formGroup.get('accreditation') as FormControl;
    }

    get identifier() {
        return this.formGroup.get('identifier') as FormArray;
    }

    @Input() modalTitle: string;
    @Input() language: string;
    @Input() modalId: string = 'achievementModal';
    @Input() editAchievementOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        title: string;
    }> = new EventEmitter();

    shownLearningOutcome: string[] = [];
    shownSubAchievements: string[] = [];
    shownInfluencedBy: string[] = [];
    shownProvenBy: {
        oid: number,
        title: string
    } = {
        oid: -1,
        title: ''
    };
    shownEntitled: {
        oid: number,
        title: string
    } = {
        oid: -1,
        title: ''
    };
    shownOrganization: {
        oid: number,
        title: string
    } = {
        oid: -1,
        title: ''
    };

    assessmentListContent: [];
    entitlementsListContent: [];
    organizationsListContent: [];

    editAchievement: LearningAchievementSpecView;
    isAwardingBodyRequired: boolean = false;
    defaultLanguage: string;
    availableLanguages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    achievementLanguage: CodeDTView[] = [];
    thematicArea: CodeDTView[] = [];
    nqfLevel: CodeDTView[] = null;
    nqfLevelParent: CodeDTView = null;
    destroy$: Subject<boolean> = new Subject<boolean>();
    achievementBody: LearningAchievementSpecView;
    isSaveDisabled: boolean = false;
    isLoading: boolean = true;
    loadingModal: boolean = false;
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
    learningOutcomesOidList: number[] = [];
    influencedByOidList: number[] = [];
    selectedAwardingBody: OrganizationSpecLiteView;
    selectedProvenBy: ResourceAssessmentSpecView;
    selectedInfluencedBy: PagedResourcesLearningActivitySpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    selectedEntitledTo: EntitlementSpecLiteView;
    indexToNextTab: number;
    openEntityModal: {
        [key: string]: { modalId: string; isOpen: boolean; oid?: number };
    } = {};
    entityWillBeOpened: Entities;

    isPartialQualificationDropDown: UxLink[] = [
        new UxLink({
            id: 0,
            label: this.translateService.instant('common.yes'),
        }),
        new UxLink({
            id: 1,
            label: this.translateService.instant('common.no'),
        }),
    ];
    additionalNoteView: NoteDTView[];
    additionalNoteSpecView: NoteDTView[];
    formGroup = new FormGroup({
        defaultTitle: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.required,
            noSpaceValidator,
        ]),
        // Achievement
        title: new FormGroup({}),
        awardingDate: new FormControl('', [dateValidator]),
        awardingBody: new FormControl(null),
        description: new FormGroup({}),
        provenBy: new FormControl(null),
        entitledTo: new FormControl(null),
        // Specification
        specificationTitle: new FormGroup({}),
        identifier: new FormArray([]),
        specificationDescription: new FormGroup({}),
        workload: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
            Validators.pattern(Constants.INTEGER_REGULAR_EXPRESSION),
        ]),
        maxDuration: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
            Validators.pattern(Constants.INTEGER_REGULAR_EXPRESSION),
        ]),
        ectsCredits: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
            Validators.pattern(Constants.INTEGER_REGULAR_EXPRESSION),
        ]),
        modeOfLearning: new FormControl(null),
        learningSetting: new FormControl(null),
        learningOpportunityType: new FormControl(null),
        entryRequirements: new FormGroup({}),
        homePage: new FormControl('', [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
        ]),
        otherWebDocuments: new FormArray([]),
        // Qualification
        eqfLevel: new FormControl(null),
        accreditation: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
        ]),
        partialQualification: new FormControl(null),
    });
    isPrimaryLanguage: boolean = true;
    modalTitleBreadcrumb: string[];
    awardingDateValueInvalid: boolean;
    unsavedActivities: LearningActivitySpecLiteView[] = [];
    unsavedAchivements: LearningAchievementSpecLiteView[] = [];
    unsavedLearning: LearningOutcomeSpecLiteView[] = [];
    additionalNoteSpecification: NoteDTView[];
    additionalNote: NoteDTView[];
    removedLanguage: string;
    addedLanguage: string;
    isAdditionalNoteValid: boolean;
    isAdditionalNoteSpecificationValid: boolean;
    isValidAccreditation: boolean;
    isCheckingAccreditationValidity: boolean;
    isAccreditationValidationFinished$: Subject<boolean> =
        new Subject<boolean>();
    saveClicked: boolean;
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
        this.isPrimaryLanguage = true;
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        this.awardingDateValueChange();
        this.titleValueChangeAutocomplete();
        if (this.editAchievementOid) {
            this.isLoading = true;
            this.loadingModal = true;
            this.modalTitle = this.translateService.instant(
                'credential-builder.achievements-tab.editAchievement'
            );
            this.getAchievementDetails();
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.achievements-tab.createAchievement'
            );
            this.language = this.language || this.translateService.currentLang;
            this.credentialBuilderService.addOtherDocumentRow(
                this.otherWebDocuments
            );
            this.defaultLanguage = this.language;
            this.selectedLanguages.push({
                code: this.language,
                label: this.language,
            });
            this.addNewLanguageControl(this.language);
            this.isLoading = false;
            this.credentialBuilderService.addIdentifierRow(this.identifier);
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.isAccreditationValidationFinished$.unsubscribe();
        this.destroy$.unsubscribe();
    }

    checkValidDate() {
        this.validateFormDatesValues();
    }

    onSave(): void {
        this.isLoading = true;
        this.saveClicked = true;
        if (!this.isCheckingAccreditationValidity) {
            this.saveForm();
        } else {
            this.isAccreditationValidationFinished$.subscribe(() => {
                if (this.saveClicked) {
                    this.saveClicked = false;
                    this.saveForm();
                }
            });
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
        this.specificationDescription.removeControl(language);
        this.entryRequirements.removeControl(language);
        this.isPrimaryLanguage = this.defaultLanguage === this.language;
    }

    titleValueChangeAutocomplete(): void {
        this.title.valueChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe((value) => {
                if (
                    this.specificationTitle.controls[this.language] &&
                    this.specificationTitle.controls[this.language].pristine
                ) {
                    this.specificationTitle.controls[this.language].setValue(
                        value[this.language]
                    );
                }
            });
    }

    awardingDateValueChange(): void {
        this.awardingDate.valueChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe((dateValue) => {
                const validator = dateValue ? Validators.required : null;
                this.isAwardingBodyRequired = !!dateValue;
                this.awardingBody.setValidators(validator);
                this.awardingBody.updateValueAndValidity();
            });
    }

    onAssessmentCharge($event) {
        this.assessmentListContent = $event.content;
    }

    onEntitlementCharge($event) {
        this.entitlementsListContent = $event.content;
    }

    onOrganizationCharge($event) {
        this.organizationsListContent = $event.content;
    }

    deleteSelectionFromList(
        list: LearningAchievementSpecLiteView[] |
        LearningActivitySpecLiteView[] |
        EntitlementSpecLiteView[],
        items: number[]
    ) {
        let returnList: string[] = [];
        list.forEach(element => {
            if (items.includes(element.oid)) {
                returnList.push(element.defaultTitle);
            }
        });
        return returnList;
    }

    onSubAchievementSelectionChange(oids: number[]): void {
        this.subAchievementOidList = oids;
    }

    onSelectedAchivementChange(selectedList: SelectedTagItemList[]): void {
        this.shownSubAchievements = [];
        this.selectedSubAchievements.content.splice(0, this.selectedSubAchievements.content.length);
        selectedList.forEach((item) => {
            this.selectedSubAchievements.content.push(item.achievement);
            this.shownSubAchievements.push(item.label);
            this.unsavedAchivements.push(item.achievement);
        });
    }

    onLearningOutcomeSelectionChange(oids: number[]): void {
        if (oids.length < this.shownLearningOutcome.length) {
            this.shownLearningOutcome = this.deleteSelectionFromList(this.unsavedLearning, oids);
        }
        this.learningOutcomesOidList = oids;
    }

    onSelectedLearningChange(selectedList: SelectedTagItemList[]): void {
        this.shownLearningOutcome = [];
        this.selectedLearningOutcomes.content.splice(0, this.selectedLearningOutcomes.content.length);
        selectedList.forEach((item) => {
            this.selectedLearningOutcomes.content.push(item.learningOutcome);
            this.unsavedLearning.push(item.learningOutcome);
            this.shownLearningOutcome.push(item.label);
        });
    }

    onEntityRemoved($event) {
        this.loadingModal = true;
        switch ($event.type) {
        case 'organization':
            this.selectedAwardingBody = null;
            this.shownOrganization.oid = -1;
            this.shownOrganization.title = '';
            this.loadingModal = false;
            break;
        case 'assessment':
            this.selectedProvenBy = null;
            this.shownProvenBy.oid = -1;
            this.shownProvenBy.title = '';
            this.loadingModal = false;
            break;
        case 'entitlement':
            this.selectedEntitledTo = null;
            this.shownEntitled.oid = -1;
            this.shownEntitled.title = '';
            this.loadingModal = false;
            break;
        case 'achievement':
            this.shownSubAchievements = [];
            this.deleteEntityFromList(this.selectedSubAchievements, $event.oid);
            this.selectedSubAchievements.content.forEach(entity => {
                this.shownSubAchievements.push(entity.defaultTitle);
            });
            this.loadingModal = false;
            break;
        case 'activity':
            this.shownInfluencedBy = [];
            this.deleteEntityFromList(this.selectedInfluencedBy, $event.oid);
            this.selectedInfluencedBy.content.forEach(entity => {
                this.shownInfluencedBy.push(entity.defaultTitle);
            });
            this.loadingModal = false;
            break;
        case 'learningOutcome':
            this.shownLearningOutcome = [];
            this.deleteEntityFromList(this.selectedLearningOutcomes, $event.oid);
            this.selectedLearningOutcomes.content.forEach(entity => {
                this.shownLearningOutcome.push(entity.defaultTitle);
            });
            this.loadingModal = false;
            break;

        default:
            break;
        }
    }

    deleteEntityFromList(list: any, oid: number) {
        if (list !== undefined) {
            let i = 0;
            let index = -1;
            list.content.forEach(element => {
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

    onAwardingBodySelectionChange(oid: number): void {
        this.loadingModal = true;
        this.formGroup.patchValue({
            awardingBody: oid,
        });
        if (this.organizationsListContent !== undefined) {
            if (oid !== null) {
                this.organizationsListContent.forEach(organization => {
                    if (organization['oid'] === oid[0]) {
                        this.shownOrganization.title = organization['defaultTitle'];
                        this.shownOrganization.oid = organization['oid'];
                        this.selectedAwardingBody = organization;
                    }
                    this.loadingModal = false;
                });
            } else {
                this.shownOrganization.title = '';
                this.shownOrganization.oid = -1;
                this.loadingModal = false;
            }
        } else {
            this.loadingModal = false;
        }
    }

    removeProvenSelection() {
        this.shownProvenBy.title = '';
        this.shownProvenBy.oid = -1;
        this.provenBy.reset();
    }

    removeEntitledSelection() {
        this.shownEntitled.title = '';
        this.shownEntitled.oid = -1;
        this.entitledTo.reset();
    }

    removeOrganizationSelection() {
        this.shownOrganization.title = '';
        this.shownOrganization.oid = -1;
        this.awardingBody.reset();
    }

    onProvenBySelectionChange(oid: number): void {
        this.loadingModal = true;
        this.formGroup.patchValue({
            provenBy: oid,
        });
        if (this.assessmentListContent !== undefined) {
            if (oid !== null) {
                this.assessmentListContent.forEach(assessment => {
                    if (assessment['oid'] === oid[0]) {
                        this.shownProvenBy.title = assessment['defaultTitle'];
                        this.shownProvenBy.oid = assessment['oid'];
                        this.selectedProvenBy = assessment;
                    }
                    this.loadingModal = false;
                });
            } else {
                this.shownProvenBy.title = '';
                this.shownProvenBy.oid = -1;
                this.loadingModal = false;
            }
        } else {
            this.loadingModal = false;
        }
    }

    onInfluencedBySelectionChange(oids: number[]): void {
        this.influencedByOidList = oids;
    }

    onSelectedActivitiesChange(selectedList: SelectedTagItemList[]): void {
        this.shownInfluencedBy = [];
        this.selectedInfluencedBy.content.splice(0, this.selectedInfluencedBy.content.length);
        selectedList.forEach((item) => {
            this.selectedInfluencedBy.content.push(item.activity);
            this.shownInfluencedBy.push(item.label);
            this.unsavedActivities.push(item.activity);
        });
    }

    onEntitledToSelectionChange(oid: number): void {
        this.loadingModal = true;
        this.formGroup.patchValue({
            entitledTo: oid,
        });
        if (this.entitlementsListContent !== undefined) {
            if (oid !== null) {
                this.entitlementsListContent.forEach(entitlement => {
                    if (entitlement['oid'] === oid[0]) {
                        this.shownEntitled.title = entitlement['defaultTitle'];
                        this.shownEntitled.oid = entitlement['oid'];
                        this.selectedEntitledTo = entitlement;
                    }
                    this.loadingModal = false;
                });
            } else {
                this.shownEntitled.title = '';
                this.shownEntitled.oid = -1;
                this.loadingModal = false;
            }
        } else {
            this.loadingModal = false;
        }
    }

    achievementLanguageSelectionChange(
        achievementLanguage: CodeDTView[]
    ): void {
        this.achievementLanguage = achievementLanguage;
    }

    thematicAreaSelectionChange(thematicArea: CodeDTView[]): void {
        this.thematicArea = thematicArea;
    }

    nqfLevelSelectionChange(nqfLevel: CodeDTView[]): void {
        this.nqfLevel = nqfLevel;
    }

    nqfLevelParentSelectionChange(nqfLevelParent: CodeDTView): void {
        if (_get(this.nqfLevelParent, 'uri') !== _get(nqfLevelParent, 'uri')) {
            this.nqfLevelParent = nqfLevelParent;
            this.nqfLevel = null;
        }
    }

    newEntityClicked(
        value: Entities,
        event = undefined,
        isMultiSelect: boolean = false
    ): void {
        if (event === undefined && !isMultiSelect) {
            this.entityWillBeOpened = value;
            this.uxService.openMessageBox('messageBoxNewEntityWarning');
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
        title?: string;
    }) {
        this.openEntityModal[this.entityWillBeOpened].isOpen = false;
        this.uxService.closeModal(
            this.credentialBuilderService.getIdFromLastModalAndRemove()
        );
        this.uxService.openModal(this.modalId);
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (closeInfo.oid) {
            let item: any = {
                oid: closeInfo.oid,
                defaultTitle: closeInfo.title,
                defaultLanguage: this.defaultLanguage,
            };
            switch (this.entityWillBeOpened) {
            case 'organization':
                this.selectedAwardingBody = item;
                break;
            case 'assessment':
                this.selectedProvenBy = item;
                break;
            case 'activity':
                this.selectedInfluencedBy =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedInfluencedBy,
                            this.influencedByOidList,
                            item,
                            this.unsavedActivities
                        );
                break;
            case 'entitlement':
                this.selectedEntitledTo = item;
                break;
            case 'achievement':
                this.selectedSubAchievements =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedSubAchievements,
                            this.subAchievementOidList,
                            item,
                            this.unsavedAchivements
                        );
                break;
            case 'learningOutcome':
                this.selectedLearningOutcomes =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedLearningOutcomes,
                            this.learningOutcomesOidList,
                            item,
                            this.unsavedLearning
                        );
                break;
            }
        }
    }

    editEntityClicked(event: { oid: number; type: Entities }) {
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

    checkAccreditationURI(): void {
        this.isValidAccreditation = null;
        if (this.accreditation.value) {
            this.isCheckingAccreditationValidity = true;
            this.api
                .getAccreditation(
                    { id: this.accreditation.value },
                    this.language,
                    'response'
                )
                .pipe(takeUntil(this.destroy$))
                .subscribe(
                    () => {
                        this.accreditation.setErrors(null);
                        this.isValidAccreditation = true;
                        this.isAccreditationValidationFinished$.next(true);
                        this.isCheckingAccreditationValidity = false;
                    },
                    (error) => {
                        if (error.status === 503) {
                            this.accreditation.setErrors({
                                serviceUnavailableError: true,
                            });
                        } else if (error.status === 404) {
                            this.accreditation.setErrors({
                                uriFormatError: true,
                            });
                        }
                        this.isAccreditationValidationFinished$.next(true);
                        this.isCheckingAccreditationValidity = false;
                    }
                );
        }
    }

    private saveForm(): void {
        this.validateFormDatesValues();
        if (
            this.isFormInvalid() ||
            this.nqfLevelInvalid() ||
            this.awardingDateValueInvalid
        ) {
            this.uxService.markControlsTouched(this.formGroup);
            this.isLoading = false;
            this.uxService.openMessageBox('messageBoxFormError');
        } else {
            this.isSaveDisabled = true;
            this.setAchievementBody();
            if (this.editAchievementOid) {
                this.updateAchievement();
            } else {
                this.createAchievement();
            }
        }
    }

    private gotoEntity(oid: number = null) {
        this.uxService.closeMessageBox('messageBoxNewEntityWarning');
        const newEntityModalId =
            this.credentialBuilderService.generateNewIdModal(this.modalTitle);
        this.openEntityModal[this.entityWillBeOpened] = {
            isOpen: true,
            modalId: newEntityModalId,
            oid,
        };
        this.uxService.closeModal(this.modalId);
        this.uxService.openModal(newEntityModalId);
        this.setAchievementBody();
    }

    private isFormInvalid(): boolean {
        return (
            this.formGroup.invalid ||
            !this.isAdditionalNoteValid ||
            !this.isAdditionalNoteSpecificationValid
        );
    }

    private getSubAchievements(): void {
        this.api
            .listSubAchievements(
                this.editAchievement.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((achievements: PagedResourcesLearningAchievementSpecLiteView) => {
                this.selectedSubAchievements = achievements;
                this.shownSubAchievements = [];
                if (achievements.content.length <= 0) {
                    achievements.content.forEach(item => {
                        this.shownSubAchievements.push(item.defaultTitle);
                    });
                }
            }, (err) => {});
    }

    private getLearningOutcomeRelations(): void {
        this.api
            .listLearningOutcomes(
                this.editAchievement.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (learningOutcome: PagedResourcesLearningOutcomeSpecLiteView) => {
                    this.selectedLearningOutcomes = learningOutcome;
                    if (learningOutcome.content.length <= 0) {
                        learningOutcome.content.forEach(item => {
                            this.shownLearningOutcome.push(item.defaultTitle);
                        });
                    }
                },
                (err) => {}
            );
    }

    private getAchievementDetails(): void {
        this.api
            .getLearningAchievement(
                this.editAchievementOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (achievement: LearningAchievementSpecView) => {
                    this.editAchievement = achievement;
                    this.availableLanguages =
                        this.editAchievement.additionalInfo.languages;
                    this.language = this.editAchievement.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages =
                        this.multilingualService.setUsedLanguages(
                            this.editAchievement.additionalInfo.languages,
                            this.defaultLanguage
                        );
                    this.credentialBuilderService.extractWebDocuments(
                        _get(
                            this.editAchievement,
                            'specifiedBy.supplementaryDocument',
                            []
                        ),
                        this.otherWebDocuments
                    );
                    this.credentialBuilderService.extractIdentifierContent(
                        _get(
                            this.editAchievement,
                            'specifiedBy.identifier',
                            []
                        ),
                        this.identifier
                    );
                    this.setForm();
                    this.getSubAchievements();
                    this.getProvenBy();
                    this.getInfluencedBy();
                    this.getEntitlesTo();
                    this.getLearningOutcomeRelations();
                    this.getAwardingBody();
                    this.markSpecTitleAsDirty();
                },
                (err) => {}
            );
    }

    private addNewLanguageControl(language: string): void {
        this.addTitleControls(language);
        this.addDescriptionControls(language);
        this.addSpecificationTitleControls(language);
        this.addSpecificationDescriptionControls(language);
        this.addEntryRequirementsControls(language);
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

    private addEntryRequirementsControls(
        language: string,
        value: string = null
    ): void {
        this.entryRequirements.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private addSpecificationTitleControls(
        language: string,
        value: string = null
    ): void {
        this.specificationTitle.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
                Validators.required,
                noSpaceValidator,
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

    private updateAchievement(): void {
        this.achievementBody.oid = this.editAchievement.oid;
        this.api
            .updateLearningAchievement(
                this.achievementBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (achievement: ResourceLearningAchievementSpecView) => {
                    this.showNotification();
                    this.closeModal(
                        true,
                        achievement.oid,
                        achievement.defaultTitle
                    );
                },
                (err) => {
                    this.closeModal(false);
                }
            );
    }

    private getProvenBy(): void {
        this.api
            .getProvenBy(
                this.editAchievementOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((provenBy: ResourceAssessmentSpecView) => {
                if (provenBy !== null) {
                    this.selectedProvenBy = provenBy;
                    this.shownProvenBy.oid = this.selectedProvenBy.oid;
                    this.shownProvenBy.title = this.selectedProvenBy.defaultTitle;
                    this.loadingModal = false;
                } else {
                    this.loadingModal = false;
                }
            });
    }

    private getInfluencedBy(): void {
        this.api
            .listInfluencedBy(
                this.editAchievementOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (influencedBy: PagedResourcesLearningActivitySpecLiteView) => {
                    if (influencedBy.content.length > 0) {
                        this.selectedInfluencedBy = influencedBy;
                        influencedBy.content.forEach(item => {
                            this.shownInfluencedBy.push(item.defaultTitle);
                        });
                    }
                }
            );
    }

    private getEntitlesTo(): void {
        this.api
            .listEntitlesTo(
                this.editAchievementOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((entitledTo: PagedResourcesEntitlementSpecLiteView) => {
                if (entitledTo.content.length > 0) {
                    this.selectedEntitledTo = entitledTo.content[0];
                    this.shownEntitled.oid = entitledTo.content[0].oid;
                    this.shownEntitled.title = entitledTo.content[0].defaultTitle;
                }
            });
    }

    private getAwardingBody(): void {
        this.api
            .listAwardingBodies(
                this.editAchievementOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (awardingBody: PagedResourcesOrganizationSpecLiteView) => {
                    if (awardingBody.content.length > 0) {
                        this.selectedAwardingBody = awardingBody.content[0];
                        this.shownOrganization.oid = awardingBody.content[0].oid;
                        this.shownOrganization.title = awardingBody.content[0].defaultTitle;
                        this.loadingModal = false;
                    } else {
                        this.loadingModal = false;
                    }
                }
            );
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
        if (this.provenBy.value) {
            relProvenBy = {
                oid: [
                    Array.isArray(this.provenBy.value)
                        ? this.provenBy.value[0]
                        : this.provenBy.value,
                ],
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
        if (this.entitledTo.value) {
            relEntitledTo = {
                oid: [
                    Array.isArray(this.entitledTo.value)
                        ? this.entitledTo.value[0]
                        : this.entitledTo.value,
                ],
            };
        }
        return relEntitledTo;
    }

    private setAwardingBody(): SubresourcesOids {
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
    }

    private createAchievement(): void {
        this.api
            .createLearningAchievement(
                this.achievementBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (achievement: ResourceLearningAchievementSpecView) => {
                    this.showNotification();
                    this.closeModal(
                        true,
                        achievement.oid,
                        achievement.defaultTitle
                    );
                },
                (err) => {
                    this.closeModal(false);
                }
            );
    }

    private setAchievementBody(): void {
        this.achievementBody = {
            defaultTitle: this.defaultTitle.value,
            defaultLanguage: this.defaultLanguage,
            title: this.credentialBuilderService.getDTView(this.title),
            description: this.credentialBuilderService.getDTView(
                this.description
            ),
            additionalNote: this.additionalNote,
            wasAwardedBy: this.getAwardingDate(),
            specifiedBy: this.getSpecification(),
            relAwardingBody: this.setAwardingBody(),
            relProvenBy: this.setProvenBy(),
            relInfluencedBy: this.setInfluencedBy(),
            relEntitlesTo: this.setEntitledTo(),
            relSubAchievements: this.setSubAchievements(),
            relLearningOutcomes: this.setLearningOutcomes(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
        };
    }

    private getAwardingDate(): AwardingProcessDCView {
        const awardingDate = this.dateFormatService.dateToStringDateTime(
            this.awardingDate.value
        );
        return awardingDate ? { awardingDate: awardingDate } : null;
    }

    private getSpecification(): LearningSpecificationDCView {
        let specifiedBy: LearningSpecificationDCView;
        let nqfLevel;
        if (Array.isArray(this.nqfLevel)) {
            nqfLevel = this.nqfLevel;
        } else if (this.nqfLevel == null) {
            nqfLevel = [];
        } else {
            nqfLevel = [this.nqfLevel];
        }
        specifiedBy = {
            title: this.credentialBuilderService.getDTView(
                this.specificationTitle
            ),
            identifier: this.credentialBuilderService.getIdentifierAchievements(
                this.identifier
            ),
            learningOutcomeDescription: this.credentialBuilderService.getDTView(
                this.specificationDescription
            ),
            maximumDuration: this.maxDuration.value,
            volumeOfLearning: this.workload.value,
            ectsCreditPoints: this.getCreditPoints(),
            homePage: this.credentialBuilderService.getHomePage(
                this.homePage.value
            ),
            supplementaryDocument:
                this.credentialBuilderService.getOtherDocument(
                    this.otherWebDocuments,
                    this.defaultLanguage
                ),
            iscedFCode: this.thematicArea,
            mode: this.credentialBuilderService.getArrayFromSingleItem(
                this.modeOfLearning.value
            ),
            learningSetting: this.learningSetting.value,
            learningOpportunityType:
                this.credentialBuilderService.getArrayFromSingleItem(
                    this.learningOpportunityType.value
                ),
            eqfLevel: this.eqfLevel.value,
            nqfLevel: nqfLevel,
            nqfLevelParent: this.nqfLevelParent,
            language: this.achievementLanguage,
            additionalNote: this.additionalNoteSpecification,
            entryRequirementsNote: this.credentialBuilderService.getDTView(
                this.entryRequirements
            ),
            partialQualification: this.partialQualification.value
                ? this.isPartialQualification()
                : null,
            hasAccreditation: this.accreditation.value,
        };
        return specifiedBy;
    }

    private getCreditPoints(): ScoreDTView {
        let credits: ScoreDTView = null;
        if (this.ectsCredits.value) {
            credits = { content: this.ectsCredits.value };
        }
        return credits;
    }

    private isPartialQualification(): boolean {
        return this.partialQualification.value.id === '0' ? true : false;
    }

    private setForm(): void {
        this.additionalNoteView = _get(
            this.editAchievement,
            'additionalNote',
            []
        );
        this.additionalNoteSpecView = _get(
            this.editAchievement,
            'specifiedBy.additionalNote',
            []
        );
        this.achievementLanguage = _get(
            this.editAchievement,
            'specifiedBy.language',
            null
        );
        this.nqfLevelParent = _get(
            this.editAchievement,
            'specifiedBy.nqfLevelParent',
            null
        );
        this.nqfLevel = _get(
            this.editAchievement,
            'specifiedBy.nqfLevel[0]',
            null
        );
        (this.thematicArea = _get(
            this.editAchievement,
            'specifiedBy.iscedFCode',
            null
        )),
            this.addControlsFromView();
        this.formGroup.patchValue({
            defaultTitle: this.editAchievement.defaultTitle,
            awardingDate: this.setAwardingDate(),
            workload: _get(
                this.editAchievement,
                'specifiedBy.volumeOfLearning',
                null
            ),
            maxDuration: _get(
                this.editAchievement,
                'specifiedBy.maximumDuration',
                null
            ),
            ectsCredits: _get(
                this.editAchievement,
                'specifiedBy.ectsCreditPoints.content',
                null
            ),
            modeOfLearning: _get(
                this.editAchievement,
                'specifiedBy.mode[0]',
                null
            ),
            learningSetting: _get(
                this.editAchievement,
                'specifiedBy.learningSetting',
                null
            ),
            learningOpportunityType: _get(
                this.editAchievement,
                'specifiedBy.learningOpportunityType[0]',
                null
            ),
            homePage: _get(
                this.editAchievement,
                'specifiedBy.homePage[0].content',
                null
            ),
            eqfLevel: _get(this.editAchievement, 'specifiedBy.eqfLevel', null),
            accreditation: _get(
                this.editAchievement,
                'specifiedBy.hasAccreditation',
                null
            ),
        });
        this.setPartialQualification();
        this.isValidAccreditation = !!this.accreditation.value;
        this.isLoading = false;
    }

    private addControlsFromView(): void {
        this.availableLanguages.forEach((language: string) => {
            this.addTitleControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    this.editAchievement.title.contents
                )
            );
            this.addDescriptionControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(this.editAchievement, 'description.contents', [])
                )
            );
            this.addSpecificationTitleControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(this.editAchievement, 'specifiedBy.title.contents', [])
                )
            );
            this.addSpecificationDescriptionControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editAchievement,
                        'specifiedBy.learningOutcomeDescription.contents',
                        []
                    )
                )
            );
            this.addEntryRequirementsControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editAchievement,
                        'specifiedBy.entryRequirementsNote.contents',
                        []
                    )
                )
            );
        });
    }

    private setAwardingDate(): Date | null {
        const date = _get(
            this.editAchievement,
            'wasAwardedBy.awardingDate',
            null
        );
        return date ? new Date(date) : null;
    }

    private showNotification() {
        if (this.editAchievement) {
            this.notificationService.showNotification({
                severity: 'success',
                summary: this.translateService.instant('common.edit'),
                detail: this.translateService.instant(
                    'credential-builder.operationSuccessful'
                ),
            });
        } else {
            this.notificationService.showNotification({
                severity: 'success',
                summary: this.translateService.instant('common.create'),
                detail: this.translateService.instant(
                    'credential-builder.operationSuccessful'
                ),
            });
        }
    }

    private setPartialQualification() {
        let partialQualification = _get(
            this.editAchievement,
            'specifiedBy.partialQualification',
            'noContent'
        );
        if (partialQualification !== 'noContent') {
            if (partialQualification) {
                this.formGroup.patchValue({
                    partialQualification: new UxLink({
                        id: 0,
                        label: this.translateService.instant('common.yes'),
                    }),
                });
            } else {
                this.formGroup.patchValue({
                    partialQualification: new UxLink({
                        id: 1,
                        label: this.translateService.instant('common.no'),
                    }),
                });
            }
        }
    }

    private nqfLevelInvalid(): boolean {
        if (this.nqfLevel === null && this.nqfLevelParent !== null) {
            return true;
        }
        return !!this.nqfLevelParent && this.nqfLevel.length <= 0;
    }

    private markSpecTitleAsDirty(): void {
        this.editAchievement.additionalInfo.languages.forEach(
            (language: string) => {
                this.specificationTitle.controls[language].markAsDirty();
            }
        );
    }

    private validateFormDatesValues() {
        if (
            this.awardingDate.value &&
            !this.dateFormatService.validateDate(this.awardingDate.value)
        ) {
            this.awardingDateValueInvalid = true;
            this.awardingDate.reset();
        } else {
            this.awardingDateValueInvalid = false;
        }
    }
}
