import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewEncapsulation, } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage, UxService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { DateFormatService } from '@services/date-format.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { Constants, Entities } from '@shared/constants';
import {
    ContentDTView, DiplomaSpecLiteView,
    EuropassCredentialSpecView,
    NoteDTView,
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesLearningAchievementSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    ResourceOrganizationSpecView,
    SubresourcesOids,
    TextDTView,
    V1Service,
} from '@shared/swagger';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-credentials-modal',
    templateUrl: './credentials-modal.component.html',
    styleUrls: ['./credentials-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class CredentialsModalComponent implements OnInit, OnDestroy {
    get defaultTitle() {
        return this.formGroup.get('defaultTitle') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
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

    @Input() modalId: string = 'credentialModal';
    @Input() modalTitle: string;
    @Input() editCredentialOid?: number;
    @Output() onCloseModal: EventEmitter<boolean> = new EventEmitter();

    editCredential: EuropassCredentialSpecView;
    isSaveDisabled: boolean = false;
    isLoading: boolean = true;
    language: string;
    maxDate: Date;
    minDate: Date;
    defaultLanguage: string;
    availableLanguages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    credentialBody: EuropassCredentialSpecView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    selectedAchievements: PagedResourcesLearningAchievementSpecLiteView = {
        content: [],
        links: [],
        page: null
    };
    selectedActivities: PagedResourcesLearningActivitySpecLiteView = {
        content: [],
        links: [],
        page: null
    };
    selectedEntitlements: PagedResourcesEntitlementSpecLiteView = {
        content: [],
        links: [],
        page: null
    };
    selectedHtmlTemplates: DiplomaSpecLiteView;
    achievementsOidList: number[] = [];
    activitiesOidList: number[] = [];
    entitlementsOidList: number[] = [];
    htmlTemplateOid: number;
    selectedIssuingOrganization: ResourceOrganizationSpecView;
    openEntityModal: { [key: string]: { modalId: string, isOpen: boolean, oid?: number } } = {};
    entityWillBeOpened: Entities;

    formGroup = new FormGroup({
        defaultTitle: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.required,
            noSpaceValidator,
        ]),
        title: new FormGroup({}),
        description: new FormGroup({}),
        validFrom: new FormControl(null, [Validators.required]),
        expiryDate: new FormControl(null),
        issuingOrganization: new FormControl(null, [Validators.required]),
        credentialType: new FormControl(null, [Validators.required]),
    });

    constructor(
        public uxService: UxService,
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private dateFormatService: DateFormatService,
    ) {}

    ngOnInit() {
        if (this.editCredentialOid) {
            this.getCredentialDetails();
        } else {
            this.language = this.translateService.currentLang;
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

    closeModal(isEdit: boolean): void {
        this.formGroup.reset();
        this.onCloseModal.emit(isEdit);
        this.isSaveDisabled = false;
    }

    onSave(): void {
        this.isLoading = true;
        if (this.formGroup.invalid) {
            this.uxService.markControlsTouched(this.formGroup);
            this.isLoading = false;
            this.uxService.openMessageBox('messageBoxFormError');
        } else {
            this.isSaveDisabled = true;
            this.setCredentialBody();
            if (this.editCredential) {
                this.updateCredential();
            } else {
                this.createCredential();
            }
        }
    }

    checkValidDate(): void {
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
    }

    onIssuingOrganizationSelectionChange(oid: number): void {
        this.formGroup.patchValue({
            issuingOrganization: oid,
        });
    }

    onAchievementSelectionChange(oids: number[]): void {
        this.achievementsOidList = oids;
    }

    onActivitySelectionChange(oids: number[]): void {
        this.activitiesOidList = oids;
    }

    onEntitlementSelectionChange(oids: number[]): void {
        this.entitlementsOidList = oids;
    }

    onHTMLTemplateSelectionChange(oid: number): void {
        this.htmlTemplateOid = oid;
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
            case 'achievement':
                this.selectedAchievements =
                    this.credentialBuilderService.fillMultipleInput(this.selectedAchievements, this.achievementsOidList, item);
                break;
            case 'organization':
                this.selectedIssuingOrganization = item;
                break;
            case 'activity':
                this.selectedActivities =
                this.credentialBuilderService.fillMultipleInput(this.selectedActivities, this.activitiesOidList, item);
                break;
            case 'entitlement':
                this.selectedEntitlements =
                this.credentialBuilderService.fillMultipleInput(this.selectedEntitlements, this.entitlementsOidList, item);
                break;
            case 'htmlTemplate':
                this.selectedHtmlTemplates = item;
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
        this.setCredentialBody();
    }

    private getCredentialDetails(): void {
        this.api
            .getCredential(
                this.editCredentialOid,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (credential: EuropassCredentialSpecView) => {
                    this.editCredential = credential;
                    this.availableLanguages = this.editCredential.additionalInfo.languages;
                    this.language = this.editCredential.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages = this.multilingualService.setUsedLanguages(
                        this.editCredential.additionalInfo.languages,
                        this.defaultLanguage
                    );
                    this.getIssuingOrganization();
                    this.setForm();
                },
                (err) => this.closeModal(false)
            );
    }
    private createCredential(): void {
        this.api
            .createCredential(
                this.credentialBody,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (credential: EuropassCredentialSpecView) => {
                    this.isLoading = false;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.create'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.closeModal(true);
                },

                (err) => {
                    this.closeModal(false);
                    this.isLoading = false;
                }
            );
    }

    private updateCredential(): void {
        this.credentialBody.oid = this.editCredential.oid;
        this.api
            .updateCredential(
                this.credentialBody,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (credential: EuropassCredentialSpecView) => {
                    this.isLoading = false;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.closeModal(true);
                },
                (err) => {
                    this.isLoading = false;
                    this.closeModal(false);
                }
            );
    }

    private setCredentialBody(): void {
        this.credentialBody = {
            defaultTitle: this.defaultTitle.value,
            defaultLanguage: this.defaultLanguage,
            title: this.getTitle(),
            description: this.getDescription(),
            issuanceDate: this.dateFormatService.dateToStringDateTime(
                this.validFrom.value
            ),
            expirationDate: this.dateFormatService.dateToStringDateTime(
                this.expiryDate.value
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
        let descriptionContents: ContentDTView[] = this.multilingualService.formToView(
            this.description.value
        );
        if (descriptionContents.length > 0) {
            description = {
                contents: descriptionContents,
            };
        }
        return description;
    }

    private getTitle(): TextDTView {
        let title: TextDTView;
        title = {
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
            .takeUntil(this.destroy$)
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
            defaultTitle: this.editCredential.defaultTitle,
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
            .takeUntil(this.destroy$)
            .subscribe(
                (
                    achievedPage: PagedResourcesLearningAchievementSpecLiteView
                ) => {
                    this.selectedAchievements = achievedPage;
                },
                (err) => {}
            );
    }

    private getPerformed(): void {
        this.api
            .listPerformed(this.editCredential.oid)
            .takeUntil(this.destroy$)
            .subscribe(
                (performedPage: PagedResourcesLearningActivitySpecLiteView) => {
                    this.selectedActivities = performedPage;
                },
                (err) => {}
            );
    }

    private getEntitledTo(): void {
        this.api
            .listEntitledTo(
                this.editCredential.oid,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (entitledToPage: PagedResourcesEntitlementSpecLiteView) => {
                    this.selectedEntitlements = entitledToPage;
                },
                (err) => {}
            );
    }

    private getHTMLTemplateTo(): void {
        this.api
            .getDiploma(
                this.editCredential.oid,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (htmlTemplateToPage: DiplomaSpecLiteView) => {
                    this.selectedHtmlTemplates = htmlTemplateToPage;
                },
                (err) => {}
            );
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
        if (this.htmlTemplateOid) {
            relHTMLTemplateTo = { oid: [
                Array.isArray(this.htmlTemplateOid) ? this.htmlTemplateOid[0] : this.htmlTemplateOid] };
        }
        return relHTMLTemplateTo;
    }

    private setIssuingOrganization(): SubresourcesOids {
        let relIssuingOrganization: SubresourcesOids = null;
        if (this.issuingOrganization.value) {
            relIssuingOrganization = {
                oid: [
                    Array.isArray(this.issuingOrganization.value) ? this.issuingOrganization.value[0] : this.issuingOrganization.value]
            };
        }
        return relIssuingOrganization;
    }
}
