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
import { UxLanguage, UxLink, UxService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { DateFormatService } from '@services/date-format.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { MoreInformationComponent } from '@shared/components/more-information/more-information.component';
import { Constants, Entities } from '@shared/constants';
import {
    AwardingProcessDCView,
    CodeDTView,
    EntitlementSpecLiteView,
    LearningAchievementSpecView,
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
    V1Service,
} from '@shared/swagger';
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

    get thematicArea() {
        return this.formGroup.get('thematicArea');
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

    @Input() modalTitle: string;
    @Input() language: string;
    @Input() modalId: string = 'achievementModal';
    @Input() editAchievementOid?: number;
    @Output() onCloseModal: EventEmitter<{isEdit: boolean, oid: number, title: string}> = new EventEmitter();
    @ViewChild('additionalNoteSpecification')
    additionalNoteSpecification: MoreInformationComponent;
    @ViewChild('additionalNote')
    additionalNote: MoreInformationComponent;

    editAchievement: LearningAchievementSpecView;
    isAwardingBodyRequired: boolean = false;
    defaultLanguage: string;
    availableLanguages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    achievementLanguage: CodeDTView[] = [];
    nqfLevel: CodeDTView[] = [];
    nqfLevelParent: CodeDTView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    achievementBody: LearningAchievementSpecView;
    isSaveDisabled: boolean = false;
    isLoading: boolean = true;
    selectedSubAchievements: PagedResourcesLearningAchievementSpecLiteView = {
        content: [],
        links: [],
        page: null
    };
    subAchievementOidList: number[] = [];
    selectedLearningOutcomes: PagedResourcesLearningOutcomeSpecLiteView = {
        content: [],
        links: [],
        page: null
    };
    learningOutcomesOidList: number[] = [];
    influencedByOidList: number[] = [];
    selectedAwardingBody: OrganizationSpecLiteView;
    selectedProvenBy: ResourceAssessmentSpecView;
    selectedInfluencedBy: PagedResourcesLearningActivitySpecLiteView = {
        content: [],
        links: [],
        page: null
    };
    selectedEntitledTo: EntitlementSpecLiteView;
    indexToNextTab: number;
    openEntityModal: { [key: string]: { modalId: string, isOpen: boolean, oid?: number } } = {};
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
        awardingDate: new FormControl(''),
        awardingBody: new FormControl(null),
        description: new FormGroup({}),
        provenBy: new FormControl(null),
        entitledTo: new FormControl(null),
        // Specification
        specificationTitle: new FormGroup({}),
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
        thematicArea: new FormControl(null),
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
        accreditation: new FormControl({ value: '', disabled: true }),
        partialQualification: new FormControl(null),
    });

    constructor(
        public uxService: UxService,
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private dateFormatService: DateFormatService
    ) { }

    ngOnInit() {
        this.awardingDateValueChange();
        this.titleValueChangeAutocomplete();
        if (this.editAchievementOid) {
            this.modalTitle = this.translateService.instant('credential-builder.achievements-tab.editAchievement');
            this.getAchievementDetails();
        } else {
            this.modalTitle = this.translateService.instant('credential-builder.achievements-tab.createAchievement');
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
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }
    onSave(): void {
        this.isLoading = true;
        if (this.isFormInvalid() || this.nqfLevelInvalid()) {
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
        this.moreInformationRemoveLanguage(language);
        this.title.removeControl(language);
        this.description.removeControl(language);
        this.specificationTitle.removeControl(language);
        this.specificationDescription.removeControl(language);
        this.entryRequirements.removeControl(language);
    }

    titleValueChangeAutocomplete(): void {
        this.title.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
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
            .pipe(takeUntil(this.destroy$)).subscribe((dateValue) => {
                const validator = dateValue ? Validators.required : null;
                this.isAwardingBodyRequired = !!dateValue;
                this.awardingBody.setValidators(validator);
                this.awardingBody.updateValueAndValidity();
            });
    }

    onSubAchievementSelectionChange(oids: number[]): void {
        this.subAchievementOidList = oids;
    }

    onLearningOutcomeSelectionChange(oids: number[]): void {
        this.learningOutcomesOidList = oids;
    }

    onAwardingBodySelectionChange(oid: number): void {
        this.formGroup.patchValue({
            awardingBody: oid,
        });
    }

    onProvenBySelectionChange(oid: number): void {
        this.formGroup.patchValue({
            provenBy: oid,
        });
    }

    onInfluencedBySelectionChange(oids: number[]): void {
        this.influencedByOidList = oids;
    }

    onEntitledToSelectionChange(oid: number): void {
        this.formGroup.patchValue({
            entitledTo: oid,
        });
    }

    achievementLanguageSelectionChange(
        achievementLanguage: CodeDTView[]
    ): void {
        this.achievementLanguage = achievementLanguage;
    }

    nqfLevelSelectionChange(nqfLevel: CodeDTView[]): void {
        this.nqfLevel = nqfLevel;
    }

    nqfLevelParentSelectionChange(nqfLevelParent: CodeDTView): void {
        if (_get(this.nqfLevelParent, 'uri') !== _get(nqfLevelParent, 'uri')) {
            this.nqfLevelParent = nqfLevelParent;
            this.nqfLevel = [];
        }
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
                this.selectedAwardingBody = item;
                break;
            case 'assessment':
                this.selectedProvenBy = item;
                break;
            case 'activity':
                this.selectedInfluencedBy =
                    this.credentialBuilderService.fillMultipleInput(this.selectedInfluencedBy, this.influencedByOidList, item);
                break;
            case 'entitlement':
                this.selectedEntitledTo = item;
                break;
            case 'achievement':
                this.selectedSubAchievements =
                    this.credentialBuilderService.fillMultipleInput(this.selectedSubAchievements, this.subAchievementOidList, item);
                break;
            case 'learningOutcome':
                this.selectedLearningOutcomes =
                    this.credentialBuilderService.fillMultipleInput(this.selectedLearningOutcomes, this.learningOutcomesOidList, item);
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
        this.setAchievementBody();
    }

    private isFormInvalid(): boolean {
        return (
            this.formGroup.invalid ||
            this.additionalNote.isFormInvalid() ||
            this.additionalNoteSpecification.isFormInvalid()
        );
    }

    private getSubAchievements(): void {
        this.api
            .listSubAchievements(
                this.editAchievement.oid,
                this.translateService.currentLang
            ).pipe(takeUntil(this.destroy$)).subscribe(
                (
                    achievements: PagedResourcesLearningAchievementSpecLiteView
                ) => {
                    this.selectedSubAchievements = achievements;
                },
                (err) => {}
            );
    }

    private getLearningOutcomeRelations(): void {
        this.api
            .listLearningOutcomes(
                this.editAchievement.oid,
                this.translateService.currentLang
            ).pipe(takeUntil(this.destroy$)).subscribe(
                (
                    learningOutcome: PagedResourcesLearningOutcomeSpecLiteView
                ) => {
                    this.selectedLearningOutcomes = learningOutcome;
                },
                (err) => {}
            );
    }

    private getAchievementDetails(): void {
        this.api
            .getLearningAchievement(
                this.editAchievementOid,
                this.translateService.currentLang
            ).pipe(takeUntil(this.destroy$)).subscribe(
                (achievement: LearningAchievementSpecView) => {
                    this.editAchievement = achievement;
                    this.availableLanguages = this.editAchievement.additionalInfo.languages;
                    this.language = this.editAchievement.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages = this.multilingualService.setUsedLanguages(
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
            ).pipe(takeUntil(this.destroy$)).subscribe(
                (achievement: ResourceLearningAchievementSpecView) => {
                    this.showNotification();
                    this.closeModal(true, achievement.oid, achievement.defaultTitle);
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
                this.selectedProvenBy = provenBy;
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
                oid: [Array.isArray(this.provenBy.value) ? this.provenBy.value[0] : this.provenBy.value],
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
                oid: [Array.isArray(this.entitledTo.value) ? this.entitledTo.value[0] : this.entitledTo.value],
            };
        }
        return relEntitledTo;
    }

    private setAwardingBody(): SubresourcesOids {
        let relAwardingBody: SubresourcesOids = null;
        if (this.awardingBody.value) {
            relAwardingBody = {
                oid: [Array.isArray(this.awardingBody.value) ? this.awardingBody.value[0] : this.awardingBody.value],
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
                    this.closeModal(true, achievement.oid, achievement.defaultTitle);
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
            additionalNote: this.additionalNote.getAdditionalNotes(),
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
        specifiedBy = {
            title: this.credentialBuilderService.getDTView(
                this.specificationTitle
            ),
            learningOutcomeDescription: this.credentialBuilderService.getDTView(
                this.specificationDescription
            ),
            maximumDuration: this.maxDuration.value,
            volumeOfLearning: this.workload.value,
            ectscreditPoints: this.getCreditPoints(),
            homePage: this.credentialBuilderService.getHomePage(
                this.homePage.value
            ),
            supplementaryDocument: this.credentialBuilderService.getOtherDocument(
                this.otherWebDocuments,
                this.defaultLanguage
            ),
            iscedfcode: this.credentialBuilderService.getArrayFromSingleItem(
                this.thematicArea.value
            ),
            mode: this.credentialBuilderService.getArrayFromSingleItem(
                this.modeOfLearning.value
            ),
            learningSetting: this.learningSetting.value,
            learningOpportunityType: this.credentialBuilderService.getArrayFromSingleItem(
                this.learningOpportunityType.value
            ),
            eqfLevel: this.eqfLevel.value,
            nqfLevel: Array.isArray(this.nqfLevel)
                ? this.nqfLevel
                : [this.nqfLevel],
            nqfLevelParent: this.nqfLevelParent,
            language: this.achievementLanguage,
            additionalNote: this.additionalNoteSpecification.getAdditionalNotes(),
            entryRequirementsNote: this.credentialBuilderService.getDTView(
                this.entryRequirements
            ),
            partialQualification: this.partialQualification.value
                ? this.isPartialQualification()
                : null,
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
                'specifiedBy.ectscreditPoints.content',
                null
            ),
            thematicArea: _get(
                this.editAchievement,
                'specifiedBy.iscedfcode[0]',
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
        });
        this.setPartialQualification();
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

    private moreInformationRemoveLanguage(language: string): void {
        this.additionalNote.languageRemoved(language);
        this.additionalNoteSpecification.languageRemoved(language);
    }

    private moreInformationAddLanguage(language: string): void {
        this.additionalNote.languageAdded(language);
        this.additionalNoteSpecification.languageAdded(language);
    }

    private nqfLevelInvalid(): boolean {
        return !!this.nqfLevelParent && this.nqfLevel.length <= 0;
    }

    private markSpecTitleAsDirty(): void {
        this.editAchievement.additionalInfo.languages.forEach(
            (language: string) => {
                this.specificationTitle.controls[language].markAsDirty();
            }
        );
    }
}
