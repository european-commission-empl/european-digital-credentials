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
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesLearningAchievementSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    PagedResourcesLearningOutcomeSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ResourceAssessmentSpecView,
    ResourceLearningAchievementSpecView,
    ScoreDTView,
    SubresourcesOids,
    V1Service
} from '@shared/swagger';
import { dateValidator } from '@shared/validators/date-validator';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-achievements-form',
    templateUrl: './achievements-form.component.html',
    styleUrls: ['./achievements-form.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [
        { provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT },
    ],
})
export class AchievementsFormComponent implements OnInit, OnDestroy {
    parts: UxLink[] = [];

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
    @ViewChild('messageBoxFormError')
        messageBoxFormError: EuiMessageBoxComponent;

    newEntity: LearningAchievementSpecView = null;

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
        return this.formGroup.get('awardingBody');
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

    get specificationDescription() {
        return this.formGroup.get('specificationDescription') as FormGroup;
    }

    get specificationDescriptionControl() {
        return this.specificationDescription.controls[
            this.language
        ] as FormControl;
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

    get accreditation() {
        return this.formGroup.get('accreditation') as FormControl;
    }

    get identifier() {
        return this.formGroup.get('identifier') as FormArray;
    }

    @Input() modalTitle: string;
    @Input() language: string;
    @Input() modalId = 'achievementModal';
    @Input() editAchievementOid?: number;
    @Input() isModal: boolean;

    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    editAchievement: LearningAchievementSpecView;
    defaultLanguage: string;
    availableLanguages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    achievementLanguage: CodeDTView[] = [];
    thematicArea: CodeDTView[] = [];
    nqfLevel: CodeDTView = null;
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
    selectedAwardingBodies: PagedResourcesOrganizationSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    awardingBodyOidList: number[] = [];
    learningOutcomesOidList: number[] = [];
    influencedByOidList: number[] = [];
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
    entityWillBeOpened: Entities | string;

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
        label: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_LABELS),
            noSpaceValidator,
        ]),
        // Achievement
        title: new FormGroup({}),
        awardingDate: new FormControl(null, [dateValidator]),
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
    },{
        validators: [this.awardingBodyRequiredIfAwardingDate]
    });
    isPrimaryLanguage = true;
    awardingDateValueInvalid: boolean;
    unsavedActivities: LearningActivitySpecLiteView[] = [];
    unsavedAchievements: LearningAchievementSpecLiteView[] = [];
    unsavedLearning: LearningOutcomeSpecLiteView[] = [];
    unsavedAwardingBody: OrganizationSpecLiteView[] = [];
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
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private dateFormatService: DateFormatService,
        private router: Router,
        private route: ActivatedRoute,
        private modalsService: ModalsService
    ) {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.loadBreadcrumb();
            });
    }

    ngOnInit() {
        if (this.isModal) {
            this.eventSave.pipe(takeUntil(this.destroy$)).subscribe((res) => {
                this.onSave();
            });
        } else {
            this.route.params.subscribe((params) => {
                if (params['id'] !== null || params['id'] !== undefined) {
                    this.editAchievementOid = params['id'];
                }
            });
        }
        this.isLoading = true;
        this.isPrimaryLanguage = true;
        this.loadBreadcrumb();
        this.awardingDateValueChange();
        this.titleValueChangeAutocomplete();
        if (this.editAchievementOid) {
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
            this.credentialBuilderService.addIdentifierRow(this.identifier);
            this.isLoading = false;
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.isAccreditationValidationFinished$.unsubscribe();
        this.destroy$.unsubscribe();
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
        this.awardingDate.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(
            () =>{
                if (
                    this.awardingDate.value &&
                    !this.dateFormatService.validateDate(this.awardingDate.value)
                ) {
                    this.awardingDate.reset(
                        null
                        ,{
                            emitEvent : false,
                            onlySelf: true
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
        this.formGroup.patchValue({
            awardingBody : oids
        });
    }

    onProvenBySelectionChange(oid): void {
        this.formGroup.patchValue({
            provenBy: oid,
        });
    }

    onInfluencedBySelectionChange(oids): void {
        this.influencedByOidList = oids;
    }

    onEntitledToSelectionChange(oid): void {
        this.formGroup.patchValue({
            entitledTo: oid,
        });
    }

    achievementLanguageSelectionChange(
        achievementLanguage: CodeDTView[]
    ): void {
        this.achievementLanguage = achievementLanguage;
    }

    thematicAreaSelectionChange(thematicArea: CodeDTView[]): void {
        this.thematicArea = thematicArea;
    }

    nqfLevelParentSelectionChange(nqfLevelParent: CodeDTView): void {
        if (
            _get(this.nqfLevelParent, 'uri', null) !==
            _get(nqfLevelParent, 'uri', null)
        ) {
            this.nqfLevelParent = nqfLevelParent;
            this.nqfLevel = null;
        }
    }

    nqfLevelSelectionChange(nqfLevel: CodeDTView): void {
        this.nqfLevel = nqfLevel;
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

    closeForm() {
        this.credentialBuilderService.setOcbTabSelected(1);
        this.router.navigateByUrl('credential-builder');
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
                this.selectedAwardingBodies =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedAwardingBodies,
                            this.awardingBodyOidList,
                            item
                        );
                break;
            case 'assessment':
                this.selectedProvenBy = item;
                break;
            case 'activity':
                this.selectedInfluencedBy =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedInfluencedBy,
                            this.influencedByOidList,
                            item
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
                            item
                        );
                break;
            case 'learningOutcome':
                this.selectedLearningOutcomes =
                        this.credentialBuilderService.fillMultipleInput(
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
                .subscribe({
                    next: () => {
                        this.accreditation.setErrors(null);
                        this.isValidAccreditation = true;
                        this.isAccreditationValidationFinished$.next(true);
                        this.isCheckingAccreditationValidity = false;
                    },
                    error: (error) => {
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
                    },
                });
        }
    }

    private saveForm(): void {
        if (
            this.isFormInvalid() ||
            this.nqfLevelInvalid() ||
            this.awardingDateValueInvalid
        ) {
            this.formGroup.markAsUntouched();
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
        this.openEntityModal[this.entityWillBeOpened] =
            this.modalsService.openModal(this.modalTitle, oid);
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
            .subscribe(
                (
                    achievements: PagedResourcesLearningAchievementSpecLiteView
                ) => {
                    this.selectedSubAchievements = achievements;
                }
            );
    }

    private getLearningOutcomeRelations(): void {
        this.api
            .listLearningOutcomes(
                this.editAchievement.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (
                    learningOutcome: PagedResourcesLearningOutcomeSpecLiteView
                ) => {
                    this.selectedLearningOutcomes = learningOutcome;
                }
            );
    }

    private getAwardingBodyRelations(): void {
        this.api
            .listAwardingBodies(
                this.editAchievement.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (awardingBody: PagedResourcesOrganizationSpecLiteView) => {
                    this.selectedAwardingBodies = awardingBody;
                }
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
                    this.getAwardingBodyRelations();
                    this.markSpecTitleAsDirty();
                },
                (err) => {
                    console.log('Error: ', err);
                }
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
            .subscribe({
                next: (achievement: ResourceLearningAchievementSpecView) => {
                    this.newEntity = achievement;
                    this.showNotification();
                }
            }).add(() => this.handleSaveNavigation());
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
                }
            });
    }

    /* private getAwardingBody(): void {
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
                    }
                }
            );
    } */

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
        this.api
            .createLearningAchievement(
                this.achievementBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (achievement: ResourceLearningAchievementSpecView) => {
                    this.newEntity = achievement;
                    this.showNotification();
                }
            }).add(() => this.handleSaveNavigation());
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
        const specifiedBy = {
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
            // Robert says: 'null better than []'
            nqfLevel: this.nqfLevel ? [this.nqfLevel] : null,
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
            label: this.editAchievement.label,
            awardingBody: this.setAwardingBody(),
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
        const partialQualification = _get(
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
        return !!this.nqfLevelParent && !this.nqfLevel;
    }

    private markSpecTitleAsDirty(): void {
        this.editAchievement.additionalInfo.languages.forEach(
            (language: string) => {
                this.specificationTitle.controls[language].markAsDirty();
            }
        );
    }


    isAwardingBodyRequired(): boolean{
        const isRequired = this.awardingDate.value && this.awardingDate.value !== 'Invalid date' ? true : false;
        return isRequired;
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

    private awardingBodyRequiredIfAwardingDate(formGroup: FormGroup){
        if (formGroup.value.awardingDate){
            return Validators.required(formGroup.get('awardingBody')) ? {
                awardingBodyRequiredError : true
            } : null;
        }
        return null;
    }
}
