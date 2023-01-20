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
    ContentDTView,
    DiplomaSpecLiteView,
    EntitlementSpecLiteView,
    EuropassCredentialSpecView,
    LearningAchievementSpecLiteView,
    LearningActivitySpecLiteView,
    NoteDTView,
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesLearningAchievementSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    ResourceOrganizationSpecView,
    SubresourcesOids,
    TextDTView,
    V1Service
} from '@shared/swagger';
import { dateValidator } from '@shared/validators/date-validator';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-credentials-form',
    templateUrl: './credentials-form.component.html',
    styleUrls: ['./credentials-form.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [
        { provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT },
    ],
})
export class CredentialsFormComponent implements OnInit, OnDestroy {
    parts: UxLink[] = [];

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
    @ViewChild('messageBoxFormError')
        messageBoxFormError: EuiMessageBoxComponent;

    newEntity: EuropassCredentialSpecView = null;
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
    availableLanguages: string[] = [];
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
    selectedHtmlTemplates: DiplomaSpecLiteView;
    achievementsOidList: number[] = [];
    activitiesOidList: number[] = [];
    entitlementsOidList: number[] = [];
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
                    this.editCredentialOid = params['id'];
                }
            });
        }
        this.isPrimaryLanguage = true;
        this.loadBreadcrumb();
        this.createForm();
        this.formGroup.get('validFrom').valueChanges.subscribe(() => {
            this.checkValidDate();
        });
        this.formGroup.get('expiryDate').valueChanges.subscribe(() => {
            this.checkValidDate();
        });
        if (this.editCredentialOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.credentials-tab.editCredential'
            );
            this.getCredentialDetails();
        } else {
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
            this.credentialBuilderService.addIdentifierRow(this.identifier);
            this.isLoading = false;
        }
    }

    createForm() {
        this.formGroup = new FormGroup({
            label: new FormControl(null, [
                Validators.maxLength(Constants.MAX_LENGTH_LABELS),
                noSpaceValidator,
            ]),
            title: new FormGroup({}),
            description: new FormGroup({}),
            validFrom: new FormControl(null, [
                Validators.required,
                dateValidator,
            ]),
            identifier: new FormArray([]),
            expiryDate: new FormControl(null, [dateValidator]),
            issuingOrganization: new FormControl(null, [Validators.required]),
            htmlTemplateSelected: new FormControl(null),
            credentialType: new FormControl(null, [Validators.required]),
        });
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
                this.formGroup.markAsUntouched();
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
        this.achievementsOidList = oids;
    }

    onActivitySelectionChange(oids): void {
        this.activitiesOidList = oids;
    }

    onEntitlementSelectionChange(oids): void {
        this.entitlementsOidList = oids;
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
                            this.achievementsOidList,
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
                            this.activitiesOidList,
                            item
                        );
                break;
            case 'entitlement':
                this.selectedEntitlements =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedEntitlements,
                            this.entitlementsOidList,
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

    private gotoEntity(oid: number = null) {
        this.openEntityModal[this.entityWillBeOpened] =
            this.modalsService.openModal(this.modalTitle, oid);
        this.setCredentialBody();
    }

    private getCredentialDetails(): void {
        this.api
            .getCredential(
                this.editCredentialOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (credential: EuropassCredentialSpecView) => {
                    this.editCredential = credential;
                    this.availableLanguages =
                        this.editCredential.additionalInfo.languages;
                    this.language = this.editCredential.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages =
                        this.multilingualService.setUsedLanguages(
                            this.editCredential.additionalInfo.languages,
                            this.defaultLanguage
                        );
                    this.credentialBuilderService.extractIdentifierContent(
                        _get(this.editCredential, 'identifier', []),
                        this.identifier
                    );
                    this.getIssuingOrganization();
                    this.setForm();
                },
                error: () => {
                    this.credentialBuilderService.setOcbTabSelected(0);
                    this.router.navigateByUrl('credential-builder');
                },
            });
    }

    private createCredential(): void {
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
                }
            }).add(() => this.handleSaveNavigation());
    }

    private updateCredential(): void {
        this.credentialBody.oid = this.editCredential.oid;
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
                }
            }).add(() => this.handleSaveNavigation());
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
            issuanceDate: this.dateFormatService.dateToStringDateTime(
                this.validFrom.value
            ),
            expirationDate: this.dateFormatService.dateToStringDateTime(
                this.expiryDate.value
            ),
            identifier: this.credentialBuilderService.getIdentifierAchievements(
                this.identifier
            ),
            type: this.credentialType.value,
            relAchieved: this.setAchieved(),
            relPerformed: this.setPerformed(),
            relEntitledTo: this.setEntitledTo(),
            relDiploma: this.setHTMLTemplateTo(),
            relIssuer: this.setIssuingOrganization(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
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

    private getIssuingOrganization(): void {
        this.api
            .getIssuer(
                this.editCredentialOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((issuingOrganization: ResourceOrganizationSpecView) => {
                this.selectedIssuingOrganization = issuingOrganization;
            });
    }

    private addTitleControls(): void {
        this.availableLanguages.forEach((language: string) => {
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
        this.availableLanguages.forEach((language: string) => {
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
            validFrom: new Date(this.editCredential.issuanceDate),
            expiryDate: this.editCredential.expirationDate
                ? new Date(this.editCredential.expirationDate)
                : null,
            credentialType: _get(this.editCredential, 'type', undefined),
        });
        this.getAchieved();
        this.getEntitledTo();
        this.getPerformed();
        this.getHTMLTemplateTo();
        this.isLoading = false;
    }

    private getAchieved(): void {
        this.api
            .listAchieved(
                this.editCredential.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (
                    achievedPage: PagedResourcesLearningAchievementSpecLiteView
                ) => {
                    this.selectedAchievements = achievedPage;
                }
            );
    }

    private getPerformed(): void {
        this.api
            .listPerformed(this.editCredential.oid)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (performedPage: PagedResourcesLearningActivitySpecLiteView) => {
                    this.selectedActivities = performedPage;
                }
            );
    }

    private getEntitledTo(): void {
        this.api
            .listEntitledTo(
                this.editCredential.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (entitledToPage: PagedResourcesEntitlementSpecLiteView) => {
                    this.selectedEntitlements = entitledToPage;
                }
            );
    }

    private getHTMLTemplateTo(): void {
        this.api
            .getDiploma(
                this.editCredential.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((htmlTemplateToPage: DiplomaSpecLiteView) => {
                if (htmlTemplateToPage !== null) {
                    this.selectedHtmlTemplates = htmlTemplateToPage;
                }
            });
    }

    private setAchieved(): SubresourcesOids {
        let relAchieved: SubresourcesOids = null;
        if (this.achievementsOidList.length > 0) {
            relAchieved = {
                oid: this.achievementsOidList,
            };
        }
        return relAchieved;
    }

    private setPerformed(): SubresourcesOids {
        let relPerformed: SubresourcesOids = null;
        if (this.activitiesOidList.length > 0) {
            relPerformed = {
                oid: this.activitiesOidList,
            };
        }
        return relPerformed;
    }

    private setEntitledTo(): SubresourcesOids {
        let relEntitledTo: SubresourcesOids = null;
        if (this.entitlementsOidList.length > 0) {
            relEntitledTo = {
                oid: this.entitlementsOidList,
            };
        }
        return relEntitledTo;
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
}
