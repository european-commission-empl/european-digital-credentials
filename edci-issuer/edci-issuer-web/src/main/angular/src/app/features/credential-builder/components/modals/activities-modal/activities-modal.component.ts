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
    CodeDTView,
    LearningActivitySpecView,
    LearningActSpecificationDCView,
    LocationDCView,
    NoteDTView,
    OrganizationSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ResourceLearningActivitySpecView,
    SubresourcesOids,
    TextDTView,
    V1Service,
} from '@shared/swagger';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-activities-modal',
    templateUrl: './activities-modal.component.html',
    styleUrls: ['./activities-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class ActivitiesModalComponent implements OnInit, OnDestroy {
    selectedDirectedBy: OrganizationSpecLiteView;
    get defaultTitle() {
        return this.formGroup.get('defaultTitle') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
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

    get directedBy() {
        return this.formGroup.get('directedBy');
    }

    get location() {
        return this.formGroup.get('location') as FormGroup;
    }

    get specificationDescription() {
        return this.formGroup.get('specificationDescription') as FormGroup;
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

    @Input() modalTitle: string;
    @Input() language: string;
    @Input() modalId: string = 'activityModal';
    @Input() editActivityOid?: number;
    @Output() onCloseModal: EventEmitter<{isEdit: boolean, oid: number, title: string}> = new EventEmitter();
    @ViewChild('additionalNoteSpecification')
    additionalNoteSpecification: MoreInformationComponent;
    @ViewChild('additionalNote')
    additionalNote: MoreInformationComponent;

    defaultLanguage: string;
    editActivity: LearningActivitySpecView;
    activityLanguage: CodeDTView[] = [];
    locationNUTS: CodeDTView[] = [];
    availableLanguages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    destroy$: Subject<boolean> = new Subject<boolean>();
    activityBody: LearningActivitySpecView;
    isSaveDisabled: boolean = false;
    isLoading: boolean = true;
    additionalNoteView: NoteDTView[];
    additionalNoteSpecView: NoteDTView[];
    selectedSubActivities: PagedResourcesLearningActivitySpecLiteView = {
        content: [],
        links: [],
        page: null
    };
    subActivitiesOidList: number[] = [];
    indexToNextTab: number;
    openEntityModal: { [key: string]: { modalId: string, isOpen: boolean, oid?: number } } = {};
    entityWillBeOpened: Entities;
    formGroup = new FormGroup({
        // Activity
        defaultTitle: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.required,
            noSpaceValidator,
        ]),
        title: new FormGroup({}),
        description: new FormGroup({}),
        startDate: new FormControl(null),
        endDate: new FormControl(null),
        workload: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_INTEGERS),
            Validators.pattern(Constants.INTEGER_REGULAR_EXPRESSION),
        ]),
        directedBy: new FormControl(null),
        location: new FormGroup({}),
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
    });

    constructor(
        public uxService: UxService,
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private multilingualService: MultilingualService,
        private notificationService: NotificationService,
        private dateFormatService: DateFormatService
    ) {}

    ngOnInit() {
        if (this.editActivityOid) {
            this.modalTitle = this.translateService.instant('credential-builder.activities-tab.editActivity');
            this.getActivityDetails();
        } else {
            this.modalTitle = this.translateService.instant('credential-builder.activities-tab.createActivity');
            this.language = this.language || this.translateService.currentLang;
            this.credentialBuilderService.addOtherDocumentRow(
                this.supplementaryDocument
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
        if (this.isFormInvalid()) {
            this.uxService.markControlsTouched(this.formGroup);
            this.isLoading = false;
            this.uxService.openMessageBox('messageBoxFormError');
        } else {
            this.isSaveDisabled = true;
            this.setActivityBody();
            if (this.editActivity) {
                this.updateActivity();
            } else {
                this.createActivity();
            }
        }
    }

    checkValidDate(): void {
        if (
            !this.dateFormatService.validateDates(
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

    closeModal(isEdit: boolean, oid?: number, title?: string): void {
        this.onCloseModal.emit( { isEdit, oid, title } );
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
        this.location.removeControl(language);
        this.specificationDescription.removeControl(language);
    }

    createActivity(): void {
        this.api
            .createLearningActivity(
                this.activityBody,
                this.translateService.currentLang
            ).pipe(takeUntil(this.destroy$)).subscribe(
                (activity: LearningActivitySpecView) => {
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.create'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.closeModal(true, activity.oid, activity.defaultTitle);
                },
                (err) => {
                    this.closeModal(false);
                }
            );
    }

    activityLanguageSelectionChange(activityLanguage: CodeDTView[]): void {
        this.activityLanguage = activityLanguage;
    }

    locationNUTSSelectionChange(locationNUTS: CodeDTView[]): void {
        this.locationNUTS = locationNUTS;
    }

    onSubActivitiesSelectionChange(oids: number[]): void {
        this.subActivitiesOidList = oids;
    }

    onDirectedBySelectionChange(oid: number): void {
        this.formGroup.patchValue({ directedBy: oid });
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
                this.selectedDirectedBy = item;
                break;
            case 'activity':
                this.selectedSubActivities =
                    this.credentialBuilderService.fillMultipleInput(this.selectedSubActivities, this.subActivitiesOidList, item);
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
        this.setActivityBody();
    }

    private getActivityDetails(): void {
        this.api
            .getLearningActivity(
                this.editActivityOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$)).subscribe(
                (credential: ResourceLearningActivitySpecView) => {
                    this.editActivity = credential;
                    this.availableLanguages = this.editActivity.additionalInfo.languages;
                    this.language = this.editActivity.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages = this.multilingualService.setUsedLanguages(
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
                    this.getDirectedBy();
                    this.getSubActivities();
                },
                (err) => this.closeModal(false)
            );
    }

    private addNewLanguageControl(language: string): void {
        this.addTitleControls(language);
        this.addDescriptionControls(language);
        this.addLocationControls(language);
        this.addSpecificationDescriptionControls(language);
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

    private addLocationControls(language: string, value: string = null): void {
        this.location.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
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

    private updateActivity(): void {
        this.activityBody.oid = this.editActivity.oid;
        this.api
            .updateLearningActivity(
                this.activityBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (activity: ResourceLearningActivitySpecView) => {
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.closeModal(true, activity.oid, activity.defaultTitle);
                },
                (err) => {
                    this.closeModal(false);
                }
            );
    }

    private setActivityBody(): void {
        this.activityBody = {
            defaultTitle: this.defaultTitle.value,
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
            workload: this.workload.value,
            location: this.getLocation(),
            additionalNote: this.additionalNote.getAdditionalNotes(),
            specifiedBy: this.getActivitiesSpecification(),
            relDirectedBy: this.setDirectedBy(),
            relHasPart: this.setSubActivities(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
        };
    }

    private getActivitiesSpecification(): LearningActSpecificationDCView {
        let specifiedBy: LearningActSpecificationDCView;
        specifiedBy = {
            description: this.credentialBuilderService.getDTView(
                this.specificationDescription
            ),
            workload: this.specificationWorkload.value,
            supplementaryDocument: this.credentialBuilderService.getOtherDocument(
                this.supplementaryDocument,
                this.defaultLanguage
            ),
            language:
                _get(this.activityLanguage, 'length', 0) > 0
                    ? this.activityLanguage
                    : null,

            learningActivityType: this.credentialBuilderService.getArrayFromSingleItem(
                this.learningActivityType.value
            ),
            mode: this.modeOfLearning.value,
            homePage: this.credentialBuilderService.getHomePage(
                this.homePage.value
            ),
            additionalNote: this.additionalNoteSpecification.getAdditionalNotes(),
        };
        return this.credentialBuilderService.getObjectIfContent(specifiedBy);
    }

    private getLocation(): LocationDCView[] {
        let location: LocationDCView[] = null;
        const geographicName: TextDTView = this.credentialBuilderService.getDTView(
            this.location
        );
        if (geographicName || this.locationNUTS.length > 0) {
            location = [
                {
                    geographicName: geographicName,
                    spatialCode:
                        this.locationNUTS.length > 0 ? this.locationNUTS : null,
                },
            ];
        }
        return location;
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

    private getSubActivities(): void {
        this.api
            .listHasActPart(
                this.editActivity.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (activities: PagedResourcesLearningActivitySpecLiteView) => {
                    this.selectedSubActivities = activities;
                },
                (err) => {}
            );
    }

    private setDirectedBy(): SubresourcesOids {
        let relDirectedBy: SubresourcesOids = null;
        if (this.directedBy.value) {
            relDirectedBy = {
                oid: [Array.isArray(this.directedBy.value) ? this.directedBy.value[0] : this.directedBy.value],
            };
        }
        return relDirectedBy;
    }

    private getDirectedBy(): void {
        this.api
            .listDirectedBy(
                this.editActivityOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((directedBy: PagedResourcesOrganizationSpecLiteView) => {
                if (directedBy.content.length > 0) {
                    this.selectedDirectedBy = directedBy.content[0];
                }
            });
    }

    private setForm(): void {
        this.additionalNoteView = _get(this.editActivity, 'additionalNote', []);
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
        this.locationNUTS = _get(
            this.editActivity,
            'location[0].spatialCode',
            []
        );
        this.addControlsFromView();
        this.formGroup.patchValue({
            defaultTitle: this.editActivity.defaultTitle,
            startDate: this.getStartDate(),
            endDate: this.getEndDate(),
            workload: _get(this.editActivity, 'workload', null),
            learningActivityType: _get(
                this.editActivity,
                'specifiedBy.learningActivityType[0]',
                null
            ),
            modeOfLearning: _get(this.editActivity, 'specifiedBy.mode', null),
            specificationWorkload: _get(
                this.editActivity,
                'specifiedBy.workload',
                null
            ),
            homePage: _get(
                this.editActivity,
                'specifiedBy.homePage[0].content',
                null
            ),
        });
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
        this.availableLanguages.forEach((language: string) => {
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

            this.addLocationControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editActivity,
                        'location[0].geographicName.contents',
                        []
                    )
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
        });
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
